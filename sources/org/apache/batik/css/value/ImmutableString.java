/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class represents immutable string values
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImmutableString extends AbstractImmutablePrimitiveValue {
    /**
     * The value of the string
     */
    protected String value;

    /**
     * The unit type
     */
    protected short unitType;

    /**
     * Creates a new value.
     */
    public ImmutableString(short type, String value) {
	unitType   = type;
	this.value = (type == CSSPrimitiveValue.CSS_IDENT)
	    ? value.toLowerCase().intern() : value;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof ImmutableString)) {
	    return false;
	}
	ImmutableString v = (ImmutableString)obj;
	if (unitType != v.unitType) {
	    return false;
	}
	if (unitType == CSSPrimitiveValue.CSS_IDENT) {
	    return value == v.value;
	}
	return value.equals(v.value);
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
	switch (unitType) {
	case CSSPrimitiveValue.CSS_URI:
	    return "url(" + value + ")";
	case CSSPrimitiveValue.CSS_STRING:
            // !!! Escaping quotes?
	    char q = (value.indexOf('"') != -1) ? '\'' : '"';
	    return q + value + q;
	}
	return value;
    }

    /**
     * The type of the value as defined by the constants specified in
     * CSSPrimitiveValue.
     */
    public short getPrimitiveType() {
	return unitType;
    }

    /**
     *  This method is used to get the string value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the CSS value doesn't contain a string
     *    value. 
     */
    public String getStringValue() throws DOMException {
	return value;
    }
}
