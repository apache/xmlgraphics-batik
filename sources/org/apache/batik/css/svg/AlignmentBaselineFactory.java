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
 * This class provides a factory for the 'alignment-baseline' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AlignmentBaselineFactory extends AbstractIdentifierFactory {
    /**
     * The 'after-edge' string.
     */
    public final static String AFTER_EDGE = "after-edge";

    /**
     * The 'after-edge' keyword.
     */
    public final static ImmutableValue AFTER_EDGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, AFTER_EDGE);

    /**
     * The 'baseline' string.
     */
    public final static String BASELINE = "baseline";

    /**
     * The 'baseline' keyword.
     */
    public final static ImmutableValue BASELINE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BASELINE);

    /**
     * The 'before-edge' string.
     */
    public final static String BEFORE_EDGE = "before-edge";

    /**
     * The 'before-edge' keyword.
     */
    public final static ImmutableValue BEFORE_EDGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BEFORE_EDGE);

    /**
     * The 'bottom' string.
     */
    public final static String BOTTOM = "bottom";

    /**
     * The 'bottom' keyword.
     */
    public final static ImmutableValue BOTTOM_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BOTTOM);

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
     * The 'mathematical' string.
     */
    public final static String MATHEMATICAL = "mathematical";

    /**
     * The 'mathematical' keyword.
     */
    public final static ImmutableValue MATHEMATICAL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, MATHEMATICAL);

    /**
     * The 'middle' string.
     */
    public final static String MIDDLE = "middle";

    /**
     * The 'middle' keyword.
     */
    public final static ImmutableValue MIDDLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, MIDDLE);

    /**
     * The 'text-after-edge' string.
     */
    public final static String TEXT_AFTER_EDGE = "text-after-edge";

    /**
     * The 'text-after-edge' keyword.
     */
    public final static ImmutableValue TEXT_AFTER_EDGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TEXT_AFTER_EDGE);

    /**
     * The 'text-before-edge' string.
     */
    public final static String TEXT_BEFORE_EDGE = "text-before-edge";

    /**
     * The 'text-before-edge' keyword.
     */
    public final static ImmutableValue TEXT_BEFORE_EDGE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TEXT_BEFORE_EDGE);

    /**
     * The 'text-bottom' string.
     */
    public final static String TEXT_BOTTOM = "text-bottom";

    /**
     * The 'text-bottom' keyword.
     */
    public final static ImmutableValue TEXT_BOTTOM_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TEXT_BOTTOM);

    /**
     * The 'text-top' string.
     */
    public final static String TEXT_TOP = "text-top";

    /**
     * The 'text-top' keyword.
     */
    public final static ImmutableValue TEXT_TOP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TEXT_TOP);

    /**
     * The 'top' string.
     */
    public final static String TOP = "top";

    /**
     * The 'top' keyword.
     */
    public final static ImmutableValue TOP_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, TOP);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(AFTER_EDGE,        AFTER_EDGE_VALUE);
	values.put(BASELINE,          BASELINE_VALUE);
	values.put(BEFORE_EDGE,       BEFORE_EDGE_VALUE);
	values.put(BOTTOM,            BOTTOM_VALUE);
	values.put(HANGING,           HANGING_VALUE);
	values.put(IDEOGRAPHIC,       IDEOGRAPHIC_VALUE);
	values.put(MATHEMATICAL,      MATHEMATICAL_VALUE);
	values.put(MIDDLE,            MIDDLE_VALUE);
	values.put(TEXT_AFTER_EDGE,   TEXT_AFTER_EDGE_VALUE);
	values.put(TEXT_BEFORE_EDGE,  TEXT_BEFORE_EDGE_VALUE);
	values.put(TEXT_BOTTOM,       TEXT_BOTTOM_VALUE);
	values.put(TEXT_TOP,          TEXT_TOP_VALUE);
	values.put(TOP,               TOP_VALUE);
    }

    /**
     * Creates a new AlignmentBaselineFactory object.
     */
    public AlignmentBaselineFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "alignment-baseline";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
