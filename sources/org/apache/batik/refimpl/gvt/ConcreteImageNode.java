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
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * An implementation of the <tt>ImageNode</tt> interface.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ConcreteImageNode extends AbstractGraphicsNode
        implements ImageNode {

    /**
     * The graphics node that represents this image node.
     */
    protected GraphicsNode image;
    /**
     * The location of this image node.
     */
    protected Point2D location;
    /**
     * The size of this image node.
     */
    protected Dimension2D size;

    /**
     * Constructs a new empty image node.
     */
    public ConcreteImageNode() {}

    //
    // Properties methods
    //

    public void setImage(GraphicsNode newImage) {
        GraphicsNode oldImage = image;
        this.image = newImage;
        firePropertyChange("image", oldImage, newImage);
    }

    public GraphicsNode getImage() {
        return image;
    }

    public void setLocation(Point2D newLocation) {
        Point2D oldLocation = location;
        this.location = newLocation;
        firePropertyChange("location", oldLocation, newLocation);
    }

    public Point2D getLocation() {
        return (Point2D) location.clone();
    }

    public void setSize(Dimension2D newSize) {
        Dimension2D oldSize = size;
        this.size = newSize;
        firePropertyChange("size", oldSize, newSize);
    }

    public Dimension2D getSize() {
        return (Dimension2D) size.clone();
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
        // <!> FIXME : TODO
    }

    //
    // Geometric methods
    //

    public Rectangle2D getBounds() {
        // <!> FIXME : TODO
        return null;
    }

    public Rectangle2D getRenderBounds() {
        // <!> FIXME : TODO
        return null;
    }

    public Shape getOutline() {
        // <!> FIXME : TODO
        return null;
    }
}
