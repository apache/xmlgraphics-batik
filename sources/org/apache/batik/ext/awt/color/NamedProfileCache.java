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

package org.apache.batik.ext.awt.color;

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
