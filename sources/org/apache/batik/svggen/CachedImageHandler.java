/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Element;

/**
 * Extends the default ImageHandler interface with calls to
 * allow caching of raster images in generated SVG content.
 *
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 */
public interface CachedImageHandler extends ImageHandler {
    /**
     * Creates an Element suitable for referring to images.
     * Note that no assumptions can be made about the name of this Element.
     */
    public Element createElement(SVGGeneratorContext generatorContext);

    /**
     * The handler should set the xlink:href tag and return a transform
     *
     * @param image             the image under consideration
     * @param imageElement      the DOM Element for this image
     * @param x                 x coordinate
     * @param y                 y coordinate
     * @param width             width for rendering
     * @param height            height for rendering
     * @param generatorContext  the SVGGeneratorContext
     *
     * @return transform converting the image dimension to rendered dimension
     */
    public AffineTransform handleImage(Image image, Element imageElement,
                                       int x, int y,
                                       int width, int height,
                                       SVGGeneratorContext generatorContext);

    /**
     * The handler should set the xlink:href tag and return a transform
     *
     * @param image             the image under consideration
     * @param imageElement      the DOM Element for this image
     * @param x                 x coordinate
     * @param y                 y coordinate
     * @param width             width for rendering
     * @param height            height for rendering
     * @param generatorContext  the SVGGeneratorContext
     *
     * @return transform converting the image dimension to rendered dimension
     */
    public AffineTransform handleImage(RenderedImage image, Element imageElement,
                                       int x, int y,
                                       int width, int height,
                                       SVGGeneratorContext generatorContext);

    /**
     * The handler should set the xlink:href tag and return a transform
     *
     * @param image             the image under consideration
     * @param imageElement      the DOM Element for this image
     * @param x                 x coordinate
     * @param y                 y coordinate
     * @param width             width for rendering
     * @param height            height for rendering
     * @param generatorContext  the SVGGeneratorContext
     *
     * @return transform converting the image dimension to rendered dimension
     */
    public AffineTransform handleImage(RenderableImage image, Element imageElement,
                                       double x, double y,
                                       double width, double height,
                                       SVGGeneratorContext generatorContext);

    /**
     * Returns the image cache instance in use by this handler
     *
     * @return the image cache
     */ 
   public ImageCacher getImageCacher();
    
}
