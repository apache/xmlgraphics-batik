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
 * This class provides a factory for the 'font-family' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontFamilyFactory
    extends    AbstractValueFactory
    implements ValueFactory {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_CURSIVE_VALUE,    CURSIVE_VALUE);
	values.put(CSS_FANTASY_VALUE,    FANTASY_VALUE);
	values.put(CSS_MONOSPACED_VALUE, MONOSPACED_VALUE);
	values.put(CSS_SERIF_VALUE,      SERIF_VALUE);
	values.put(CSS_SANS_SERIF_VALUE, SANS_SERIF_VALUE);
    }

    /**
     * The identifier factory.
     */
    protected IdentFactory identFactory = new IdentFactory(getParser());

    /**
     * The string factory.
     */
    protected ValueFactory stringFactory = new StringFactory(getParser());

    /**
     * Creates a new FontFamilyFactory object.
     */
    public FontFamilyFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_FONT_FAMILY_PROPERTY;
    }

    /**
     * Creates a value from a lexical unit.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
        switch (lu.getLexicalUnitType()) {
        case LexicalUnit.SAC_INHERIT:
            return INHERIT;
        case LexicalUnit.SAC_IDENT:
        case LexicalUnit.SAC_STRING_VALUE:
	    ImmutableValueList list = new ImmutableValueList();
	    do {
		if (lu.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE) {
		    list.append(new CSSOMValue(stringFactory,
					       stringFactory.createValue(lu)));
		    if (lu != null) {
			lu = lu.getNextLexicalUnit();
		    }
		} else {
		    LexicalUnit l = lu;
		    String s = l.getStringValue();
		    lu = lu.getNextLexicalUnit();
		    if (lu != null &&
                        lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
			do {
			    s += " " + lu.getStringValue();
			    lu = lu.getNextLexicalUnit();
			} while (lu != null &&
				 lu.getLexicalUnitType() ==
                                 LexicalUnit.SAC_IDENT);
			ImmutableValue v;
			v = new ImmutableString(CSSPrimitiveValue.CSS_STRING,
                                                s);
			list.append(new CSSOMValue(stringFactory, v));
		    } else {
			if (values.get(s.toLowerCase().intern()) != null) {
			    list.append
                                (new CSSOMValue(identFactory,
                                                identFactory.createValue(l)));
			} else {
			    s = "\"" + s + "\"";
			    list.append
                                (new CSSOMValue(stringFactory,
                                                stringFactory.createValue(s)));
			}
		    }
		}
		if (lu != null) {
		    lu = lu.getNextLexicalUnit();
		}
	    } while (lu != null);
	    return list;
        }
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.INVALID_ACCESS_ERR,
	     "invalid.lexical.unit",
	     new Object[] { new Integer(lu.getLexicalUnitType()),
                            getPropertyName() });
    }

    /**
     * This class provides a factory for the identifiers of the list.
     */
    protected class IdentFactory extends AbstractIdentifierFactory {
	/**
	 * Creates a new ident factory.
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
