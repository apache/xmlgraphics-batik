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

/**
 * This is a Grid based implementation of the TileStore.
 * This makes it pretty quick, but it can use a fair amount of
 * memory for large tile grids.
 */

public class TileGrid implements TileStore {
    private static final boolean DEBUG = false;
    private static final boolean COUNT = false;		

    private int xSz, ySz;
    private int minTileX, minTileY;
    private TileLRUMember   [][] rasters=null;
    private TileGenerator source = null;
    private LRUCache      cache = null;

    public TileGrid(int minTileX, int minTileY,
                    int xSz, int ySz, 
                    TileGenerator source,
                    LRUCache cache) {
        this.cache    = cache;
        this.source   = source;
        this.minTileX = minTileX;
        this.minTileY = minTileY;
        this.xSz      = xSz;
        this.ySz      = ySz;

        rasters = new TileLRUMember[ySz][];
    }

    public void setTile(int x, int y, Raster ras) {
        x-= minTileX;
        y-= minTileY;
        if ((x<0) || (x>=xSz)) return;
        if ((y<0) || (y>=ySz)) return;

        TileLRUMember [] row = rasters[y];
        TileLRUMember item;
        if (ras == null) {
            // Clearing entry.
            if (row == null) return;
            item = row[x];
            if (item == null) return;

            row[x] = null;
            cache.remove(item);
            return;
        }
		
        if (row != null) {
            item = row[x];
            if (item == null) {
                item = new TileLRUMember();
                row[x] = item;
            }
        } else {
            row = new TileLRUMember[xSz];
            item = new TileLRUMember();
            row[x] = item;
            rasters[y] = row;
        } 
        item.setRaster(ras);
		
        cache.add(item);

        if (DEBUG) System.out.println("Setting: (" + (x+minTileX) + ", " + 
                                      (y+minTileY) + ")");
    }

    // Returns Raster if the tile is _currently_ in the cache.  
    // If it is not currently in the cache it returns null.
    public Raster getTileNoCompute(int x, int y) {
        x-=minTileX;
        y-=minTileY;
        if ((x<0) || (x>=xSz)) return null;
        if ((y<0) || (y>=ySz)) return null;

        TileLRUMember [] row = rasters[y];
        if (row == null)
            return null;
        TileLRUMember item = row[x];
        if (item == null)
            return null;
        Raster ret = item.retrieveRaster();
        if (ret != null)
            cache.add(item);
        return ret;
    }

    public Raster getTile(int x, int y) {
        x-=minTileX;
        y-=minTileY;
        if ((x<0) || (x>=xSz)) return null;
        if ((y<0) || (y>=ySz)) return null;

        if (DEBUG) System.out.println("Fetching: (" + (x+minTileX) + ", " + 
                                      (y+minTileY) + ")");
        if (COUNT) synchronized (TileGrid.class) { requests++; }

        Raster       ras  = null;
        TileLRUMember [] row  = rasters[y];
        TileLRUMember    item = null;
        if (row != null) {
            item = row[x];
            if (item != null)
                ras = item.retrieveRaster();
            else {
                item = new TileLRUMember();
                row[x] = item;
            }
        } else {
            row = new TileLRUMember[xSz];
            rasters[y] = row;
            item = new TileLRUMember();
            row[x] = item;
        }

        if (ras == null) {
            if (DEBUG) System.out.println("Generating: ("+(x+minTileX)+", "+
                                          (y+minTileY) + ")");
            if (COUNT) synchronized (TileGrid.class) { misses++; }
            ras = source.genTile(x+minTileX, y+minTileY);

            // In all likelyhood the contents of this tile is junk!
            // So don't cache it (returning is probably fine since it
            // won't come back to haunt us...
            if (Thread.currentThread().isInterrupted())
                return ras;

            item.setRaster(ras);
        }

        // Update the item's position in the cache..
        cache.add(item);

        return ras;
    }

    static int requests;
    static int misses;
}
