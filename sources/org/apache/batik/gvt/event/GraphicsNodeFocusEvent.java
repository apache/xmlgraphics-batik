/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import org.apache.batik.gvt.GraphicsNode;

/**
 * A low-level event which indicates that a graphics node has gained or
 * lost the keyboard focus.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class GraphicsNodeFocusEvent extends GraphicsNodeEvent {

    /**
     * The first number in the range of ids used for focus events.
     */
    static final int FOCUS_FIRST = 1004;

    /**
     * The id for the "focusGained" event. This event indicates that
     * the component gained the keyboard focus.
     */
    public static final int FOCUS_GAINED = FOCUS_FIRST;

    /**
     * The id for the "focusLoses" event. This event indicates that
     * the component lost the keyboard focus.
     */
    public static final int FOCUS_LOST = FOCUS_FIRST + 1;

    /**
     * Constructs a new graphics node focus event.
     * @param source the graphics node where the event originated
     * @param id the id of this event
     */
    public GraphicsNodeFocusEvent(GraphicsNode source, int id) {
        super(source, id);
    }
}
