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

import org.w3c.dom.Element;

/**
 * Used to represent an SVG Paint. This can be achieved with
 * to values: an SVG paint value and an SVG opacity value
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGPaintDescriptor implements SVGDescriptor, SVGSyntax{
    private Element def;
    private String paintValue;
    private String opacityValue;

    public SVGPaintDescriptor(String paintValue,
                              String opacityValue){
        this.paintValue = paintValue;
        this.opacityValue = opacityValue;
    }

    public SVGPaintDescriptor(String paintValue,
                              String opacityValue,
                              Element def){
        this(paintValue, opacityValue);
        this.def = def;
    }

    public String getPaintValue(){
        return paintValue;
    }

    public String getOpacityValue(){
        return opacityValue;
    }

    public Element getDef(){
        return def;
    }

    public Map getAttributeMap(Map attrMap){
        if(attrMap == null)
            attrMap = new Hashtable();

        attrMap.put(ATTR_FILL, paintValue);
        attrMap.put(ATTR_STROKE, paintValue);
        attrMap.put(ATTR_FILL_OPACITY, opacityValue);
        attrMap.put(ATTR_STROKE_OPACITY, opacityValue);

        return attrMap;
    }

    public Set getDefinitionSet(Set defSet){
        if(defSet == null)
            defSet = new HashSet();

        if(def != null)
            defSet.add(def);

        return defSet;
    }
}
