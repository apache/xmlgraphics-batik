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
 * This class provides a factory for the 'visibility' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class VisibilityFactory
    extends    AbstractIdentifierFactory
    implements ValueFactory {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_COLLAPSE_VALUE, COLLAPSE_VALUE);
	values.put(CSS_HIDDEN_VALUE,   HIDDEN_VALUE);
	values.put(CSS_VISIBLE_VALUE,  VISIBLE_VALUE);
    }

    /**
     * Creates a new VisibilityFactory object.
     * @param p The CSS parser used to parse the CSS texts.
     */
    public VisibilityFactory(Parser p) {
	super(p);
    }

    /**
     * Returns the name of the property handled.
     */
    public String getPropertyName() {
	return CSS_VISIBILITY_PROPERTY;
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
