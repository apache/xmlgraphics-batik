/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.Paint;
import java.awt.Composite;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * The ExtensionHandler interface allows the user to handler
 * Java 2D API extensions that map to SVG concepts (such as custom
 * Paints, Composites or BufferedImageOp filters.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see               java.awt.Paint, java.awt.Composite, java.awt.image.BufferedImageOp
 */
public interface ExtensionHandler{
    /**
     * @param paint Custom Paint to be converted to SVG
     * @param domFactory allows the handler to build DOM objects as needed.
     * @return an SVGPaintDescriptor
     */
    public SVGPaintDescriptor handlePaint(Paint paint, Document domFactory);

    /**
     * @param composite Custom Composite to be converted to SVG.
     * @param domFactory allows the handler to build DOM objects as needed.
     * @return an SVGCompositeDescriptor which contains a valid SVG filter, or null if the composite cannot be handled
     *
     */
    public SVGCompositeDescriptor handleComposite(Composite composite, Document domFactory);

    /**
     * @param filter Custom filter to be converted to SVG.
     * @param filterRect Rectangle, in device space, that defines the area
     *        to which filtering applies. May be null, meaning that the
     *        area is undefined.
     * @param domFactory allows the handler to build DOM objects as needed.
     * @return an SVGFilterDescriptor which contains a valid SVG filter, or null if the composite cannot be handled
     */
    public SVGFilterDescriptor handleFilter(BufferedImageOp filter,
                                            Rectangle filterRect,
                                            Document domFactory);
}
