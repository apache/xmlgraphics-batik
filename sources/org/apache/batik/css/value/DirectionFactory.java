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
 * This class provides a factory for the 'direction' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DirectionFactory extends AbstractIdentifierFactory {
    /**
     * The 'ltr' string.
     */
    public final static String LTR = "ltr";

    /**
     * The 'ltr' keyword.
     */
    public final static ImmutableValue LTR_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, LTR);

    /**
     * The 'rtl' string.
     */
    public final static String RTL = "rtl";

    /**
     * The 'rtl' keyword.
     */
    public final static ImmutableValue RTL_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, RTL);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(LTR, LTR_VALUE);
	values.put(RTL, RTL_VALUE);
    }

    /**
     * Creates a new DirectionFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public DirectionFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "direction";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
