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
public class FontSizeFactory extends AbstractLengthFactory {
    /**
     * The 'large' string.
     */
    public final static String LARGE = "large";

    /**
     * The 'large' identifier value.
     */
    public final static ImmutableValue LARGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LARGE);

    /**
     * The 'larger' string.
     */
    public final static String LARGER = "larger";

    /**
     * The 'larger' identifier value.
     */
    public final static ImmutableValue LARGER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LARGER);

    /**
     * The 'medium' string.
     */
    public final static String MEDIUM = "medium";

    /**
     * The 'medium' identifier value.
     */
    public final static ImmutableValue MEDIUM_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, MEDIUM);

    /**
     * The 'small' string.
     */
    public final static String SMALL = "small";

    /**
     * The 'small' identifier value.
     */
    public final static ImmutableValue SMALL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SMALL);

    /**
     * The 'smaller' string.
     */
    public final static String SMALLER = "smaller";

    /**
     * The 'smaller' identifier value.
     */
    public final static ImmutableValue SMALLER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SMALLER);

    /**
     * The 'x-large' string.
     */
    public final static String X_LARGE = "x-large";

    /**
     * The 'x-large' identifier value.
     */
    public final static ImmutableValue X_LARGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, X_LARGE);

    /**
     * The 'x-small' string.
     */
    public final static String X_SMALL = "x-small";

    /**
     * The 'x-small' identifier value.
     */
    public final static ImmutableValue X_SMALL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, X_SMALL);

    /**
     * The 'xx-large' string.
     */
    public final static String XX_LARGE = "xx-large";

    /**
     * The 'xx-large' identifier value.
     */
    public final static ImmutableValue XX_LARGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, XX_LARGE);

    /**
     * The 'xx-small' string.
     */
    public final static String XX_SMALL = "xx-small";

    /**
     * The 'xx-small' identifier value.
     */
    public final static ImmutableValue XX_SMALL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, XX_SMALL);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
        values.put(LARGE,    LARGE_VALUE);
        values.put(LARGER,   LARGER_VALUE);
        values.put(MEDIUM,   MEDIUM_VALUE);
        values.put(SMALL,    SMALL_VALUE);
        values.put(SMALLER,  SMALLER_VALUE);
        values.put(X_LARGE,  X_LARGE_VALUE);
        values.put(X_SMALL,  X_SMALL_VALUE);
        values.put(XX_LARGE, XX_LARGE_VALUE);
        values.put(XX_SMALL, XX_SMALL_VALUE);
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
	return "font-size";
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
