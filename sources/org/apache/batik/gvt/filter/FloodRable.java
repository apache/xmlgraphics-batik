/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.filter;

import java.awt.Color;
import java.awt.Shape;

/**
 * Fills the input image with a given color
 *
 * @author <a href="mailto:dean@w3.org">Dean Jackson</a>
 * @version $Id$
 */

public interface FloodRable extends Filter {
    /**
     * Set the flood color.
     * @param color the flood color to use when filling
     */
    public void setFloodColor(Color color);

    /**
     * Get the flood color.
     * @return The current flood color for the filter
     */
    public Color getFloodColor();

    /**
     * Sets the flood region
     * @param floodRegion region to flood with floodColor
     */
    public void setFloodRegion(FilterRegion floodRegion);
    
    /**
     * Get the flood region
     */
     public FilterRegion getFloodRegion();
}


