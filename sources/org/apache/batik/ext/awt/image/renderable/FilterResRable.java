/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderableImage;

/**
 * Interface for implementing filter resolution.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface FilterResRable extends Filter {
    /**
     * Returns the source to be cropped.
     */
    public Filter getSource();

    /**
     * Sets the source to be cropped
     * @param src image to offset.
     */
    public void setSource(Filter src);

    /**
     * Returns the resolution along the X axis.
     */
    public int getFilterResolutionX();

    /**
     * Sets the resolution along the X axis, i.e., the maximum
     * size for intermediate images along that axis.
     * The value should be greater than zero to have an effect.
     */
    public void setFilterResolutionX(int filterResolutionX);

    /**
     * Returns the resolution along the Y axis.
     */
    public int getFilterResolutionY();

    /**
     * Sets the resolution along the Y axis, i.e., the maximum
     * size for intermediate images along that axis.
     * The value should be greater than zero to have an effect.
     */
    public void setFilterResolutionY(int filterResolutionY);

}
