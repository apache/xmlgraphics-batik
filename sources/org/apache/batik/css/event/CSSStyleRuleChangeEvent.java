/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

/**
 * This class represents the events which get fired whenever a
 * CSS style rule is changed.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSStyleRuleChangeEvent {
    /**
     * The source of this event.
     */
    protected Object source;

    /**
     * Creates a new CSSStyleRuleChangeEvent object.
     * @param source The source of this event.
     */
    protected CSSStyleRuleChangeEvent(Object source) {
	this.source = source;
    }

    /**
     * Returns the event source (ie. the style rule).
     */
    public Object getSource() {
	return source;
    }
}
