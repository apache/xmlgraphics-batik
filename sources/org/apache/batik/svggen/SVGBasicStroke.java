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

        return new SVGStrokeDescriptor(strokeWidth, capStyle, joinStyle, miterLimit,
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
            dashArrayBuf.append(SPACE);
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
            return VALUE_LINE_JOIN_BEVEL;
        case BasicStroke.JOIN_ROUND:
            return VALUE_LINE_JOIN_ROUND;
        case BasicStroke.JOIN_MITER:
        default:
            return VALUE_LINE_JOIN_MITER;
        }
    }

    /**
     * @param endCap cap style
     */
    private static String endCapToSVG(int endCap){
        switch(endCap){
        case BasicStroke.CAP_BUTT:
            return VALUE_LINE_CAP_BUTT;
        case BasicStroke.CAP_ROUND:
            return VALUE_LINE_CAP_ROUND;
        default:
        case BasicStroke.CAP_SQUARE:
            return VALUE_LINE_CAP_SQUARE;
        }
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        Document domFactory = TestUtil.getDocumentPrototype();

        BasicStroke strokes[] = { new BasicStroke(),
                                  new BasicStroke(2),
                                  new BasicStroke(4.5f),
                                  new BasicStroke(10, BasicStroke.CAP_BUTT,
                                                  BasicStroke.JOIN_MITER),
                                  new BasicStroke(10, BasicStroke.CAP_SQUARE,
                                                  BasicStroke.JOIN_MITER),
                                  new BasicStroke(10, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_MITER),
                                  new BasicStroke(10, BasicStroke.CAP_BUTT,
                                                  BasicStroke.JOIN_BEVEL),
                                  new BasicStroke(10, BasicStroke.CAP_BUTT,
                                                  BasicStroke.JOIN_ROUND),
                                  new BasicStroke(50, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_MITER, 100),
                                  new BasicStroke(75, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_ROUND, 50,
                                                  new float[]{1, 2, 3, 4}, 0.5f),
                                  new BasicStroke(75, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_ROUND, 60,
                                                  new float[]{10.1f, 2.4f, 3.5f, 4.2f},
                                                  10)
        };

        Element rectGroup = domFactory.createElement(SVG_G_TAG);

        for(int i=0; i<strokes.length; i++){
            BasicStroke stroke = strokes[i];
            Map attrMap = SVGBasicStroke.toSVG(stroke).getAttributeMap(null);
            Element rectElement = domFactory.createElement(TAG_RECT);
            Iterator iter = attrMap.keySet().iterator();
            while(iter.hasNext()){
                String attrName = (String)iter.next();
                String attrValue = (String)attrMap.get(attrName);
                rectElement.setAttribute(attrName, attrValue);
            }
            rectGroup.appendChild(rectElement);
        }
        TestUtil.trace(rectGroup, System.out);
    }
}
