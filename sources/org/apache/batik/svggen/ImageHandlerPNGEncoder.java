/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.*;

import org.apache.batik.ext.awt.image.codec.ImageEncoder;
import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;

import org.w3c.dom.*;

/**
 * This implementation of the abstract AbstractImageHandlerEncoder
 * class creates PNG images in the image directory and sets the
 * url pointing to that file in the xlink:href attributes of the
 * image elements it handles.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 * @see             org.apache.batik.svggen.ImageHandlerJPEGEncoder
 * @see             org.apache.batik.svggen.ImageHandlerPNGEncoder
 */
public class ImageHandlerPNGEncoder extends AbstractImageHandlerEncoder {
    /**
     * @param imageDir directory where this handler should generate images.
     *        If null, an IllegalArgumentException is thrown.
     * @param urlRoot root for the urls that point to images created by this
     *        image handler. If null, then the url corresponding to imageDir
     *        is used.
     */
    public ImageHandlerPNGEncoder(String imageDir, String urlRoot)
        throws SVGGraphics2DIOException {
        super(imageDir, urlRoot);
    }

    /**
     * @return the suffix used by this encoder. E.g., ".png" for
     * ImageHandlerPNGEncoder.
     */
    public final String getSuffix(){
        return ".png";
    }

    /**
     * @return the prefix used by this encoder. E.g., "pngImage" for
     * ImageHandlerPNGEncoder.
     */
    public final String getPrefix(){
        return "pngImage";
    }

    /**
     * Derived classes should implement this method and encode the input
     * BufferedImage as needed
     */
    public void encodeImage(BufferedImage buf, File imageFile)
        throws SVGGraphics2DIOException {
        try {
            OutputStream os = new FileOutputStream(imageFile);
            ImageEncoder encoder = new PNGImageEncoder(os, null);
            encoder.encode(buf);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new SVGGraphics2DIOException(ERR_WRITE+imageFile.getName());
        }
    }

    /**
     * This method creates a BufferedImage with an alpha channel, as this is
     * supported by PNG.
     */
    public BufferedImage buildBufferedImage(Dimension size){
        return new BufferedImage(size.width, size.height,
                                 BufferedImage.TYPE_INT_ARGB);
    }
}
