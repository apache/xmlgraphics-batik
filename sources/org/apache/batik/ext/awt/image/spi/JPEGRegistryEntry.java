/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGCodec;

import java.io.InputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.renderable.DeferRable;

public class JPEGRegistryEntry 
    extends MagicNumberRegistryEntry {

    static final byte [] signature = {(byte)0xFF, (byte)0xd8, 
                                      (byte)0xFF, (byte)0xe0};
    static final String [] exts    = {"jpeg", "jpg" };
    public JPEGRegistryEntry() {
        super("JPEG", exts, 0, signature);
    }

    /**
     * Decode the Stream into a RenderableImage
     *
     * @param is The input stream that contains the image.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  
     */
    public Filter handleStream(InputStream inIS, boolean needRawData) {

        final DeferRable  dr  = new DeferRable();
        final InputStream is  = inIS;

        Thread t = new Thread() {
                public void run() {
                    Filter filt;
                    try{
                        JPEGImageDecoder decoder;
                        decoder = JPEGCodec.createJPEGDecoder(is);
                        BufferedImage image;
                        image   = decoder.decodeAsBufferedImage();
                        filt = new RedRable(GraphicsUtil.wrap(image));
                    } catch (IOException ioe) {
                        // Something bad happened here...
                        filt = ImageTagRegistry.getBrokenLinkImage();
                    }

                    dr.setSource(filt);
                }
            };
        t.start();
        return dr;
    }
}
