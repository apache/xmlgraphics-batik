/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.*;
import java.awt.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a Polygon object into
 * an SVG element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGPolygon extends SVGGraphicObjectConverter{
    /**
     * @param domFactory used to build Elements
     */
    public SVGPolygon(Document domFactory){
        super(domFactory);
    }

    /**
     * @param polygon polygon object to convert to SVG
     */
    public Element toSVG(Polygon polygon){

        Element svgPolygon = domFactory.createElement(TAG_POLYGON);
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

        svgPolygon.setAttribute(ATTR_POINTS, points.substring(0, points.length() - 1));

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

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        Document domFactory = TestUtil.getDocumentPrototype();

        Polygon polygon = new Polygon();
        polygon.addPoint(350, 75);
        polygon.addPoint(379, 161);
        polygon.addPoint(469, 161);
        polygon.addPoint(397, 215);
        polygon.addPoint(423, 301);
        polygon.addPoint(350, 250);
        polygon.addPoint(277, 301);
        polygon.addPoint(303, 215);
        polygon.addPoint(231, 161);
        polygon.addPoint(321, 161);

        SVGPolygon converter = new SVGPolygon(domFactory);
        Element svgPolygon = converter.toSVG(polygon);
        TestUtil.trace(svgPolygon, System.out);
        System.out.println();
    }
}
