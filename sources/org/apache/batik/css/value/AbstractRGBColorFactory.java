/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMValue;
import org.apache.batik.css.CSSDOMExceptionFactory;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for CSS RGBColor.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractRGBColorFactory extends AbstractValueFactory {
    /**
     * Creates a new AbstractRGBColorFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    protected AbstractRGBColorFactory(Parser p) {
	super(p);
    }

    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	if (lu.getLexicalUnitType() != LexicalUnit.SAC_RGBCOLOR) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.lexical.unit",
		 new Object[] { new Integer(lu.getLexicalUnitType()),
                                getPropertyName() });
	}
	lu = lu.getParameters();
	ValueFactory ph = new ColorComponentFactory(getParser());
	CSSPrimitiveValue r = new CSSOMValue(ph, createColorValue(lu));
	lu = lu.getNextLexicalUnit().getNextLexicalUnit();
	CSSPrimitiveValue g = new CSSOMValue(ph, createColorValue(lu));
	lu = lu.getNextLexicalUnit().getNextLexicalUnit();
	CSSPrimitiveValue b = new CSSOMValue(ph, createColorValue(lu));
	return new ImmutableRGBColor(r, g, b);
    }

    /**
     * Creates a float value usable by an RGBColor.
     * @param lu The SAC lexical unit used to create the value.
     */
    protected ImmutableValue createColorValue(LexicalUnit lu) {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INTEGER:
	    return new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
				      lu.getIntegerValue());
	case LexicalUnit.SAC_REAL:
	    return new ImmutableFloat(CSSPrimitiveValue.CSS_NUMBER,
				      lu.getFloatValue());
	case LexicalUnit.SAC_PERCENTAGE:
	    return new ImmutableFloat(CSSPrimitiveValue.CSS_PERCENTAGE,
				      lu.getFloatValue());
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.rgb.lexical.unit",
		 new Object[] { new Integer(lu.getLexicalUnitType()),
                                getPropertyName() });
	}
    }

    /**
     * To manage color component values.
     */
    protected class ColorComponentFactory extends AbstractValueFactory {
	/**
	 * Creates a new ColorComponentFactory object.
	 * @param p The CSS parser used to parse the CSS texts.
	 */
	public ColorComponentFactory(Parser p) {
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
	 * @param lu The SAC lexical unit used to create the value.
	 */
	public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	    return createColorValue(lu);
	}
    }
}
