/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.spi;

import org.apache.batik.util.ParsedURL;
import org.apache.batik.ext.awt.image.renderable.Filter;

/**
 * This type of Image tag registy entry is used for 'odd' URL types.
 * Ussually this means that the URL uses a non-standard protocol.  In
 * these cases you should be aware that in order for the construction
 * of the URL object to succeed you must register a @see
 * URLStreamHandler using one of the methods listed in 
 * @see java.net.URL#URL(java.lang.String, java.lang.String, int, java.lang.String).
 *  */
public interface URLRegistryEntry extends RegistryEntry {
    /**
     * Check if the URL references an image that can be
     * handled by this format handler.  Generally speaking
     * this should not open the URL.  The decision should
     * be based on the structure of the URL (such as
     * the protocol in use).
     *
     * If you don't care about the structure of the URL and only about
     * the contents of the URL you should register as a
     * StreamRegistryEntry, so the URL "connection" will be made
     * only once.  
     */
    public boolean isCompatibleURL(ParsedURL url);

    /**
     * Decode the URL into a RenderableImage, here you should feel
     * free to open the URL yourself.
     *
     * @param url The url that reference the image.
     * @param needRawData If true the image returned should not have
     *                    any default color correction the file may 
     *                    specify applied.  
     */
    public Filter handleURL(ParsedURL url, boolean needRawData);
}
