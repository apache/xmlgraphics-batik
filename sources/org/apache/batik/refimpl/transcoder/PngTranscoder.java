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
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
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
