/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.resources.Messages;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFEncodeParam;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFImageEncoder;
import org.apache.batik.ext.awt.image.rendered.FormatRed;
import org.apache.batik.ext.awt.image.GraphicsUtil;


/**
 * This class is an <tt>ImageTranscoder</tt> that produces a TIFF image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TIFFTranscoder extends ImageTranscoder {

    /**
     * Constructs a new transcoder that produces tiff images.
     */
    public TIFFTranscoder() { }

    /**
     * Creates a new ARGB image with the specified dimension.
     * @param width the image width in pixels
     * @param height the image height in pixels
     */
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Writes the specified image to the specified output.
     * @param img the image to write
     * @param output the output where to store the image
     * @param TranscoderException if an error occured while storing the image
     */
    public void writeImage(BufferedImage img, TranscoderOutput output)
            throws TranscoderException {

        OutputStream ostream = output.getOutputStream();
        if (ostream == null) {
            throw new TranscoderException(
                Messages.formatMessage("tiff.badoutput", null));
        }
        TIFFEncodeParam params = new TIFFEncodeParam();

        //
        // This is a trick so that viewers which do not support
        // the alpha channel will see a white background (and not
        // a black one).
        //
        int w = img.getWidth(), h = img.getHeight();
        DataBufferInt biDB = (DataBufferInt)img.getRaster().getDataBuffer();
        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)img.getSampleModel();
        int scanStride = sppsm.getScanlineStride();
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
        try {
            TIFFImageEncoder tiffEncoder = 
                new TIFFImageEncoder(ostream, params);
            int bands = sppsm.getNumBands();
            int [] off = new int[bands];
            for (int i=0; i<bands; i++)
                off[i] = i;
            SampleModel sm = new PixelInterleavedSampleModel
                (DataBuffer.TYPE_BYTE, w, h, bands, w*bands, off);
            
            RenderedImage rimg = new FormatRed(GraphicsUtil.wrap(img), sm);
            tiffEncoder.encode(rimg);
        } catch (IOException ex) {
            throw new TranscoderException(ex);
        }
    }
}
