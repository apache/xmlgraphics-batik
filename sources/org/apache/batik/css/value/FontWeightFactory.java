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
 * This class provides a factory for the 'font-weight' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontWeightFactory extends AbstractIdentifierFactory {
    /**
     * The '100' float value.
     */
    public final static ImmutableValue VALUE_100 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 100);

    /**
     * The '200' float value.
     */
    public final static ImmutableValue VALUE_200 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 200);

     /**
     * The '300' float value.
     */
    public final static ImmutableValue VALUE_300 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 300);

    /**
     * The '400' float value.
     */
    public final static ImmutableValue VALUE_400 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 400);

    /**
     * The '500' float value.
     */
    public final static ImmutableValue VALUE_500 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 500);

    /**
     * The '600' float value.
     */
    public final static ImmutableValue VALUE_600 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 600);

    /**
     * The '700' float value.
     */
    public final static ImmutableValue VALUE_700 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 700);

    /**
     * The '800' float value.
     */
    public final static ImmutableValue VALUE_800 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 800);

    /**
     * The '900' float value.
     */
    public final static ImmutableValue VALUE_900 =
	new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 900);

   /**
     * The 'bold' string.
     */
    public final static String BOLD = "bold";

    /**
     * The 'bold' identifier value.
     */
    public final static ImmutableValue BOLD_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BOLD);

    /**
     * The 'bolder' string.
     */
    public final static String BOLDER = "bolder";

    /**
     * The 'bolder' identifier value.
     */
    public final static ImmutableValue BOLDER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BOLDER);

    /**
     * The 'lighter' string.
     */
    public final static String LIGHTER = "lighter";

    /**
     * The 'lighter' identifier value.
     */
    public final static ImmutableValue LIGHTER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LIGHTER);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(BOLD,    BOLD_VALUE);
	values.put(BOLDER,  BOLDER_VALUE);
	values.put(LIGHTER, LIGHTER_VALUE);
	values.put(NORMAL,  NORMAL_VALUE);
    }

    /**
     * Creates a new FontWeightFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public FontWeightFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "font-weight";
    }
    
    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	if (lu.getLexicalUnitType() == LexicalUnit.SAC_INTEGER) {
	    int i = lu.getIntegerValue();
	    switch (i) {
	    case 100:
		return VALUE_100;
	    case 200:
		return VALUE_200;
	    case 300:
		return VALUE_300;
	    case 400:
		return VALUE_400;
	    case 500:
		return VALUE_500;
	    case 600:
		return VALUE_600;
	    case 700:
		return VALUE_700;
	    case 800:
		return VALUE_800;
	    case 900:
		return VALUE_900;
	    }
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.number",
		 new Object[] { new Integer(i) });
	}
	return super.createValue(lu);
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
	    int i = (int)floatValue;
	    if (floatValue == i) {
		switch (i) {
		case 100:
		case 200:
		case 300:
		case 400:
		case 500:
		case 600:
		case 700:
		case 800:
		case 900:
		    return new ImmutableFloat(unitType, floatValue);
		}
	    }
	}
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.number",
	     new Object[] { new Integer((int)floatValue) });
    }

    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
