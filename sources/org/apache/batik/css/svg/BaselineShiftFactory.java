/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.PropertyMap;
import org.apache.batik.css.value.AbstractLengthFactory;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'baseline-shift' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class BaselineShiftFactory
    extends    AbstractLengthFactory
    implements SVGValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_BASELINE_VALUE,        BASELINE_VALUE);
	values.put(CSS_SUB_VALUE,             SUB_VALUE);
	values.put(CSS_SUPER_VALUE,           SUPER_VALUE);
    }

    /**
     * Creates a new BaselineShiftFactory object.
     */
    public BaselineShiftFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_BASELINE_SHIFT_PROPERTY;
    }
    
    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_IDENT:
	    Object v = values.get(lu.getStringValue().toLowerCase().intern());
	    if (v == null) {
		throw CSSDOMExceptionFactory.createDOMException
		    (DOMException.INVALID_ACCESS_ERR,
		     "invalid.identifier",
		     new Object[] { lu.getStringValue(), getPropertyName() });
	    }
	    return (ImmutableValue)v;
	default:
	    return super.createValue(lu);
	}
    }

    /**
     * Creates and returns a new string value.
     * @param type  A string code as defined in CSSPrimitiveValue. The string
     *   code can only be a string unit type.
     * @param value  The new string value. 
     */
    public ImmutableValue createStringValue(short type, String value)
	throws DOMException {
	if (type != CSSPrimitiveValue.CSS_IDENT) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value, getPropertyName() });
	}
	Object v = values.get(value.toLowerCase().intern());
	if (v == null) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value, getPropertyName() });
	}
	return (ImmutableValue)v;
    }
}
