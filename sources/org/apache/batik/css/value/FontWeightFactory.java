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
public class FontWeightFactory
    extends    AbstractIdentifierFactory
    implements ValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_BOLD_VALUE,    BOLD_VALUE);
	values.put(CSS_BOLDER_VALUE,  BOLDER_VALUE);
	values.put(CSS_LIGHTER_VALUE, LIGHTER_VALUE);
	values.put(CSS_NORMAL_VALUE,  NORMAL_VALUE);
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
	return CSS_FONT_WEIGHT_PROPERTY;
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
		return NUMBER_100;
	    case 200:
		return NUMBER_200;
	    case 300:
		return NUMBER_300;
	    case 400:
		return NUMBER_400;
	    case 500:
		return NUMBER_500;
	    case 600:
		return NUMBER_600;
	    case 700:
		return NUMBER_700;
	    case 800:
		return NUMBER_800;
	    case 900:
		return NUMBER_900;
	    }
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.number",
		 new Object[] { new Integer(i), getPropertyName() });
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
	     new Object[] { new Integer((int)floatValue), getPropertyName() });
    }

    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
