/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;

import java.util.List;

/**
 * Interface for GVT Renderers that render into raster images.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface ImageRenderer extends Renderer{
    /**
     * Update the required size of the offscreen buffer.
     */
    public void updateOffScreen(int width, int height);

    /**
     * Get the Current offscreen buffer used for rendering
     */
    public BufferedImage getOffScreen();

    /**
     * Tells renderer to clear current contents of offscreen buffer
     */
    public void clearOffScreen();

    /**
     * Flush any cached image data (preliminary interface).
     */
    public void flush();

    /**
     * Flush a rectangle of cached image data (preliminary interface).
     */
    public void flush(Rectangle r);

    /**
     * Flush a list of rectangles of cached image data (preliminary
     * interface). Each area are transformed via the usr2dev's renderer
     * transform before the flush(Rectangle) is called.
     */
    public void flush(List areas);
}
