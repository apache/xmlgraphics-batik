/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * This class manages a cache of soft references to objects that may
 * take some time to load or create, such as images loaded from the 
 * network.
 *
 * Adding an object is two fold: <br />
 * + First you add the key, this lets the cache know that someone is
 *   working on that key.  <br />
 * + Then when the completed object is ready you put it into the cache.<P>
 *
 * If someone requests a key after it has been added but before it has
 * been put they will be blocked until the put.
 */

public class SoftReferenceCache {
    HashMap map = new HashMap();

    /**
     * Let people create there own caches.
     */
    protected SoftReferenceCache() { }


    /**
     * Let people flush the cache (remove any cached data).  Pending
     * requests will be treated as though clear() was called on the
     * key, this should cause them to go and re-read the data.  
     */
    public synchronized void flush() {
        map.clear();
        this.notifyAll();
    }

    /**
     * Check if <tt>request(key)</tt> will return with an Object
     * (not putting you on the hook for it).  Note that it is possible
     * that this will return true but between this call and the call
     * to request the soft-reference will be cleared.  So it
     * is still possible for request to return NULL, just much less
     * likely (you can always call 'clear' in that case). 
     */
    protected final synchronized boolean isPresentImpl(Object key) {
        if (!map.containsKey(key))
            return false;

        Object o = map.get(key);
        if (o == null)  
            // It's been requested but hasn't been 'put' yet.
            return true;

        // It's been put let's make sure the soft reference hasn't
        // been cleared.
        SoftReference sr = (SoftReference)o;
        o = sr.get();
        if (o != null)
            return true;

        // Soft reference was cleared, so remove our record of key.
        clearImpl(key);
        return false;
    }

    /**
     * Check if <tt>request(key)</tt> will return immediately with the
     * Object.  Note that it is possible that this will return
     * true but between this call and the call to request the
     * soft-reference will be cleared.
     */
    protected final synchronized boolean isDoneImpl(Object key) {
        Object o = map.get(key);
        if (o == null) return false;
        SoftReference sr = (SoftReference)o;
        o = sr.get();
        if (o != null)
            return true;

        // Soft reference was cleared
        clearImpl(key);
        return false;
    }

    /**
     * If this returns null then you are now 'on the hook'.
     * to put the Object associated with key into the
     * cache.  */
    protected final synchronized Object requestImpl(Object key) {
        if (map.containsKey(key)) {

            Object o = map.get(key);
            while(o == null) {
                try {
                    // When something is cleared or put we will be notified.
                    wait();
                }
                catch (InterruptedException ie) { }

                // check if key was cleared, if so it will most likely
                // never be 'put'.
                if (!map.containsKey(key))
                    break;

                // Let's see if it was put...
                o = map.get(key);
            }
            if (o != null) {
                SoftReference sr = (SoftReference)o;
                o = sr.get();
                if (o != null)
                    return o;
            }
        }

        // So now the caller get's the hot potato.
        map.put(key, null);
        return null;
    }

    /**
     * Clear the entry for key.
     * This is the easiest way to 'get off the hook'.
     * if you didn't indend to get on it.
     */
    protected final synchronized void clearImpl(Object key) {
        map.remove(key);
        this.notifyAll();
    }

    /**
     * Associate object with key.  'object' is only referenced through
     * a soft reference so don't rely on the cache to keep it
     * around.  If the map no longer contains our url it was
     * probably cleared or flushed since we were put on the hook
     * for it, so in that case we will do nothing.
     */
    protected final synchronized void putImpl(Object key, Object object) {
        if (map.containsKey(key)) {
            SoftReference ref = new SoftReference(object, queue);
            map.put(key, ref);
            synchronized (refMap) {
                refMap.put(ref, new Info(key, this));
            }
            this.notifyAll();
        }
    }

    static class Info {
        Object key;
        SoftReference cacheRef;
        public Info(Object key,
                    SoftReferenceCache cache) {
            this.key = key;
            this.cacheRef = new SoftReference(cache);
        }

        public Object getKey() { return key; }

        public SoftReferenceCache getCache() { 
            return (SoftReferenceCache)cacheRef.get(); 
        }
    }

    private static HashMap        refMap = new HashMap();
    private static ReferenceQueue queue = new ReferenceQueue();
    private static Thread cleanup;

    static {
        cleanup = new Thread() {
                public void run() {
                    while(true) {
                        Reference ref;
                        try {
                            ref = queue.remove();
                        } catch (InterruptedException ie) {
                            continue;
                        }

                        Object o;
                        synchronized (refMap) {
                            o = refMap.remove(ref);
                        }

                        // System.out.println("Cleaning: " + o);
                        if (o == null) continue;
                        Info info = (Info)o;
                        SoftReferenceCache cache = info.getCache();
                        if (cache == null) continue;
                        synchronized (cache) {
                            o = cache.map.remove(info.getKey());
                            if (ref != o)
                                // Must not have been ours put it back...
                                // Can happen if a clear is done.
                                cache.map.put(info.getKey(), o);
                        }
                    }
                }
            };
        cleanup.setDaemon(true);
        cleanup.start();
    }


}
