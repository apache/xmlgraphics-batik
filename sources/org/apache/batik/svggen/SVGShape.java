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

import java.util.Map;
import java.util.Hashtable;

import org.w3c.dom.Document;
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
public class SVGShape extends SVGGraphicObjectConverter{
    /*
     * Subconverts, for each type of Shape class
     */
    SVGPolygon svgPolygon;
    SVGRectangle svgRectangle;
    SVGEllipse svgEllipse;
    SVGLine svgLine;
    SVGPath svgPath;

    /**
     * @param domFactory used to build Elements
     */
    public SVGShape(Document domFactory){
        super(domFactory);
        svgPolygon = new SVGPolygon(domFactory);
        svgRectangle = new SVGRectangle(domFactory);
        svgEllipse = new SVGEllipse(domFactory);
        svgLine = new SVGLine(domFactory);
        svgPath = new SVGPath(domFactory);
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

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        Polygon polygon = new Polygon();
        polygon.addPoint(1, 1);
        polygon.addPoint(2, 1);
        polygon.addPoint(3, 2);
        polygon.addPoint(3, 3);
        polygon.addPoint(2, 4);
        polygon.addPoint(1, 3);
        polygon.addPoint(1, 2);

        GeneralPath square = new GeneralPath();
        square.moveTo(0, 0);
        square.lineTo(1, 0);
        square.lineTo(1, 1);
        square.lineTo(0, 1);
        square.closePath();

        Ellipse2D hole = new Ellipse2D.Double(0, 0, 1, 1);
        Area area = new Area(square);
        area.subtract(new Area(hole));

        Shape shapes[] = {
            // polygon
            polygon,

            // rect
            new Rectangle(10, 20, 30, 40),
            new Rectangle2D.Double(100., 200., 300., 400.),
            new Rectangle2D.Float(1000f, 2000f, 3000f, 4000f),
            new RoundRectangle2D.Double(15., 16., 17., 18., 30., 20.),
            new RoundRectangle2D.Float(35f, 45f, 55f, 65f, 25f, 45f),

            // Circle
            new Ellipse2D.Float(0, 0, 100, 100),
            new Ellipse2D.Double(40, 40, 240, 240),

            // Ellipse
            new Ellipse2D.Float(0, 0, 100, 200),
            new Ellipse2D.Float(40, 100, 240, 200),

            // line
            new Line2D.Double(1, 2, 3, 4),
            new Line2D.Double(10, 20, 30, 40),

            // path
            new QuadCurve2D.Float(20, 30, 40, 50, 60, 70),
            new CubicCurve2D.Float(15, 25, 35, 45, 55, 65, 75, 85),
            new Arc2D.Double(0, 0, 100, 100, 0, 90, Arc2D.OPEN),
            square,
            area
        };

        Document domFactory = TestUtil.getDocumentPrototype();
        Element group = domFactory.createElement(SVG_G_TAG);
        SVGShape converter = new SVGShape(domFactory);

        for(int i=0; i<shapes.length; i++)
            group.appendChild(converter.toSVG(shapes[i]));

        TestUtil.trace(group, System.out);
    }
}
