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
public class ConcreteImageNode extends ConcreteCompositeGraphicsNode
        implements ImageNode {

    /**
     * Constructs a new empty image node.
     */
    public ConcreteImageNode() {}

    //
    // Properties methods
    //

    public void setImage(GraphicsNode newImage) {
        getChildren().add(0, newImage);
    }

    public GraphicsNode getImage() {
        if (count > 0) {
            return children[0];
        } else {
            return null;
        }
    }
}
