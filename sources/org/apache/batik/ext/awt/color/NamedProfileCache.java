/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.color;

import java.awt.image.RenderedImage;

import java.net.URL;

import java.util.Map;
import java.util.HashMap;

import java.lang.ref.SoftReference;

import org.apache.batik.util.SoftReferenceCache;

/**
 * This class manages a cache of soft references to named profiles that
 * we have already loaded. 
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */

public class NamedProfileCache extends SoftReferenceCache{

    static NamedProfileCache theCache = new NamedProfileCache();

    public static NamedProfileCache getDefaultCache() { return theCache; }

    /**
     * Let people create there own caches.
     */
    public NamedProfileCache() { }

    /**
     * Check if <tt>request(profileName)</tt> will return with a ICCColorSpaceExt
     * (not putting you on the hook for it).  Note that it is possible
     * that this will return true but between this call and the call
     * to request the soft-reference will be cleared.  So it
     * is still possible for request to return NULL, just much less
     * likely (you can always call 'clear' in that case). 
     */
    public synchronized boolean isPresent(String profileName) {
        return super.isPresentImpl(profileName);
    }

    /**
     * Check if <tt>request(profileName)</tt> will return immediately with the
     * ICCColorSpaceExt.  Note that it is possible that this will return
     * true but between this call and the call to request the
     * soft-reference will be cleared.
     */
    public synchronized boolean isDone(String profileName) {
        return super.isDoneImpl(profileName);
    }

    /**
     * If this returns null then you are now 'on the hook'.
     * to put the ICCColorSpaceExt associated with String into the
     * cache.  */
    public synchronized ICCColorSpaceExt request(String profileName) {
        return (ICCColorSpaceExt)super.requestImpl(profileName);
    }

    /**
     * Clear the entry for String.
     * This is the easiest way to 'get off the hook'.
     * if you didn't indend to get on it.
     */
    public synchronized void clear(String profileName) {
        super.clearImpl(profileName);
    }

    /**
     * Associate bi with profileName.  bi is only referenced through
     * a soft reference so don't rely on the cache to keep it
     * around.  If the map no longer contains our profileName it was
     * probably cleared or flushed since we were put on the hook
     * for it, so in that case we will do nothing.
     */
    public synchronized void put(String profileName, ICCColorSpaceExt bi) {
        super.putImpl(profileName, bi);
    }
}
