/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for CSS spacing properties.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SpacingFactory extends AbstractLengthFactory {
    /**
     * The handled property.
     */
    protected String property;

    /**
     * Creates a new LengthFactory object.
     */
    public SpacingFactory(Parser p, String prop) {
	super(p);
	property = prop;
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return property;
    }
    
    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	if (lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT &&
	    lu.getStringValue().equalsIgnoreCase(CSS_NORMAL_VALUE)) {
	    return NORMAL_VALUE;
	}
	return super.createValue(lu);
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
            !value.equalsIgnoreCase(CSS_NORMAL_VALUE)) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value });
	}
	return NORMAL_VALUE;
    }
}
