/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
