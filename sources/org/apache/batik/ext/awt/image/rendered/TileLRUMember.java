/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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

