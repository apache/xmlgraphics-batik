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
 * This the generic interface for a source of tiles.  This is used
 * when the cache has a miss.
 */
public interface TileGenerator {
	public Raster genTile(int x, int y);
}
