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
 * This class provides a factory for the 'font-stretch' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontStretchFactory
    extends    AbstractIdentifierFactory
    implements ValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_CONDENSED_VALUE,       CONDENSED_VALUE);
	values.put(CSS_EXPANDED_VALUE,        EXPANDED_VALUE);
	values.put(CSS_EXTRA_CONDENSED_VALUE, EXTRA_CONDENSED_VALUE);
	values.put(CSS_EXTRA_EXPANDED_VALUE,  EXTRA_EXPANDED_VALUE);
	values.put(CSS_NARROWER_VALUE,        NARROWER_VALUE);
	values.put(CSS_NORMAL_VALUE,          NORMAL_VALUE);
	values.put(CSS_SEMI_CONDENSED_VALUE,  SEMI_CONDENSED_VALUE);
	values.put(CSS_SEMI_EXPANDED_VALUE,   SEMI_EXPANDED_VALUE);
	values.put(CSS_ULTRA_CONDENSED_VALUE, ULTRA_CONDENSED_VALUE);
	values.put(CSS_ULTRA_EXPANDED_VALUE,  ULTRA_EXPANDED_VALUE);
	values.put(CSS_WIDER_VALUE,           WIDER_VALUE);
    }

    /**
     * Creates a new FontStretchFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public FontStretchFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_FONT_STRETCH_PROPERTY;
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
