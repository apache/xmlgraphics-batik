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
 * This class provides a factory for the 'font-size' property value..
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontSizeFactory
    extends    AbstractLengthFactory
    implements ValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
        values.put(CSS_LARGE_VALUE,    LARGE_VALUE);
        values.put(CSS_LARGER_VALUE,   LARGER_VALUE);
        values.put(CSS_MEDIUM_VALUE,   MEDIUM_VALUE);
        values.put(CSS_SMALL_VALUE,    SMALL_VALUE);
        values.put(CSS_SMALLER_VALUE,  SMALLER_VALUE);
        values.put(CSS_X_LARGE_VALUE,  X_LARGE_VALUE);
        values.put(CSS_X_SMALL_VALUE,  X_SMALL_VALUE);
        values.put(CSS_XX_LARGE_VALUE, XX_LARGE_VALUE);
        values.put(CSS_XX_SMALL_VALUE, XX_SMALL_VALUE);
    }

    /**
     * Creates a new FontSizeFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public FontSizeFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_FONT_SIZE_PROPERTY;
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
		     new Object[] { lu.getStringValue() });
            }
            return (ImmutableValue)v;
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
