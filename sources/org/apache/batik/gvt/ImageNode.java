/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;

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

    //
    // Properties methods
    //

    /**
     * Sets the graphics node that represents the image.
     *
     * @param newImage the new graphics node that represents the image
     */
    public void setImage(GraphicsNode newImage) {
        getChildren().add(0, newImage);
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
