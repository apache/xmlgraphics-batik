/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for values of type paint.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PaintFactory extends SVGColorFactory {
    /**
     * The 'currentColor' string.
     */
    public final static String CURRENTCOLOR = "currentcolor";

    /**
     * The 'currentColor' keyword.
     */
    public final static ImmutableValue CURRENTCOLOR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, CURRENTCOLOR);

    static {
	values.put(CURRENTCOLOR, CURRENTCOLOR_VALUE);
	values.put(NONE,         NONE_VALUE);
    }

    /**
     * Creates a new PaintFactory object.
     */
    public PaintFactory(Parser p, String prop) {
	super(p, prop);
    }

    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	if (lu.getLexicalUnitType() == LexicalUnit.SAC_URI) {
	    return new ImmutableString(CSSPrimitiveValue.CSS_URI,
				       lu.getStringValue());
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
}
