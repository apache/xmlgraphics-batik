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

import org.apache.batik.dom.util.XLinkSupport;

/**
 * This interface default implementation of the ImageHandler
 * interface simply puts a place holder in the xlink:href
 * attribute and sets the width and height of the element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 */
public class DefaultImageHandler implements ImageHandler {
    private static final String ERROR_CONTEXT_NULL =
        "generatorContext should not be null";

    /**
     * The context in which the image handler will have to work.
     */
    protected SVGGeneratorContext generatorContext;

    /**
     * Build a <code>DefaultImageHandler</code>.
     */
    public DefaultImageHandler(SVGGeneratorContext generatorContext) {
        if (generatorContext == null)
            throw new IllegalArgumentException(ERROR_CONTEXT_NULL);
        this.generatorContext = generatorContext;
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(Image image, Element imageElement) {
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
        handleHREF(image, imageElement);
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(RenderedImage image, Element imageElement){
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
        handleHREF(image, imageElement);
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(RenderableImage image, Element imageElement){
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
        handleHREF(image, imageElement);
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(Image image, Element imageElement) {
        // Simply write a placeholder
        imageElement.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderedImage image, Element imageElement){
        // System.out.println("********************************************");
        // System.out.println("Setting HREF attribute....");
        // Simply write a placeholder
        imageElement.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderableImage image, Element imageElement){
        // Simply write a placeholder
        imageElement.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }
}
