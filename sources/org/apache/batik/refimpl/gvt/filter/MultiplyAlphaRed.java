/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.CachableRed;

import java.util.List;
import java.util.ArrayList;

import java.awt.Point;
import java.awt.Rectangle;

import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;

import java.awt.color.ColorSpace;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.WritableRaster;


/**
 * This implements a masking operation by multiply the alpha channel of
 * one image by a luminance image (the mask).
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class MultiplyAlphaRed extends AbstractRed {

    /**
     * Multiply the alpha of one image with a mask image.
     * The size of the resultant image is the intersection of the
     * two image bounds.  If you want the end image to be the size
     * of one or the other please use the PadRed operator.
     *
     * @param src   The image to convert to multiply the alpha of
     * @param alpha The mask image to multiply the alpha channel of src
     *              with.
     */
    public MultiplyAlphaRed(CachableRed src, CachableRed alpha) {
        super(makeList(src, alpha),
              makeBounds(src,alpha),
              fixColorModel(src),
              fixSampleModel(src),
              src.getTileGridXOffset(),
              src.getTileGridYOffset(),
              null);
    }

    public WritableRaster copyData(WritableRaster wr) {
        // Get my source.
        CachableRed srcRed   = (CachableRed)getSources().get(0);
        CachableRed alphaRed = (CachableRed)getSources().get(1);

        ColorModel cm = srcRed.getColorModel();
        if (cm.hasAlpha()) {
            // Already has alpha channel so we use it.
            srcRed.copyData(wr);

            cm.coerceData(wr, false);

            Rectangle rgn = wr.getBounds();
            rgn = rgn.intersection(alphaRed.getBounds());
            
            int [] wrData    = null;
            int [] alphaData = null;

            Raster r = alphaRed.getData(rgn);
            int    b = srcRed.getSampleModel().getNumBands()-1;
            int    x = rgn.x;
            int    w = rgn.width;
            for (int y=rgn.y; y<rgn.y+rgn.height; y++) {
                wrData    = wr.getSamples(x, y, w, 1, b, wrData);
                alphaData = r .getSamples(x, y, w, 1, 0, alphaData);
                for (int i=0; i<wrData.length; i++) {
                    wrData[i] = ((wrData[i]&0xFF)*(alphaData[i]&0xFF))>>8;
                }
                wr.setSamples(x, y, w, 1, b, wrData);
            }

            return wr;
        }

        // No alpha in source, so we hide the alpha channel in wr and
        // have our source fill wr with color info...
        int [] bands = new int[wr.getNumBands()-1];
        for (int i=0; i<bands.length; i++)
            bands[i] = i;

        WritableRaster subWr;
        subWr = wr.createWritableChild(wr.getMinX(),  wr.getMinY(), 
                                       wr.getWidth(), wr.getHeight(),
                                       wr.getMinX(),  wr.getMinY(),
                                       bands);

        srcRed.copyData(subWr);

        
        Rectangle rgn = wr.getBounds();
        rgn = rgn.intersection(alphaRed.getBounds());
            

        bands = new int [] { wr.getNumBands()-1 };
        subWr = wr.createWritableChild(rgn.x,     rgn.y, 
                                       rgn.width, rgn.height,
                                       rgn.x,     rgn.y, 
                                       bands);
        alphaRed.copyData(subWr);

        return wr;
    }

    public static List makeList(CachableRed src1, CachableRed src2) {
        List ret = new ArrayList(2);
        ret.add(src1);
        ret.add(src2);
        return ret;
    }

    public static Rectangle makeBounds(CachableRed src1, CachableRed src2) {
        Rectangle r1 = src1.getBounds();
        Rectangle r2 = src2.getBounds();
        return r1.intersection(r2);
    }

    public static SampleModel fixSampleModel(CachableRed src) {
        ColorModel  cm = src.getColorModel();
        SampleModel srcSM = src.getSampleModel();

        if (cm.hasAlpha()) 
            return srcSM;

        int w = srcSM.getWidth();
        int h = srcSM.getHeight();
        int b = srcSM.getNumBands()+1;
        int [] offsets = new int[b];
        for (int i=0; i < b; i++) 
            offsets[i] = i;

        // Really should check DataType range in srcSM...
        return new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
                                               w, h, b, w*b, offsets);
    }

    public static ColorModel fixColorModel(CachableRed src) {
        ColorModel  cm = src.getColorModel();
        if (cm.hasAlpha()) {
            WritableRaster wr = cm.createCompatibleWritableRaster(1,1);
            // We don't want our alpha pre-mult otherwise we would
            // need to update all the image pixels as well.
            // This way we only mess with the alpha channel.
            return cm.coerceData(wr, false);
        }

        int b = src.getSampleModel().getNumBands()+1;
        int [] bits = new int[b];
        for (int i=0; i < b; i++) 
            bits[i] = 8;

        ColorSpace cs = cm.getColorSpace();

        return new ComponentColorModel(cs, bits, true, false, 
                                       Transparency.TRANSLUCENT,
                                       DataBuffer.TYPE_BYTE);
    }

}    
