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
     * The Bounds of this image node.
     */
    protected Rectangle2D imageBounds;

    /**
     * Constructs a new empty <tt>RasterImageNode</tt>.
     */
    public RasterImageNode() {}

    //
    // Properties methods
    //

    /**
     * Sets the raster image of this raster image node.
     * @param newImage the new raster image of this raster image node
     */
    public void setImage(Filter newImage) {
        invalidateGeometryCache();
        this.image = newImage;
    }

    /**
     * Returns the raster image of this raster image node.
     * @return the raster image of this raster image node
     */
    public Filter getImage() {
        return image;
    }

    /**
     * Sets the bounds of this raster image node.
     * @param newBounds the new bounds of this raster image node
     */
    public void setImageBounds(Rectangle2D newImageBounds) {
        invalidateGeometryCache();
        this.imageBounds = newImageBounds;
    }

    /**
     * Returns the bounds of this raster image node.
     * @return the bounds of this raster image node
     */
    public Rectangle2D getImageBounds() {
        return (Rectangle2D) imageBounds.clone();
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node without applying Filter, Mask, Composite and clip.
     *
     * @param g2d the Graphics2D to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if ((image == null)||
            (imageBounds.getWidth()  == 0) ||
            (imageBounds.getHeight() == 0)) {
            return;
        }

        // get the current affine transform
        AffineTransform origAt = g2d.getTransform();
        AffineTransform at     = (AffineTransform)origAt.clone();

        float tx0 = image.getMinX();
        float ty0 = image.getMinY();

        float sx  = (float)(imageBounds.getWidth() /image.getWidth());
        float sy  = (float)(imageBounds.getHeight()/image.getHeight());

        float tx1 = (float)imageBounds.getX();
        float ty1 = (float)imageBounds.getY();

        // Make the affine go from our src Img's coord system to
        // the device coord system, including scaling to our bounds.
        at.concatenate(AffineTransform.getTranslateInstance(tx1, ty1));
        at.concatenate(AffineTransform.getScaleInstance    (sx, sy));
        at.concatenate(AffineTransform.getTranslateInstance(-tx0, -ty0));

        AffineTransform usr2src = new AffineTransform();
        usr2src.concatenate(AffineTransform.getTranslateInstance(tx0, ty0));
        usr2src.concatenate(AffineTransform.getScaleInstance    (1/sx, 1/sy));
        usr2src.concatenate(AffineTransform.getTranslateInstance(-tx1, -ty1));

        Shape aoi = g2d.getClip();
        if(aoi == null) {
            aoi = getImageBounds();
        }

        Shape newAOI = usr2src.createTransformedShape(aoi);
        rc.setTransform(at);
        rc.setAreaOfInterest(newAOI);
        RenderedImage renderedNodeImage = image.createRendering(rc);

        if(renderedNodeImage != null){
            g2d.setTransform(IDENTITY);
            g2d.drawRenderedImage(renderedNodeImage, IDENTITY);
        }

        // Restore default rendering attributes
        g2d.setTransform(origAt);
        rc.setTransform(origAt);
        rc.setAreaOfInterest(g2d.getClip());
    }

    //
    // Geometric methods
    //

    /**
     * Returns the primitive bounds in user space of this text node.
     */
    public Rectangle2D getPrimitiveBounds(GraphicsNodeRenderContext rc) {
        return (Rectangle2D) imageBounds.clone();
    }

    /**
     * Returns the geometric bounds in user space of this text node.
     */
    public Rectangle2D getGeometryBounds(GraphicsNodeRenderContext rc) {
        return (Rectangle2D) imageBounds.clone();
    }

    /**
     * Returns a shape which matches the text's geometry.
     */
    public Shape getOutline(GraphicsNodeRenderContext rc) {
        return (Rectangle2D) imageBounds.clone();
    }
}
