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

package org.apache.batik.ext.awt.image.codec.tiff;

import java.awt.image.RenderedImage;
import java.io.IOException;

import org.apache.batik.ext.awt.image.codec.ImageDecodeParam;
import org.apache.batik.ext.awt.image.codec.ImageDecoderImpl;
import org.apache.batik.ext.awt.image.codec.SeekableStream;

/**
 * A baseline TIFF reader. The reader has some functionality in addition to 
 * the baseline specifications for Bilevel images, for which the group 3 and
 * group 4 decompression schemes have been implemented. Support for LZW 
 * decompression has also been added. Support for Horizontal differencing 
 * predictor decoding is also included, when used with LZW compression. 
 * However, this support is limited to data with bitsPerSample value of 8. 
 * When reading in RGB images, support for alpha and extraSamples being
 * present has been added. Support for reading in images with 16 bit samples
 * has been added. Support for the SampleFormat tag (signed samples as well
 * as floating-point samples) has also been added. In all other cases, support
 * is limited to Baseline specifications.
 *
 *
 */
public class TIFFImageDecoder extends ImageDecoderImpl {

    // All the TIFF tags that we care about
    public static final int TIFF_IMAGE_WIDTH                = 256;
    public static final int TIFF_IMAGE_LENGTH               = 257;
    public static final int TIFF_BITS_PER_SAMPLE            = 258;
    public static final int TIFF_COMPRESSION                = 259;
    public static final int TIFF_PHOTOMETRIC_INTERPRETATION = 262;
    public static final int TIFF_FILL_ORDER                 = 266;
    public static final int TIFF_STRIP_OFFSETS              = 273;
    public static final int TIFF_SAMPLES_PER_PIXEL          = 277;
    public static final int TIFF_ROWS_PER_STRIP             = 278;
    public static final int TIFF_STRIP_BYTE_COUNTS          = 279;
    public static final int TIFF_X_RESOLUTION               = 282;
    public static final int TIFF_Y_RESOLUTION               = 283;
    public static final int TIFF_PLANAR_CONFIGURATION       = 284;
    public static final int TIFF_T4_OPTIONS                 = 292;
    public static final int TIFF_T6_OPTIONS                 = 293;
    public static final int TIFF_RESOLUTION_UNIT            = 296;
    public static final int TIFF_PREDICTOR                  = 317;
    public static final int TIFF_COLORMAP                   = 320;
    public static final int TIFF_TILE_WIDTH                 = 322;
    public static final int TIFF_TILE_LENGTH                = 323;
    public static final int TIFF_TILE_OFFSETS               = 324;
    public static final int TIFF_TILE_BYTE_COUNTS           = 325;
    public static final int TIFF_EXTRA_SAMPLES              = 338;
    public static final int TIFF_SAMPLE_FORMAT              = 339;
    public static final int TIFF_S_MIN_SAMPLE_VALUE         = 340;
    public static final int TIFF_S_MAX_SAMPLE_VALUE         = 341;

    public TIFFImageDecoder(SeekableStream input,
                            ImageDecodeParam param) {
        super(input, param);
    }

    public int getNumPages() throws IOException {
        return TIFFDirectory.getNumDirectories(input);
    }

    public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if  ((page < 0) || (page >= getNumPages())) {
            throw new IOException("TIFFImageDecoder0");
        }
        return new TIFFImage(input, (TIFFDecodeParam)param, page);
    }
}
