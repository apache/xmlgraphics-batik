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

import com.sun.image.codec.jpeg.*;

import org.w3c.dom.*;

/**
 * This implementation of the abstract AbstractImageHandlerEncoder
 * class creates JPEG images in the image directory and sets the
 * url pointing to that file in the xlink:href attributes of the
 * image elements it handles.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 * @see             org.apache.batik.svggen.ImageHandlerJPEGEncoder
 * @see             org.apache.batik.svggen.ImageHandlerPNGEncoder
 */
public class ImageHandlerJPEGEncoder extends AbstractImageHandlerEncoder {
    /**
     * @param imageDir directory where this handler should generate images.
     *        If null, an IllegalArgumentException is thrown.
     * @param urlRoot root for the urls that point to images created by this
     *        image handler. If null, then the url corresponding to imageDir
     *        is used.
     */
    public ImageHandlerJPEGEncoder(String imageDir, String urlRoot) {
        super(imageDir, urlRoot);
    }

    /**
     * @return the suffix used by this encoder. E.g., ".jpg" for
     *  ImageHandlerJPEGEncoder
     */
    public final String getSuffix(){
        return ".jpg";
    }

    /**
     * @return the prefix used by this encoder. E.g., "jpegImage" for
     * ImageHandlerJPEGEncoder
     */
    public final String getPrefix(){
        return "jpegImage";
    }

    /**
     * Derived classes should implement this method and encode the input
     * BufferedImage as needed
     */
    public void encodeImage(BufferedImage buf, File imageFile) {
        try{
            OutputStream os = new FileOutputStream(imageFile);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(buf);
            param.setQuality(1, false);
            encoder.encode(buf, param);
            os.flush();
            os.close();
        }catch(IOException e){
            throw new Error("Could not write imageFile: " + imageFile.getName());
        }
    }

    /**
     * This method creates a BufferedImage of the right size and type
     * for the derived class.
     */
    public BufferedImage buildBufferedImage(Dimension size) {
        return new BufferedImage(size.width, size.height,
                                 BufferedImage.TYPE_INT_RGB);
    }
}
