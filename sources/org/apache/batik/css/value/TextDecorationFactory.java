/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSDOMExceptionFactory;
import org.apache.batik.css.CSSOMValue;
import org.apache.batik.css.PropertyMap;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'text-decoration' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TextDecorationFactory
    extends    AbstractValueFactory
    implements ValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_BLINK_VALUE,        BLINK_VALUE);
	values.put(CSS_LINE_THROUGH_VALUE, LINE_THROUGH_VALUE);
	values.put(CSS_OVERLINE_VALUE,     OVERLINE_VALUE);
	values.put(CSS_UNDERLINE_VALUE,    UNDERLINE_VALUE);
    }

    /**
     * The identifier factory.
     */
    protected IdentFactory identFactory = new IdentFactory(getParser());

    /**
     * Creates a new text decoration factory.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public TextDecorationFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_TEXT_DECORATION_PROPERTY;
    }
    
    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return INHERIT;
	case LexicalUnit.SAC_IDENT:
	    if (lu.getStringValue().equalsIgnoreCase(CSS_NONE_VALUE)) {
		return NONE_VALUE;
	    }
	    ImmutableValueList list = new ImmutableValueList(' ');
	    do {
		list.append(new CSSOMValue(identFactory,
                                           identFactory.createValue(lu)));
		lu = lu.getNextLexicalUnit();
	    } while (lu != null);
	    return list;
	default:
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.lexical.unit",
		 new Object[] { new Integer(lu.getLexicalUnitType()) });
	}
    }

    /**
     * Creates and returns a new string value.
     * @param type  A string code as defined in CSSPrimitiveValue. The string
     *   code can only be a string unit type.
     * @param value  The new string value. 
     */
    public ImmutableValue createStringValue(short type, String value)
	throws DOMException {
	if (type != CSSPrimitiveValue.CSS_IDENT ||
	    !value.equalsIgnoreCase(CSS_NONE_VALUE)) {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "invalid.identifier",
		 new Object[] { value });
	}
	return NONE_VALUE;
    }

    /**
     * This class provides a factory for the identifiers of the list.
     */
    protected class IdentFactory extends AbstractIdentifierFactory {
	/**
	 * Creates a new ident factory.
	 * @param p The CSS parser used to parse the CSS texts.
	 */
	public IdentFactory(Parser p) {
	    super(p);
	}

	/**
	 * Returns the name of the property handled.
	 */
	public String getPropertyName() {
	    return null;
	}
    
	/**
	 * Returns the property map that contains the possible values.
	 */
	protected PropertyMap getIdentifiers() {
	    return values;
	}
    }
}
