/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.ext.awt.RadialGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * This class bridges an SVG <tt>radialGradient</tt> element with
 * a <tt>RadialGradientPaint</tt>
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGRadialGradientBridge extends SVGGradientBridge
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
                                Element paintElement){

        GraphicsNodeRenderContext rc =
                         ctx.getGraphicsNodeRenderContext();
        DocumentLoader loader = ctx.getDocumentLoader();

        //
        // Get unit processor to compute gradient control points
        //
        CSSStyleDeclaration cssDecl
            = CSSUtilities.getComputedStyle(paintElement);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        // parse the gradientUnits attribute, (default is 'objectBoundingBox')
        String units =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 ATTR_GRADIENT_UNITS,
                                                 loader);
        if(units.length() == 0){
            units = SVG_OBJECT_BOUNDING_BOX_VALUE;
        }

        // parse the cx attribute, (default is 50%)
        String cx =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 SVG_CX_ATTRIBUTE,
                                                 loader);
        if(cx.length() == 0){
            cx = "50%";
        }

        // parse the cy attribute, (default is 50%)
        String cy =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 SVG_CY_ATTRIBUTE,
                                                 loader);
        if(cy.length() == 0){
            cy = "50%";
        }

        // parse the r attribute, (default is 50%)
        String r =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 SVG_R_ATTRIBUTE,
                                                 loader);
        if(r.length() == 0){
            r = "50%";
        }

        // parse the fx attribute, (default is same as cx)
        String fx =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 ATTR_FX,
                                                 loader);
        if(fx.length() == 0){
            fx = cx;
        }

        // parse the fy attribute, (default is same as cy)
        String fy =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 ATTR_FY,
                                                 loader);
        if(fy.length() == 0){
            fy = cy;
        }

        int unitsType;
        try {
            unitsType = SVGUtilities.parseCoordinateSystem(units);
        } catch (IllegalArgumentException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("radialGradient.units.invalid",
                                       new Object[] {units,
                                                     ATTR_GRADIENT_UNITS}));
        }

        Point2D c
            = SVGUtilities.convertGradientPoint(paintedElement,
                                                SVG_CX_ATTRIBUTE, cx,
                                                SVG_CY_ATTRIBUTE, cy,
                                                unitsType, uctx);
        Point2D f
            = SVGUtilities.convertGradientPoint(paintedElement,
                                                ATTR_FX, fx,
                                                ATTR_FY, fy,
                                                unitsType, uctx);
        float radius
            = SVGUtilities.convertGradientLength(paintedElement,
                                                 SVG_R_ATTRIBUTE, r,
                                                 unitsType, uctx);

        // parse the 'spreadMethod' attribute, (default is PAD)
        String spreadMethod =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 ATTR_SPREAD_METHOD,
                                                 loader);
        if (spreadMethod.length() == 0) {
            spreadMethod = VALUE_PAD;
        }

        RadialGradientPaint.CycleMethodEnum cycleMethod =
            convertSpreadMethod(spreadMethod);

        // Extract gradient transform
        String transformStr =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 ATTR_GRADIENT_TRANSFORM,
                                                 loader);
        AffineTransform at;
        if (transformStr.length() == 0) {
            at = new AffineTransform();
        } else {
            at = SVGUtilities.convertAffineTransform(transformStr);
        }

        at = SVGUtilities.convertAffineTransform(at,
                                                 paintedNode,
                                                 rc,
                                                 unitsType);

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

        // Radius check : A value of zero will cause the area to be painted as
        // a single color using the color and opacity of the last gradient stop.
        if (radius == 0) {
            return ((GradientStop) stopVector.lastElement()).stopColor;
        }

        // Convert the stop offsets to intervals
        int nStops = stopVector.size();
        float curOffset = 0;
        if (nStops > 0) {
            GradientStop stop = (GradientStop)stopVector.elementAt(0);
            curOffset = stop.offset;
        }
        for (int i=1; i<nStops; i++) {
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

        RadialGradientPaint.ColorSpaceEnum colorSpace =
            RadialGradientPaint.SRGB;

        if(CSS_LINEARRGB_VALUE.equals(colorInterpolation.getStringValue())){
            colorSpace = RadialGradientPaint.LINEAR_RGB;
        }

        // Build Paint
        Paint paint = null;
        if (nStops > 0) {
            Color colors[] = new Color[nStops];
            float offsets[] = new float[nStops];
            for(int i=0; i<nStops; i++){
                GradientStop stop = (GradientStop)stopVector.elementAt(i);
                colors[i] = stop.stopColor;
                offsets[i] = stop.offset;
            }

            paint = new RadialGradientPaint(c, radius, f, offsets, colors,
                                            cycleMethod,
                                            RadialGradientPaint.SRGB,
                                            at);
        }
        return paint;
    }
}
