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
 * This class provides a factory for the 'font-stretch' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontStretchFactory extends AbstractIdentifierFactory {
    /**
     * The 'condensed' string.
     */
    public final static String CONDENSED = "condensed";

    /**
     * The 'condensed' identifier value.
     */
    public final static ImmutableValue CONDENSED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, CONDENSED);

    /**
     * The 'expanded' string.
     */
    public final static String EXPANDED = "expanded";

    /**
     * The 'expanded' identifier value.
     */
    public final static ImmutableValue EXPANDED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, EXPANDED);

    /**
     * The 'extra-condensed' string.
     */
    public final static String EXTRA_CONDENSED = "extra-condensed";

    /**
     * The 'extra-condensed' identifier value.
     */
    public final static ImmutableValue EXTRA_CONDENSED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, EXTRA_CONDENSED);

    /**
     * The 'extra-expanded' string.
     */
    public final static String EXTRA_EXPANDED = "extra-expanded";

    /**
     * The 'extra-expanded' identifier value.
     */
    public final static ImmutableValue EXTRA_EXPANDED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, EXTRA_EXPANDED);

    /**
     * The 'narrower' string.
     */
    public final static String NARROWER = "narrower";

    /**
     * The 'narrower' identifier value.
     */
    public final static ImmutableValue NARROWER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, NARROWER);

    /**
     * The 'semi-condensed' string.
     */
    public final static String SEMI_CONDENSED = "semi-condensed";

    /**
     * The 'semi-condensed' identifier value.
     */
    public final static ImmutableValue SEMI_CONDENSED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SEMI_CONDENSED);

    /**
     * The 'semi-expanded' string.
     */
    public final static String SEMI_EXPANDED = "semi-expanded";

    /**
     * The 'semi-expanded' identifier value.
     */
    public final static ImmutableValue SEMI_EXPANDED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SEMI_EXPANDED);

    /**
     * The 'ultra-condensed' string.
     */
    public final static String ULTRA_CONDENSED = "ultra-condensed";

    /**
     * The 'ultra-condensed' identifier value.
     */
    public final static ImmutableValue ULTRA_CONDENSED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, ULTRA_CONDENSED);

    /**
     * The 'ultra-expanded' string.
     */
    public final static String ULTRA_EXPANDED = "ultra-expanded";

    /**
     * The 'ultra-expanded' identifier value.
     */
    public final static ImmutableValue ULTRA_EXPANDED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, ULTRA_EXPANDED);

    /**
     * The 'wider' string.
     */
    public final static String WIDER = "wider";

    /**
     * The 'wider' identifier value.
     */
    public final static ImmutableValue WIDER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, WIDER);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CONDENSED,       CONDENSED_VALUE);
	values.put(EXPANDED,        EXPANDED_VALUE);
	values.put(EXTRA_CONDENSED, EXTRA_CONDENSED_VALUE);
	values.put(EXTRA_EXPANDED,  EXTRA_EXPANDED_VALUE);
	values.put(NARROWER,        NARROWER_VALUE);
	values.put(NORMAL,          NORMAL_VALUE);
	values.put(SEMI_CONDENSED,  SEMI_CONDENSED_VALUE);
	values.put(SEMI_EXPANDED,   SEMI_EXPANDED_VALUE);
	values.put(ULTRA_CONDENSED, ULTRA_CONDENSED_VALUE);
	values.put(ULTRA_EXPANDED,  ULTRA_EXPANDED_VALUE);
	values.put(WIDER,           WIDER_VALUE);
    }

    /**
     * Creates a new FontStretchFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public FontStretchFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "font-stretch";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
