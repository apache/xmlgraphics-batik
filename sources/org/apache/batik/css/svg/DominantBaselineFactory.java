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
public class DominantBaselineFactory
    extends    AbstractIdentifierFactory
    implements SVGValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_AUTO_VALUE,             AUTO_VALUE);
	values.put(CSS_AUTOSENSE_SCRIPT_VALUE, AUTOSENSE_SCRIPT_VALUE);
	values.put(CSS_HANGING_VALUE,          HANGING_VALUE);
	values.put(CSS_IDEOGRAPHIC_VALUE,      IDEOGRAPHIC_VALUE);
	values.put(CSS_LOWER_VALUE,            LOWER_VALUE);
	values.put(CSS_MATHEMATICAL_VALUE,     MATHEMATICAL_VALUE);
	values.put(CSS_NO_CHANGE_VALUE,        NO_CHANGE_VALUE);
	values.put(CSS_RESET_VALUE,            RESET_VALUE);
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
	return CSS_DOMINANT_BASELINE_PROPERTY;
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
