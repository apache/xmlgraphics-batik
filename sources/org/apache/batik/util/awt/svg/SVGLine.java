/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.geom.*;
import java.awt.Shape;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a Line2D object into
 * a corresponding SVG line element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGLine extends SVGGraphicObjectConverter{
    /**
     * @param domFactory used to build Elements
     */
    public SVGLine(Document domFactory){
        super(domFactory);
    }

    /**
     * @param line the Line2D object to be converted
     */
    public Element toSVG(Line2D line){
        Element svgLine = domFactory.createElement(TAG_LINE);
        svgLine.setAttribute(ATTR_X1, doubleString(line.getX1()));
        svgLine.setAttribute(ATTR_Y1, doubleString(line.getY1()));
        svgLine.setAttribute(ATTR_X2, doubleString(line.getX2()));
        svgLine.setAttribute(ATTR_Y2, doubleString(line.getY2()));
        return svgLine;
    }

    /**
     * Unit testing
     */
    public static final void main(String args[]) throws Exception{
        Line2D lines [] = { new Line2D.Double(1, 2, 3, 4),
                            new Line2D.Double(10, 20, 30, 40) };

        Document domFactory = TestUtil.getDocumentPrototype();
        SVGLine converter = new SVGLine(domFactory);
        Element group = domFactory.createElement(SVG_G_TAG);
        for(int i=0; i<lines.length; i++)
            group.appendChild(converter.toSVG(lines[i]));

        TestUtil.trace(group, System.out);
    }
}
