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

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.w3c.dom.Element;

/**
 * Utility class that converts a Shape object into the corresponding
 * SVG element. Note that this class analyzes the input Shape class
 * to generate the most appropriate corresponding SVG element:
 * + Polygon is mapped to polygon
 * + Rectangle2D and RoundRectangle2D are mapped to rect
 * + Ellipse2D is mapped to circle or ellipse
 * + Line2D is mapped to line
 * + Arc2D, CubicCurve2D, Area, GeneralPath and QuadCurve2D are mapped to
 *   path.
 * + Any custom Shape implementation is mapped to path as well.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGShape extends SVGGraphicObjectConverter {
    /*
     * Subconverts, for each type of Shape class
     */
    private SVGPolygon svgPolygon;
    private SVGRectangle svgRectangle;
    private SVGEllipse svgEllipse;
    private SVGLine svgLine;
    private SVGPath svgPath;

    /**
     * @param generatorContext used to build Elements
     */
    public SVGShape(SVGGeneratorContext generatorContext) {
        super(generatorContext);
        svgPolygon = new SVGPolygon(generatorContext);
        svgRectangle = new SVGRectangle(generatorContext);
        svgEllipse = new SVGEllipse(generatorContext);
        svgLine = new SVGLine(generatorContext);
        svgPath = new SVGPath(generatorContext);
    }

    /**
     * @param shape Shape object to be converted
     */
    public Element toSVG(Shape shape){
        if(shape instanceof Polygon)
            return svgPolygon.toSVG((Polygon)shape);
        else if(shape instanceof Rectangle2D)
            return svgRectangle.toSVG((Rectangle2D)shape);
        else if(shape instanceof RoundRectangle2D)
            return svgRectangle.toSVG((RoundRectangle2D)shape);
        else if(shape instanceof Ellipse2D)
            return svgEllipse.toSVG((Ellipse2D)shape);
        else if(shape instanceof Line2D)
            return svgLine.toSVG((Line2D)shape);
        else
            return svgPath.toSVG(shape);
    }
}
