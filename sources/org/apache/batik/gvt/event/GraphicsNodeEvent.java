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
 * A low-level event which indicates that a node changed visibility or
 * geometry.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class GraphicsNodeEvent extends EventObject implements Cloneable {

    /**
     * The event mask for selecting composite graphics node events.
     */
    public static final int COMPOSITE_GRAPHICS_NODE_EVENT_MASK = 0x02;
    /**
     * The event mask for selecting graphics node mouse events.
     */
    public static final int GRAPHICS_NODE_MOUSE_EVENT_MASK     = 0x04;
    /**
     * The event mask for selecting graphics node key events.
     */
    public static final int GRAPHICS_NODE_KEY_EVENT_MASK       = 0x08;


    /**
     * The id for the "graphicsNodeShown" event.
     */
    public static final int GRAPHICS_NODE_SHOWN = 0;
    /**
     * The id for the "graphicsNodeHidden" event.
     */
    public static final int GRAPHICS_NODE_HIDDEN = 1;
    /**
     * The id for the "graphicsNodeTransformed" event.
     */
    public static final int GRAPHICS_NODE_TRANSFORMED = 2;


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

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Attempt to clone GraphicsNodeEvent failed.");
        }
    }
}
