/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import org.w3c.dom.*;
import java.util.*;
import java.awt.BasicStroke;

import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * Utility class that converts a Java BasicStroke object into
 * a set of SVG style attributes
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGBasicStroke extends AbstractSVGConverter{
    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc){
        if(gc.getStroke() instanceof BasicStroke)
            return toSVG((BasicStroke)gc.getStroke());
        else
            return null;
    }

    /**
     * @param stroke BasicStroke to convert to a set of
     *        SVG attributes
     * @return map of attributes describing the stroke
     */
    public static SVGStrokeDescriptor toSVG(BasicStroke stroke)
    {
        // Stroke width
        String strokeWidth = doubleString(stroke.getLineWidth());

        // Cap style
        String capStyle = endCapToSVG(stroke.getEndCap());

        // Join style
        String joinStyle = joinToSVG(stroke.getLineJoin());

        // Miter limit
        String miterLimit = doubleString(stroke.getMiterLimit());

        // Dash array
        float[] array = stroke.getDashArray();
        String dashArray = null;
        if(array != null)
            dashArray = dashArrayToSVG(array);
        else
            dashArray = SVG_NONE_VALUE;

        // Dash offset
        String dashOffset = doubleString(stroke.getDashPhase());

        return new SVGStrokeDescriptor(strokeWidth, capStyle,
                                       joinStyle, miterLimit,
                                       dashArray, dashOffset);
    }

    /**
     * @param dashArray float array to convert to a string
     */
    private static String dashArrayToSVG(float dashArray[]){
        StringBuffer dashArrayBuf = new StringBuffer();
        if(dashArray.length > 0)
            dashArrayBuf.append(doubleString(dashArray[0]));

        for(int i=1; i<dashArray.length; i++){
            dashArrayBuf.append(COMMA);
            dashArrayBuf.append(doubleString(dashArray[i]));
        }

        return dashArrayBuf.toString();
    }

    /**
     * @param lineJoin join style
     */
    private static String joinToSVG(int lineJoin){
        switch(lineJoin){
        case BasicStroke.JOIN_BEVEL:
            return SVG_BEVEL_VALUE;
        case BasicStroke.JOIN_ROUND:
            return SVG_ROUND_VALUE;
        case BasicStroke.JOIN_MITER:
        default:
            return SVG_MITER_VALUE;
        }
    }

    /**
     * @param endCap cap style
     */
    private static String endCapToSVG(int endCap){
        switch(endCap){
        case BasicStroke.CAP_BUTT:
            return SVG_BUTT_VALUE;
        case BasicStroke.CAP_ROUND:
            return SVG_ROUND_VALUE;
        default:
        case BasicStroke.CAP_SQUARE:
            return SVG_SQUARE_VALUE;
        }
    }
}
