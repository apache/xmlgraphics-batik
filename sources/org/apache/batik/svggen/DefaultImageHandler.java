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
 * This interface default implementation of the ImageHandler
 * interface simply puts a place holder in the xlink:href
 * attribute and sets the width and height of the element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 */
public class DefaultImageHandler implements ImageHandler, 
                                            CachedImageHandler, 
                                            ErrorConstants {
    // duplicate the string here to remove dependencies on
    // org.apache.batik.dom.util.XLinkSupport
    static final String XLINK_NAMESPACE_URI =
        "http://www.w3.org/1999/xlink";

    /**
     * Build a <code>DefaultImageHandler</code>.
     */
    public DefaultImageHandler() {}

    /*=================================================================*
     * ImageHandler implementations
     *=================================================================*/

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


    /*=================================================================*
     * CachedImageHandler implementations
     *=================================================================*/
    
    protected ImageCacher imageCacher;

    /**
     * The image cache can be used by subclasses for efficient image storage
     */
    public ImageCacher getImageCacher() {
        return imageCacher;
    }

    void setImageCacher(ImageCacher imageCacher) {
        this.imageCacher = imageCacher;
    }

    /**
     * Creates an Element which can refer to an image.
     * Note that no assumptions should be made by the caller about the
     * corresponding SVG tag.
     */
    public Element createElement(SVGGeneratorContext generatorContext) {
        // Create a DOM Element in SVG namespace to refer to an image
        Element imageElement =
            generatorContext.getDOMFactory().createElementNS(
                                                             SVG_NAMESPACE_URI, SVG_IMAGE_TAG);

        return imageElement;
    }

    /**
     * The handler sets the xlink:href tag and returns a transform
     */
    public AffineTransform handleImage(Image image,
                                       Element imageElement,
                                       int x, int y,
                                       int width, int height,
                                       SVGGeneratorContext generatorContext) {

        int imageWidth      = image.getWidth(null);
        int imageHeight     = image.getHeight(null);
        AffineTransform af  = null;

        if(imageWidth == 0 || imageHeight == 0 ||
           width == 0 || height == 0) {

            // Forget about it
            handleEmptyImage(imageElement);

        } else {
            // First set the href
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

            // Then create the transformation:
            // Because we cache image data, the stored image may
            // need to be scaled.
            af = handleTransform(imageElement, (double) x, (double) y,
                                 (double) imageWidth, (double) imageHeight,
                                 (double) width, (double) height);
        }
        return af;
    }

    /**
     * The handler sets the xlink:href tag and returns a transform
     */
    public AffineTransform handleImage(RenderedImage image,
                                       Element imageElement,
                                       int x, int y,
                                       int width, int height,
                                       SVGGeneratorContext generatorContext) {

        int imageWidth      = image.getWidth();
        int imageHeight     = image.getHeight();
        AffineTransform af  = null;

        if(imageWidth == 0 || imageHeight == 0 ||
           width == 0 || height == 0) {

            // Forget about it
            handleEmptyImage(imageElement);

        } else {
            // First set the href
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

            // Then create the transformation:
            // Because we cache image data, the stored image may
            // need to be scaled.
            af = handleTransform(imageElement, (double) x, (double) y,
                                 (double) imageWidth, (double) imageHeight,
                                 (double) width, (double) height);
        }
        return af;
    }

    /**
     * The handler sets the xlink:href tag and returns a transform
     */
    public AffineTransform handleImage(RenderableImage image,
                                       Element imageElement,
                                       double x, double y,
                                       double width, double height,
                                       SVGGeneratorContext generatorContext) {

        double imageWidth   = image.getWidth();
        double imageHeight  = image.getHeight();
        AffineTransform af  = null;

        if(imageWidth == 0 || imageHeight == 0 ||
           width == 0 || height == 0) {

            // Forget about it
            handleEmptyImage(imageElement);

        } else {
            // First set the href
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

            // Then create the transformation:
            // Because we cache image data, the stored image may
            // need to be scaled.
            af = handleTransform(imageElement, x,y,
                                 imageWidth, imageHeight,
                                 width, height);
        }
        return af;
    }

    /**
     * Determines the transformation needed to get the cached image to
     * scale & position properly. Sets x and y attributes on the element
     * accordingly.
     */
    protected AffineTransform handleTransform(Element imageElement,
                                              double x, double y,
                                              double srcWidth,
                                              double srcHeight,
                                              double dstWidth,
                                              double dstHeight) {
        // In this the default case, <image> element, we just
        // set x, y, width and height attributes.
        // No additional transform is necessary.
        
        imageElement.setAttributeNS(null,
                                    SVG_X_ATTRIBUTE,
                                    AbstractSVGConverter.doubleString(x));
        imageElement.setAttributeNS(null,
                                    SVG_Y_ATTRIBUTE,
                                    AbstractSVGConverter.doubleString(y));
        imageElement.setAttributeNS(null,
                                    SVG_WIDTH_ATTRIBUTE,
                                    AbstractSVGConverter.doubleString(dstWidth));
        imageElement.setAttributeNS(null,
                                    SVG_HEIGHT_ATTRIBUTE,
                                    AbstractSVGConverter.doubleString(dstHeight));
        return null;
    }
              
    protected void handleEmptyImage(Element imageElement) {
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, "");
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE, "0");
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE, "0");
    }

}
