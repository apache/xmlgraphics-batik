/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.util.Map;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.*;

/**
 * Describes an SVG font
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.util.awt.svg.SVGFont
 */
public class SVGFontDescriptor implements SVGDescriptor, SVGSyntax {
    public static final String ERROR_NULL_INPUT = "none of the input arguments should be null";

    private String fontSize;
    private String fontWeight;
    private String fontStyle;
    private String fontFamily;

    /**
     * Constructor
     */
    public SVGFontDescriptor(String fontSize,
                             String fontWeight,
                             String fontStyle,
                             String fontFamily){
        if(fontSize == null ||
           fontWeight == null ||
           fontStyle == null ||
           fontFamily == null)
            throw new IllegalArgumentException(ERROR_NULL_INPUT);

        this.fontSize = fontSize;
        this.fontWeight = fontWeight;
        this.fontStyle = fontStyle;
        this.fontFamily = fontFamily;
    }

    public Map getAttributeMap(Map attrMap){
        if(attrMap == null)
            attrMap = new Hashtable();

        attrMap.put(ATTR_FONT_SIZE, fontSize);
        attrMap.put(ATTR_FONT_WEIGHT, fontWeight);
        attrMap.put(ATTR_FONT_STYLE, fontStyle);
        attrMap.put(ATTR_FONT_FAMILY, fontFamily);

        return attrMap;
    }

    public Set getDefinitionSet(Set defSet){
        if(defSet == null)
            defSet = new HashSet();

        return defSet;
    }
}
