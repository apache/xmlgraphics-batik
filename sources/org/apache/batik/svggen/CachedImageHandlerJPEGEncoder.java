/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.image.BufferedImage;
import org.w3c.dom.*;

/**
 * Caching version of the JPEG image handler.
 *
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 */
public class CachedImageHandlerJPEGEncoder extends ImageHandlerJPEGEncoder {

    /**
     * @param imageDir directory where this handler should generate images.
     *        If null, an IllegalArgumentException is thrown.
     * @param urlRoot root for the urls that point to images created by this
     *        image handler. If null, then the url corresponding to imageDir
     *        is used.
     */
    public CachedImageHandlerJPEGEncoder(String imageDir, String urlRoot)
        throws SVGGraphics2DIOException {
        super(imageDir, urlRoot);
        setImageCacher(new ImageCacher.External(imageDir,
                                                getPrefix(),
                                                getSuffix()));
    }

    /**
     * Save with caching.
     */
    protected void saveBufferedImageToFile(Element imageElement,
                                           BufferedImage buf,
                                           SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        cacheBufferedImage(imageElement, buf, generatorContext);
    }                
}
