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
public class BaselineShiftFactory extends AbstractLengthFactory {
    /**
     * The 'baseline' string.
     */
    public final static String BASELINE = "baseline";

    /**
     * The 'baseline' keyword.
     */
    public final static ImmutableValue BASELINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BASELINE);

    /**
     * The 'sub' string.
     */
    public final static String SUB = "sub";

    /**
     * The 'sub' keyword.
     */
    public final static ImmutableValue SUB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SUB);

    /**
     * The 'super' string.
     */
    public final static String SUPER = "super";

    /**
     * The 'super' keyword.
     */
    public final static ImmutableValue SUPER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SUPER);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(BASELINE,        BASELINE_VALUE);
	values.put(SUB,             SUB_VALUE);
	values.put(SUPER,           SUPER_VALUE);
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
	return "baseline-shift";
    }
    
    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_PERCENTAGE:
	    return createFloatValue(CSSPrimitiveValue.CSS_PERCENTAGE,
				    lu.getFloatValue());
	case LexicalUnit.SAC_IDENT:
	    Object v = values.get(lu.getStringValue().toLowerCase().intern());
	    if (v == null) {
		throw CSSDOMExceptionFactory.createDOMException
		    (DOMException.INVALID_ACCESS_ERR,
		     "invalid.identifier",
		     new Object[] { lu.getStringValue() });
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
		 new Object[] { value });
	}
	Object v = values.get(value.toLowerCase().intern());
	if (v == null) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value });
	}
	return (ImmutableValue)v;
    }
}
