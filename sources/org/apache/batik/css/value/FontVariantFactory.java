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
 * This class provides a factory for the 'font-variant' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontVariantFactory extends AbstractIdentifierFactory {
    /**
     * The 'small-caps' string.
     */
    public final static String SMALL_CAPS = "small-caps";

    /**
     * The 'small-caps' identifier value.
     */
    public final static ImmutableValue SMALL_CAPS_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SMALL_CAPS);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(NORMAL,     NORMAL_VALUE);
	values.put(SMALL_CAPS, SMALL_CAPS_VALUE);
    }

    /**
     * Creates a new FontVariantFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public FontVariantFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "font-variant";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
