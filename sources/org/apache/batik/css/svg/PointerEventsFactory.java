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
 * This class provides a factory for the 'pointer-events' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PointerEventsFactory extends AbstractIdentifierFactory {
    /**
     * The 'all' string.
     */
    public final static String ALL = "all";

    /**
     * The 'all' keyword.
     */
    public final static ImmutableValue ALL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, ALL);

    /**
     * The 'fill' string.
     */
    public final static String FILL = "fill";

    /**
     * The 'fill' keyword.
     */
    public final static ImmutableValue FILL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, FILL);

    /**
     * The 'fillstroke' string.
     */
    public final static String FILLSTROKE = "fillstroke";

    /**
     * The 'fillstroke' keyword.
     */
    public final static ImmutableValue FILLSTROKE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, FILLSTROKE);

    /**
     * The 'painted' string.
     */
    public final static String PAINTED = "painted";

    /**
     * The 'painted' keyword.
     */
    public final static ImmutableValue PAINTED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, PAINTED);

    /**
     * The 'stroke' string.
     */
    public final static String STROKE = "stroke";

    /**
     * The 'stroke' keyword.
     */
    public final static ImmutableValue STROKE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, STROKE);

    /**
     * The 'visible' string.
     */
    public final static String VISIBLE = "visible";

    /**
     * The 'visible' keyword.
     */
    public final static ImmutableValue VISIBLE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, VISIBLE);

    /**
     * The 'visiblefill' string.
     */
    public final static String VISIBLEFILL = "visiblefill";

    /**
     * The 'visiblefill' keyword.
     */
    public final static ImmutableValue VISIBLEFILL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, VISIBLEFILL);

    /**
     * The 'visiblefillstroke' string.
     */
    public final static String VISIBLEFILLSTROKE = "visiblefillstroke";

    /**
     * The 'visiblefillstroke' keyword.
     */
    public final static ImmutableValue VISIBLEFILLSTROKE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, VISIBLEFILLSTROKE);

    /**
     * The 'visiblepainted' string.
     */
    public final static String VISIBLEPAINTED = "visiblepainted";

    /**
     * The 'visiblepainted' keyword.
     */
    public final static ImmutableValue VISIBLEPAINTED_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, VISIBLEPAINTED);

    /**
     * The 'visiblestroke' string.
     */
    public final static String VISIBLESTROKE = "visiblestroke";

    /**
     * The 'visiblestroke' keyword.
     */
    public final static ImmutableValue VISIBLESTROKE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, VISIBLESTROKE);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(ALL,                ALL_VALUE);
	values.put(FILL,               FILL_VALUE);
	values.put(FILLSTROKE,         FILLSTROKE_VALUE);
	values.put(NONE,               NONE_VALUE);
	values.put(PAINTED,            PAINTED_VALUE);
	values.put(STROKE,             STROKE_VALUE);
	values.put(VISIBLE,            VISIBLE_VALUE);
	values.put(VISIBLEFILL,        VISIBLEFILL_VALUE);
	values.put(VISIBLEFILLSTROKE,  VISIBLEFILLSTROKE_VALUE);
	values.put(VISIBLEPAINTED,     VISIBLEPAINTED_VALUE);
	values.put(VISIBLESTROKE,      VISIBLESTROKE_VALUE);
    }

    /**
     * Creates a new PointerEventsFactory object.
     */
    public PointerEventsFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "pointer-events";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
