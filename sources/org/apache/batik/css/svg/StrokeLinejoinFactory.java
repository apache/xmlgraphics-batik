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
 * This class provides a factory for the 'stroke-linejoin' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StrokeLinejoinFactory
    extends    AbstractIdentifierFactory
    implements SVGValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_MITER_VALUE,  MITER_VALUE);
	values.put(CSS_ROUND_VALUE,  ROUND_VALUE);
	values.put(CSS_BEVEL_VALUE,  BEVEL_VALUE);
    }

    /**
     * Creates a new StrokeLinejoinFactory object.
     */
    public StrokeLinejoinFactory(Parser p) {
	super(p);
    }

     /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_STROKE_LINEJOIN_PROPERTY;
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
