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
 * This class provides a factory for the 'stroke-linecap' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StrokeLinecapFactory extends AbstractIdentifierFactory {
    /**
     * The 'butt' keyword.
     */
    public final static String BUTT = "butt";

    /**
     * The 'butt' keyword.
     */
    public final static ImmutableValue BUTT_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, BUTT);

    /**
     * The 'round' keyword.
     */
    public final static String ROUND = "round";

    /**
     * The 'round' keyword.
     */
    public final static ImmutableValue ROUND_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, ROUND);

    /**
     * The 'square' keyword.
     */
    public final static String SQUARE = "square";

    /**
     * The 'square' keyword.
     */
    public final static ImmutableValue SQUARE_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SQUARE);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(BUTT,   BUTT_VALUE);
	values.put(ROUND,  ROUND_VALUE);
	values.put(SQUARE, SQUARE_VALUE);
    }

    /**
     * Creates a new StrokeLinecapFactory object.
     */
    public StrokeLinecapFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "stroke-linecap";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
