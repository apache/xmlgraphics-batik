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
