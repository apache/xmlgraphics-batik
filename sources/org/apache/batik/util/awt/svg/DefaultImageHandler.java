/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

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
 * @see             org.apache.batik.util.awt.svg.SVGGraphics2D
 */
public class DefaultImageHandler implements ImageHandler{
    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(Image image, Element imageElement){
        //
        // First, set the image width and height
        //
        imageElement.setAttribute(ATTR_WIDTH, "" + image.getWidth(null));
        imageElement.setAttribute(ATTR_HEIGHT, "" + image.getHeight(null));

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
        imageElement.setAttribute(ATTR_WIDTH, "" + image.getWidth());
        imageElement.setAttribute(ATTR_HEIGHT, "" + image.getHeight());

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
        imageElement.setAttribute(ATTR_WIDTH, "" + image.getWidth());
        imageElement.setAttribute(ATTR_HEIGHT, "" + image.getHeight());

        //
        // Now, set the href
        //
        handleHREF(image, imageElement);
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(Image image, Element imageElement){
        // Simply write a placeholder
        imageElement.setAttribute(ATTR_HREF, image.toString());
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderedImage image, Element imageElement){
        System.out.println("********************************************");
        System.out.println("Setting HREF attribute....");
        // Simply write a placeholder
        imageElement.setAttribute(ATTR_HREF, image.toString());
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderableImage image, Element imageElement){
        // Simply write a placeholder
        imageElement.setAttribute(ATTR_HREF, image.toString());
    }
}
