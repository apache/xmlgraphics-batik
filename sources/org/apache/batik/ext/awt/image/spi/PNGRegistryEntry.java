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

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.codec.PNGRed;
import org.apache.batik.ext.awt.image.GraphicsUtil;

public class PNGRegistryEntry 
    extends MagicNumberRegistryEntry {


    static final byte [] signature = {(byte)0x89, 80, 78, 71, 13, 10, 26, 10};

    public PNGRegistryEntry() {
        super("PNG", "png", 0, signature);
    }

    public Filter handleStream(InputStream inIS) {
        final DeferRable dr = new DeferRable();
        final InputStream is = inIS;
	
        Thread t = new Thread() {
                public void run() {
                    Filter filt;
                    try {
                        filt = new RedRable(new PNGRed(is));
                    } catch (IOException ioe) {
                        filt = getBrokenLinkImage();
                    }

                    dr.setSource(filt);
                }
            };
        t.start();
        return dr;
    }
}
