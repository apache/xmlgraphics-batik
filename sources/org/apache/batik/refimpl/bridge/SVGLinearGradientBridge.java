/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.StringReader;
import java.util.Vector;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;
import org.apache.batik.util.awt.LinearGradientPaint;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * This class bridges an SVG <tt>linearGradient</tt> element with
 * a <tt>LinearGradientPaint</tt>
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGLinearGradientBridge extends SVGGradientBridge
        implements PaintBridge {

    /**
     * Creates a <tt>Paint</tt> used to draw the outline of a
     * <tt>Shape</tt> of a <tt>ShapeNode</tt>.
     * @param ctx the context to use
     * @param paintedElement the Element with 'stroke' and
     * 'stroke-opacity' attributes.
     * @param paintElement teh Element which contains the paint's definition
     */
    public Paint createStrokePaint(BridgeContext ctx,
                                   GraphicsNode paintedNode,
                                   Element paintedElement,
                                   Element paintElement){
        return createPaint(ctx, paintedNode, paintedElement, paintElement);
    }

    /**
     * Creates a <tt>Paint</tt> used to fill a <tt>Shape</tt> of a
     * <tt>ShapeNode</tt>.
     * @param ctx the context to use
     * @param paintedElement the Element with 'fill' and
     * 'fill-opacity' attributes.
     * @param paintElement teh Element which contains the paint's definition
     */
    public Paint createFillPaint(BridgeContext ctx,
                                 GraphicsNode paintedNode,
                                 Element paintedElement,
                                 Element paintElement){
        return createPaint(ctx, paintedNode, paintedElement, paintElement);
    }

    protected Paint createPaint(BridgeContext ctx,
                                GraphicsNode paintedNode,
                                Element paintedElement,
                                Element paintElement) {
        //
        // Get unit processor to compute gradient control points
        //
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(paintElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        // parse the gradientUnits attribute, (default is 'objectBoundingBox')
        String units = paintElement.getAttributeNS(null, ATTR_GRADIENT_UNITS);
        if(units.length() == 0){
            units = SVG_OBJECT_BOUNDING_BOX_VALUE;
        }

        // parse the x1 attribute, (default is 0%)
        String x1 = paintElement.getAttributeNS(null, ATTR_X1);
        if (x1.length() == 0){
            x1 = "0%";
        }

        // parse the y1 attribute, (default is 0%)
        String y1 = paintElement.getAttributeNS(null, ATTR_Y1);
        if (y1.length() == 0){
            y1 = "0%";
        }

        // parse the x2 attribute, (default is 100%)
        String x2 = paintElement.getAttributeNS(null, ATTR_X2);
        if (x2.length() == 0){
            x2 = "100%";
        }

        // parse the y2 attribute, (default is 0%)
        String y2 = paintElement.getAttributeNS(null, ATTR_Y2);
        if (y2.length() == 0){
            y2 = "0%";
        }

        int unitsType;
        try {
            unitsType = SVGUtilities.parseCoordinateSystem(units);
        } catch (IllegalArgumentException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("linearGradient.units.invalid",
                                       new Object[] {units,
                                                     ATTR_GRADIENT_UNITS}));
        }
        SVGElement svgPaintedElement = (SVGElement) paintedElement;
        Point2D p1
            = SVGUtilities.convertGradientPoint(svgPaintedElement,
                                                ATTR_X1, x1,
                                                ATTR_Y1, y1,
                                                unitsType, uctx);
        Point2D p2
            = SVGUtilities.convertGradientPoint(svgPaintedElement,
                                                ATTR_X2, x2,
                                                ATTR_Y2, y2,
                                                unitsType, uctx);

        // parse the 'spreadMethod' attribute, (default is PAD)
        String spreadMethod =
            paintElement.getAttributeNS(null, ATTR_SPREAD_METHOD);
        if (spreadMethod.length() == 0) {
            spreadMethod = VALUE_PAD;
        }

        LinearGradientPaint.CycleMethodEnum cycleMethod =
            convertSpreadMethod(spreadMethod);

        // Extract gradient transform
        AffineTransform at =
            SVGUtilities.convertAffineTransform(paintElement,
                                                ATTR_GRADIENT_TRANSFORM,
                                                ctx.getParserFactory());

        at  = SVGUtilities.convertAffineTransform(at, paintedNode, unitsType);

        // Extract stop colors and intervals
        Vector stopVector = extractGradientStops(paintElement, ctx);

        // if no stop, fill is 'none'
        if (stopVector.size() == 0) {
            return null;
        }

        // if one stop, the fill is just one color
        if (stopVector.size() == 1) {
            return ((GradientStop) stopVector.get(0)).stopColor;
        }

        // Convert the stop offsets to intervals
        int nStops = stopVector.size();
        float curOffset = 0;
        if (nStops > 0) {
            GradientStop stop = (GradientStop) stopVector.elementAt(0);
            curOffset = stop.offset;
        }
        for (int i=1; i < nStops; i++) {
            GradientStop stop = (GradientStop)stopVector.elementAt(i);
            if(stop.offset < curOffset){
                stop.offset = curOffset;
            }
            curOffset = stop.offset;
        }

        // Extract the color interpolation property
        CSSPrimitiveValue colorInterpolation =
            (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (CSS_COLOR_INTERPOLATION_PROPERTY);

        LinearGradientPaint.ColorSpaceEnum colorSpace
            = LinearGradientPaint.SRGB;

        if(CSS_LINEARRGB_VALUE.equals(colorInterpolation.getStringValue())){
            colorSpace = LinearGradientPaint.LINEAR_RGB;
        }

        // Build Paint
        Paint paint = null;
        if(nStops > 0){
            Color colors[] = new Color[nStops];
            float offsets[] = new float[nStops];
            for(int i=0; i<nStops; i++){
                GradientStop stop = (GradientStop)stopVector.elementAt(i);
                colors[i] = stop.stopColor;
                offsets[i] = stop.offset;
            }
            paint = new LinearGradientPaint(p1, p2, offsets, colors,
                                            cycleMethod,
                                            colorSpace,
                                            at);
        }
        return paint;
    }
}
