package org.apache.batik.ext.awt.image.rendered;

import  java.awt.image.Raster;
import  java.lang.ref.WeakReference;

/**
 * This is a useful class that wraps a Raster for patricipation in
 * an LRU Cache.  When this object drops out of the LRU cache it
 * removes it's hard reference to the tile, but retains it's soft
 * reference allowing for the recovery of the tile when the JVM is
 * not under memory pressure
 */
public class TileLRUMember implements LRUCache.LRUObj {
    private static final boolean DEBUG = false;
			
	private LRUCache.LRUNode myNode  = null;
	private WeakReference    wRaster = null;
	private Raster           hRaster = null;

	public TileLRUMember() { }

	public TileLRUMember(Raster ras) { 
	    setRaster(ras);
	}

	public void setRaster(Raster ras) {
	    hRaster = ras;
	    wRaster = new WeakReference(ras);
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

