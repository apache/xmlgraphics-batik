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

import org.w3c.dom.*;

/**
 * Describes an SVG clip
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see           org.apache.batik.ext.awt.g2d.GraphicContext
 * @see           org.apache.batik.svggen.SVGDescriptor
 */
public class SVGClipDescriptor implements SVGDescriptor, SVGSyntax{
    private String clipPathValue;
    private Element clipPathDef;

    /**
     * @param clipPathDef definition of a clip path
     * @param attribute value referencing clipPathDef
     */
    public SVGClipDescriptor(String clipPathValue, Element clipPathDef){
        if (clipPathValue == null)
            throw new SVGGraphics2DRuntimeException(ErrorConstants.ERR_CLIP_NULL);

        this.clipPathValue = clipPathValue;
        this.clipPathDef = clipPathDef;
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
            attrMap = new Hashtable();

        attrMap.put(SVG_CLIP_PATH_ATTRIBUTE, clipPathValue);

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
    public Set getDefinitionSet(Set defSet){
        if(defSet == null)
            defSet = new HashSet();

        if(clipPathDef != null)
            defSet.add(clipPathDef);

        return defSet;
    }
}
