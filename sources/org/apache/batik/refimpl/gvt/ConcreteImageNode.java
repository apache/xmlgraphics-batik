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
        if (image != null) {
            image.primitivePaint(g2d, rc);
        }
    }

    //
    // Geometric methods
    //

    public Rectangle2D getPrimitiveBounds() {
        if (image == null) {
            return null;
        } else {
            return image.getPrimitiveBounds();
        }
    }

    public Shape getOutline() {
        if (image == null) {
            return null;
        } else {
            return image.getOutline();
        }
    }

    public Rectangle2D getGeometryBounds(){
        if (image == null) {
            return null;
        } else {
            return image.getGeometryBounds();
        }
    }
}
