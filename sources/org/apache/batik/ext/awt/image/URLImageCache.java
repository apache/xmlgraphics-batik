/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SoftReferenceCache;

/**
 * This class manages a cache of soft references to Images that
 * we have already loaded.  Adding an image is two fold.
 * First you add the ParsedURL, this lets the cache know that someone is
 * working on this ParsedURL.  Then when the completed RenderedImage is
 * ready you put it into the cache.<P>
 *
 * If someone requests a ParsedURL after it has been added but before it has
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
     * Check if <tt>request(url)</tt> will return with a Filter
     * (not putting you on the hook for it).  Note that it is possible
     * that this will return true but between this call and the call
     * to request the soft-reference will be cleared.  So it
     * is still possible for request to return NULL, just much less
     * likely (you can always call 'clear' in that case). 
     */
    public synchronized boolean isPresent(ParsedURL purl) {
        return super.isPresentImpl(purl);
    }

    /**
     * Check if <tt>request(url)</tt> will return immediately with the
     * Filter.  Note that it is possible that this will return
     * true but between this call and the call to request the
     * soft-reference will be cleared.
     */
    public synchronized boolean isDone(ParsedURL purl) {
        return super.isDoneImpl(purl);
    }

    /**
     * If this returns null then you are now 'on the hook'.
     * to put the Filter associated with ParsedURL into the
     * cache.  */
    public synchronized Filter request(ParsedURL purl) {
        return (Filter)super.requestImpl(purl);
    }

    /**
     * Clear the entry for ParsedURL.
     * This is the easiest way to 'get off the hook'.
     * if you didn't indend to get on it.
     */
    public synchronized void clear(ParsedURL purl) {
        super.clearImpl(purl);
    }

    /**
     * Associate bi with purl.  bi is only referenced through
     * a soft reference so don't rely on the cache to keep it
     * around.  If the map no longer contains our purl it was
     * probably cleared or flushed since we were put on the hook
     * for it, so in that case we will do nothing.
     */
    public synchronized void put(ParsedURL purl, Filter filt) {
        super.putImpl(purl, filt);
    }
}
