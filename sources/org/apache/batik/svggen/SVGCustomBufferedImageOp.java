/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.image.*;
import java.awt.geom.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts an custom BufferedImageOp object into
 * an equivalent SVG filter.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.svggen.SVGBufferedImageOp
 */
public class SVGCustomBufferedImageOp extends AbstractSVGFilterConverter{
    public static final String ERROR_EXTENSION_HANDLER_NULL = "extensionHandler should not be null";

    /**
     * BufferedImageOp conversion is handed to the extensionHandler.
     * This class keeps track of already converted BufferedImageOps
     */
    private ExtensionHandler extensionHandler;

    /**
     * @param domFactory for use by SVGCustomBufferedImageOp to build Elements
     */
    public SVGCustomBufferedImageOp(Document domFactory,
                                    ExtensionHandler extensionHandler){
        super(domFactory);

        if(extensionHandler == null)
            throw new IllegalArgumentException(ERROR_EXTENSION_HANDLER_NULL);

        this.extensionHandler = extensionHandler;
    }

    /**
     * @param filter the BufferedImageOp object to convert to SVG
     * @param filterRect Rectangle, in device space, that defines the area
     *        to which filtering applies. May be null, meaning that the
     *        area is undefined.
     * @return an SVGFilterDescriptor mapping the SVG
     *         BufferedImageOp equivalent to the input BufferedImageOp.
     */
    public SVGFilterDescriptor toSVG(BufferedImageOp filter,
                                     Rectangle filterRect){
        SVGFilterDescriptor filterDesc = (SVGFilterDescriptor)descMap.get(filter);

        if(filterDesc == null){
            // First time this filter is used. Request handler
            // to do the convertion
            filterDesc
                = extensionHandler.handleFilter(filter, filterRect, domFactory);

            if(filterDesc != null){
                Element def = filterDesc.getDef();
                if(def != null)
                    defSet.add(def);
                descMap.put(filter, filterDesc);
            }
            else{
                System.err.println("SVGCustomBufferedImageOp:: ExtensionHandler could not convert filter");
            }
        }

        return filterDesc;
    }

}

