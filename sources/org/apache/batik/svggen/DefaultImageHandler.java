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
 * This interface default implementation of the ImageHandler
 * interface simply puts a place holder in the xlink:href
 * attribute and sets the width and height of the element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 */
public class DefaultImageHandler implements ImageHandler, ErrorConstants {
    // reducing the dependency on dom package by doing this only once:
    static final String XLINK_NAMESPACE_URI =
        org.apache.batik.dom.util.XLinkSupport.XLINK_NAMESPACE_URI;

    /**
     * Build a <code>DefaultImageHandler</code>.
     */
    public DefaultImageHandler() {}

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(Image image, Element imageElement,
                            SVGGeneratorContext generatorContext) {
        //
        // First, set the image width and height
        //
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE,
                                    "" + image.getWidth(null));
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE,
                                    "" + image.getHeight(null));

        //
        // Now, set the href
        //
        try {
            handleHREF(image, imageElement, generatorContext);
        } catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            } catch (SVGGraphics2DIOException io) {
                // we need a runtime exception because
                // java.awt.Graphics2D method doesn't throw exceptions..
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(RenderedImage image, Element imageElement,
                            SVGGeneratorContext generatorContext) {
        //
        // First, set the image width and height
        //
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE,
                                    "" + image.getWidth());
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE,
                                    "" + image.getHeight());

        //
        // Now, set the href
        //
        try {
            handleHREF(image, imageElement, generatorContext);
        } catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            } catch (SVGGraphics2DIOException io) {
                // we need a runtime exception because
                // java.awt.Graphics2D method doesn't throw exceptions..
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(RenderableImage image, Element imageElement,
                            SVGGeneratorContext generatorContext) {
        //
        // First, set the image width and height
        //
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE,
                                    "" + image.getWidth());
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE,
                                    "" + image.getHeight());

        //
        // Now, set the href
        //
        try {
            handleHREF(image, imageElement, generatorContext);
        } catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            } catch (SVGGraphics2DIOException io) {
                // we need a runtime exception because
                // java.awt.Graphics2D method doesn't throw exceptions..
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(Image image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Simply write a placeholder
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderedImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Simply write a placeholder
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderableImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Simply write a placeholder
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }
}
