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

package org.apache.batik.ext.awt.image.rendered;

import  java.awt.image.Raster;
import  java.lang.ref.Reference;
import  java.lang.ref.SoftReference;

/**
 * This is a useful class that wraps a Raster for patricipation in
 * an LRU Cache.  When this object drops out of the LRU cache it
 * removes it's hard reference to the tile, but retains it's soft
 * reference allowing for the recovery of the tile when the JVM is
 * not under memory pressure
 */
public class TileLRUMember implements LRUCache.LRUObj {
    private static final boolean DEBUG = false;
			
	protected LRUCache.LRUNode myNode  = null;
	protected Reference        wRaster = null;
	protected Raster           hRaster = null;

	public TileLRUMember() { }

	public TileLRUMember(Raster ras) { 
	    setRaster(ras);
	}

	public void setRaster(Raster ras) {
	    hRaster = ras;
	    wRaster = new SoftReference(ras);
	}

	public boolean checkRaster() {
	    if (hRaster != null) return true;

	    if ((wRaster       != null) && 
            (wRaster.get() != null)) return true;
			
	    return false;
	}

	public Raster retrieveRaster() {
	    if (hRaster != null) return hRaster;
	    if (wRaster == null) return null;

	    hRaster = (Raster)wRaster.get();

	    if (hRaster == null)  // didn't manage to retrieve it...
            wRaster = null;

	    return hRaster;
	}

	public LRUCache.LRUNode lruGet()         { return myNode; }
	public void lruSet(LRUCache.LRUNode nde) { myNode = nde; }
	public void lruRemove()                  { 
	    myNode  = null; 
	    hRaster = null;
	    if (DEBUG) System.out.println("Removing");
	}
}

