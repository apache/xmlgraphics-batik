/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.transcoder;

import java.awt.Color;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.batik.transcoder.TranscodingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An <tt>ImageTranscoder</tt> that produces a jpeg image. The default
 * background color is white.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class JpegTranscoder extends ImageTranscoder {

    /**
     * Constructs a new jpeg transcoder.
     */
    public JpegTranscoder(){
        hints.put(BatikHints.KEY_BACKGROUND, Color.white);
    }

    /**
     * Creates a new image of type RGB.
     * @param w the width of the image
     * @param h the height of the image
     */
    public BufferedImage createImage(int w, int h){
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Writes the specified image as a jpeg to the specified ouput stream.
     * @param img the image to write
     * @param ostream the output stream where to write the image
     */
    public void writeImage(BufferedImage img, OutputStream ostream)
            throws IOException {
        JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(ostream);
        JPEGEncodeParam params = JPEGCodec.getDefaultJPEGEncodeParam(img);
        params.setQuality(1f, true);
        jpegEncoder.encode(img, params);
    }

    /**
     * Returns the jpeg mime type <tt>image/jpeg</tt>.
     */
    public String getMimeType() {
        return "image/jpeg";
    }
}
