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

import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;


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

    public boolean is_INT_PACK_BYTE_COMP(SampleModel srcSM,
                                         SampleModel alpSM) {
          // Check SampleModel types DirectColorModel
        if(!(srcSM instanceof SinglePixelPackedSampleModel)) return false;
        if(!(alpSM instanceof ComponentSampleModel))         return false;

        // Check transfer types
        if(srcSM.getDataType() != DataBuffer.TYPE_INT)       return false;
        if(alpSM.getDataType() != DataBuffer.TYPE_BYTE)      return false;


        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)srcSM;

        int [] masks = sppsm.getBitMasks();
        if(masks.length != 4) return false;
        if(masks[0] != 0x00ff0000) return false;
        if(masks[1] != 0x0000ff00) return false;
        if(masks[2] != 0x000000ff) return false;
        if(masks[3] != 0xff000000) return false;
 
        ComponentSampleModel csm;
        csm = (ComponentSampleModel)alpSM;
        if (csm.getNumBands()    != 1) return false;
        if (csm.getPixelStride() != 1) return false;

        return true;
   }

    public WritableRaster INT_PACK_BYTE_COMP_Impl (WritableRaster wr) {
          // Get my source.
        CachableRed srcRed   = (CachableRed)getSources().get(0);
        CachableRed alphaRed = (CachableRed)getSources().get(1);

        // Already has alpha channel so we use it.
        srcRed.copyData(wr);

        Rectangle rgn = wr.getBounds();
        rgn = rgn.intersection(alphaRed.getBounds());
            
        Raster r = alphaRed.getData(rgn);

        ComponentSampleModel csm;
        csm = (ComponentSampleModel)r.getSampleModel();
        final int alpScanStride = csm.getScanlineStride();

        DataBufferByte alpDB   = (DataBufferByte)r.getDataBuffer();
        final int      alpBase 
            = (alpDB.getOffset() + 
               csm.getOffset(rgn.x-r.getSampleModelTranslateX(), 
                             rgn.y-r.getSampleModelTranslateY()));

            
          // Access the pixel data array
        final byte alpPixels[] = alpDB.getBankData()[0];

        SinglePixelPackedSampleModel sppsm;
        sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        final int srcScanStride = sppsm.getScanlineStride();

        DataBufferInt srcDB   = (DataBufferInt)wr.getDataBuffer();
        final int     srcBase 
            = (srcDB.getOffset() + 
               sppsm.getOffset(rgn.x-wr.getSampleModelTranslateX(), 
                               rgn.y-wr.getSampleModelTranslateY()));

          // Access the pixel data array
        final int srcPixels[] = srcDB.getBankData()[0];

        ColorModel cm = srcRed.getColorModel();

        if (cm.isAlphaPremultiplied()) {
            // For alpha premult we need to multiply all comps.
            for (int y=0; y<rgn.height; y++) {
                int sp = srcBase + y*srcScanStride;
                int ap = alpBase + y*alpScanStride;
                int end = sp + rgn.width;

                while (sp<end) {
                    int a = ((int)alpPixels[ap++])&0xFF;
                    final int pix = srcPixels[sp];
                    srcPixels[sp] = 
                        ((((((pix>>>24)     ) *a)&0xFF00)<<16) |
                         (((((pix>>>16)&0xFF) *a)&0xFF00)<<8 ) |
                         (((((pix>>> 8)&0xFF) *a)&0xFF00)    ) |
                         (((((pix     )&0xFF) *a)&0xFF00)>>8 ));
                    sp++;
                }
            }
                
        } else {
              // For non-alpha premult we only need to multiply alpha.
            for (int y=0; y<rgn.height; y++) {
                int sp = srcBase + y*srcScanStride;
                int ap = alpBase + y*alpScanStride;
                int end = sp + rgn.width;
                while (sp<end) {
                    int a = ((int)alpPixels[ap++])&0xFF;
                    int sa = srcPixels[sp]>>>24;
                    srcPixels[sp] = ((((sa*a) & 0xFF00)<<16)|
                                     srcPixels[sp]&0x00FFFFFF);
                    sp++;
                }
            }
        }

        return wr;
    }

    public WritableRaster copyData(WritableRaster wr) {
        // Get my source.
        CachableRed srcRed   = (CachableRed)getSources().get(0);
        CachableRed alphaRed = (CachableRed)getSources().get(1);

        if (is_INT_PACK_BYTE_COMP(srcRed.getSampleModel(),
                                  alphaRed.getSampleModel()))
            return INT_PACK_BYTE_COMP_Impl(wr);

        ColorModel cm = srcRed.getColorModel();
        if (cm.hasAlpha()) {
            // Already has alpha channel so we use it.
            srcRed.copyData(wr);

            Rectangle rgn = wr.getBounds();
            if (rgn.intersects(alphaRed.getBounds()))
                rgn = rgn.intersection(alphaRed.getBounds());
            else 
                return wr;
            
            int [] wrData    = null;
            int [] alphaData = null;

            Raster r = alphaRed.getData(rgn);
            int    w = rgn.width;

            final int bands = wr.getSampleModel().getNumBands();

            if (cm.isAlphaPremultiplied()) {
                for (int y=rgn.y; y<rgn.y+rgn.height; y++) {
                    wrData    = wr.getPixels (rgn.x, y, w, 1, wrData);
                    alphaData = r .getSamples(rgn.x, y, w, 1, 0, alphaData);
                    int i=0, a, b;
                          // 4 is the most common case.  
                          // 2 is probably next most common...
                    switch (bands) {
                    case 2: 
                        for (int x=0; x<alphaData.length; x++) {
                            a = alphaData[x]&0xFF;
                            wrData[i] = ((wrData[i]&0xFF)*a)>>8; ++i;
                            wrData[i] = ((wrData[i]&0xFF)*a)>>8; ++i;
                        }
                        break;
                    case 4: 
                        for (int x=0; x<alphaData.length; x++) {
                            a = alphaData[x]&0xFF;
                            wrData[i] = ((wrData[i]&0xFF)*a)>>8; ++i;
                            wrData[i] = ((wrData[i]&0xFF)*a)>>8; ++i;
                            wrData[i] = ((wrData[i]&0xFF)*a)>>8; ++i;
                            wrData[i] = ((wrData[i]&0xFF)*a)>>8; ++i;
                        }
                        break;
                    default:
                        for (int x=0; x<alphaData.length; x++) {
                            a = alphaData[x]&0xFF;
                            for (b=0; b<bands; b++) {
                                wrData[i] = ((wrData[i]&0xFF)*a)>>8; 
                                ++i;
                            }
                        }
                    }
                    wr.setPixels(rgn.x, y, w, 1, wrData);
                }
            } else {
                int b = srcRed.getSampleModel().getNumBands()-1;
                for (int y=rgn.y; y<rgn.y+rgn.height; y++) {
                    wrData    = wr.getSamples(rgn.x, y, w, 1, b, wrData);
                    alphaData = r .getSamples(rgn.x, y, w, 1, 0, alphaData);
                    for (int i=0; i<wrData.length; i++) {
                        wrData[i] = ((wrData[i]&0xFF)*(alphaData[i]&0xFF))>>8;
                    }
                    wr.setSamples(rgn.x, y, w, 1, b, wrData);
                }
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

        if (cm.hasAlpha())
            return cm;

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
