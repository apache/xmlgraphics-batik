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
 * An <tt>ImageTranscoder</tt> that produces a png image.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class PngTranscoder extends ImageTranscoder {

    public PngTranscoder(){
        /*hints.put(TranscodingHints.KEY_BACKGROUND,
          new Color(0, 0, 0, 0));*/
    }


    protected BufferedImage createImage(int w, int h){
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    protected void writeImage(BufferedImage img, OutputStream ostream)
            throws IOException {
        // PNGEncodeParam params = PNGEncodeParam.getDefaultEncodeParam(img);
        PNGImageEncoder pngEncoder = new PNGImageEncoder(ostream, null);
        // params.setQuality(1f, true);
        pngEncoder.encode(img);
    }

    public String getMimeType() {
        return "image/png";
    }

}
