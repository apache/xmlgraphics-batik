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

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import org.w3c.dom.Element;

/**
 * Utility class that converts a Rectangle2D or RoundRectangle2D
 * object into an SVG element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGRectangle extends SVGGraphicObjectConverter {
    /**
     * Line converter used for degenerate cases
     */
    private SVGLine svgLine;

    /**
     * @param generatorContext used to build Elements
     */
    public SVGRectangle(SVGGeneratorContext generatorContext) {
        super(generatorContext);
        svgLine = new SVGLine(generatorContext);
    }

    /**
     * @param rect rectangle object to convert to SVG
     */
    public Element toSVG(Rectangle2D rect) {
        return toSVG((RectangularShape)rect);
    }


    /**
     * In the Java 2D API, arc width/height are used
     * as absolute values.
     *
     * @param rect rectangle object to convert to SVG
     */
    public Element toSVG(RoundRectangle2D rect) {
        Element svgRect = toSVG((RectangularShape)rect);
        if(svgRect != null && svgRect.getTagName() == SVG_RECT_TAG){
            svgRect.setAttributeNS(null, SVG_RX_ATTRIBUTE,
                                   doubleString(Math.abs(rect.getArcWidth()/2)));
            svgRect.setAttributeNS(null, SVG_RY_ATTRIBUTE,
                                   doubleString(Math.abs(rect.getArcHeight()/2)));
        }

        return svgRect;
    }


    /**
     * @param rect rectangle object to convert to SVG
     */
    private Element toSVG(RectangularShape rect) {
        if(rect.getWidth() > 0 && rect.getHeight() > 0){
            Element svgRect =
                generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                            SVG_RECT_TAG);
            svgRect.setAttributeNS(null, SVG_X_ATTRIBUTE, doubleString(rect.getX()));
            svgRect.setAttributeNS(null, SVG_Y_ATTRIBUTE, doubleString(rect.getY()));
            svgRect.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE,
                                   doubleString(rect.getWidth()));
            svgRect.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE,
                                   doubleString(rect.getHeight()));
            
            return svgRect;
        }
        else{
            // Handle degenerate cases
            if(rect.getWidth() == 0 && rect.getHeight() > 0){
                // Degenerate to a line
                Line2D line = new Line2D.Double(rect.getX(), rect.getY(), rect.getX(), 
                                                rect.getY() + rect.getHeight());
                return svgLine.toSVG(line);
            }
            else if(rect.getWidth() > 0 && rect.getHeight() == 0){
                // Degenerate to a line
                Line2D line = new Line2D.Double(rect.getX(), rect.getY(),
                                                rect.getX() + rect.getWidth(),
                                                rect.getY());
                return svgLine.toSVG(line);
            }
            return null;
        }
    }
}
