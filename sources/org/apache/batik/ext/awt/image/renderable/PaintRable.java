/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.Graphics2D;

/**
 * Interface for Rable's that can more efficently represent there
 * action as a paint method instead of a RenderedImage when going to a
 * Graphics2D anyways.
 */
public interface PaintRable {
    /**
     * Should perform the equivilent action as 
     * createRendering followed by drawing the RenderedImage.
     *
     * @param g2d The Graphics2D to draw to.
     * @return true if the paint call succeeded, false if
     *         for some reason the paint failed (in which 
     *         case a createRendering should be used).
     */
    public boolean paintRable(Graphics2D g2d);
}
