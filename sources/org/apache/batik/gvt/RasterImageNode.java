/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;
import java.awt.image.renderable.RenderableImage;

/**
 * A graphics node that represents a raster image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface RasterImageNode extends LeafGraphicsNode {

    /**
     * Sets the location of this raster image node.
     * @param newLocation the new location of this raster image node
     */
    void setLocation(Point2D newLocation);

    /**
     * Returns the location of this raster image node.
     * @return the location of this raster image node
     */
    Point2D getLocation();

    /**
     * Sets the size of this raster image node.
     * @param newSize the new size of this raster image node
     */
    void setSize(Dimension2D newSize);

    /**
     * Returns the size of this raster image node.
     * @return the size of this raster image node
     */
    Dimension2D getSize();

    /**
     * Sets the raster image of this raster image node.
     * @param newImage the new raster image of this raster image node
     */
    void setImage(RenderableImage newImage);

    /**
     * Returns the raster image of this raster image node.
     * @return the raster image of this raster image node
     */
    RenderableImage getImage();

}
