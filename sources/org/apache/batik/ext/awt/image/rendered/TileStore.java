package org.apache.batik.ext.awt.image.rendered;

import  java.awt.image.Raster;


/**
 * This the generic interface for a TileStore.  This is used to
 * store and retrieve tiles from the cache.
 */
public interface TileStore {
    
    public void setTile(int x, int y, Raster ras);

    // Returns true if the tile is _currently_ in the cache.  This
    // may not be true by the time you get around to calling
    // getTile however...
    public boolean checkTile(int x, int y);

    public Raster getTile(int x, int y);
}
