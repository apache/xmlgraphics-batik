/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Method;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import org.w3c.dom.Element;

/**
 * This abstract implementation of the ImageHandler interface
 * is intended to be the base class for ImageHandlers that generate
 * image files for all the images they handle. This class stores
 * images in an configurable directory. The xlink:href value the
 * class generates is made of a configurable url root and the name
 * of the file created by this handler.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 * @see             org.apache.batik.svggen.ImageHandlerJPEGEncoder
 * @see             org.apache.batik.svggen.ImageHandlerPNGEncoder
 */
public abstract class AbstractImageHandlerEncoder extends DefaultImageHandler {
    private static final AffineTransform IDENTITY = new AffineTransform();

    /**
     * Directory where all images are placed
     */
    private String imageDir = "";

    /**
     * Value for the url root corresponding to the directory
     */
    private String urlRoot = "";

    // for createGraphics method.
    private static Method createGraphics = null;
    private static boolean initDone = false;
    private final static Class[] paramc = new Class[] {BufferedImage.class};
    private static Object[] paramo = null;

    /**
     * This method creates a <code>Graphics2D</code> from a
     * <code>BufferedImage</code>. If Batik extensions to AWT are
     * in the CLASSPATH it uses them, otherwise, it uses the regular
     * AWT method.
     */
    private static Graphics2D createGraphics(BufferedImage buf) {
        if (!initDone) {
            try {
                Class clazz = Class.forName("org.apache.batik.ext.awt.image.GraphicsUtil");
                createGraphics = clazz.getMethod("createGraphics", paramc);
                paramo = new Object[1];
            } catch (Throwable t) {
                // happen only if Batik extensions are not there
            } finally {
                initDone = true;
            }
        }
        if (createGraphics == null)
            return buf.createGraphics();
        else {
            paramo[0] = buf;
            Graphics2D g2d = null;
            try {
                g2d = (Graphics2D)createGraphics.invoke(null, paramo);
            } catch (Exception e) {
                // should not happen
            }
            return g2d;
        }
    }

    /**
     * @param generatorContext the context in which the handler will work.
     * @param imageDir directory where this handler should generate images.
     *        If null, an SVGGraphics2DRuntimeException is thrown.
     * @param urlRoot root for the urls that point to images created by this
     *        image handler. If null, then the url corresponding to imageDir
     *        is used.
     */
    public AbstractImageHandlerEncoder(String imageDir, String urlRoot)
        throws SVGGraphics2DIOException {
        if (imageDir == null)
            throw new SVGGraphics2DRuntimeException(ERR_IMAGE_DIR_NULL);

        File imageDirFile = new File(imageDir);
        if (!imageDirFile.exists())
            throw new SVGGraphics2DRuntimeException(ERR_IMAGE_DIR_DOES_NOT_EXIST);

        this.imageDir = imageDir;
        if (urlRoot != null)
            this.urlRoot = urlRoot;
        else {
            try{
                this.urlRoot = imageDirFile.toURL().toString();
            } catch (MalformedURLException e) {
                throw new SVGGraphics2DIOException(ERR_CANNOT_USE_IMAGE_DIR+
                                                   e.getMessage(),
                                                   e);
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
        // Create an buffered image where the image will be drawn
        Dimension size = new Dimension(image.getWidth(null),
                                       image.getHeight(null));
        BufferedImage buf = buildBufferedImage(size);

        Graphics2D g = createGraphics(buf);

        g.drawImage(image, 0, 0, null);
        g.dispose();

        // Save image into file
        saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderedImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Create an buffered image where the image will be drawn
        Dimension size = new Dimension(image.getWidth(), image.getHeight());
        BufferedImage buf = buildBufferedImage(size);

        Graphics2D g = createGraphics(buf);

        g.drawRenderedImage(image, IDENTITY);
        g.dispose();

        // Save image into file
        saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderableImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Create an buffered image where the image will be drawn
        Dimension size = new Dimension((int)Math.ceil(image.getWidth()),
                                       (int)Math.ceil(image.getHeight()));
        BufferedImage buf = buildBufferedImage(size);

        Graphics2D g = createGraphics(buf);

        g.drawRenderableImage(image, IDENTITY);
        g.dispose();

        // Save image into file
        saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    void cacheBufferedImage(Element imageElement,
                            BufferedImage buf,
                            SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {

        ByteArrayOutputStream os;

        if (generatorContext == null)
            throw new SVGGraphics2DRuntimeException(ERR_CONTEXT_NULL);

        try {
            os = new ByteArrayOutputStream();
            // encode the image in memory
            encodeImage(buf, os);
            os.close();
        } catch (IOException e) {
            // should not happen since we do in-memory processing
            throw new SVGGraphics2DIOException(ERR_UNEXPECTED, e);
        }
    
        // ask the cacher for a reference
        String imageFileName = imageCacher.lookup(os,
                                                  buf.getWidth(),
                                                  buf.getHeight(),
                                                  generatorContext);
        
        // set the URL
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF,
                                    urlRoot + "/" + imageFileName);
    }                


    protected void saveBufferedImageToFile(Element imageElement,
                                           BufferedImage buf,
                                           SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        if (generatorContext == null)
            throw new SVGGraphics2DRuntimeException(ERR_CONTEXT_NULL);

        // Create a new file in image directory
        File imageFile = null;

        // While the files we are generating exist, try to create another
        // id that is unique.
        while (imageFile == null) {
            String fileId = generatorContext.idGenerator.generateID(getPrefix());
            imageFile = new File(imageDir, fileId + getSuffix());
            if (imageFile.exists())
                imageFile = null;
        }

        // Encode image here
        encodeImage(buf, imageFile);

        // Update HREF
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, urlRoot + "/" +
                                    imageFile.getName());
    }

    /**
     * @return the suffix used by this encoder. E.g., ".jpg" for
     * ImageHandlerJPEGEncoder
     */
    public abstract String getSuffix();

    /**
     * @return the prefix used by this encoder. E.g., "jpegImage" for
     * ImageHandlerJPEGEncoder
     */
    public abstract String getPrefix();

    /**
     * Derived classes should implement this method and encode the input
     * BufferedImage as needed
     */
    public abstract void encodeImage(BufferedImage buf, OutputStream os)
        throws IOException;
    // NOTE: This breaks super <- sub interface!
    // The (BufferedImage, File) signature used to be abstract.
    // Instead, it is now the (BufferedImage, OutputStream) signature.
    // How serious is this?

    /**
     * This method encodes the BufferedImage to a file
     */
    public void encodeImage(BufferedImage buf, File imageFile)
        throws SVGGraphics2DIOException {
        try {
            OutputStream os = new FileOutputStream(imageFile);
            encodeImage(buf, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new SVGGraphics2DIOException(ERR_WRITE+imageFile.getName());
        }
    }

    /**
     * This method creates a BufferedImage of the right size and type
     * for the derived class.
     */
    public abstract BufferedImage buildBufferedImage(Dimension size);
}
