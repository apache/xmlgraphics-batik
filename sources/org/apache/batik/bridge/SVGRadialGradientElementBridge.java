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

import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.ext.awt.RadialGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;radialGradient> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGRadialGradientElementBridge
    extends AbstractSVGGradientElementBridge {


    /**
     * Constructs a new SVGRadialGradientElementBridge.
     */
    public SVGRadialGradientElementBridge() {}

    /**
     * Returns 'radialGradient'.
     */
    public String getLocalName() {
        return SVG_RADIAL_GRADIENT_TAG;
    }

    /**
     * Builds a radial gradient according to the specified parameters.
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

        // 'cx' attribute - default is 50%
        String cxStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_CX_ATTRIBUTE, ctx);
        if (cxStr.length() == 0) {
            cxStr = SVG_RADIAL_GRADIENT_CX_DEFAULT_VALUE;
        }

        // 'cy' attribute - default is 50%
        String cyStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_CY_ATTRIBUTE, ctx);
        if (cyStr.length() == 0) {
            cyStr = SVG_RADIAL_GRADIENT_CY_DEFAULT_VALUE;
        }

        // 'r' attribute - default is 50%
        String rStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_R_ATTRIBUTE, ctx);
        if (rStr.length() == 0) {
            rStr = SVG_RADIAL_GRADIENT_R_DEFAULT_VALUE;
        }

        // 'fx' attribute - default is same as cx
        String fxStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_FX_ATTRIBUTE, ctx);
        if (fxStr.length() == 0) {
            fxStr = cxStr;
        }

        // 'fy' attribute - default is same as cy
        String fyStr = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_FY_ATTRIBUTE, ctx);
        if (fyStr.length() == 0) {
            fyStr = cyStr;
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
            transform = SVGUtilities.toObjectBBox(transform,
                                                  paintedNode);
        }
        UnitProcessor.Context uctx
            = UnitProcessor.createContext(ctx, paintElement);

        float r = SVGUtilities.convertLength(rStr,
                                             SVG_R_ATTRIBUTE,
                                             coordSystemType,
                                             uctx);
	// A value of zero will cause the area to be painted as a single color
	// using the color and opacity of the last gradient stop.
        if (r == 0) {
            return colors[colors.length-1];
        } else {
            Point2D c = SVGUtilities.convertPoint(cxStr,
                                                  SVG_CX_ATTRIBUTE,
                                                  cyStr,
                                                  SVG_CY_ATTRIBUTE,
                                                  coordSystemType,
                                                  uctx);

            Point2D f = SVGUtilities.convertPoint(fxStr,
                                                  SVG_FX_ATTRIBUTE,
                                                  fyStr,
                                                  SVG_FY_ATTRIBUTE,
                                                  coordSystemType,
                                                  uctx);

            // <!> FIXME: colorSpace ignored for radial gradient at this time
            return new RadialGradientPaint(c,
                                           r,
                                           f,
                                           offsets,
                                           colors,
                                           spreadMethod,
                                           RadialGradientPaint.SRGB,
                                           transform);
        }
    }
}
