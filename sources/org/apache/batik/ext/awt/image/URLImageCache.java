/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

import java.awt.image.RenderedImage;

import java.net.URL;

import java.util.Map;
import java.util.HashMap;

import java.lang.ref.SoftReference;

import org.apache.batik.util.SoftReferenceCache;

/**
 * This class manages a cache of soft references to Images that
 * we have already loaded.  Adding an image is two fold.
 * First you add the URL, this lets the cache know that someone is
 * working on this URL.  Then when the completed RenderedImage is
 * ready you put it into the cache.<P>
 *
 * If someone requests a URL after it has been added but before it has
 * been put they will be blocked until the put.
 */

public class URLImageCache extends SoftReferenceCache{

    static URLImageCache theCache = new URLImageCache();

    public static URLImageCache getDefaultCache() { return theCache; }

    /**
     * Let people create there own caches.
     */
    public URLImageCache() { }

    /**
     * Check if <tt>request(url)</tt> will return with a RenderedImage
     * (not putting you on the hook for it).  Note that it is possible
     * that this will return true but between this call and the call
     * to request the soft-reference will be cleared.  So it
     * is still possible for request to return NULL, just much less
     * likely (you can always call 'clear' in that case). 
     */
    public synchronized boolean isPresent(URL url) {
        return super.isPresentImpl(url);
    }

    /**
     * Check if <tt>request(url)</tt> will return immediately with the
     * RenderedImage.  Note that it is possible that this will return
     * true but between this call and the call to request the
     * soft-reference will be cleared.
     */
    public synchronized boolean isDone(URL url) {
        return super.isDoneImpl(url);
    }

    /**
     * If this returns null then you are now 'on the hook'.
     * to put the RenderedImage associated with URL into the
     * cache.  */
    public synchronized RenderedImage request(URL url) {
        return (RenderedImage)super.requestImpl(url);
    }

    /**
     * Clear the entry for URL.
     * This is the easiest way to 'get off the hook'.
     * if you didn't indend to get on it.
     */
    public synchronized void clear(URL url) {
        super.clearImpl(url);
    }

    /**
     * Associate bi with url.  bi is only referenced through
     * a soft reference so don't rely on the cache to keep it
     * around.  If the map no longer contains our url it was
     * probably cleared or flushed since we were put on the hook
     * for it, so in that case we will do nothing.
     */
    public synchronized void put(URL url, RenderedImage bi) {
        super.putImpl(url, bi);
    }
}
