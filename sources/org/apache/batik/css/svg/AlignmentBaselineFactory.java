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
public class AlignmentBaselineFactory
    extends    AbstractIdentifierFactory
    implements SVGValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_AFTER_EDGE_VALUE,        AFTER_EDGE_VALUE);
	values.put(CSS_BASELINE_VALUE,          BASELINE_VALUE);
	values.put(CSS_BEFORE_EDGE_VALUE,       BEFORE_EDGE_VALUE);
	values.put(CSS_BOTTOM_VALUE,            BOTTOM_VALUE);
	values.put(CSS_HANGING_VALUE,           HANGING_VALUE);
	values.put(CSS_IDEOGRAPHIC_VALUE,       IDEOGRAPHIC_VALUE);
	values.put(CSS_MATHEMATICAL_VALUE,      MATHEMATICAL_VALUE);
	values.put(CSS_MIDDLE_VALUE,            MIDDLE_VALUE);
	values.put(CSS_TEXT_AFTER_EDGE_VALUE,   TEXT_AFTER_EDGE_VALUE);
	values.put(CSS_TEXT_BEFORE_EDGE_VALUE,  TEXT_BEFORE_EDGE_VALUE);
	values.put(CSS_TEXT_BOTTOM_VALUE,       TEXT_BOTTOM_VALUE);
	values.put(CSS_TEXT_TOP_VALUE,          TEXT_TOP_VALUE);
	values.put(CSS_TOP_VALUE,               TOP_VALUE);
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
	return CSS_ALIGNMENT_BASELINE_PROPERTY;
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
