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

import org.w3c.dom.Document;

/**
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class SVGGraphicObjectConverter implements SVGSyntax{
    public static final String ERROR_DOM_FACTORY_NULL = "domFactory should not be null";

    /**
     * Used by converters to create Elements and other DOM objects
     */
    protected Document domFactory;

    /**
     * @param domFactory Can be used by the SVGGraphicObjectConverter extentions
     *        to create Elements and other types of DOM objects.
     */
    public SVGGraphicObjectConverter(Document domFactory){
        if(domFactory==null)
            throw new IllegalArgumentException(ERROR_DOM_FACTORY_NULL);

        this.domFactory = domFactory;
    }

    /**
     * @return the double value formated as an int if there
     *         is no fractional part. This avoids the extra
     *         ".0" that a standard convertion gives.
     */
    public static String doubleString(double value){
        if(((int)value) == value)
            return Integer.toString((int)value);
        else
            return Double.toString(value);
    }
}
