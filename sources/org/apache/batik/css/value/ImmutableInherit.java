/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;

/**
 * This singleton class represents the immutable 'inherit' value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImmutableInherit extends AbstractImmutableValue {
    /**
     * The only instance of this class.
     */
    public final static ImmutableInherit INSTANCE = new ImmutableInherit();
    
    /**
     * Creates a new ImmutableInherit object.
     */
    protected ImmutableInherit() {
    }

    /**
     * Returns a deep read-only copy of this object.
     */
    public ImmutableValue createReadOnlyCopy() {
	return this;
    }

    /**
     *  A string representation of the current value. 
     */
    public String getCssText() {
	return "inherit";
    }

    /**
     * A code defining the type of the value. 
     */
    public short getCssValueType() {
	return CSSValue.CSS_INHERIT;
    }

    /**
     *  This method is used to get the string value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the CSS value doesn't contain a string
     *    value. 
     */
    public String getStringValue() throws DOMException {
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "inherit.not.string",
	     new Object[] {});
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	return this == obj;
    }
}
