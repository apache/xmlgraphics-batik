/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.util.Map;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.Element;

/**
 * Used to represent an SVG Paint. This can be achieved with
 * to values: an SVG paint value and an SVG opacity value
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGStrokeDescriptor implements SVGDescriptor, SVGSyntax{
    public static final String ERROR_NULL_ARGUMENT = "None of the stroke attribute values should be null";

    private String strokeWidth;
    private String capStyle;
    private String joinStyle;
    private String miterLimit;
    private String dashArray;
    private String dashOffset;


    public SVGStrokeDescriptor(String strokeWidth, String capStyle, String joinStyle,
                               String miterLimit, String dashArray, String dashOffset){
        if(strokeWidth == null ||
           capStyle == null    ||
           joinStyle == null   ||
           miterLimit == null  ||
           dashArray == null   ||
           dashOffset == null)
            throw new IllegalArgumentException(ERROR_NULL_ARGUMENT);

        this.strokeWidth = strokeWidth;
        this.capStyle = capStyle;
        this.joinStyle = joinStyle;
        this.miterLimit = miterLimit;
        this.dashArray = dashArray;
        this.dashOffset = dashOffset;
    }

    String getStrokeWidth(){ return strokeWidth; }
    String getCapStyle(){ return capStyle; }
    String getJoinStyle(){ return joinStyle; }
    String getMiterLimit(){ return miterLimit; }
    String getDashArray(){ return dashArray; }
    String getDashOffset(){ return dashOffset; }

    public Map getAttributeMap(Map attrMap){
        if(attrMap == null)
            attrMap = new Hashtable();

        attrMap.put(SVG_STROKE_WIDTH_ATTRIBUTE, strokeWidth);
        attrMap.put(SVG_STROKE_LINECAP_ATTRIBUTE, capStyle);
        attrMap.put(SVG_STROKE_LINEJOIN_ATTRIBUTE, joinStyle);
        attrMap.put(SVG_STROKE_MITERLIMIT_ATTRIBUTE, miterLimit);
        attrMap.put(SVG_STROKE_DASHARRAY_ATTRIBUTE, dashArray);
        attrMap.put(SVG_STROKE_DASHOFFSET_ATTRIBUTE, dashOffset);

        return attrMap;
    }

    public Set getDefinitionSet(Set defSet){
        if(defSet == null)
            defSet = new HashSet();

        return defSet;
    }
}
