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
import java.awt.geom.PathIterator;

import org.w3c.dom.Element;

/**
 * Utility class that converts a Polygon object into
 * an SVG element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGPolygon extends SVGGraphicObjectConverter {
    /**
     * @param generatorContext used to build Elements
     */
    public SVGPolygon(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    /**
     * @param polygon polygon object to convert to SVG
     */
    public Element toSVG(Polygon polygon) {
        Element svgPolygon =
            generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                        SVG_POLYGON_TAG);
        StringBuffer points = new StringBuffer(" ");
        PathIterator pi = polygon.getPathIterator(null);
        float seg[] = new float[6];
        int segType = 0;
        while(!pi.isDone()){
            segType = pi.currentSegment(seg);
            switch(segType){
            case PathIterator.SEG_MOVETO:
                appendPoint(points, seg[0], seg[1]);
                break;
            case PathIterator.SEG_LINETO:
                appendPoint(points, seg[0], seg[1]);
                break;
            case PathIterator.SEG_CLOSE:
                break;
            case PathIterator.SEG_QUADTO:
            case PathIterator.SEG_CUBICTO:
            default:
                throw new Error();
            }
            pi.next();
        } // while !isDone

        svgPolygon.setAttributeNS(null,
                                  SVG_POINTS_ATTRIBUTE,
                                  points.substring(0, points.length() - 1));

        return svgPolygon;
    }

    /**
     *  Appends a coordinate to the path data
     */
    private void appendPoint(StringBuffer points, float x, float y){
        points.append(doubleString(x));
        points.append(SPACE);
        points.append(doubleString(y));
        points.append(SPACE);
    }
}
