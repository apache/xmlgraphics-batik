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



import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.ColorSpaceHintKey;

/**
 * This converts any source into a mask according to the SVG masking rules.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$ */
public class FilterAsAlphaRed extends AbstractRed {

    /**
     * Construct an alpah channel from the given src, according to
     * the SVG masking rules.
     *
     * @param src The image to convert to an alpha channel (mask image)
     */
    public FilterAsAlphaRed(CachableRed src) {
        super(new Any2LumRed(src),src.getBounds(), 
              new ComponentColorModel
                  (ColorSpace.getInstance(ColorSpace.CS_GRAY),
                   new int [] {8}, false, false,
                   Transparency.OPAQUE, 
                   DataBuffer.TYPE_BYTE),
              new PixelInterleavedSampleModel
                  (DataBuffer.TYPE_BYTE, 
                   src.getSampleModel().getWidth(),
                   src.getSampleModel().getHeight(),
                   1, src.getSampleModel().getWidth(),
                   new int [] { 0 }),
              src.getTileGridXOffset(),
              src.getTileGridYOffset(),
              null);

        props.put(ColorSpaceHintKey.PROPERTY_COLORSPACE,
                  ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA);
    }

    public WritableRaster copyData(WritableRaster wr) {
        // Get my source.
        CachableRed srcRed = (CachableRed)getSources().get(0);

        SampleModel sm = srcRed.getSampleModel();
        if (sm.getNumBands() == 1)
            // Already one band of data so we just use it...
            return srcRed.copyData(wr);

        // Two band case so we need to multiply them...
        // Note: Our source will always have either one or two bands
        // since we insert an Any2Lum transform before ourself in the
        // rendering chain.

        Raster srcRas = srcRed.getData(wr.getBounds());
        PixelInterleavedSampleModel srcSM;
        srcSM = (PixelInterleavedSampleModel)srcRas.getSampleModel();

        DataBufferByte srcDB = (DataBufferByte)srcRas.getDataBuffer();
        byte []        src   = srcDB.getData();
        
        PixelInterleavedSampleModel dstSM;
        dstSM = (PixelInterleavedSampleModel)wr.getSampleModel();

        DataBufferByte dstDB = (DataBufferByte)wr.getDataBuffer();
        byte []        dst   = dstDB.getData();

        int srcX0 = srcRas.getMinX()-srcRas.getSampleModelTranslateX();
        int srcY0 = srcRas.getMinY()-srcRas.getSampleModelTranslateY();

        int dstX0 = wr.getMinX()-wr.getSampleModelTranslateX();
        int dstX1 = dstX0+wr.getWidth()-1;
        int dstY0 = wr.getMinY()-wr.getSampleModelTranslateY();

        int    srcStep = srcSM.getPixelStride();
        int [] offsets = srcSM.getBandOffsets();
        int    srcLOff = offsets[0];
        int    srcAOff = offsets[1];

        if (srcRed.getColorModel().isAlphaPremultiplied()) {
            // Lum is already multiplied by alpha so we just copy lum channel.
            for (int y=0; y<srcRas.getHeight(); y++) {
                int srcI  = srcDB.getOffset() + srcSM.getOffset(srcX0,  srcY0);
                int dstI  = dstDB.getOffset() + dstSM.getOffset(dstX0,  dstY0);
                int dstE  = dstDB.getOffset() + dstSM.getOffset(dstX1+1,dstY0);

                srcI += srcLOff; // Go to Lum Channel (already mult by alpha).

                while (dstI < dstE) {
                    dst[dstI++] = src[srcI];
                        srcI += srcStep; // Go to next pixel
                }
                srcY0++;
                dstY0++;
            }
        }
        else {
            // This allows me to pre-adjust my index by srcLOff
            // Then only add the offset for srcAOff
            srcAOff = srcAOff-srcLOff;

            for (int y=0; y<srcRas.getHeight(); y++) {
                int srcI  = srcDB.getOffset() + srcSM.getOffset(srcX0,  srcY0);
                int dstI  = dstDB.getOffset() + dstSM.getOffset(dstX0,  dstY0);
                int dstE  = dstDB.getOffset() + dstSM.getOffset(dstX1+1,dstY0);

                srcI += srcLOff;

                while (dstI < dstE) {
                    int sl = (src[srcI])&0xFF; // LOff already included
                    int sa = (src[srcI+srcAOff])&0xFF;
                    // the + 0x80 forces proper rounding.
                    dst[dstI++] = (byte)((sl*sa+0x80)>>8);

                    srcI+= srcStep; //  next pixel
                }
                srcY0++;
                dstY0++;
            }
        }

        return wr;
    }

}    
