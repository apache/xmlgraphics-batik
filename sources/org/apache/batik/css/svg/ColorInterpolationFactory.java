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
 * This class provides a factory for the 'color-interpolation' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ColorInterpolationFactory extends AbstractIdentifierFactory {
    /**
     * The 'linearRGB' string.
     */
    public final static String LINEARRGB = "linearrgb";

    /**
     * The 'linearRGB' keyword.
     */
    public final static ImmutableValue LINEARRGB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LINEARRGB);

    /**
     * The 'sRGB' string.
     */
    public final static String SRGB = "srgb";

    /**
     * The 'sRGB' keyword.
     */
    public final static ImmutableValue SRGB_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, SRGB);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(AUTO,      AUTO_VALUE);
	values.put(LINEARRGB, LINEARRGB_VALUE);
	values.put(SRGB,      SRGB_VALUE);
    }

    /**
     * Creates a new ColorInterpolationFactory object.
     */
    public ColorInterpolationFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "color-interpolation";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
