/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;

/**
 * Fills the input image with a given paint
 *
 * @author <a href="mailto:dean@w3.org">Dean Jackson</a>
 * @version $Id$
 */

public interface FloodRable extends Filter {
    /**
     * Set the flood paint.
     * @param paint the flood paint to use when filling
     */
    public void setFloodPaint(Paint paint);

    /**
     * Get the flood paint.
     * @return The current flood paint for the filter
     */
    public Paint getFloodPaint();

    /**
     * Sets the flood region
     * @param floodRegion region to flood with floodPaint
     */
    public void setFloodRegion(Rectangle2D floodRegion);
    
    /**
     * Get the flood region
     */
     public Rectangle2D getFloodRegion();
}


