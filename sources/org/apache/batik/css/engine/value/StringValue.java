/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class represents string values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StringValue extends AbstractValue {
    
    /**
     * The value of the string
     */
    protected String value;

    /**
     * The unit type
     */
    protected short unitType;

    /**
     * Creates a new StringValue.
     */
    public StringValue(short type, String s) {
        unitType = type;
        value = s;
    }

    /**
     * The type of the value.
     */
    public short getPrimitiveType() {
        return unitType;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof StringValue)) {
	    return false;
	}
	StringValue v = (StringValue)obj;
	if (unitType != v.unitType) {
	    return false;
	}
	return value.equals(v.value);
    }

    /**
     * A string representation of the current value. 
     */
    public String getCssText() {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_URI:
	    return "url(" + value + ")";

	case CSSPrimitiveValue.CSS_STRING:
	    char q = (value.indexOf('"') != -1) ? '\'' : '"';
	    return q + value + q;
	}
	return value;
    }

    /**
     *  This method is used to get the string value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a string
     *    value. 
     */
    public String getStringValue() throws DOMException {
        return value;
    }

    /**
     * Returns a printable representation of this value.
     */
    public String toString() {
        return getCssText();
    }
}
