/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.transcoder;

import java.awt.Color;

import org.apache.batik.util.awt.image.codec.PNGImageEncoder;
import org.apache.batik.util.awt.image.codec.PNGEncodeParam;
import org.apache.batik.util.awt.image.codec.ImageEncoder;
import org.apache.batik.transcoder.TranscodingHints;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An <tt>ImageTranscoder</tt> that produces a png image. The default
 * background color is transparent.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class PngTranscoder extends ImageTranscoder {

    /**
     * Constructs a new png transcoder.
     */
    public PngTranscoder(){
    }

    /**
     * Creates a new image of type ARGB with the specified dimension.
     * @param w the width of the image
     * @param h the height of the image
     */
    public BufferedImage createImage(int w, int h) {
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        return bi;
    }

    /**
     * Writes the specified image as a png to the specified ouput stream.
     * @param img the image to write
     * @param ostream the output stream where to write the image
     */
    public void writeImage(BufferedImage img, OutputStream ostream)
            throws IOException {
        PNGEncodeParam.RGB params =
            (PNGEncodeParam.RGB)PNGEncodeParam.getDefaultEncodeParam(img);
        params.setBackgroundRGB(new int[] { 255, 255, 255 });

        //
        // This is a trick so that viewers which do not support
        // the alpha channel will see a white background (and not 
        // a black one).
        //
        int w = img.getWidth(), h = img.getHeight();
        DataBufferInt biDB = (DataBufferInt)img.getRaster().getDataBuffer();
        int scanStride = ((SinglePixelPackedSampleModel)img.getSampleModel()).getScanlineStride();
        int dbOffset = biDB.getOffset();
        int pixels[] = biDB.getBankData()[0];
        int p = dbOffset;
        int adjust = scanStride - w;
        int a=0, r=0, g=0, b=0, pel=0;
        for(int i=0; i<h; i++){
            for(int j=0; j<w; j++){
                pel = pixels[p];
                a = (pel >> 24) & 0xff;
                r = (pel >> 16) & 0xff;
                g = (pel >> 8 ) & 0xff;
                b =  pel        & 0xff;
                r = (255*(255 -a) + a*r)/255;
                g = (255*(255 -a) + a*g)/255;
                b = (255*(255 -a) + a*b)/255;
                pixels[p++] =
                            (a<<24 & 0xff000000) |
                            (r<<16 & 0xff0000) |
                            (g<<8  & 0xff00) |
                            (b     & 0xff);
             }
            p += adjust;
        }

        PNGImageEncoder pngEncoder = new PNGImageEncoder(ostream, params);
        pngEncoder.encode(img);
    }

    /**
     * Returns the png mime type <tt>image/png</tt>.
     */
    public String getMimeType() {
        return "image/png";
    }
}
