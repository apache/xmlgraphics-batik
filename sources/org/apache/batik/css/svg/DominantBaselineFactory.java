/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.PropertyMap;
import org.apache.batik.css.value.AbstractIdentifierFactory;
import org.apache.batik.css.value.ImmutableString;
import org.apache.batik.css.value.ImmutableValue;
import org.w3c.css.sac.Parser;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a factory for the 'dominant-baseline' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DominantBaselineFactory extends AbstractIdentifierFactory {
    /**
     * The 'autosense-script' string.
     */
    public final static String AUTOSENSE_SCRIPT = "autosense-script";

    /**
     * The 'autosense-script' keyword.
     */
    public final static ImmutableValue AUTOSENSE_SCRIPT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, AUTOSENSE_SCRIPT);

    /**
     * The 'hanging' string.
     */
    public final static String HANGING = "hanging";

    /**
     * The 'hanging' keyword.
     */
    public final static ImmutableValue HANGING_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, HANGING);

    /**
     * The 'ideographic' string.
     */
    public final static String IDEOGRAPHIC = "ideographic";

    /**
     * The 'ideographic' keyword.
     */
    public final static ImmutableValue IDEOGRAPHIC_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, IDEOGRAPHIC);

    /**
     * The 'lower' string.
     */
    public final static String LOWER = "lower";

    /**
     * The 'lower' keyword.
     */
    public final static ImmutableValue LOWER_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LOWER);

    /**
     * The 'mathematical' string.
     */
    public final static String MATHEMATICAL = "mathematical";

    /**
     * The 'mathematical' keyword.
     */
    public final static ImmutableValue MATHEMATICAL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, MATHEMATICAL);

    /**
     * The 'no-change' string.
     */
    public final static String NO_CHANGE = "no-change";

    /**
     * The 'no-change' keyword.
     */
    public final static ImmutableValue NO_CHANGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, NO_CHANGE);

    /**
     * The 'reset' string.
     */
    public final static String RESET = "reset";

    /**
     * The 'reset' keyword.
     */
    public final static ImmutableValue RESET_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, RESET);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(AUTO,             AUTO_VALUE);
	values.put(AUTOSENSE_SCRIPT, AUTOSENSE_SCRIPT_VALUE);
	values.put(HANGING,          HANGING_VALUE);
	values.put(IDEOGRAPHIC,      IDEOGRAPHIC_VALUE);
	values.put(LOWER,            LOWER_VALUE);
	values.put(MATHEMATICAL,     MATHEMATICAL_VALUE);
	values.put(NO_CHANGE,        NO_CHANGE_VALUE);
	values.put(RESET,            RESET_VALUE);
    }

    /**
     * Creates a new DominantBaselineFactory object.
     */
    public DominantBaselineFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "dominant-baseline";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
