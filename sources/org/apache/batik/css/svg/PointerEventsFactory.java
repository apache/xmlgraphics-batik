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
public class PointerEventsFactory
    extends    AbstractIdentifierFactory
    implements SVGValueConstants {

    /**
     * The identifier values.
     */
    protected final static PropertyMap values = new PropertyMap();
    static {
	values.put(CSS_ALL_VALUE,                ALL_VALUE);
	values.put(CSS_FILL_VALUE,               FILL_VALUE);
	values.put(CSS_FILLSTROKE_VALUE,         FILLSTROKE_VALUE);
	values.put(CSS_NONE_VALUE,               NONE_VALUE);
	values.put(CSS_PAINTED_VALUE,            PAINTED_VALUE);
	values.put(CSS_STROKE_VALUE,             STROKE_VALUE);
	values.put(CSS_VISIBLE_VALUE,            VISIBLE_VALUE);
	values.put(CSS_VISIBLEFILL_VALUE,        VISIBLEFILL_VALUE);
	values.put(CSS_VISIBLEFILLSTROKE_VALUE,  VISIBLEFILLSTROKE_VALUE);
	values.put(CSS_VISIBLEPAINTED_VALUE,     VISIBLEPAINTED_VALUE);
	values.put(CSS_VISIBLESTROKE_VALUE,      VISIBLESTROKE_VALUE);
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
	return CSS_POINTER_EVENTS_PROPERTY;
    }
    
    /**
     * Returns the property map that contains the possible values.
     */
    protected PropertyMap getIdentifiers() {
	return values;
    }
}
