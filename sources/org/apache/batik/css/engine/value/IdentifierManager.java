/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSEngine;

import org.w3c.css.sac.LexicalUnit;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a manager for the property with support for
 * identifier values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class IdentifierManager extends AbstractValueManager {

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return ValueConstants.INHERIT_VALUE;

	case LexicalUnit.SAC_IDENT:
            String s = lu.getStringValue().toLowerCase().intern();
	    Object v = getIdentifiers().get(s);
	    if (v == null) {
		throw createInvalidIdentifierDOMException(lu.getStringValue());
	    }
	    return (Value)v;

	default:
	    throw createInvalidLexicalUnitDOMException
                (lu.getLexicalUnitType());
	}
    }

    /**
     * Implements {@link
     * ValueManager#createStringValue(short,String,CSSEngine)}.
     */
    public Value createStringValue(short type, String value, CSSEngine engine)
	throws DOMException {
	if (type != CSSPrimitiveValue.CSS_IDENT) {
            throw createInvalidStringTypeDOMException(type);
	}
	Object v = getIdentifiers().get(value.toLowerCase().intern());
	if (v == null) {
            throw createInvalidIdentifierDOMException(value);
	}
	return (Value)v;
    }

    /**
     * Returns the map that contains the name/value mappings for each
     * possible identifiers.
     */
    protected abstract StringMap getIdentifiers();
}
