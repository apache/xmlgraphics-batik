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
 * This class provides a factory for the 'unicode-bidi' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class UnicodeBidiFactory extends AbstractIdentifierFactory {
    /**
     * The 'bidi-override' string.
     */
    public final static String BIDI_OVERRIDE = "bidi-override";

    /**
     * The 'bidi-override' identifier value.
     */
    public final static ImmutableValue BIDI_OVERRIDE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BIDI_OVERRIDE);

    /**
     * The 'embed' string.
     */
    public final static String EMBED = "embed";

    /**
     * The 'embed' identifier value.
     */
    public final static ImmutableValue EMBED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, EMBED);

    /**
     * The 'normal' string.
     */
    public final static String NORMAL = "normal";

    /**
     * The 'normal' identifier value.
     */
    public final static ImmutableValue NORMAL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, NORMAL);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(BIDI_OVERRIDE, BIDI_OVERRIDE_VALUE);
	values.put(EMBED,         EMBED_VALUE);
	values.put(NORMAL,        NORMAL_VALUE);
    }

    /**
     * Creates a new UnicodeBidiFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public UnicodeBidiFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "unicode-bidi";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
