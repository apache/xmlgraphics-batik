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

/**
 * Repository of SVG attribute descriptions, accessible by
 * name.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGAttributeMap{
    /**
     * Map of attribute name to SVGAttribute objects
     */
    private static Map attrMap = new Hashtable();

    /**
     * @param name SVG name of the requested attribute
     * @return attribute with requested name
     */
    public static SVGAttribute get(String attrName){
        return (SVGAttribute)attrMap.get(attrName);
    }

    /**
     * Static initializer that populates the map
     */
    /*
      static {
      HashSet tagSet = new HashSet();

      tagSet.add(TAG_G);
      tagSet.add(TAG_DEFS);
      tagSet.add(TAG_TEXT);
      tagSet.add(TAG_TSPAN);
      tagSet.add(TAG_TREF);
      tagSet.add(TAG_TEXT_PATH);
      tagSet.add(TAG_USE);
      tagSet.add(TAG_IMAGE);
      tagSet.add(TAG_SYMBOL);
      tagSet.add(TAG_MARKER);
      tagSet.add(TAG_CLIPPATH);
      tagSet.add(TAG_MASK);
      tagSet.add(TAG_PATTERN);
      tagSet.add(TAG_A);
      tagSet.add(TAG_SWITCH);
      tagSet.add(TAG_FOREIGN_OBJECT);
      tagSet.add(TAG_FE_IMAGE);
      }*/
}
