/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.CSSOMValue;
import org.apache.batik.css.value.AbstractLengthFactory;
import org.apache.batik.css.value.AbstractValueFactory;
import org.apache.batik.css.value.ImmutableFloat;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.css.value.ImmutableValueList;
import org.apache.batik.css.value.ValueFactory;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'stroke-dasharray' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StrokeDasharrayFactory extends AbstractValueFactory {
    /**
     * The numbers factory.
     */
    protected final ValueFactory NUMBER_FACTORY =
	new NumberFactory(getParser());

    /**
     * Creates a new StrokeDasharrayFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public StrokeDasharrayFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return SVGValueConstants.CSS_STROKE_DASHARRAY_PROPERTY;
    }
    
    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	short type = lu.getLexicalUnitType();
	switch (type) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_IDENT:
	    return createStringValue(CSSPrimitiveValue.CSS_IDENT, CSS_NONE_VALUE);
	default:
	    return createValueList(lu);
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
	if (type != CSSPrimitiveValue.CSS_IDENT ||
            !value.equalsIgnoreCase(CSS_NONE_VALUE)) {
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.identifier",
	     new Object[] { value });
	}
	return NONE_VALUE;
    }

    /**
     * Creates the quotes list.
     */
    protected ImmutableValue createValueList(LexicalUnit lu)
        throws DOMException {
	ImmutableValueList list = new ImmutableValueList(' ');
	do {
	    list.append(new CSSOMValue(NUMBER_FACTORY,
                                       NUMBER_FACTORY.createValue(lu)));
	    lu = lu.getNextLexicalUnit();
	} while (lu != null);
	return list;
    }

    /**
     * To manage number values.
     */
    protected static class NumberFactory extends AbstractLengthFactory {
	/**
	 * Creates a new NumberFactory object.
	 */
	public NumberFactory(Parser p) {
	    super(p);
	}

	/**
	 * Returns the name of the property handled.
	 */
	public String getPropertyName() {
	    return null;
	}
    
	/**
	 * Creates a value from a lexical unit.
	 */
	public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	    switch (lu.getLexicalUnitType()) {
	    case LexicalUnit.SAC_INTEGER:
		return new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
					  lu.getIntegerValue());
	    case LexicalUnit.SAC_REAL:
		return new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
					  lu.getFloatValue());
	    default:
		return super.createValue(lu);
	    }
	}

	/**
	 * Creates and returns a new float value.
	 * @param unitType  A unit code as defined above. The unit code
         * can only be a float unit type
	 * @param floatValue  The new float value. 
	 */
	public ImmutableValue createFloatValue(short unitType,
                                               float floatValue)
	    throws DOMException {
	    switch (unitType) {
	    case CSSPrimitiveValue.CSS_NUMBER:
		return new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
                                          floatValue);
	    default:
		return super.createFloatValue(unitType, floatValue);
	    }	
	}
    }
}
