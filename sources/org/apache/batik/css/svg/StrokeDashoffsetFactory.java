/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.AbstractLengthFactory;
import org.apache.batik.css.value.ImmutableFloat;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'stroke-dashoffset' property.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StrokeDashoffsetFactory extends AbstractLengthFactory {
    /**
     * The 0 number.
     */
    public final static ImmutableValue ZERO_VALUE =
        new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER, 0);

    /**
     * Creates a new LengthFactory object.
     */
    public StrokeDashoffsetFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "stroke-dashoffset";
    }
    
    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INTEGER:
	    return createFloatValue(CSSPrimitiveValue.CSS_NUMBER,
				    lu.getIntegerValue());
	case LexicalUnit.SAC_REAL:
	    return createFloatValue(CSSPrimitiveValue.CSS_NUMBER,
				    lu.getFloatValue());
	default:
	    return super.createValue(lu);
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
	switch (unitType) {
	case CSSPrimitiveValue.CSS_NUMBER:
	    return new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
                                      floatValue);
	default:
	    return super.createFloatValue(unitType, floatValue);
	}	
    }
}
