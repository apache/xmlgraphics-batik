/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.image.renderable.*;

import org.w3c.dom.*;

import org.apache.batik.ext.awt.g2d.AbstractGraphics2D;
import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * This specialization of the SVGGraphics2D class
 * uses a caching mechanism for raster images.
 *
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 */
public class CachedImageSVGGraphics2D extends SVGGraphics2D {

    public CachedImageSVGGraphics2D(Document domFactory) {
        super(domFactory);
        
        CachedImageHandler imageHandler =
            new CachedImageHandlerBase64Encoder();
        imageHandler.getImageCacher().setDOMTreeManager(getDOMTreeManager());
        getGeneratorContext().setImageHandler(imageHandler);
    }

    public CachedImageSVGGraphics2D(Document domFactory,
                                    CachedImageHandler imageHandler,
                                    ExtensionHandler extensionHandler,
                                    boolean textAsShapes) {
        super(domFactory, imageHandler, extensionHandler, textAsShapes);

        getCachedImageHandler().getImageCacher().setDOMTreeManager(
                                                       getDOMTreeManager());
    }

    public CachedImageSVGGraphics2D(SVGGeneratorContext generatorCtx,
                                    boolean textAsShapes) {
        super(generatorCtx, textAsShapes);

        getCachedImageHandler().getImageCacher().setDOMTreeManager(
                                                       getDOMTreeManager());
    }

    public CachedImageHandler getCachedImageHandler() {
        ImageHandler imageHandler = getImageHandler();
        
        if( ! (imageHandler instanceof CachedImageHandler)) {
            throw new SVGGraphics2DRuntimeException(
                ERR_IMAGE_HANDLER_NOT_SUPPORTED+
                imageHandler.getClass().getName());
        }
        return (CachedImageHandler) imageHandler;
    }

    /**
     * Draws as much of the specified image as is currently available.
     * The image is drawn with its top-left corner at
     * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate
     * space. Transparent pixels in the image do not affect whatever
     * pixels are already there.
     * <p>
     * This method returns immediately in all cases, even if the
     * complete image has not yet been loaded, and it has not been dithered
     * and converted for the current output device.
     * <p>
     * If the image has not yet been completely loaded, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies
     * the specified image observer.
     * @param    img the specified image to be drawn.
     * @param    x   the <i>x</i> coordinate.
     * @param    y   the <i>y</i> coordinate.
     * @param    observer    object to be notified as more of
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y,
                             ImageObserver observer) {
        Element imageElement =
            getCachedImageHandler().createElement(getGeneratorContext());
        AffineTransform xform = getCachedImageHandler().handleImage(
                                            img, imageElement,
                                            x, y,
                                            img.getWidth(null),
                                            img.getHeight(null),
                                            getGeneratorContext());

        if (xform == null) {
            getDOMGroupManager().addElement(imageElement);
        } else {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            } catch(NoninvertibleTransformException e) {
                // This should never happen since handleImage
                // always returns invertible transform
                throw new SVGGraphics2DRuntimeException(ERR_UNEXPECTED);
            }
            gc.transform(xform);
            getDOMGroupManager().addElement(imageElement);
            gc.transform(inverseTransform);
        }
        return true;
    }


    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside the specified rectangle of this
     * graphics context's coordinate space, and is scaled if
     * necessary. Transparent pixels do not affect whatever pixels
     * are already there.
     * <p>
     * This method returns immediately in all cases, even if the
     * entire image has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies
     * the image observer by calling its <code>imageUpdate</code> method.
     * <p>
     * A scaled version of an image will not necessarily be
     * available immediately just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param    img    the specified image to be drawn.
     * @param    x      the <i>x</i> coordinate.
     * @param    y      the <i>y</i> coordinate.
     * @param    width  the width of the rectangle.
     * @param    height the height of the rectangle.
     * @param    observer    object to be notified as more of
     *                          the image is converted.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y,
                             int width, int height,
                             ImageObserver observer){
        Element imageElement =
            getCachedImageHandler().createElement(getGeneratorContext());
        AffineTransform xform = getCachedImageHandler().handleImage(
                                            img, imageElement,
                                            x, y,
                                            width, height,
                                            getGeneratorContext());

        if (xform == null) {
            getDOMGroupManager().addElement(imageElement);
        } else {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            } catch(NoninvertibleTransformException e) {
                // This should never happen since handleImage
                // always returns invertible transform
                throw new SVGGraphics2DRuntimeException(ERR_UNEXPECTED);
            }
            gc.transform(xform);
            getDOMGroupManager().addElement(imageElement);
            gc.transform(inverseTransform);
        }
        return true;
    }


    /**
     * Renders a {@link RenderedImage},
     * applying a transform from image
     * space into user space before drawing.
     * The transformation from user space into device space is done with
     * the current <code>Transform</code> in the <code>Graphics2D</code>.
     * The specified transformation is applied to the image before the
     * transform attribute in the <code>Graphics2D</code> context is applied.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>, and <code>Composite</code> attributes. Note
     * that no rendering is done if the specified transform is
     * noninvertible.
     * @param img the image to be rendered
     * @param xform the transformation from image space into user space
     * @see #transform
     * @see #setTransform
     * @see #setComposite
     * @see #clip
     * @see #setClip
     */
    public void drawRenderedImage(RenderedImage img,
                                  AffineTransform trans2) {

        Element image =
            getCachedImageHandler().createElement(getGeneratorContext());
        AffineTransform trans1 = getCachedImageHandler().handleImage(
                                            img, image,
                                            img.getMinX(),
                                            img.getMinY(),
                                            img.getWidth(),
                                            img.getHeight(),
                                            getGeneratorContext());

        AffineTransform xform;

        // Concatenate the transformation we receive from the imageHandler
        // to the user-supplied one. Be aware that both may be null.
        if (trans2 == null) {
            xform = trans1;
        } else {
            if(trans1 == null) {
                xform = trans2;
             } else {
                xform = new AffineTransform(trans2);
                xform.concatenate(trans1);
            }
        }

        if(xform == null) {
            getDOMGroupManager().addElement(image);
        } else if(xform.getDeterminant() != 0){
            AffineTransform inverseTransform = null;
            try{
                inverseTransform = xform.createInverse();
            }catch(NoninvertibleTransformException e){
                // This should never happen since we checked
                // the matrix determinant
                throw new SVGGraphics2DRuntimeException(ERR_UNEXPECTED);
            }
            gc.transform(xform);
            getDOMGroupManager().addElement(image);
            gc.transform(inverseTransform);
        } else {
            AffineTransform savTransform = new AffineTransform(gc.getTransform());
            gc.transform(xform);
            getDOMGroupManager().addElement(image);
            gc.setTransform(savTransform);
        }
    }


