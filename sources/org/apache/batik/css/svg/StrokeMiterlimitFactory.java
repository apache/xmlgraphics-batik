/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.value.AbstractValueFactory;
import org.apache.batik.css.value.ImmutableFloat;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'stroke-miterlimit' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StrokeMiterlimitFactory extends AbstractValueFactory {
    /**
     * The 4 number.
     */
    public final static ImmutableValue FOUR_VALUE =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 4);

    /**
     * Creates a new StrokeMiterlimitFactory object.
     */
    public StrokeMiterlimitFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "stroke-miterlimit";
    }
    
    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case  LexicalUnit.SAC_INTEGER:
	    return new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
				      lu.getIntegerValue());
	case  LexicalUnit.SAC_REAL:
	    return new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
				      lu.getFloatValue());
	default:
            throw CSSDOMExceptionFactory.createDOMException
                (DOMException.INVALID_ACCESS_ERR,
                 "invalid.lexical.unit",
                 new Object[] { new Integer(lu.getLexicalUnitType()) });
	}
    }

    /**
     * Creates and returns a new float value.
     * @param unitType  A unit code as defined above. The unit code can only 
     *   be a float unit type
     * @param floatValue  The new float value. 
     */
    public ImmutableValue createFloatValue(short unitType, float floatValue)
	throws DOMException {
	if (unitType == CSSPrimitiveValue.CSS_NUMBER) {
	    return new ImmutableFloat(unitType, floatValue);
	}
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.unit",
	     new Object[] { new Integer(unitType) });
    }
}
