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
public class TextDecorationFactory extends AbstractValueFactory {
    /**
     * The 'blink' string.
     */
    public final static String BLINK = "blink";

    /**
     * The 'blink' identifier value.
     */
    public final static ImmutableValue BLINK_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BLINK);

    /**
     * The 'line-through' string.
     */
    public final static String LINE_THROUGH = "line-through";

    /**
     * The 'line-through' identifier value.
     */
    public final static ImmutableValue LINE_THROUGH_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LINE_THROUGH);

    /**
     * The 'overline' string.
     */
    public final static String OVERLINE = "overline";

    /**
     * The 'overline' identifier value.
     */
    public final static ImmutableValue OVERLINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, OVERLINE);

    /**
     * The 'underline' string.
     */
    public final static String UNDERLINE = "underline";

    /**
     * The 'underline' identifier value.
     */
    public final static ImmutableValue UNDERLINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, UNDERLINE);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(BLINK,        BLINK_VALUE);
	values.put(LINE_THROUGH, LINE_THROUGH_VALUE);
	values.put(OVERLINE,     OVERLINE_VALUE);
	values.put(UNDERLINE,    UNDERLINE_VALUE);
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
	return "text-decoration";
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
	    if (lu.getStringValue().equalsIgnoreCase(NONE)) {
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
	    !value.equalsIgnoreCase(NONE)) {
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
