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
     * The Bounds of this image node.
     */
    protected Rectangle2D imageBounds;

    protected AffineTransform img2usr, usr2img;
    
    protected boolean calcAffine = true;

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
        calcAffine = true;
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
        calcAffine = true;
    }

    /**
     * Returns the bounds of this raster image node.
     * @return the bounds of this raster image node
     */
    public Rectangle2D getImageBounds() {
        return (Rectangle2D) imageBounds.clone();
    }

    protected void updateAffine() {
        
        float tx0 = image.getMinX();
        float ty0 = image.getMinY();

        float sx  = (float)(imageBounds.getWidth() /image.getWidth());
        float sy  = (float)(imageBounds.getHeight()/image.getHeight());

        float tx1 = (float)imageBounds.getX();
        float ty1 = (float)imageBounds.getY();

        // Make the affine go from our src Img's coord system to
        // the device coord system, including scaling to our bounds.
        img2usr = AffineTransform.getTranslateInstance          ( tx1,  ty1);
        img2usr.concatenate(AffineTransform.getScaleInstance    ( sx ,  sy ));
        img2usr.concatenate(AffineTransform.getTranslateInstance(-tx0, -ty0));

        usr2img = AffineTransform.getTranslateInstance          ( tx0,  ty0);
        usr2img.concatenate(AffineTransform.getScaleInstance    (1/sx, 1/sy));
        usr2img.concatenate(AffineTransform.getTranslateInstance(-tx1, -ty1));

        calcAffine = false;
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

        if (calcAffine) 
            updateAffine();

        // get the current affine transform
        rc.setTransform(img2usr);

        rc.setAreaOfInterest(null);

        GraphicsUtil.drawImage(g2d, image, rc);

        // Restore default rendering attributes
        rc.setTransform     (g2d.getTransform());
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
