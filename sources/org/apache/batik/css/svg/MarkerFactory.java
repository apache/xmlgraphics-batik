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
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'marker' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class MarkerFactory extends AbstractValueFactory {
    /**
     * The URI factory.
     */
    protected URIFactory uriFactory = new URIFactory(getParser());

    /**
     * The property name.
     */
    protected String property;

    /**
     * Creates a new MarkerFactory.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public MarkerFactory(Parser p, String prop) {
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
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_URI:
	    return new ImmutableString(CSSPrimitiveValue.CSS_URI,
                                       lu.getStringValue());
	case LexicalUnit.SAC_IDENT:
	    if (lu.getStringValue().equalsIgnoreCase(NONE)) {
		return NONE_VALUE;
	    }
	default:
            throw CSSDOMExceptionFactory.createDOMException
                (DOMException.INVALID_ACCESS_ERR,
                 "invalid.lexical.unit",
                 new Object[] { new Integer(lu.getLexicalUnitType()) });
	}
    }

    /**
     * Creates and returns a new string value.
     * @param type   A string code as defined in CSSPrimitiveValue. The string
     *               code can only be a string unit type.
     * @param value  The new string value. 
     */
    public ImmutableValue createStringValue(short type, String value)
	throws DOMException {
	if (type == CSSPrimitiveValue.CSS_IDENT &&
	    value.equalsIgnoreCase(NONE)) {
	    return NONE_VALUE;
	}
	if (type == CSSPrimitiveValue.CSS_URI) {
	    return new ImmutableString(type, value);
	}
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.identifier",
	     new Object[] { value });
    }
}
