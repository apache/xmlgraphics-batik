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

import org.w3c.dom.Element;

/**
 * This interface allows the user of the Graphics2D SVG generator
 * to decide how to handle images that it renders. For example,
 * an implementation could decide to embed JPEG/PNG encoded images
 * into SVG source document using the data protocol (RFC 1521, paragraph 5.2)
 * Another option is to save images into JPEG/PNG files and store URI
 * in SVG source. <br>
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 */
public interface ImageHandler extends SVGSyntax {
    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(Image image, Element imageElement,
                            SVGGeneratorContext generatorContext);

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(RenderedImage image, Element imageElement,
                            SVGGeneratorContext generatorContext);

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(RenderableImage image, Element imageElement,
                            SVGGeneratorContext generatorContext);
}