    /**
     * Renders a
     * {@link RenderableImage},
     * applying a transform from image space into user space before drawing.
     * The transformation from user space into device space is done with
     * the current <code>Transform</code> in the <code>Graphics2D</code>.
     * The specified transformation is applied to the image before the
     * transform attribute in the <code>Graphics2D</code> context is applied.
     * The rendering attributes applied include the <code>Clip</code>,
     * <code>Transform</code>, and <code>Composite</code> attributes. Note
     * that no rendering is done if the specified transform is
     * noninvertible.
     *<p>
     * Rendering hints set on the <code>Graphics2D</code> object might
     * be used in rendering the <code>RenderableImage</code>.
     * If explicit control is required over specific hints recognized by a
     * specific <code>RenderableImage</code>, or if knowledge of which hints
     * are used is required, then a <code>RenderedImage</code> should be
     * obtained directly from the <code>RenderableImage</code>
     * and rendered using
     *{@link #drawRenderedImage(RenderedImage, AffineTransform)}.
     * @param img the image to be rendered
     * @param xform the transformation from image space into user space
     * @see #transform
     * @see #setTransform
     * @see #setComposite
     * @see #clip
     * @see #setClip
     * @see #drawRenderedImage
     */
    public void drawRenderableImage(RenderableImage img,
                                    AffineTransform trans2){
                                    
        Element image =
            getCachedImageHandler().createElement(getGeneratorContext());

        AffineTransform trans1 = getCachedImageHandler().handleImage(
                                            img, image,
                                            img.getMinX(),
                                            img.getMinY(),
                                            img.getWidth(),
                                            img.getHeight(),
                                            getGeneratorContext());

        AffineTransform xform;

        // Concatenate the transformation we receive from the imageHandler
        // to the user-supplied one. Be aware that both may be null.
        if (trans2 == null) {
            xform = trans1;
        } else {
            if(trans1 == null) {
                xform = trans2;
             } else {
                xform = new AffineTransform(trans2);
                xform.concatenate(trans1);
            }
        }

        if (xform == null) {
            getDOMGroupManager().addElement(image);
        } else if(xform.getDeterminant() != 0){
            AffineTransform inverseTransform = null;
            try{
                inverseTransform = xform.createInverse();
            }catch(NoninvertibleTransformException e){
                // This should never happen because we checked the
                // matrix determinant
                throw new SVGGraphics2DRuntimeException(ERR_UNEXPECTED);
            }
            gc.transform(xform);
            getDOMGroupManager().addElement(image);
            gc.transform(inverseTransform);
        } else {
            AffineTransform savTransform = new AffineTransform(gc.getTransform());
            gc.transform(xform);
            getDOMGroupManager().addElement(image);
            gc.setTransform(savTransform);
        }
    }
}
