/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.rendered;

import  java.awt.image.Raster;


/**
 * This the generic interface for a TileStore.  This is used to
 * store and retrieve tiles from the cache.
 */
public interface TileStore {
    
    public void setTile(int x, int y, Raster ras);

    public Raster getTile(int x, int y);

    // This is return the tile if it is available otherwise
    // returns null.  It will not compute the tile if it is
    // not present.
    public Raster getTileNoCompute(int x, int y);
}
