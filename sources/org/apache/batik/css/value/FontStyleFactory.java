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
 * This class provides a factory for the 'font-style' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontStyleFactory extends AbstractIdentifierFactory {
    /**
     * The 'italic' string.
     */
    public final static String ITALIC = "italic";

    /**
     * The 'italic' identifier value.
     */
    public final static ImmutableValue ITALIC_VALUE =
        new ImmutableString(CSSPrimitiveValue.CSS_IDENT, ITALIC);

    /**
     * The 'oblique' string.
     */
    public final static String OBLIQUE = "oblique";

    /**
     * The 'oblique' identifier value.
     */
    public final static ImmutableValue OBLIQUE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, OBLIQUE);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(ITALIC,  ITALIC_VALUE);
	values.put(NORMAL,  NORMAL_VALUE);
	values.put(OBLIQUE, OBLIQUE_VALUE);
    }

    /**
     * Creates a new FontStyleFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public FontStyleFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "font-style";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
