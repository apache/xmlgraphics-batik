/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.transcoder;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An <tt>ImageTranscoder</tt> that produces a jpeg image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class JpegTranscoder extends ImageTranscoder {

    protected void writeImage(BufferedImage img, OutputStream ostream)
            throws IOException {
        JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(ostream);
        JPEGEncodeParam params = JPEGCodec.getDefaultJPEGEncodeParam(img);
        params.setQuality(1f, true);
        jpegEncoder.encode(img, params);
    }

    public String getMimeType() {
        return "image/jpeg";
    }
}
