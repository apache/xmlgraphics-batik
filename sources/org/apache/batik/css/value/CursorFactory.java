/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMValue;
import org.apache.batik.css.PropertyMap;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'cursor' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CursorFactory
    extends    AbstractIdentifierFactory
    implements ValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_AUTO_VALUE,       AUTO_VALUE);
	values.put(CSS_CROSSHAIR_VALUE,  CROSSHAIR_VALUE);
	values.put(CSS_DEFAULT_VALUE,    DEFAULT_VALUE);
	values.put(CSS_E_RESIZE_VALUE,   E_RESIZE_VALUE);
	values.put(CSS_HELP_VALUE,       HELP_VALUE);
	values.put(CSS_MOVE_VALUE,       MOVE_VALUE);
	values.put(CSS_N_RESIZE_VALUE,   N_RESIZE_VALUE);
	values.put(CSS_NE_RESIZE_VALUE,  NE_RESIZE_VALUE);
	values.put(CSS_NW_RESIZE_VALUE,  NW_RESIZE_VALUE);
	values.put(CSS_POINTER_VALUE,    POINTER_VALUE);
	values.put(CSS_S_RESIZE_VALUE,   S_RESIZE_VALUE);
	values.put(CSS_SE_RESIZE_VALUE,  SE_RESIZE_VALUE);
	values.put(CSS_SW_RESIZE_VALUE,  SW_RESIZE_VALUE);
	values.put(CSS_TEXT_VALUE,       TEXT_VALUE);
	values.put(CSS_W_RESIZE_VALUE,   W_RESIZE_VALUE);
	values.put(CSS_WAIT_VALUE,       WAIT_VALUE);
    }

    /**
     * The URI factory.
     */
    protected ValueFactory uriFactory = new URIFactory(getParser());

    /**
     * The identifier factory.
     */
    protected ValueFactory identFactory = new IdentifierFactory(getParser());

    /**
     * Creates a new CursorFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public CursorFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_CURSOR_PROPERTY;
    }
    
    /**
     * Creates a value from a lexical unit.
     * @param lu The SAC lexical unit used to create the value.
     */
    public ImmutableValue createValue(LexicalUnit lu) throws DOMException {
	if (lu.getLexicalUnitType() == LexicalUnit.SAC_URI) {
	    ImmutableValueList list = new ImmutableValueList();
	    do {
		list.append(new CSSOMValue(uriFactory,
                                           uriFactory.createValue(lu)));
		lu = lu.getNextLexicalUnit().getNextLexicalUnit();
	    } while (lu.getLexicalUnitType() == LexicalUnit.SAC_URI);
	    list.append(new CSSOMValue(identFactory,
                                       identFactory.createValue(lu)));
	    return list;
	}
	return super.createValue(lu);
    }

    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }

    /**
     * To manage the identifier values.
     */
    protected class IdentifierFactory extends AbstractValueFactory {
	/**
	 * Creates a new URIFactory object.
	 * @param p The CSS parser used to parse the CSS texts.
	 */
	public IdentifierFactory(Parser p) {
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
	    return CursorFactory.super.createValue(lu);
	}

	/**
	 * Creates and returns a new string value.
	 */
	public ImmutableValue createStringValue(short type, String value)
	    throws DOMException {
	    return CursorFactory.super.createStringValue(type, value);
	}
    }
}
