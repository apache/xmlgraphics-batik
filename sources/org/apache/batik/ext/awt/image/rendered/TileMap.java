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

import java.awt.Point;
import java.awt.image.Raster;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.apache.batik.util.CleanerThread;

public class TileMap implements TileStore {
    private static final boolean DEBUG = false;
    private static final boolean COUNT = false;		

    private HashMap rasters=new HashMap();

    static class TileMapLRUMember extends TileLRUMember {
        public Point   pt;
        public SoftReference parent;

        class RasterSoftRef extends CleanerThread.SoftReferenceCleared {
            RasterSoftRef(Object o) { super(o); }
            public void cleared() {
                if (DEBUG) System.err.println("Cleaned: " + this);
                TileMap tm = (TileMap)parent.get();
                if (tm != null)
                    tm.rasters.remove(pt);
            }
        };

        TileMapLRUMember(TileMap parent, Point pt, Raster ras) {
            super(ras);
            this.parent = new SoftReference(parent);
            this.pt     = pt;
        }

        public void setRaster(Raster ras) {
            hRaster = ras;
            wRaster = new RasterSoftRef(ras);
        }
    }

    private TileGenerator source = null;
    private LRUCache      cache = null;

    public TileMap(TileGenerator source,
		   LRUCache cache) {
        this.cache    = cache;
        this.source   = source;
    }

    public void setTile(int x, int y, Raster ras) {
        Point pt = new Point(x, y);

        if (ras == null) {
            // Clearing entry...
            Object o = rasters.remove(pt);
            if (o != null) 
                cache.remove((TileMapLRUMember)o);
            return;
        }

        Object o = rasters.get(pt);
        TileMapLRUMember item;
        if (o == null) {
            item = new TileMapLRUMember(this, pt, ras);
            rasters.put(pt, item);
        } else {
            item = (TileMapLRUMember)o;
            item.setRaster(ras);
        }
		
        cache.add(item);
        if (DEBUG) System.out.println("Setting: (" + x + ", " + y + ")");
    }

    // Returns Raster if the tile is _currently_ in the cache.  
    // If it is not currently in the cache it returns null.
    public Raster getTileNoCompute(int x, int y) {
        Point pt = new Point(x, y);
        Object o = rasters.get(pt);
        if (o == null) 
            return null;

        TileMapLRUMember item = (TileMapLRUMember)o;
        Raster ret = item.retrieveRaster();
        if (ret != null)
            cache.add(item);
        return ret;
    }

    public Raster getTile(int x, int y) {
        if (DEBUG) System.out.println("Fetching: (" + (x) + ", " + 
                                      (y) + ")");
        if (COUNT) synchronized (TileMap.class) { requests++; }

        Raster       ras  = null;
        Point pt = new Point(x, y);
        Object o = rasters.get(pt);
        TileMapLRUMember item = null;
        if (o != null) {
            item = (TileMapLRUMember)o;
            ras = item.retrieveRaster();
        }
		
        if (ras == null) {
            if (DEBUG) System.out.println("Generating: ("+(x)+", "+
                                          (y) + ")");
            if (COUNT) synchronized (TileMap.class) { misses++; }
            ras = source.genTile(x, y);

            // In all likelyhood the contents of this tile is junk!
            // So don't cache it (returning is probably fine since it
            // shouldn't come back to haunt us...)
            if (Thread.currentThread().isInterrupted())
                return ras;

            if (item != null)
                item.setRaster(ras);
            else  {
                item = new TileMapLRUMember(this, pt, ras);
                rasters.put(pt, item);
            }
        }

        // Update the item's position in the cache..
        cache.add(item);

        return ras;
    }

    static int requests;
    static int misses;
}
