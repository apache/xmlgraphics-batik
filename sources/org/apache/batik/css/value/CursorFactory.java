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
public class CursorFactory extends AbstractIdentifierFactory {
    /**
     * The 'crosshair' string.
     */
    public final static String CROSSHAIR = "crosshair";

    /**
     * The 'crosshair' identifier value.
     */
    public final static ImmutableValue CROSSHAIR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, CROSSHAIR);

    /**
     * The 'default' string.
     */
    public final static String DEFAULT = "default";

    /**
     * The 'default' identifier value.
     */
    public final static ImmutableValue DEFAULT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, DEFAULT);

    /**
     * The 'e-resize' string.
     */
    public final static String E_RESIZE = "e-resize";

    /**
     * The 'e-resize' identifier value.
     */
    public final static ImmutableValue E_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, E_RESIZE);

    /**
     * The 'help' string.
     */
    public final static String HELP = "help";

    /**
     * The 'help' identifier value.
     */
    public final static ImmutableValue HELP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, HELP);

    /**
     * The 'move' string.
     */
    public final static String MOVE = "move";

    /**
     * The 'move' identifier value.
     */
    public final static ImmutableValue MOVE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, MOVE);

    /**
     * The 'n-resize' string.
     */
    public final static String N_RESIZE = "n-resize";

    /**
     * The 'n-resize' identifier value.
     */
    public final static ImmutableValue N_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, N_RESIZE);

    /**
     * The 'ne-resize' string.
     */
    public final static String NE_RESIZE = "ne-resize";

    /**
     * The 'ne-resize' identifier value.
     */
    public final static ImmutableValue NE_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, NE_RESIZE);

    /**
     * The 'nw-resize' string.
     */
    public final static String NW_RESIZE = "nw-resize";

    /**
     * The 'nw-resize' identifier value.
     */
    public final static ImmutableValue NW_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, NW_RESIZE);

    /**
     * The 'pointer' string.
     */
    public final static String POINTER = "pointer";

    /**
     * The 'pointer' identifier value.
     */
    public final static ImmutableValue POINTER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, POINTER);

    /**
     * The 's-resize' string.
     */
    public final static String S_RESIZE = "s-resize";

    /**
     * The 's-resize' identifier value.
     */
    public final static ImmutableValue S_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, S_RESIZE);

    /**
     * The 'se-resize' string.
     */
    public final static String SE_RESIZE = "se-resize";

    /**
     * The 'se-resize' identifier value.
     */
    public final static ImmutableValue SE_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SE_RESIZE);

    /**
     * The 'sw-resize' string.
     */
    public final static String SW_RESIZE = "sw-resize";

    /**
     * The 'sw-resize' identifier value.
     */
    public final static ImmutableValue SW_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SW_RESIZE);

    /**
     * The 'text' string.
     */
    public final static String TEXT = "text";

    /**
     * The 'text' identifier value.
     */
    public final static ImmutableValue TEXT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TEXT);

    /**
     * The 'w-resize' string.
     */
    public final static String W_RESIZE = "w-resize";

    /**
     * The 'w-resize' identifier value.
     */
    public final static ImmutableValue W_RESIZE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, W_RESIZE);

    /**
     * The 'wait' string.
     */
    public final static String WAIT = "wait";

    /**
     * The 'wait' identifier value.
     */
    public final static ImmutableValue WAIT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, WAIT);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(AUTO,       AUTO_VALUE);
	values.put(CROSSHAIR,  CROSSHAIR_VALUE);
	values.put(DEFAULT,    DEFAULT_VALUE);
	values.put(E_RESIZE,   E_RESIZE_VALUE);
	values.put(HELP,       HELP_VALUE);
	values.put(MOVE,       MOVE_VALUE);
	values.put(N_RESIZE,   N_RESIZE_VALUE);
	values.put(NE_RESIZE,  NE_RESIZE_VALUE);
	values.put(NW_RESIZE,  NW_RESIZE_VALUE);
	values.put(POINTER,    POINTER_VALUE);
	values.put(S_RESIZE,   S_RESIZE_VALUE);
	values.put(SE_RESIZE,  SE_RESIZE_VALUE);
	values.put(SW_RESIZE,  SW_RESIZE_VALUE);
	values.put(TEXT,       TEXT_VALUE);
	values.put(W_RESIZE,   W_RESIZE_VALUE);
	values.put(WAIT,       WAIT_VALUE);
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
	return "cursor";
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
