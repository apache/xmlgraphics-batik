/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides an abstract factory for CSS lengths.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractLengthFactory extends AbstractCSSValueFactory {
    /**
     * Creates a new LengthFactory object.
     */
    protected AbstractLengthFactory() {
    }

    /**
     * Returns a CSSValue built from the given SAC lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public CSSValue createCSSValue(LexicalUnit lu) {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_EM:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_EMS,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_EX:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_EXS,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_PIXEL:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_PX,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_CENTIMETER:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_CM,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_MILLIMETER:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_MM,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_INCH:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_IN,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_POINT:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_PT,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_PICA:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_PC,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_INTEGER:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                     lu.getIntegerValue());
	case LexicalUnit.SAC_REAL:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_NUMBER,
                                     lu.getFloatValue());
	case LexicalUnit.SAC_DIMENSION:
	    return new CSSFloatValue(CSSPrimitiveValue.CSS_DIMENSION,
                                     lu.getFloatValue());
	}
	throw new DOMException
	    (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("invalid.lexical.unit",
              new Object[] { new Integer(lu.getLexicalUnitType()) }));
    }
}
