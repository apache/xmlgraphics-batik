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
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGPath extends SVGGraphicObjectConverter{
    /**
     * @param domFactory used to build Elements
     */
    public SVGPath(Document domFactory){
        super(domFactory);
    }

    /**
     * @param path the Shape that should be converted to an SVG path
     *        element.
     * @return a path Element.
     */
    public Element toSVG(Shape path){
        // Convert input path to GeneralPath if necessary
        GeneralPath shape = null;
        if(path instanceof GeneralPath)
            shape = (GeneralPath)path;
        else
            shape = new GeneralPath(path);

        // Create the path element and process its
        // d attribute.
        String dAttr = toSVGPathData(shape);
        if(dAttr==null || dAttr.trim().length() == 0){
            return null;
        }

        Element svgPath = domFactory.createElementNS(SVG_NAMESPACE_URI, TAG_PATH);
        svgPath.setAttributeNS(null, SVG_D_ATTRIBUTE, dAttr);

        // Set winding rule if different than SVG's default
        if(shape.getWindingRule() == GeneralPath.WIND_EVEN_ODD)
            svgPath.setAttributeNS(null, SVG_FILL_RULE_ATTRIBUTE, VALUE_EVEN_ODD);

        return svgPath;

    }

    /**
     * @param path the GeneralPath to convert
     * @return the value of the corresponding d attribute
     */
    static String toSVGPathData(GeneralPath path){
        StringBuffer d = new StringBuffer("");
        PathIterator pi = path.getPathIterator(null);
        float seg[] = new float[6];
        int segType = 0;
        while(!pi.isDone()){
            segType = pi.currentSegment(seg);
            switch(segType){
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

        if(d.length()>0)
            return d.substring(0, d.length() - 1);
        else
            return "";

    }



    /**
     * Appends a coordinate to the path data
     */
    private static void appendPoint(StringBuffer d, float x, float y){
        d.append(doubleString(x));
        d.append(SPACE);
        d.append(doubleString(y));
        d.append(SPACE);
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{
        GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        Shape shapes[] = { generalPath,
                           new Rectangle2D.Float(20, 30, 40, 50),
                           new Ellipse2D.Float(25, 35, 80, 60),
                           new Line2D.Float(30, 40, 50, 60),
                           new QuadCurve2D.Float(20, 30, 40, 50, 60, 70),
                           new CubicCurve2D.Float(15, 25, 35, 45, 55, 65, 75, 85)
        };

        Document domFactory = TestUtil.getDocumentPrototype();
        SVGPath converter = new SVGPath(domFactory);

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        for(int i=0; i<shapes.length; i++){
            Shape shape = shapes[i];
            Element path = converter.toSVG(shape);
            group.appendChild(path);
        }

        TestUtil.trace(group, System.out);
    }
}
