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

import org.w3c.dom.svg.SVGPaint;

/**
 * This class provides a factory for values of type paint.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PaintFactory
    extends    SVGColorFactory
    implements SVGValueConstants {

    /**
     * Creates a new PaintFactory object.
     */
    public PaintFactory(Parser p, String prop, SystemColorResolver scr) {
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
            } else if (s == CSS_NONE_VALUE) {
                return NONE_VALUE;
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
                         new Object[] { new Integer(0), getPropertyName() });
                }
                nl.appendItem(new SVGCSSNumber(getColorValue(lu)));
                lu = lu.getNextLexicalUnit();
            }
            return new ImmutableSVGPaintValue
                (SVGPaint.SVG_PAINTTYPE_RGBCOLOR_ICCCOLOR,
                 r, g, b, cp, nl, null);

        case LexicalUnit.SAC_URI:
            String uri = lu.getStringValue();
            lu = lu.getNextLexicalUnit();
            if (lu == null) {
                return new ImmutableString(CSSPrimitiveValue.CSS_URI, uri);
            }

            switch (lu.getLexicalUnitType()) {
            case LexicalUnit.SAC_RGBCOLOR:
                l = lu.getParameters();
                ph = new ColorComponentFactory(getParser());
                r = new CSSOMValue(ph, createColorValue(l));
                l = l.getNextLexicalUnit().getNextLexicalUnit();
                g = new CSSOMValue(ph, createColorValue(l));
                l = l.getNextLexicalUnit().getNextLexicalUnit();
                b = new CSSOMValue(ph, createColorValue(l));
                lu = lu.getNextLexicalUnit();
                if (lu == null) {
                    return new ImmutableSVGPaintValue
                        (SVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR,
                         r, g, b, null, null, uri);
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
                cp = lu.getStringValue();
                lu = lu.getNextLexicalUnit();
                nl = new SVGCSSNumberList();
                while (lu != null) {
                    lu = lu.getNextLexicalUnit();
                    if (lu == null) {
                        throw CSSDOMExceptionFactory.createDOMException
                            (DOMException.INVALID_ACCESS_ERR,
                             "invalid.lexical.unit",
                             new Object[] { new Integer(0), getPropertyName() });
                    }
                    nl.appendItem(new SVGCSSNumber(getColorValue(lu)));
                    lu = lu.getNextLexicalUnit();
                }
                return new ImmutableSVGPaintValue
                    (SVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR_ICCCOLOR,
                     r, g, b, cp, nl, uri);
            case LexicalUnit.SAC_IDENT:
                String id = lu.getStringValue().toLowerCase();
                if (id.equals(CSS_NONE_VALUE)) {
                    return new ImmutableSVGPaintValue
                        (SVGPaint.SVG_PAINTTYPE_URI_NONE,
                         null, null, null, null, null, uri);
                } else if (id.equals(CSS_CURRENTCOLOR_VALUE)) {
                    return new ImmutableSVGPaintValue
                        (SVGPaint.SVG_PAINTTYPE_URI_CURRENTCOLOR,
                         null, null, null, null, null, uri);
                } else {
                    throw CSSDOMExceptionFactory.createDOMException
                        (DOMException.INVALID_ACCESS_ERR,
                         "invalid.lexical.unit",
                         new Object[] { new Integer(lu.getLexicalUnitType()),
                                        getPropertyName() });
                }
            }
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
	if (type == CSSPrimitiveValue.CSS_URI) {
	    return new ImmutableString(CSSPrimitiveValue.CSS_URI, value);
	}
	return super.createStringValue(type, value);
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
