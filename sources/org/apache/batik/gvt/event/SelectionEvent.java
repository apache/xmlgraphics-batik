/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.awt.Shape;

/**
 * An event which indicates that a selection is being made or has been made.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public class SelectionEvent {

    /**
     * The id for the "selection changing" event.
     * (Selection process is under way)
     */
    public static final int SELECTION_CHANGED = 1;

    /**
     * The id for the "selection cleared" event.
     */
    public static final int SELECTION_CLEARED = 3;

    /**
     * The id for the "selection started" event.
     */
    public static final int SELECTION_START = 4;

    /**
     * The id for the "selection completed" event.
     * (Selection process is complete).
     */
    public static final int SELECTION_DONE = 2;

    /** The shape enclosing the selection */
    protected Shape highlightShape;

    /** The object which composes the selection */
    protected Object selection;

    /** The event type of the current selection event */
    protected int id;

    /**
     * Constructs a new graphics node paint event.
     * @param selection the selection
     * @param id the id of this event
     * @param highlightShape a user-space shape enclosing the selection.
     */
    public SelectionEvent(Object selection, int id, Shape highlightShape ) {
        this.id = id;
        this.selection = selection;
	this.highlightShape = highlightShape;
    }

    /**
     * Returns a shape in user space that encloses the current selection.
     */
    public Shape getHighlightShape() {
        return highlightShape;
    }

    /**
     * Returns the selection associated with this event.
     * Only guaranteed current for events of type SELECTION_DONE.
     */
    public Object getSelection() {
        return selection;
    }

    /**
     * Returns the event's selection event type.
     * @see org.apache.batik.gvt.event.SelectionEvent.SELECTION_CHANGED
     * @see org.apache.batik.gvt.event.SelectionEvent.SELECTION_CLEARED
     * @see org.apache.batik.gvt.event.SelectionEvent.SELECTION_DONE
     */
    public int getType() {
        return id;
    }



}
