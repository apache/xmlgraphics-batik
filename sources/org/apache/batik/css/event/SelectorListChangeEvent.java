/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

import org.w3c.css.sac.SelectorList;

/**
 * This class represents the events which get fired whenever a
 * selector list is changed.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class SelectorListChangeEvent {
    /**
     * The source of this event.
     */
    protected Object source;

    /**
     * The selector list old value.
     */
    protected SelectorList oldValue;

    /**
     * The selector list new value.
     */
    protected SelectorList newValue;

    /**
     * Creates a new SelectorListChangeEvent object.
     * @param source The source of this event.
     * @param property The property name.
     * @param before The selector list value before the change.
     * @param after The selector value after the change.
     */
    public SelectorListChangeEvent(Object       source,
				   SelectorList before,
				   SelectorList after) {
	this.source = source;
	oldValue = before;
	newValue = after;
    }

    /**
     * Returns the event source.
     */
    public Object getSource() {
	return source;
    }

    /**
     * Returns the value of the property before the change.
     */
    public SelectorList getOldValue() {
	return oldValue;
    }

    /**
     * Returns the value of the property after the change.
     */
    public SelectorList getNewValue() {
	return newValue;
    }
}
