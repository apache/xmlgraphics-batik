/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import java.awt.Point;
import java.awt.image.Raster;
import java.util.HashMap;


public class TileMap implements TileStore {
    private static final boolean DEBUG = false;
    private static final boolean COUNT = false;		

    private HashMap rasters=new HashMap();
    private TileGenerator source = null;
    private LRUCache      cache = null;

    public TileMap(TileGenerator source,
                    LRUCache cache) {
        this.cache    = cache;
        this.source   = source;
    }

    public void setTile(int x, int y, Raster ras) {
        Point pt = new Point(x, y);
        Object o = rasters.get(pt);
        TileLRUMember item;
        if (o == null) {
            item = new TileLRUMember(ras);
        } else {
            item = (TileLRUMember)o;
            item.setRaster(ras);
        }
		
        if (item.lruGet() != null) cache.touch(item);
        else                       cache.add(item);
        if (DEBUG) System.out.println("Setting: (" + x + ", " + y + ")");
    }

    // Returns true if the tile is _currently_ in the cache.  This
    // may not be true by the time you get around to calling
    // getTile however...
    public boolean checkTile(int x, int y) {
        Point pt = new Point(x, y);
        Object o = rasters.get(pt);
        if (o == null) 
            return false;

        TileLRUMember item = (TileLRUMember)o;
        return item.checkRaster();
    }

    public Raster getTile(int x, int y) {
        if (DEBUG) System.out.println("Fetching: (" + (x) + ", " + 
                                      (y) + ")");
        if (COUNT) synchronized (TileMap.class) { requests++; }

        Raster       ras  = null;
        Point pt = new Point(x, y);
        Object o = rasters.get(pt);
        TileLRUMember item;
        if (o == null) {
            item = new TileLRUMember();
            rasters.put(pt, item);
        } else {
            item = (TileLRUMember)o;
            ras = item.retrieveRaster();
        }
		
        if (ras == null) {
            if (DEBUG) System.out.println("Generating: ("+(x)+", "+
                                          (y) + ")");
            if (COUNT) synchronized (TileMap.class) { misses++; }
            ras = source.genTile(x, y);
            item.setRaster(ras);
        }

        // Update the item's position in the cache..
        cache.add(item);

        return ras;
    }

    static int requests;
    static int misses;
}
