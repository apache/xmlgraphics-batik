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

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import org.w3c.dom.Element;

/**
 * Utility class that converts a Shape object into an SVG
 * path element. Note that this class does not attempt to
 * find out what type of object (e.g., whether the input
 * Shape is a Rectangle or an Ellipse. This type of analysis
 * is done by the SVGShape class).
 * Note that this class assumes that the parent of the
 * path element it generates defines the fill-rule as
 * nonzero. This is not the SVG default value. However,
 * because it is the GeneralPath's default, it is preferable
 * to have this attribute specified once to set the default
 * (in the parent element, e.g., a group) and then only in
 * the rare instance where the winding rule is different
 * than the default. Otherwise, the attribute would have
 * to be specified in the majority of path elements.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGPath extends SVGGraphicObjectConverter {
    /**
     * @param generatorContext used to build Elements
     */
    public SVGPath(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    /**
     * @param path the Shape that should be converted to an SVG path
     *        element.
     * @return a path Element.
     */
    public Element toSVG(Shape path) {
        // Create the path element and process its
        // d attribute.
        String dAttr = toSVGPathData(path, generatorContext);
        if (dAttr==null || dAttr.length() == 0){
            // be careful not to append null to the DOM tree
            // because it will crash
            return null;
        }

        Element svgPath =
            generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                        SVG_PATH_TAG);
        svgPath.setAttributeNS(null, SVG_D_ATTRIBUTE, dAttr);

        // Set winding rule if different than SVG's default
        if (path.getPathIterator(null).getWindingRule() == GeneralPath.WIND_EVEN_ODD)
            svgPath.setAttributeNS(null, SVG_FILL_RULE_ATTRIBUTE, SVG_EVEN_ODD_VALUE);

        return svgPath;
    }

    /**
     * @param path the GeneralPath to convert
     * @return the value of the corresponding d attribute
     */
     public static String toSVGPathData(Shape path, SVGGeneratorContext gc) {
        StringBuffer d = new StringBuffer("");
        PathIterator pi = path.getPathIterator(null);
        float seg[] = new float[6];
        int segType = 0;
        while (!pi.isDone()) {
            segType = pi.currentSegment(seg);
            switch(segType) {
            case PathIterator.SEG_MOVETO:
                d.append(PATH_MOVE);
                appendPoint(d, seg[0], seg[1], gc);
                break;
            case PathIterator.SEG_LINETO:
                d.append(PATH_LINE_TO);
                appendPoint(d, seg[0], seg[1], gc);
                break;
            case PathIterator.SEG_CLOSE:
                d.append(PATH_CLOSE);
                break;
            case PathIterator.SEG_QUADTO:
                d.append(PATH_QUAD_TO);
                appendPoint(d, seg[0], seg[1], gc);
                appendPoint(d, seg[2], seg[3], gc);
                break;
            case PathIterator.SEG_CUBICTO:
                d.append(PATH_CUBIC_TO);
                appendPoint(d, seg[0], seg[1], gc);
                appendPoint(d, seg[2], seg[3], gc);
                appendPoint(d, seg[4], seg[5], gc);
                break;
            default:
                throw new Error();
            }
            pi.next();
        } // while !isDone

        if (d.length() > 0)
            return d.toString().trim();
        else {
            // This is a degenerate case: there was no initial moveTo
            // in the path and no data at all. However, this happens 
            // in the Java 2D API (e.g., when clipping to a rectangle
            // with negative height/width, the clip will be a GeneralPath
            // with no data, which causes everything to be clipped)
            // It is the responsibility of the users of SVGPath to detect
            // instances where the converted element (see #toSVG above)
            // returns null, which only happens for degenerate cases.
            return "";
        }
    }

    /**
     * Appends a coordinate to the path data
     */
    private static void appendPoint(StringBuffer d, float x, float y, SVGGeneratorContext gc) {
        d.append(gc.doubleString(x));
        d.append(SPACE);
        d.append(gc.doubleString(y));
        d.append(SPACE);
    }
}
