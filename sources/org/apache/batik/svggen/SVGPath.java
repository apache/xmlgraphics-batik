/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.*;
import java.awt.Shape;

import org.w3c.dom.Document;
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
        String dAttr = toSVGPathData(path);
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
    static String toSVGPathData(Shape path) {
        StringBuffer d = new StringBuffer("");
        PathIterator pi = path.getPathIterator(null);
        float seg[] = new float[6];
        int segType = 0;
        while (!pi.isDone()) {
            segType = pi.currentSegment(seg);
            switch(segType) {
            case PathIterator.SEG_MOVETO:
                d.append(PATH_MOVE);
                appendPoint(d, seg[0], seg[1]);
                break;
            case PathIterator.SEG_LINETO:
                d.append(PATH_LINE_TO);
                appendPoint(d, seg[0], seg[1]);
                break;
            case PathIterator.SEG_CLOSE:
                d.append(PATH_CLOSE);
                break;
            case PathIterator.SEG_QUADTO:
                d.append(PATH_QUAD_TO);
                appendPoint(d, seg[0], seg[1]);
                appendPoint(d, seg[2], seg[3]);
                break;
            case PathIterator.SEG_CUBICTO:
                d.append(PATH_CUBIC_TO);
                appendPoint(d, seg[0], seg[1]);
                appendPoint(d, seg[2], seg[3]);
                appendPoint(d, seg[4], seg[5]);
                break;
            default:
                throw new Error();
            }
            pi.next();
        } // while !isDone

        if (d.length() > 0)
            return d.toString().trim();
        else
            return "";
    }

    /**
     * Appends a coordinate to the path data
     */
    private static void appendPoint(StringBuffer d, float x, float y) {
        d.append(doubleString(x));
        d.append(SPACE);
        d.append(doubleString(y));
        d.append(SPACE);
    }
}
