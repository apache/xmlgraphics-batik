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
import java.awt.geom.Point2D;
import java.util.List;

import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;linearGradient> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGLinearGradientElementBridge
    extends SVGAbstractGradientElementBridge {

    /**
     * Constructs a new SVGLinearGradientElementBridge.
     */
    public SVGLinearGradientElementBridge() {}

    /**
     * Builds a linear gradient according to the specified parameters.
     *
     * @param paintElement the element that defines a Paint
     * @param paintedElement the element referencing the paint
     * @param paintedNode the graphics node on which the Paint will be applied
     * @param spreadMethod the spread method
     * @param colorSpace the color space (sRGB | LinearRGB)
     * @param transform the gradient transform
     * @param colors the colors of the gradient
     * @param offsets the offsets
     * @param ctx the bridge context to use
     */
    protected
        Paint buildGradient(Element paintElement,
                            Element paintedElement,
                            GraphicsNode paintedNode,
                            MultipleGradientPaint.CycleMethodEnum spreadMethod,
                            MultipleGradientPaint.ColorSpaceEnum colorSpace,
                            AffineTransform transform,
                            Color [] colors,
                            float [] offsets,
                            BridgeContext ctx) {

        // 'x1' attribute - default is 0%
        String x1Str = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_X1_ATTRIBUTE, ctx);
        if (x1Str.length() == 0) {
            x1Str = "0%";
        }

        // 'y1' attribute - default is 0%
        String y1Str = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_Y1_ATTRIBUTE, ctx);
        if (y1Str.length() == 0) {
            y1Str = "0%";
        }

        // 'x2' attribute - default is 100%
        String x2Str = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_X2_ATTRIBUTE, ctx);
        if (x2Str.length() == 0) {
            x2Str = "100%";
        }

        // 'y2' attribute - default is 0%
        String y2Str = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_Y2_ATTRIBUTE, ctx);
        if (y2Str.length() == 0) {
            y2Str = "0%";
        }

        // 'gradientUnits' attribute - default is objectBoundingBox
        short coordSystemType;
        String s = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_GRADIENT_UNITS_ATTRIBUTE, ctx);
        if (s.length() == 0) {
            coordSystemType = SVGUtilities.OBJECT_BOUNDING_BOX;
        } else {
            coordSystemType = SVGUtilities.parseCoordinateSystem
                (paintElement, SVG_GRADIENT_UNITS_ATTRIBUTE, s);
        }

        // additional transform to move to objectBoundingBox coordinate system
        if (coordSystemType == SVGUtilities.OBJECT_BOUNDING_BOX) {
            GraphicsNodeRenderContext rc = ctx.getGraphicsNodeRenderContext();
            transform = SVGUtilities.toObjectBBox(transform,
                                                  paintedNode,
                                                  rc);
        }
        UnitProcessor.Context uctx
            = UnitProcessor.createContext(ctx, paintElement);

        Point2D p1 = SVGUtilities.convertPoint(x1Str,
                                               SVG_X1_ATTRIBUTE,
                                               y1Str,
                                               SVG_Y1_ATTRIBUTE,
                                               coordSystemType,
                                               uctx);

        Point2D p2 = SVGUtilities.convertPoint(x2Str,
                                               SVG_X2_ATTRIBUTE,
                                               y2Str,
                                               SVG_Y2_ATTRIBUTE,
                                               coordSystemType,
                                               uctx);

        return new LinearGradientPaint(p1,
                                       p2,
                                       offsets,
                                       colors,
                                       spreadMethod,
                                       colorSpace,
                                       transform);
    }
}
