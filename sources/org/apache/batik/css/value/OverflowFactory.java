/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.PropertyMap;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'overflow' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class OverflowFactory extends AbstractIdentifierFactory {
    /**
     * The 'hidden' string.
     */
    public final static String HIDDEN = "hidden";

    /**
     * The 'hidden' identifier value.
     */
    public final static ImmutableValue HIDDEN_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, HIDDEN);

    /**
     * The 'scroll' string.
     */
    public final static String SCROLL = "scroll";

    /**
     * The 'scroll' identifier value.
     */
    public final static ImmutableValue SCROLL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SCROLL);

    /**
     * The 'visible' string.
     */
    public final static String VISIBLE = "visible";

    /**
     * The 'visible' identifier value.
     */
    public final static ImmutableValue VISIBLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, VISIBLE);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(AUTO,     AUTO_VALUE);
	values.put(HIDDEN,   HIDDEN_VALUE);
	values.put(SCROLL,   SCROLL_VALUE);
	values.put(VISIBLE,  VISIBLE_VALUE);
    }

    /**
     * Creates a new OverflowFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public OverflowFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "overflow";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
