/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import org.w3c.dom.Element;

/**
 * Utility class that converts an Ellipse2D object into
 * a corresponding SVG element, i.e., a circle or an ellipse.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGEllipse extends SVGGraphicObjectConverter {
    /**
     * Line converter used for degenerate cases
     */
    private SVGLine svgLine;

    /**
     * @param generatorContext used to build Elements
     */
    public SVGEllipse(SVGGeneratorContext generatorContext) {
        super(generatorContext);

        svgLine = new SVGLine(generatorContext);
    }

    /**
     * @param ellipse the Ellipse2D object to be converted
     */
    public Element toSVG(Ellipse2D ellipse) {
        if(ellipse.getWidth() < 0 || ellipse.getHeight() < 0){
            return null;
        }

        if(ellipse.getWidth() == ellipse.getHeight())
            return toSVGCircle(ellipse);
        else
            return toSVGEllipse(ellipse);
    }

    /**
     * @param ellipse the Ellipse2D object to be converted to a circle
     */
    private Element toSVGCircle(Ellipse2D ellipse){
        Element svgCircle =
            generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                        SVG_CIRCLE_TAG);
        svgCircle.setAttributeNS(null, SVG_CX_ATTRIBUTE,
                                 doubleString(ellipse.getX() +
                                              ellipse.getWidth()/2));
        svgCircle.setAttributeNS(null, SVG_CY_ATTRIBUTE,
                                 doubleString(ellipse.getY() +
                                              ellipse.getHeight()/2));
        svgCircle.setAttributeNS(null, SVG_R_ATTRIBUTE,
                                 doubleString(ellipse.getWidth()/2));
        return svgCircle;
    }

    /**
     * @param ellipse the Ellipse2D object to be converted to an ellipse
     */
    private Element toSVGEllipse(Ellipse2D ellipse){
        if(ellipse.getWidth() > 0 && ellipse.getHeight() > 0){
            Element svgCircle =
                generatorContext.domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                            SVG_ELLIPSE_TAG);
            svgCircle.setAttributeNS(null, SVG_CX_ATTRIBUTE,
                                     doubleString(ellipse.getX() +
                                                  ellipse.getWidth()/2));
            svgCircle.setAttributeNS(null, SVG_CY_ATTRIBUTE,
                                     doubleString(ellipse.getY() +
                                                  ellipse.getHeight()/2));
            svgCircle.setAttributeNS(null, SVG_RX_ATTRIBUTE,
                                     doubleString(ellipse.getWidth()/2));
            svgCircle.setAttributeNS(null, SVG_RY_ATTRIBUTE,
                                     doubleString(ellipse.getHeight()/2));
            return svgCircle;
        }
        else if(ellipse.getWidth() == 0 && ellipse.getHeight() > 0){
            // Degenerate to a line
            Line2D line = new Line2D.Double(ellipse.getX(), ellipse.getY(), ellipse.getX(), 
                                            ellipse.getY() + ellipse.getHeight());
            return svgLine.toSVG(line);
        }
        else if(ellipse.getWidth() > 0 && ellipse.getHeight() == 0){
            // Degenerate to a line
            Line2D line = new Line2D.Double(ellipse.getX(), ellipse.getY(),
                                            ellipse.getX() + ellipse.getWidth(),
                                            ellipse.getY());
            return svgLine.toSVG(line);
        }
        return null;
    }
}
