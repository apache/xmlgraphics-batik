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

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import org.w3c.dom.Element;

/**
 * Utility class that converts an Ellipse2D object into
 * a corresponding SVG element, i.e., a circle or an ellipse.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGEllipse extends SVGGraphicObjectConverter {
    /**
     * Line converter used for degenerate cases
     */
    private SVGLine svgLine;

    /**
     * @param generatorContext used to build Elements
     */
    public SVGEllipse(SVGGeneratorContext generatorContext) {
        super(generatorContext);

        svgLine = new SVGLine(generatorContext);
    }

    /**
     * @param ellipse the Ellipse2D object to be converted
     */
    public Element toSVG(Ellipse2D ellipse) {
        if(ellipse.getWidth() < 0 || ellipse.getHeight() < 0){
            return null;
        }

        if(ellipse.getWidth() == ellipse.getHeight())
            return toSVGCircle(ellipse);
        else
            return toSVGEllipse(ellipse);
    }

    /**
     * @param ellipse the Ellipse2D object to be converted to a circle
     */
    private Element toSVGCircle(Ellipse2D ellipse){
        Element svgCircle =
            generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                        SVG_CIRCLE_TAG);
        svgCircle.setAttributeNS(null, SVG_CX_ATTRIBUTE,
                                 doubleString(ellipse.getX() +
                                              ellipse.getWidth()/2));
        svgCircle.setAttributeNS(null, SVG_CY_ATTRIBUTE,
                                 doubleString(ellipse.getY() +
                                              ellipse.getHeight()/2));
        svgCircle.setAttributeNS(null, SVG_R_ATTRIBUTE,
                                 doubleString(ellipse.getWidth()/2));
        return svgCircle;
    }

    /**
     * @param ellipse the Ellipse2D object to be converted to an ellipse
     */
    private Element toSVGEllipse(Ellipse2D ellipse){
        if(ellipse.getWidth() > 0 && ellipse.getHeight() > 0){
            Element svgCircle =
                generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                            SVG_ELLIPSE_TAG);
            svgCircle.setAttributeNS(null, SVG_CX_ATTRIBUTE,
                                     doubleString(ellipse.getX() +
                                                  ellipse.getWidth()/2));
            svgCircle.setAttributeNS(null, SVG_CY_ATTRIBUTE,
                                     doubleString(ellipse.getY() +
                                                  ellipse.getHeight()/2));
            svgCircle.setAttributeNS(null, SVG_RX_ATTRIBUTE,
                                     doubleString(ellipse.getWidth()/2));
            svgCircle.setAttributeNS(null, SVG_RY_ATTRIBUTE,
                                     doubleString(ellipse.getHeight()/2));
            return svgCircle;
        }
        else if(ellipse.getWidth() == 0 && ellipse.getHeight() > 0){
            // Degenerate to a line
            Line2D line = new Line2D.Double(ellipse.getX(), ellipse.getY(), ellipse.getX(), 
                                            ellipse.getY() + ellipse.getHeight());
            return svgLine.toSVG(line);
        }
        else if(ellipse.getWidth() > 0 && ellipse.getHeight() == 0){
            // Degenerate to a line
            Line2D line = new Line2D.Double(ellipse.getX(), ellipse.getY(),
                                            ellipse.getX() + ellipse.getWidth(),
                                            ellipse.getY());
            return svgLine.toSVG(line);
        }
        return null;
    }
}
