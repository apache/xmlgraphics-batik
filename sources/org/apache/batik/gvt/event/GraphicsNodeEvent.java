/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.util.EventObject;

import org.apache.batik.gvt.GraphicsNode;

/**
 * A low-level event for GraphicsNode.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class GraphicsNodeEvent extends EventObject {

    /** Indicates whether or not this event is consumed. */
    private boolean consumed = false;

    /** The ID of this event. */
    protected int id;

    /**
     * Constructs a new graphics node event with the specified source and ID.
     * @param source the graphics node where the event originated
     * @param id the id of this event
     */
    public GraphicsNodeEvent(GraphicsNode source, int id) {
        super(source);
        this.id = id;
    }

    /**
     * Returns the ID of this event.
     */
    public int getID() {
        return id;
    }

    /**
     * Returns the graphics node where the event is originated.
     */
    public GraphicsNode getGraphicsNode() {
        return (GraphicsNode) source;
    }

    /**
     * Consumes this event so that it will not be processed
     * in the default manner by the source which originated it.
     */
    public void consume() {
        consumed = true;
    }

    /**
     * Returns whether or not this event has been consumed.
     */
    public boolean isConsumed() {
        return consumed;
    }
}
