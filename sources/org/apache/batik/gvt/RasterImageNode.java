/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.GraphicsUtil;

/**
 * A graphics node that represents a raster image.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class RasterImageNode extends AbstractGraphicsNode {

    /**
     * The renderable image that represents this image node.
     */
    protected Filter image;

    /**
     * Constructs a new empty <tt>RasterImageNode</tt>.
     */
    public RasterImageNode() {}

    //
    // Properties methods
    //

    /**
     * Sets the raster image of this raster image node.
     *
     * @param newImage the new raster image of this raster image node
     */
    public void setImage(Filter newImage) {
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        this.image = newImage;
        fireGraphicsNodeChangeCompleted();
    }

    /**
     * Returns the raster image of this raster image node.
     *
     * @return the raster image of this raster image node
     */
    public Filter getImage() {
        return image;
    }

    /**
     * Returns the bounds of this raster image node.
     *
     * @return the bounds of this raster image node
     */
    public Rectangle2D getImageBounds() {
        return (Rectangle2D) image.getBounds2D().clone();
    }

    /**
     * Returns the RenderableImage for this node.  The returned
     * RenderableImage this node before any of the filter operations
     * have been applied.  
     */
    public Filter getGraphicsNodeRable() {
        return image;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node without applying Filter, Mask, Composite and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d) {
        if (image == null) return;

        GraphicsUtil.drawImage(g2d, image);
    }

    //
    // Geometric methods
    //

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds() {
        return image.getBounds2D();
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into account. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example.
     */
    public Rectangle2D getGeometryBounds() {
        return image.getBounds2D();
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline() {
        return image.getBounds2D();
    }
}
