/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.ext.awt.image.rendered;


import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.ColorSpaceHintKey;
import org.apache.batik.ext.awt.image.GraphicsUtil;

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

        props.put(ColorSpaceHintKey.PROPERTY_COLORSPACE,
                  ColorSpaceHintKey.VALUE_COLORSPACE_GREY);
    }

    public WritableRaster copyData(WritableRaster wr) {
        // Get my source.
        CachableRed src = (CachableRed)getSources().get(0);

        SampleModel sm = src.getSampleModel();
        ColorModel  srcCM = src.getColorModel();
        if (srcCM == null) {
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
            Raster         srcRas = src.getData(wr.getBounds());
            WritableRaster srcWr  = (WritableRaster)srcRas;

            // Divide out alpha if we have it.  We need to do this since
            // the color convert may not be a linear operation which may 
            // lead to out of range values.
            if (srcCM.hasAlpha())
                GraphicsUtil.coerceData(srcWr, srcCM, false);

            BufferedImage srcBI, dstBI;
            srcBI = new BufferedImage(srcCM, 
                                      srcWr.createWritableTranslatedChild(0,0),
                                      false, 
                                      null);
            ColorModel dstCM = getColorModel();
            if (!dstCM.hasAlpha()) {
                // No alpha ao we don't have to work around the bug
                // in the color convert op.
                dstBI = new BufferedImage
                    (dstCM, wr.createWritableTranslatedChild(0,0),
                     dstCM.isAlphaPremultiplied(), null);
            } else {
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
                dstWr = dstWr.createWritableChild
                    (wr.getMinX()-wr.getSampleModelTranslateX(),
                     wr.getMinY()-wr.getSampleModelTranslateY(),
                     wr.getWidth(), wr.getHeight(),
                     0, 0, null);
                
                ColorModel cmna = new ComponentColorModel
                    (ColorSpace.getInstance(ColorSpace.CS_GRAY),
                     new int [] {8}, false, false,
                     Transparency.OPAQUE, 
                     DataBuffer.TYPE_BYTE);

                dstBI = new BufferedImage(cmna, dstWr, false, null);
            }

            ColorConvertOp op = new ColorConvertOp(null);
            op.filter(srcBI, dstBI);

            // I never have to 'fix' alpha premult since I take
            // it's value from my source....
            if (dstCM.hasAlpha())
                copyBand(srcWr, sm.getNumBands()-1,
                         wr,    getSampleModel().getNumBands()-1);
        }
        return wr;
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
                     true, Transparency.TRANSLUCENT, 
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
