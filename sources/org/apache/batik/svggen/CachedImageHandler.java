/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;


/**
 * Extends the default ImageHandler interface with calls to
 * allow caching of raster images in generated SVG content.
 *
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 */
public interface CachedImageHandler extends GenericImageHandler {
    /**
     * Returns the image cache instance in use by this handler
     *
     * @return the image cache
     */ 
   public ImageCacher getImageCacher();
    
}
