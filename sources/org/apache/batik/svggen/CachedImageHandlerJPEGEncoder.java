/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * GenericImageHandler which caches JPEG images.
 *
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 */
public class CachedImageHandlerJPEGEncoder extends DefaultCachedImageHandler {
    public static final String CACHED_JPEG_PREFIX = "jpegImage";
    public static final String CACHED_JPEG_SUFFIX = ".jpg";

    protected String refPrefix = "";
     
    /**
     * @param imageDir directory where this handler should generate images.
     *        If null, an IllegalArgumentException is thrown.
     * @param urlRoot root for the urls that point to images created by this
     *        image handler. If null, then the url corresponding to imageDir
     *        is used.
     */
    public CachedImageHandlerJPEGEncoder(String imageDir, String urlRoot)
        throws SVGGraphics2DIOException {
        refPrefix = urlRoot + "/";
        setImageCacher(new ImageCacher.External(imageDir,
                                                CACHED_JPEG_PREFIX,
                                                CACHED_JPEG_SUFFIX));
    }
   
    /**
     * Uses JPEG encoding.
     */
    public void encodeImage(BufferedImage buf, OutputStream os)
        throws IOException {
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(buf);
        param.setQuality(1, false);
        encoder.encode(buf, param);
    }

    public int getBufferedImageType(){
        return BufferedImage.TYPE_INT_RGB;
    }

    public String getRefPrefix(){
        return refPrefix;
    }
}
