/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.Line2D;

import org.w3c.dom.Element;

/**
 * Utility class that converts a Line2D object into
 * a corresponding SVG line element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGLine extends SVGGraphicObjectConverter {
    /**
     * @param generatorContext used to build Elements
     */
    public SVGLine(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    /**
     * @param line the Line2D object to be converted
     */
    public Element toSVG(Line2D line) {
        Element svgLine =
            generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                        SVG_LINE_TAG);
        svgLine.setAttributeNS
            (null, SVG_X1_ATTRIBUTE, doubleString(line.getX1()));
        svgLine.setAttributeNS
            (null, SVG_Y1_ATTRIBUTE, doubleString(line.getY1()));
        svgLine.setAttributeNS
            (null, SVG_X2_ATTRIBUTE, doubleString(line.getX2()));
        svgLine.setAttributeNS
            (null, SVG_Y2_ATTRIBUTE, doubleString(line.getY2()));
        return svgLine;
    }
}
