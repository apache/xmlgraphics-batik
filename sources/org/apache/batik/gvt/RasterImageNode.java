/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;

/**
 * A graphics node that represents a raster image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public interface RasterImageNode extends LeafGraphicsNode {
    /**
     * Sets the bounds of this raster image node.
     * @param newBounds the new bounds of this raster image node
     */
    void setImageBounds(Rectangle2D newBounds);

    /**
     * Returns the bounds of this raster image node.
     * @return the bounds of this raster image node
     */
    Rectangle2D getImageBounds();

    /**
     * Sets the raster image of this raster image node.
     * @param newImage the new raster image of this raster image node
     */
    void setImage(Filter newImage);

    /**
     * Returns the raster image of this raster image node.
     * @return the raster image of this raster image node
     */
    Filter getImage();
}
