/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import java.io.InputStream;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.codec.PNGRed;
import org.apache.batik.ext.awt.image.codec.PNGDecodeParam;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.util.ParsedURL;

public class PNGRegistryEntry 
    extends MagicNumberRegistryEntry {


    static final byte [] signature = {(byte)0x89, 80, 78, 71, 13, 10, 26, 10};

    public PNGRegistryEntry() {
        super("PNG", "png", 0, signature);
    }

    /**
     * Decode the Stream into a RenderableImage
     *
     * @param is The input stream that contains the image.
     * @param origURL The original URL, if any, for documentation
     *                purposes only.  This may be null.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  */
    public Filter handleStream(InputStream inIS, 
                               ParsedURL   origURL,
                               boolean needRawData) {

        final DeferRable  dr  = new DeferRable();
        final InputStream is  = inIS;
        final boolean     raw = needRawData;
        final String      errCode;
        final Object []   errParam;
        if (origURL != null) {
            errCode  = ERR_URL_FORMAT_UNREADABLE;
            errParam = new Object[] {"PNG", origURL};
        } else {
            errCode  = ERR_STREAM_FORMAT_UNREADABLE;
            errParam = new Object[] {"PNG"};
        }

        Thread t = new Thread() {
                public void run() {
                    Filter filt;
                    try {
                        PNGDecodeParam param = new PNGDecodeParam();
                        param.setExpandPalette(true);
                        
                        if (raw) 
                            param.setPerformGammaCorrection(false);
                        else {
                            param.setPerformGammaCorrection(true);
                            param.setDisplayExponent(2.2f); // sRGB gamma
                        }
                        CachableRed cr = new PNGRed(is, param);
                        cr = new Any2sRGBRed(cr);
                        filt = new RedRable(cr);
                    } catch (IOException ioe) {
                        filt = ImageTagRegistry.getBrokenLinkImage
                            (errCode, errParam);
                    } catch (Throwable t) {
                        filt = ImageTagRegistry.getBrokenLinkImage
                            (errCode, errParam);
                    }

                    dr.setSource(filt);
                }
            };
        t.start();
        return dr;
    }
}
