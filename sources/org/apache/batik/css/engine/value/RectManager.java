/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value;

import org.apache.batik.util.CSSConstants;

import org.apache.batik.css.engine.CSSEngine;

import org.w3c.css.sac.LexicalUnit;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a manager for the property with support for
 * rect values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class RectManager extends AbstractValueManager {
    
    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_FUNCTION:
            if (!lu.getFunctionName().equalsIgnoreCase("rect")) {
                break;
            }
        case LexicalUnit.SAC_RECT_FUNCTION:
            lu = lu.getParameters();
            Value top = createRectComponent(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw createMalformedRectDOMException();
            }
            lu = lu.getNextLexicalUnit();
            Value right = createRectComponent(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw createMalformedRectDOMException();
            }
            lu = lu.getNextLexicalUnit();
            Value bottom = createRectComponent(lu);
            lu = lu.getNextLexicalUnit();
            if (lu == null ||
                lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA) {
                throw createMalformedRectDOMException();
            }
            lu = lu.getNextLexicalUnit();
            Value left = createRectComponent(lu);
            return new RectValue(top, right, bottom, left);
        }
        throw createMalformedRectDOMException();
    }

    private Value createRectComponent(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_IDENT:
	    if (lu.getStringValue().equalsIgnoreCase
                (CSSConstants.CSS_AUTO_VALUE)) {
		return ValueConstants.AUTO_VALUE;
	    }
            break;
	case LexicalUnit.SAC_EM:
	    return new FloatValue(CSSPrimitiveValue.CSS_EMS,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_EX:
	    return new FloatValue(CSSPrimitiveValue.CSS_EXS,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_PIXEL:
	    return new FloatValue(CSSPrimitiveValue.CSS_PX,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_CENTIMETER:
	    return new FloatValue(CSSPrimitiveValue.CSS_CM,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_MILLIMETER:
	    return new FloatValue(CSSPrimitiveValue.CSS_MM,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_INCH:
	    return new FloatValue(CSSPrimitiveValue.CSS_IN,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_POINT:
	    return new FloatValue(CSSPrimitiveValue.CSS_PT,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_PICA:
	    return new FloatValue(CSSPrimitiveValue.CSS_PC,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_INTEGER:
	    return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  lu.getIntegerValue());
	case LexicalUnit.SAC_REAL:
	    return new FloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                  lu.getFloatValue());
	case LexicalUnit.SAC_PERCENTAGE:
	    return new FloatValue(CSSPrimitiveValue.CSS_PERCENTAGE,
                                  lu.getFloatValue());
        }
        throw createMalformedRectDOMException();
    }

    private DOMException createMalformedRectDOMException() {
        Object[] p = new Object[] { getPropertyName() };
        String s = Messages.formatMessage("malformed.rect", p);
        return new DOMException(DOMException.SYNTAX_ERR, s);
    }
}
