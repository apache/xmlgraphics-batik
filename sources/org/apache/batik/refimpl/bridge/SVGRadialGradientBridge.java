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
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.StringReader;
import java.util.Vector;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.apache.batik.util.awt.RadialGradientPaint;
import org.apache.batik.util.awt.geom.AffineTransformSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
        //
        // Get unit processor to compute gradient control points
        //
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(paintElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        //
        // Get gradient units
        //
        String units = paintElement.getAttributeNS(null, ATTR_GRADIENT_UNITS);

        //
        // Extract cx, cy, fx, fy and r
        //
        String cx = paintElement.getAttributeNS(null, ATTR_CX);
        if(cx.length() == 0){
            cx = "50%";
        }

        String cy = paintElement.getAttributeNS(null, ATTR_CY);
        if(cy.length() == 0){
            cy = "50%";
        }

        String fx = paintElement.getAttributeNS(null, ATTR_FX);
        if(fx.length() == 0){
            fx = cx;
        }

        String fy = paintElement.getAttributeNS(null, ATTR_FY);
        if(fy.length() == 0){
            fy = cy;
        }

        String r = paintElement.getAttributeNS(null, ATTR_R);
        if(r.length() == 0){
            r = "50%";
        }

        System.out.println("cx : " + cx + " fx : " + fx +
                           " cy : " + cy + " fy : " + fy + " r: " + r);

        SVGElement svgPaintedElement = (SVGElement) paintedElement;
        Point2D c
            = SVGUtilities.convertPoint(svgPaintedElement, cx, cy,
                                        units, paintedNode, uctx);
        Point2D f
            = SVGUtilities.convertPoint(svgPaintedElement, fx, fy,
                                        units, paintedNode, uctx);
        float radius
            = SVGUtilities.convertLength((SVGElement)paintedElement, r, 
                                         units, paintedNode, uctx);

        //
        // Extract the spread method
        //
        String spreadMethod = paintElement.getAttributeNS(null,
                                                          ATTR_SPREAD_METHOD);
        RadialGradientPaint.CycleMethodEnum cycleMethod =
            convertSpreadMethod(spreadMethod);

        //
        // Extract gradient transform
        //
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(paintElement.getAttributeNS(null, ATTR_GRADIENT_TRANSFORM)), 
             ctx.getParserFactory());

        AffineTransformSource ats 
            = SVGUtilities.convertAffineTransformSource(at, 
                                                        paintedNode, 
                                                        units);

        //
        // Extract stop colors and intervals
        //
        Vector stopVector = extractGradientStops(paintElement, ctx);
        // if no stop, fill is 'none'
        if (stopVector.size() == 0) {
            return null;
        }
        // if one stop, the fill is just one color
        if (stopVector.size() == 1) {
            return ((GradientStop) stopVector.get(0)).stopColor;
        }
        //
        // Convert the stop offsets to intervals
        //
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

        //
        // Build Paint
        //
        Paint paint = null;
        if (nStops > 0) {
            Color colors[] = new Color[nStops];
            float offsets[] = new float[nStops];
            for(int i=0; i<nStops; i++){
                GradientStop stop = (GradientStop)stopVector.elementAt(i);
                colors[i] = stop.stopColor;
                offsets[i] = stop.offset;
                System.out.println("offset[" + i + "] = " + offsets[i]);
                System.out.println("colors[" + i + "] = " +
                                   Integer.toHexString(colors[i].getRGB()));
            }

            paint = new RadialGradientPaint(c, radius, f, offsets, colors,
                                            cycleMethod,
                                            RadialGradientPaint.SRGB,
                                            ats);
        }
        return paint;
    }
}
