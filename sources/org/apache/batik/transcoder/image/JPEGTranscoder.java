/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;

/**
 * This class is an <tt>ImageTranscoder</tt> that produces a JPEG image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class JPEGTranscoder extends ImageTranscoder {

    /**
     * Constructs a new transcoder that produces jpeg images.
     */
    public JPEGTranscoder() {
        // the default background color for jpeg images
        hints.put(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white);
    }

    /**
     * Creates a new ARGB image with the specified dimension.
     * @param width the image width in pixels
     * @param height the image height in pixels
     */
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Writes the specified image to the specified output.
     * @param img the image to write
     * @param output the output where to store the image
     * @param TranscoderException if an error occured while storing the image
     */
    public void writeImage(BufferedImage img, TranscoderOutput output)
            throws TranscoderException {

    }
}
