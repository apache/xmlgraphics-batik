/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.awt.event.InputEvent;
import org.apache.batik.gvt.GraphicsNode;

/**
 * An event which indicates that a change action occurred on a graphics node.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class GraphicsNodeChangeEvent extends GraphicsNodeEvent {

    /**
     * The first number in the range of ids used for change events.
     */
    static final int CHANGE_FIRST = 9800;

    /**
     * The id for the "changeStarted" event. This change event occurs
     * when a change has started on a graphics node (but no changes have
     * occured on the graphics node it's self).
     */
    public static final int CHANGE_STARTED = CHANGE_FIRST;

    /**
     * The id for the "changeCompleted" event. This change event
     * occurs when a change has completed on a graphics node (all
     * changes have completed on the graphics node it's self).  
     */
    public static final int CHANGE_COMPLETED = CHANGE_FIRST+1;

    /**
     * Constructs a new graphics node event with the specified source and ID.
     * @param source the graphics node where the event originated
     * @param id the id of this event
     */
    public GraphicsNodeChangeEvent(GraphicsNode source, int id) {
        super(source, id);
    }


}
