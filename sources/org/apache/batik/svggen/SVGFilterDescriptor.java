/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import org.w3c.dom.Element;

/**
 * Used to represent an SVG Composite. This can be achieved with
 * to values: an SVG opacity and a filter
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFilterDescriptor {
    private Element def;
    private String filterValue;

    public SVGFilterDescriptor(String filterValue){
        this.filterValue = filterValue;
    }

    public SVGFilterDescriptor(String filterValue,
                               Element def){
        this(filterValue);
        this.def = def;
    }

    public String getFilterValue(){
        return filterValue;
    }

    public Element getDef(){
        return def;
    }
}
