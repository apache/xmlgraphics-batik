/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.PropertyMap;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for 'font-size-adjust' property values..
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontSizeAdjustFactory extends AbstractValueFactory {
    /**
     * Creates a new FontSizeAdjustFactory object.
     */
    public FontSizeAdjustFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_FONT_SIZE_ADJUST_PROPERTY;
    }
    
    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_IDENT:
	    if (lu.getStringValue().equalsIgnoreCase(CSS_NONE_VALUE)) {
		return NONE_VALUE;
	    }
	    break;
	case LexicalUnit.SAC_INTEGER:
	    return createFloatValue(CSSPrimitiveValue.CSS_NUMBER,
				    lu.getIntegerValue());
	case LexicalUnit.SAC_REAL:
	    return createFloatValue(CSSPrimitiveValue.CSS_NUMBER,
				    lu.getFloatValue());
	}
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.lexical.unit",
	     new Object[] { new Integer(lu.getLexicalUnitType()) });
    }

    /**
     * Creates and returns a new float value.
     * @param unitType  A unit code as defined above. The unit code can only 
     *   be a float unit type
     * @param floatValue  The new float value. 
     */
    public ImmutableValue createFloatValue(short unitType, float floatValue)
	throws DOMException {
	if (unitType != CSSPrimitiveValue.CSS_NUMBER) {
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.primitive.unit",
	     new Object[] { new Integer(unitType) });
	}
	return new ImmutableFloat(unitType, floatValue);
    }

    /**
     * Creates and returns a new string value.
     * @param type  A string code as defined in CSSPrimitiveValue. The string
     *   code can only be a string unit type.
     * @param value  The new string value. 
     */
    public ImmutableValue createStringValue(short type, String value)
	throws DOMException {
	if (type != CSSPrimitiveValue.CSS_IDENT ||
	    !value.equalsIgnoreCase(CSS_NONE_VALUE)) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value });
	}
	return NONE_VALUE;
    }
}
