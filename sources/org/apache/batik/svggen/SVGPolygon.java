/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
