/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.awt.geom.Rectangle2D;
import org.apache.batik.gvt.GraphicsNode;

/**
 * An event which indicates that a modification occurred in a graphics
 * node. The node has to be repainted.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class GraphicsNodePaintEvent extends GraphicsNodeEvent {

    /**
     * The id for the "graphicsNodeModified" event.
     */
    public static final int GRAPHICS_NODE_MODIFIED = 1;

    /** The old bounds of the source. */
    protected Rectangle2D oldBounds;

    /**
     * Constructs a new graphics node paint event.
     * @param source the graphics node where the event originated
     * @param id the id of this event
     * @param oldBounds the old bounds of the graphics node
     */
    public GraphicsNodePaintEvent(GraphicsNode source, int id,
                                  Rectangle2D oldBounds) {
        super(source, id);
        this.oldBounds = oldBounds;
    }

    /**
     * Returns the old bounds of the modified graphics node.
     */
    public Rectangle2D getOldBounds() {
        return oldBounds;
    }

}
