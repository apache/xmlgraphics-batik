/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;

/**
 * A low-level event which indicates that a composite graphics node's contents
 * changed because a graphics node was added or removed.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class CompositeGraphicsNodeEvent extends GraphicsNodeEvent {

    /**
     * The id for the "graphicsNodeAdded" event.
     */
    public static final int GRAPHICS_NODE_ADDED = 0;
    /**
     * The id for the "graphicsNodeRemoved" event.
     */
    public static final int GRAPHICS_NODE_REMOVED = 1;

    /** The graphics node that was added or removed. */
    protected GraphicsNode child;

    /**
     * Constructs a new composite graphics node.
     * @param source the graphics node where the event originated
     * @param id the id of this event
     * @param child the graphics node that was added or removed
     */
    public CompositeGraphicsNodeEvent(GraphicsNode source, int id,
                                      GraphicsNode child) {
        super(source, id);
        this.child = child;
    }

    /**
     * Returns the graphics node that was affected by the event.
     * @return the graphics node that was added or removed
     */
    public GraphicsNode getChild() {
        return child;
    }

    /**
     * Returns the originator of the event.
     * @return the CompositeGraphicsNode that originated the event
     */
    public CompositeGraphicsNode getCompositeGraphicsNode() {
        return (CompositeGraphicsNode) source;
    }
}
