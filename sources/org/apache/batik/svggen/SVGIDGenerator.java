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
 * Generates id for an arbitrary number of prefix
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGIDGenerator {
    private static Map prefixMap = new Hashtable();

    /**
     * Generates an id for the given prefix. This class keeps
     * track of all invocations to that it generates unique ids
     *
     * @param prefix defines the prefix for which the id should
     *               be generated.
     * @return a value of the form <prefix><n>
     */
    public static String generateID(String prefix){
        Integer maxId = (Integer)prefixMap.get(prefix);
        if(maxId == null){
            maxId = new Integer(0);
            prefixMap.put(prefix, maxId);
        }

        maxId = new Integer(maxId.intValue()+1);
        prefixMap.put(prefix, maxId);
        return prefix + maxId;
    }
}
