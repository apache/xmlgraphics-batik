/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.PropertyMap;
import org.apache.batik.css.value.AbstractIdentifierFactory;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the color-profile property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ColorProfileFactory
    extends    AbstractIdentifierFactory
    implements SVGValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_AUTO_VALUE,  AUTO_VALUE);
	values.put(CSS_SRGB_VALUE,  SRGB_VALUE);
    }

    /**
     * Creates a new ColorProfileFactory.
     */
    public ColorProfileFactory(Parser p) {
        super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_COLOR_PROFILE_PROPERTY;
    }
    
    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	if (lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
            String s = lu.getStringValue().toLowerCase();
            if (!s.equals(CSS_AUTO_VALUE) &&
                !s.equals(CSS_SRGB_VALUE)) {
                return new ImmutableString(CSSPrimitiveValue.CSS_IDENT, s);
	    }
	}
        return super.createValue(lu);
    }

    /**
     * Creates and returns a new string value.
     * @param type   A string code as defined in CSSPrimitiveValue. The string
     *               code can only be a string unit type.
     * @param value  The new string value. 
     */
    public ImmutableValue createStringValue(short type, String value)
	throws DOMException {
	if (type == CSSPrimitiveValue.CSS_IDENT) {
            String s = value.toLowerCase();
            if (!s.equals(CSS_AUTO_VALUE) &&
                !s.equals(CSS_SRGB_VALUE)) {
                return new ImmutableString(CSSPrimitiveValue.CSS_IDENT, s);
            }
        }
        return super.createStringValue(type, value);
    }

    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
