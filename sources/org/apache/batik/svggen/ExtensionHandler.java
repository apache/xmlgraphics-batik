/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;

/**
 * The ExtensionHandler interface allows the user to handle
 * Java 2D API extensions that map to SVG concepts (such as custom
 * Paints, Composites or BufferedImageOp filters).
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface ExtensionHandler {
    /**
     * @param paint Custom Paint to be converted to SVG
     * @param generatorContext allows the handler to build DOM objects as needed.
     * @return an SVGPaintDescriptor
     */
    public SVGPaintDescriptor handlePaint(Paint paint,
                                          SVGGeneratorContext generatorContext);

    /**
     * @param composite Custom Composite to be converted to SVG.
     * @param generatorContext allows the handler to build DOM objects as needed.
     * @return an SVGCompositeDescriptor which contains a valid SVG filter,
     * or null if the composite cannot be handled
     *
     */
    public SVGCompositeDescriptor handleComposite(Composite composite,
                                                  SVGGeneratorContext generatorContext);

/**
     * @param filter Custom filter to be converted to SVG.
     * @param filterRect Rectangle, in device space, that defines the area
     *        to which filtering applies. May be null, meaning that the
     *        area is undefined.
     * @param generatorContext allows the handler to build DOM objects as needed.
     * @return an SVGFilterDescriptor which contains a valid SVG filter,
     * or null if the composite cannot be handled
     */
    public SVGFilterDescriptor handleFilter(BufferedImageOp filter,
                                            Rectangle filterRect,
                                            SVGGeneratorContext generatorContext);
}
