/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.CSSOMStyleDeclaration;
import org.apache.batik.css.CSSOMValue;
import org.apache.batik.css.value.AbstractValueFactory;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.apache.batik.css.value.ValueFactory;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'marker' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class MarkerShorthandFactory extends AbstractValueFactory {

    /**
     * The marker-end factory.
     */
    protected ValueFactory markerEndFactory =
        new MarkerFactory(parser, CSS_MARKER_END_PROPERTY);
    
    /**
     * The marker-mid factory.
     */
    protected ValueFactory markerMidFactory =
        new MarkerFactory(parser, CSS_MARKER_MID_PROPERTY);
    
    /**
     * The marker-start factory.
     */
    protected ValueFactory markerStartFactory =
        new MarkerFactory(parser, CSS_MARKER_START_PROPERTY);
    
    /**
     * Creates a new MarkerFactory.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public MarkerShorthandFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_MARKER_PROPERTY;
    }
    
    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
        throw CSSDOMExceptionFactory.createDOMException
            (DOMException.INVALID_ACCESS_ERR, "not.supported", null);
    }

    /**
     * Creates a value from a lexical unit and a style declaration.
     * This method must only be called for null values.
     * @param lu  The SAC lexical unit used to create the value.
     * @param d   The style declaration in which to add the created value.
     * @param imp The property priority.
     */
    public void createCSSValue(LexicalUnit          lu,
			       CSSOMStyleDeclaration d,
			       String               imp) throws DOMException {
        ImmutableValue val;
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    val = INHERIT;
            break;
	case LexicalUnit.SAC_URI:
	    val = new ImmutableString(CSSPrimitiveValue.CSS_URI,
                                      lu.getStringValue());
            break;
	case LexicalUnit.SAC_IDENT:
	    if (lu.getStringValue().equalsIgnoreCase(CSS_NONE_VALUE)) {
		val = NONE_VALUE;
                break;
	    }
	default:
            throw CSSDOMExceptionFactory.createDOMException
                (DOMException.INVALID_ACCESS_ERR,
                 "invalid.lexical.unit",
                 new Object[] { new Integer(lu.getLexicalUnitType()) });
	}
        d.setPropertyCSSValue(CSS_MARKER_END_PROPERTY,
                              new CSSOMValue(markerEndFactory, val), imp);
        d.setPropertyCSSValue(CSS_MARKER_MID_PROPERTY,
                              new CSSOMValue(markerMidFactory, val), imp);
        d.setPropertyCSSValue(CSS_MARKER_START_PROPERTY,
                              new CSSOMValue(markerStartFactory, val), imp);
    }
}
