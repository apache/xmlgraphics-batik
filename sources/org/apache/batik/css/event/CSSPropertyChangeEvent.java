/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.event;

import org.w3c.dom.css.CSSValue;

/**
 * This class represents the events which get fired whenever a
 * CSS property value is changed.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class CSSPropertyChangeEvent {
    /**
     * The source of this event.
     */
    protected Object source;

    /**
     * The CSS property name.
     */
    protected String propertyName;

    /**
     * The CSS property old value.
     */
    protected CSSValue oldValue;

    /**
     * The CSS property new value.
     */
    protected CSSValue newValue;

    /**
     * Creates a new CSSPropertyChangeEvent object.
     * @param source The source of this event.
     * @param property The property name.
     * @param before The property value before the change.
     * @param after The property value after the change.
     */
    public CSSPropertyChangeEvent(Object   source,
				  String   property,
				  CSSValue before,
				  CSSValue after) {
	this.source = source;
	propertyName = property;
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
     * Returns the name of the changing property.
     */
    public String getPropertyName() {
	return propertyName;
    }

    /**
     * Returns the value of the property before the change.
     */
    public CSSValue getOldValue() {
	return oldValue;
    }

    /**
     * Returns the value of the property after the change.
     */
    public CSSValue getNewValue() {
	return newValue;
    }
}
