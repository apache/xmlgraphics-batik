/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.apache.batik.ext.awt.LinearGradientPaint;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;linearGradient> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGLinearGradientElementBridge
    extends AbstractSVGGradientElementBridge {

    /**
     * Constructs a new SVGLinearGradientElementBridge.
     */
    public SVGLinearGradientElementBridge() {}

    /**
     * Returns 'linearGradient'.
     */
    public String getLocalName() {
        return SVG_LINEAR_GRADIENT_TAG;
    }

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
            x1Str = SVG_LINEAR_GRADIENT_X1_DEFAULT_VALUE;
        }

        // 'y1' attribute - default is 0%
        String y1Str = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_Y1_ATTRIBUTE, ctx);
        if (y1Str.length() == 0) {
            y1Str = SVG_LINEAR_GRADIENT_Y1_DEFAULT_VALUE;
        }

        // 'x2' attribute - default is 100%
        String x2Str = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_X2_ATTRIBUTE, ctx);
        if (x2Str.length() == 0) {
            x2Str = SVG_LINEAR_GRADIENT_X2_DEFAULT_VALUE;
        }

        // 'y2' attribute - default is 0%
        String y2Str = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_Y2_ATTRIBUTE, ctx);
        if (y2Str.length() == 0) {
            y2Str = SVG_LINEAR_GRADIENT_Y2_DEFAULT_VALUE;
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
            transform = SVGUtilities.toObjectBBox(transform, paintedNode);
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

	// If x1 = x2 and y1 = y2, then the area to be painted will be painted
	// as a single color using the color and opacity of the last gradient
	// stop.
        if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
            return colors[colors.length-1];
	} else {
	    return new LinearGradientPaint(p1,
					   p2,
					   offsets,
					   colors,
					   spreadMethod,
					   colorSpace,
					   transform);
	}
    }
}
