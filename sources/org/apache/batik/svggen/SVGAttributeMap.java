/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.util.Map;
import java.util.HashMap;

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
    private static Map attrMap = new HashMap();

    /**
     * @param name SVG name of the requested attribute
     * @return attribute with requested name
     */
    public static SVGAttribute get(String attrName) {
        return (SVGAttribute)attrMap.get(attrName);
    }
}
