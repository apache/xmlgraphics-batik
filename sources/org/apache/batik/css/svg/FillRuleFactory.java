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
 * This class provides a factory for the 'fill-rule' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FillRuleFactory extends AbstractIdentifierFactory {
    /**
     * The 'evenodd' keyword.
     */
    public final static String EVENODD = "evenodd";

    /**
     * The 'evenodd' keyword.
     */
    public final static ImmutableValue EVENODD_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, EVENODD);

    /**
     * The 'nonzero' keyword.
     */
    public final static String NONZERO = "nonzero";

    /**
     * The 'nonzero' keyword.
     */
    public final static ImmutableValue NONZERO_VALUE =
	new ImmutableString(CSSPrimitiveValue.CSS_IDENT, NONZERO);

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(EVENODD, EVENODD_VALUE);
	values.put(NONZERO, NONZERO_VALUE);
    }

    /**
     * Creates a new FillRuleFactory object.
     */
    public FillRuleFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return "fill-rule";
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
