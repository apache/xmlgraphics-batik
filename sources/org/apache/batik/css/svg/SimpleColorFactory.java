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

import org.apache.batik.css.value.ImmutableFloat;
import org.apache.batik.css.value.ImmutableRGBColor;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.css.value.SystemColorResolver;
import org.apache.batik.css.value.ValueFactory;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;

import org.w3c.dom.DOMException;

import org.w3c.dom.css.CSSPrimitiveValue;

import org.w3c.dom.svg.SVGColor;

/**
 * This class provides a factory for values of type color.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SimpleColorFactory
    extends    SVGColorFactory
    implements SVGValueConstants {

    /**
     * Creates a new SimpleColorFactory object.
     */
    public SimpleColorFactory(Parser p, String prop, SystemColorResolver scr) {
	super(p, prop, scr);
    }

    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_IDENT:
            String s = lu.getStringValue().toLowerCase().intern();
            if (s == CSS_CURRENTCOLOR_VALUE) {
                return CURRENTCOLOR_VALUE;
            }
            return super.createValue(lu);
        case LexicalUnit.SAC_RGBCOLOR:
            LexicalUnit l = lu.getParameters();
            ValueFactory ph = new ColorComponentFactory(getParser());
            CSSPrimitiveValue r = new CSSOMValue(ph, createColorValue(l));
            l = l.getNextLexicalUnit().getNextLexicalUnit();
            CSSPrimitiveValue g = new CSSOMValue(ph, createColorValue(l));
            l = l.getNextLexicalUnit().getNextLexicalUnit();
            CSSPrimitiveValue b = new CSSOMValue(ph, createColorValue(l));
            lu = lu.getNextLexicalUnit();
            if (lu == null) {
                return new ImmutableRGBColor(r, g, b);
            }
            if (lu.getLexicalUnitType() != LexicalUnit.SAC_FUNCTION) {
                throw CSSDOMExceptionFactory.createDOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     "invalid.lexical.unit",
                     new Object[] { new Integer(lu.getLexicalUnitType()),
                                    getPropertyName() });
            }
            if (!lu.getFunctionName().toLowerCase().equals("icc-color")) {
                throw CSSDOMExceptionFactory.createDOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     "invalid.lexical.unit",
                     new Object[] { new Integer(lu.getLexicalUnitType()),
                                    getPropertyName() });
            }
            lu = lu.getParameters();
            if (lu.getLexicalUnitType() != LexicalUnit.SAC_IDENT) {
                throw CSSDOMExceptionFactory.createDOMException
                    (DOMException.INVALID_ACCESS_ERR,
                     "invalid.lexical.unit",
                     new Object[] { new Integer(lu.getLexicalUnitType()),
                                    getPropertyName() });
            }
            String cp = lu.getStringValue();
            lu = lu.getNextLexicalUnit();
            SVGCSSNumberList nl = new SVGCSSNumberList();
            while (lu != null) {
                lu = lu.getNextLexicalUnit();
                if (lu == null) {
                    throw CSSDOMExceptionFactory.createDOMException
                        (DOMException.INVALID_ACCESS_ERR,
                         "invalid.lexical.unit",
                         new Object[] { new Integer(0),
                                        getPropertyName() });
                }
                nl.appendItem(new SVGCSSNumber(getColorValue(lu)));
                lu = lu.getNextLexicalUnit();
            }
            return new ImmutableSVGColorValue
                (SVGColor.SVG_COLORTYPE_RGBCOLOR_ICCCOLOR,
                 r, g, b, cp, nl);
	}
	return super.createValue(lu);
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
		 "invalid.lexical.unit",
		 new Object[] { new Integer(lu.getLexicalUnitType()),
                                getPropertyName() });
	}
    }

    /**
     * Creates a float value usable by an RGBColor.
     * @param lu The SAC lexical unit used to create the value.
     */
    protected float getColorValue(LexicalUnit lu) {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INTEGER:
	    return lu.getIntegerValue();
	case LexicalUnit.SAC_REAL:
	    return lu.getFloatValue();
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.lexical.unit",
		 new Object[] { new Integer(lu.getLexicalUnitType()),
                                getPropertyName() });
	}
    }
}
