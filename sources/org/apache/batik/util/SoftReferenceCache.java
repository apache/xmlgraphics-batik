/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.util;

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
            SoftReference ref = new SoftRefKey(object, key);
            map.put(key, ref);
            this.notifyAll();
        }
    }

    class SoftRefKey extends CleanerThread.SoftReferenceCleared {
        Object key;
        public SoftRefKey(Object o, Object key) {
            super(o);
            this.key = key;
        }

        public void cleared() {
            SoftReferenceCache cache = SoftReferenceCache.this;
            if (cache == null) return; // Can't really happen.
            synchronized (cache) {
                Object o = cache.map.remove(key);
                if (this != o)
                    // Must not have been ours put it back...
                    // Can happen if a clear is done.
                    cache.map.put(key, o);
            }
        }
    }
}
