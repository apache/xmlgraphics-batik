/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;

/**
 * A renderable that can tile its source into the tile region.
 * 
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface TileRable extends FilterColorInterpolation {
    /**
     * Returns the tile region
     */
    public Rectangle2D getTileRegion();

    /**
     * Sets the tile region
     */
    public void setTileRegion(Rectangle2D tileRegion);

    /**
     * Returns the tiled region
     */
    public Rectangle2D getTiledRegion();

    /**
     * Sets the tile region
     */
    public void setTiledRegion(Rectangle2D tiledRegion);

    /**
     * Returns whether or not the source can overflow
     * the tile region or if the tile region should clip
     * the source
     */
    public boolean isOverflow();

    /**
     * Sets the overflow strategy
     */
    public void setOverflow(boolean overflow);

    /**
     * Sets the filter source (the tile content used to fill the 
     * tile region.
     */
    public void setSource(Filter source);

    /**
     * Return's the tile source (the tile content used to fill
     * the tile region.
     */
    public Filter getSource();
}
