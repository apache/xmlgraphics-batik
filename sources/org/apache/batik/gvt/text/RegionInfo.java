/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.gvt.text;

import java.awt.geom.Rectangle2D;

/**
 * This class holds the neccessary information to render a
 * <batik:regin> that is defined within the <batik:flowRegion>
 * element.  Namely it holds the bounds of the region and the desired
 * vertical alignment.
 */
public class RegionInfo
       extends Rectangle2D.Float
{
    private float verticalAlignment = 0.0f;

    public RegionInfo(float x, float y, float w, float h, 
                      float verticalAlignment) {
        super(x, y, w, h);
        this.verticalAlignment = verticalAlignment;
    }

    /**
     * Gets the vertical alignment for this flow region.
     * @return the vertical alignment for this flow region. 
     *         It will be 0.0 for top, 0.5 for middle and 1.0 for bottom.
     */
    public float getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Sets the alignment position of the text within this flow region.  
     * The value must be 0.0 for top, 0.5 for middle and 1.0 for bottom.
     * @param verticalAlignment the vertical alignment of the text.
     */
    public void setVerticalAlignment(float verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }
}
