/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Describes an SVG transform
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see           org.apache.batik.ext.awt.g2d.GraphicContext
 */
public class SVGTransformDescriptor implements SVGDescriptor, SVGSyntax{
    private String transform;

    public SVGTransformDescriptor(String transform){
        this.transform = transform;
    }

    /**
     * @param attrMap if not null, attribute name/value pairs
     *        for this descriptor should be written in this Map.
     *        Otherwise, a new Map will be created and attribute
     *        name/value pairs will be written into it.
     * @return a map containing the SVG attributes needed by the
     *         descriptor.
     */
    public Map getAttributeMap(Map attrMap){
        if(attrMap == null)
            attrMap = new HashMap();

        attrMap.put(SVG_TRANSFORM_ATTRIBUTE, transform);

        return attrMap;
    }

    /**
     * @param defSet if not null, definitions required to provide
     *        targets for the descriptor attribute values will be
     *        copied into defSet. If null, a new Set should be created
     *        and definitions copied into it. The set contains
     *        zero, one or more Elements.
     * @return a set containing Elements that represent the definition
     *         of the descriptor's attribute values
     */
    public List getDefinitionSet(List defSet) {
        if (defSet == null)
            defSet = new LinkedList();

        return defSet;
    }
}
