/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.awt.image.RenderedImage;

import org.apache.batik.gvt.RasterImageNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.Filter;

/**
 * An implementation of the <tt>RasterImageNode</tt> interface.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com>Thomas DeWeese</a>
 * @version $Id$
 */
public class ConcreteRasterImageNode extends AbstractGraphicsNode
        implements RasterImageNode {

    /**
     * The renderable image that represents this image node.
     */
    protected Filter image;

    /**
     * The Bounds of this image node.
     */
    protected Rectangle2D bounds;

    public static AffineTransform IDENTITY = new AffineTransform();

    /**
     * Constructs a new empty raster image node.
     */
    public ConcreteRasterImageNode() {}

    //
    // Properties methods
    //

    public void setImage(Filter newImage) {
        Filter oldImage = image;
        this.image = newImage;
        firePropertyChange("image", oldImage, newImage);
    }

    public Filter getImage() {
        return image;
    }

    public void setBounds(Rectangle2D newBounds) {
        Rectangle2D oldBounds = this.bounds;
        this.bounds = newBounds;
        firePropertyChange("bounds", oldBounds, newBounds);
    }

    public Rectangle2D getBounds() {
        return (Rectangle2D)bounds.clone();
    }

    //
    // Drawing methods
    //
    public boolean hasProgressivePaint() {
        // <!> FIXME : TODO
        return false;
    }

    public void progressivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        // <!> FIXME : TODO
    }

    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if ((image == null)||
            (bounds.getWidth()  == 0) ||
            (bounds.getHeight() == 0)) {
            return;
        }

        // get the current affine transform
        AffineTransform origAt = g2d.getTransform();
        AffineTransform at     = (AffineTransform)origAt.clone();

        float tx0 = image.getMinX();
        float ty0 = image.getMinY();

        float sx  = (float)(bounds.getWidth() /image.getWidth());
        float sy  = (float)(bounds.getHeight()/image.getHeight());

        float tx1 = (float)bounds.getX();
        float ty1 = (float)bounds.getY();

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
            aoi = getBounds();
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

    public Rectangle2D getPrimitiveBounds() {
        return (Rectangle2D)bounds.clone();
    }

    public Rectangle2D getGeometryBounds() {
        return getPrimitiveBounds();
    }

    public Shape getOutline() {
        return getPrimitiveBounds();
    }
}
