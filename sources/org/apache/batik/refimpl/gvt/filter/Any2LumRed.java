/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.filter;

import org.apache.batik.gvt.filter.CachableRed;

import java.awt.Point;
import java.awt.Rectangle;

import java.awt.Transparency;
import java.awt.color.ColorSpace;

import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.WritableRaster;

/**
 * This function will tranform an image from any colorspace into a
 * luminance image.  The alpha channel if any will be copied to the
 * new image.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class Any2LumRed extends AbstractRed {

    /**
     * Construct a luminace image from src.
     *
     * @param src The image to convert to a luminance image
     */
    public Any2LumRed(CachableRed src) {
        super(src,src.getBounds(), 
              fixColorModel(src),
              fixSampleModel(src),
              src.getTileGridXOffset(),
              src.getTileGridYOffset(),
              null);

        props.put(FilterAsAlphaRable.PROPERTY_COLORSPACE,
                  FilterAsAlphaRable.VALUE_COLORSPACE_GREY);
    }


    public WritableRaster copyData(WritableRaster wr) {
        // Get my source.
        CachableRed src = (CachableRed)getSources().get(0);

        SampleModel sm = src.getSampleModel();
        ColorModel  cm = src.getColorModel();
        if (cm == null) {
            // We don't really know much about this source.

            float [][] matrix = null;
            if (sm.getNumBands() == 2) {
                matrix = new float[2][2];
                matrix[0][0] = 1;
                matrix[1][1] = 1;
            } else {
                matrix = new float[sm.getNumBands()][1];
                matrix[0][0] = 1;
            }

            Raster srcRas = src.getData(wr.getBounds());
            BandCombineOp op = new BandCombineOp(matrix, null);
            op.filter(srcRas, wr);
        } else {
            // REVIEW: Alpha handling may not be correct through here.
            // Since the colorconversion may not be a linear op it
            // is probably required to divide out the alpha before
            // doing the color conversion.
            //
            // This might be especially tricky since there are bugs
            // in the ColorConvert Ops handling of alpha...

            ColorConvertOp op = new ColorConvertOp(null);
            Raster srcRas = src.getData(wr.getBounds());
            Point pt = new Point(srcRas.getMinX(), srcRas.getMinY());

            WritableRaster srcWr = (WritableRaster)srcRas;
            // srcWr = Raster.createWritableRaster(srcRas.getSampleModel(),
            //                                     srcRas.getDataBuffer(),
            //                                     pt);

            BufferedImage srcBI, dstBI;
            srcBI = new BufferedImage(cm, 
                                      srcWr.createWritableTranslatedChild(0,0),
                                      cm.isAlphaPremultiplied(), 
                                      null);

            // All this nonsense is to work around the fact that the
            // Color convert op doesn't properly copy the Alpha from
            // src to dst.
            PixelInterleavedSampleModel dstSM;
            dstSM = (PixelInterleavedSampleModel)wr.getSampleModel();
            SampleModel smna = new PixelInterleavedSampleModel
                (dstSM.getDataType(),    
                 dstSM.getWidth(),       dstSM.getHeight(),
                 dstSM.getPixelStride(), dstSM.getScanlineStride(),
                 new int [] { 0 });

            WritableRaster dstWr;
            dstWr = Raster.createWritableRaster(smna,
                                                wr.getDataBuffer(),
                                                new Point(0,0));

            ColorModel cmna = new ComponentColorModel
                (ColorSpace.getInstance(ColorSpace.CS_GRAY),
                 new int [] {8}, false, false,
                 Transparency.OPAQUE, 
                 DataBuffer.TYPE_BYTE);

            dstBI = new BufferedImage(cmna, dstWr, false, null);
            op.filter(srcBI, dstBI);

            // I never have to 'fix' alpha premult since I take
            // it's value from my source....
            if (cm.hasAlpha() && getColorModel().hasAlpha())
                copyBand(srcWr, sm.getNumBands()-1,
                         wr, dstSM.getNumBands()-1);
        }
        return wr;
    }

    protected static void copyBand(Raster         src, int srcBand, 
                                   WritableRaster dst, int dstBand) {
        Rectangle srcR = new Rectangle(src.getMinX(),  src.getMinY(),
                                       src.getWidth(), src.getHeight());
        Rectangle dstR = new Rectangle(dst.getMinX(),  dst.getMinY(),
                                       dst.getWidth(), dst.getHeight());

        Rectangle cpR  = srcR.intersection(dstR);
        System.out.println("In CopyBand(" + srcBand + ", " + dstBand + ", " +
                           cpR + ", " + src.getBounds() + ", " + dst.getBounds());

        int [] samples = null;
        for (int y=cpR.y; y< cpR.y+cpR.height; y++) {
            samples = src.getSamples(cpR.x, y, cpR.width, 1, srcBand, samples);
            dst.setSamples(cpR.x, y, cpR.width, 1, dstBand, samples);
        }
    }

        /**
         * This function 'fixes' the source's color model.  Right now
         * it just selects if it should have one or two bands based on
         * if the source had an alpha channel.
         */
    protected static ColorModel fixColorModel(CachableRed src) {
        ColorModel  cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha())
                return new ComponentColorModel
                    (ColorSpace.getInstance(ColorSpace.CS_GRAY),
                     new int [] {8,8}, true,
                     cm.isAlphaPremultiplied(),
                     Transparency.TRANSLUCENT, 
                     DataBuffer.TYPE_BYTE);

            return new ComponentColorModel
                (ColorSpace.getInstance(ColorSpace.CS_GRAY),
                 new int [] {8}, false, false,
                 Transparency.OPAQUE, 
                 DataBuffer.TYPE_BYTE);
        } 
        else {
            // No ColorModel so try to make some intelligent
            // decisions based just on the number of bands...
            // 1 bands -> lum
            // 2 bands -> lum (Band 0) & alpha (Band 1)
            // >2 bands -> lum (Band 0) - No color conversion...
            SampleModel sm = src.getSampleModel();

            if (sm.getNumBands() == 2)
                return new ComponentColorModel
                    (ColorSpace.getInstance(ColorSpace.CS_GRAY),
                     new int [] {8,8}, true,
                     cm.isAlphaPremultiplied(),
                     Transparency.TRANSLUCENT, 
                     DataBuffer.TYPE_BYTE);

            return new ComponentColorModel
                (ColorSpace.getInstance(ColorSpace.CS_GRAY),
                 new int [] {8}, false, false,
                 Transparency.OPAQUE, 
                 DataBuffer.TYPE_BYTE);
        }
    }

    /**
     * This function 'fixes' the source's sample model.
     * Right now it just selects if it should have one or two bands
     * based on if the source had an alpha channel.
     */
    protected static SampleModel fixSampleModel(CachableRed src) {
        SampleModel sm = src.getSampleModel();

        int width  = sm.getWidth();
        int height = sm.getHeight();

        ColorModel  cm = src.getColorModel();
        if (cm != null) {
            if (cm.hasAlpha()) 
                return new PixelInterleavedSampleModel
                    (DataBuffer.TYPE_BYTE, width, height, 2, 2*width,
                     new int [] { 0, 1 });

            return new PixelInterleavedSampleModel
                (DataBuffer.TYPE_BYTE, width, height, 1, width,
                 new int [] { 0 });
        }
        else {
            // No ColorModel so try to make some intelligent
            // decisions based just on the number of bands...
            // 1 bands -> lum
            // 2 bands -> lum (Band 0) & alpha (Band 1)
            // >2 bands -> lum (Band 0) - No color conversion...
            if (sm.getNumBands() == 2)
                return new PixelInterleavedSampleModel
                    (DataBuffer.TYPE_BYTE, width, height, 2, 2*width,
                     new int [] { 0, 1 });

            return new PixelInterleavedSampleModel
                (DataBuffer.TYPE_BYTE, width, height, 1, width,
                 new int [] { 0 });
        }
    }
}
