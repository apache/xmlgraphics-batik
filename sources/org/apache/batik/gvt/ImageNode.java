/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * A graphics node that represents an image described as a graphics node.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ImageNode extends CompositeGraphicsNode {

    /**
     * Constructs a new empty <tt>ImageNode</tt>.
     */
    public ImageNode() {}

    /**
     * Paints this node.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d) {
        if (isVisible) {
            super.paint(g2d);
        }
    }

    /**
     * Returns true if the specified Point2D is inside the boundary of this
     * node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     */
    public boolean contains(Point2D p) {
        switch(pointerEventType) {
        case VISIBLE_PAINTED:
        case VISIBLE_FILL:
        case VISIBLE_STROKE:
        case VISIBLE:
            return isVisible && super.contains(p);
        case PAINTED:
        case FILL:
        case STROKE:
        case ALL:
            return super.contains(p);
        case NONE:
            return false;
        default:
            return false;
        }
    }

    /**
     * Returns the GraphicsNode containing point p if this node or one of its
     * children is sensitive to mouse events at p.
     *
     * @param p the specified Point2D in the user space
     */
    public GraphicsNode nodeHitAt(Point2D p) {
        return (contains(p) ? super.nodeHitAt(p) : null);
    }

    //
    // Properties methods
    //

    /**
     * Sets the graphics node that represents the image.
     *
     * @param newImage the new graphics node that represents the image
     */
    public void setImage(GraphicsNode newImage) {
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        if (count == 0) ensureCapacity(1);
        children[0] = newImage;
        ((AbstractGraphicsNode)newImage).setParent(this);
        ((AbstractGraphicsNode)newImage).setRoot(getRoot());
        count=1;
        fireGraphicsNodeChangeCompleted();
    }

    /**
     * Returns the graphics node that represents the image.
     */
    public GraphicsNode getImage() {
        if (count > 0) {
            return children[0];
        } else {
            return null;
        }
    }
}
