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
 * Used to represent an SVG Composite. This can be achieved with
 * to values: an SVG opacity and a filter
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGCompositeDescriptor implements SVGDescriptor, SVGSyntax{
    private Element def;
    private String opacityValue;
    private String filterValue;

    public SVGCompositeDescriptor(String opacityValue,
                                  String filterValue){
        this.opacityValue = opacityValue;
        this.filterValue = filterValue;
    }

    public SVGCompositeDescriptor(String opacityValue,
                                  String filterValue,
                                  Element def){
        this(opacityValue, filterValue);
        this.def = def;
    }

    public String getOpacityValue(){
        return opacityValue;
    }

    public String getFilterValue(){
        return filterValue;
    }

    public Element getDef(){
        return def;
    }

    public Map getAttributeMap(Map attrMap){
        if(attrMap == null)
            attrMap = new Hashtable();

        attrMap.put(SVG_OPACITY_ATTRIBUTE, opacityValue);
        attrMap.put(SVG_FILTER_ATTRIBUTE, filterValue);

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
