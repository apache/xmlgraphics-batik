/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for CSS lengths.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractLengthFactory extends AbstractValueFactory {
    /**
     * Creates a new LengthFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public AbstractLengthFactory(Parser p) {
	super(p);
    }

    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_EM:
	    return createFloatValue(CSSPrimitiveValue.CSS_EMS,
				    lu.getFloatValue());
	case LexicalUnit.SAC_EX:
	    return createFloatValue(CSSPrimitiveValue.CSS_EXS,
				    lu.getFloatValue());
	case LexicalUnit.SAC_PIXEL:
	    return createFloatValue(CSSPrimitiveValue.CSS_PX,
				    lu.getFloatValue());
	case LexicalUnit.SAC_CENTIMETER:
	    return createFloatValue(CSSPrimitiveValue.CSS_CM,
				    lu.getFloatValue());
	case LexicalUnit.SAC_MILLIMETER:
	    return createFloatValue(CSSPrimitiveValue.CSS_MM,
				    lu.getFloatValue());
	case LexicalUnit.SAC_INCH:
	    return createFloatValue(CSSPrimitiveValue.CSS_IN,
				    lu.getFloatValue());
	case LexicalUnit.SAC_POINT:
	    return createFloatValue(CSSPrimitiveValue.CSS_PT,
				    lu.getFloatValue());
	case LexicalUnit.SAC_PICA:
	    return createFloatValue(CSSPrimitiveValue.CSS_PC,
				    lu.getFloatValue());
	case LexicalUnit.SAC_INTEGER:
	    return createFloatValue(CSSPrimitiveValue.CSS_NUMBER,
				    lu.getIntegerValue());
	case LexicalUnit.SAC_REAL:
	    return createFloatValue(CSSPrimitiveValue.CSS_NUMBER,
				    lu.getFloatValue());
	case LexicalUnit.SAC_DIMENSION:
	    return createFloatValue(CSSPrimitiveValue.CSS_DIMENSION,
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
	switch (unitType) {
	case CSSPrimitiveValue.CSS_PERCENTAGE:
	case CSSPrimitiveValue.CSS_EMS:
	case CSSPrimitiveValue.CSS_EXS:
	case CSSPrimitiveValue.CSS_PX:
	case CSSPrimitiveValue.CSS_CM:
	case CSSPrimitiveValue.CSS_MM:
	case CSSPrimitiveValue.CSS_IN:
	case CSSPrimitiveValue.CSS_PT:
	case CSSPrimitiveValue.CSS_PC:
	case CSSPrimitiveValue.CSS_NUMBER:
	    return new ImmutableFloat(unitType, floatValue);
	}
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.unit.type",
	     new Object[] { new Integer(unitType) });
    }
}
