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

package org.apache.batik.svggen;

import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a Java GradientPaint into an
 * SVG linear gradient element
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGLinearGradient extends AbstractSVGConverter {
    /**
     * @param generatorContext used to build Elements
     */
    public SVGLinearGradient(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc) {
        Paint paint = gc.getPaint();
        return toSVG((GradientPaint)paint);
    }

    /**
     * @param gradient the GradientPaint to be converted
     * @return a description of the SVG paint and opacity corresponding
     *         to the gradient Paint. The definiton of the
     *         linearGradient is put in the linearGradientDefsMap
     */
    public SVGPaintDescriptor toSVG(GradientPaint gradient) {
        // Reuse definition if gradient has already been converted
        SVGPaintDescriptor gradientDesc =
            (SVGPaintDescriptor)descMap.get(gradient);

        Document domFactory = generatorContext.domFactory;

        if (gradientDesc == null) {
            Element gradientDef =
                domFactory.createElementNS(SVG_NAMESPACE_URI,
                                           SVG_LINEAR_GRADIENT_TAG);
            gradientDef.setAttributeNS(null, SVG_GRADIENT_UNITS_ATTRIBUTE,
                                       SVG_USER_SPACE_ON_USE_VALUE);

            //
            // Process gradient vector
            //
            Point2D p1 = gradient.getPoint1();
            Point2D p2 = gradient.getPoint2();
            gradientDef.setAttributeNS(null, SVG_X1_ATTRIBUTE,
                                       "" + doubleString(p1.getX()));
            gradientDef.setAttributeNS(null, SVG_Y1_ATTRIBUTE,
                                       "" + doubleString(p1.getY()));
            gradientDef.setAttributeNS(null, SVG_X2_ATTRIBUTE,
                                       "" + doubleString(p2.getX()));
            gradientDef.setAttributeNS(null, SVG_Y2_ATTRIBUTE,
                                       "" + doubleString(p2.getY()));

            //
            // Spread method
            //
            String spreadMethod = SVG_PAD_VALUE;
            if(gradient.isCyclic())
                spreadMethod = SVG_REFLECT_VALUE;
            gradientDef.setAttributeNS
                (null, SVG_SPREAD_METHOD_ATTRIBUTE, spreadMethod);

            //
            // First gradient stop
            //
            Element gradientStop =
                domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_STOP_TAG);
            gradientStop.setAttributeNS(null, SVG_OFFSET_ATTRIBUTE,
                                      SVG_ZERO_PERCENT_VALUE);

            SVGPaintDescriptor colorDesc = SVGColor.toSVG(gradient.getColor1(), generatorContext);
            gradientStop.setAttributeNS(null, SVG_STOP_COLOR_ATTRIBUTE,
                                      colorDesc.getPaintValue());
            gradientStop.setAttributeNS(null, SVG_STOP_OPACITY_ATTRIBUTE,
                                      colorDesc.getOpacityValue());

            gradientDef.appendChild(gradientStop);

            //
            // Second gradient stop
            //
            gradientStop =
                domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_STOP_TAG);
            gradientStop.setAttributeNS(null, SVG_OFFSET_ATTRIBUTE,
                                      SVG_HUNDRED_PERCENT_VALUE);

            colorDesc = SVGColor.toSVG(gradient.getColor2(), generatorContext);
            gradientStop.setAttributeNS(null, SVG_STOP_COLOR_ATTRIBUTE,
                                        colorDesc.getPaintValue());
            gradientStop.setAttributeNS(null, SVG_STOP_OPACITY_ATTRIBUTE,
                                        colorDesc.getOpacityValue());

            gradientDef.appendChild(gradientStop);

            //
            // Gradient ID
            //
            gradientDef.
                setAttributeNS(null, ATTR_ID,
                               generatorContext.idGenerator.
                               generateID(ID_PREFIX_LINEAR_GRADIENT));

            //
            // Build Paint descriptor
            //
            StringBuffer paintAttrBuf = new StringBuffer(URL_PREFIX);
            paintAttrBuf.append(SIGN_POUND);
            paintAttrBuf.append(gradientDef.getAttributeNS(null, ATTR_ID));
            paintAttrBuf.append(URL_SUFFIX);

            gradientDesc = new SVGPaintDescriptor(paintAttrBuf.toString(),
                                                  SVG_OPAQUE_VALUE,
                                                  gradientDef);

            //
            // Update maps so that gradient can be reused if needed
            //
            descMap.put(gradient, gradientDesc);
            defSet.add(gradientDef);
        }

        return gradientDesc;
    }
}
