/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.util.CSSConstants;

import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;

/**
 * This class provides a manager for the 'pointer-events' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PointerEventsManager extends IdentifierManager {
    
    /**
     * The identifier values.
     */
    protected final static StringMap values = new StringMap();
    static {
	values.put(CSSConstants.CSS_ALL_VALUE,
                   SVGValueConstants.ALL_VALUE);
	values.put(CSSConstants.CSS_FILL_VALUE,
                   SVGValueConstants.FILL_VALUE);
	values.put(CSSConstants.CSS_FILLSTROKE_VALUE,
                   SVGValueConstants.FILLSTROKE_VALUE);
	values.put(CSSConstants.CSS_NONE_VALUE,
                   SVGValueConstants.NONE_VALUE);
	values.put(CSSConstants.CSS_PAINTED_VALUE,
                   SVGValueConstants.PAINTED_VALUE);
	values.put(CSSConstants.CSS_STROKE_VALUE,
                   SVGValueConstants.STROKE_VALUE);
	values.put(CSSConstants.CSS_VISIBLE_VALUE,
                   SVGValueConstants.VISIBLE_VALUE);
	values.put(CSSConstants.CSS_VISIBLEFILL_VALUE,
                   SVGValueConstants.VISIBLEFILL_VALUE);
	values.put(CSSConstants.CSS_VISIBLEFILLSTROKE_VALUE,
                   SVGValueConstants.VISIBLEFILLSTROKE_VALUE);
	values.put(CSSConstants.CSS_VISIBLEPAINTED_VALUE,
                   SVGValueConstants.VISIBLEPAINTED_VALUE);
	values.put(CSSConstants.CSS_VISIBLESTROKE_VALUE,
                   SVGValueConstants.VISIBLESTROKE_VALUE);
    }

    /**
     * Implements {@link
     * org.apache.batik.css.engine.value.ValueManager#isInheritedProperty()}.
     */
    public boolean isInheritedProperty() {
	return true;
    }

    /**
     * Implements {@link
     * org.apache.batik.css.engine.value.ValueManager#getPropertyName()}.
     */
    public String getPropertyName() {
	return CSSConstants.CSS_POINTER_EVENTS_PROPERTY;
    }
    
    /**
     * Implements {@link
     * org.apache.batik.css.engine.value.ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return SVGValueConstants.VISIBLEPAINTED_VALUE;
    }

    /**
     * Implements {@link IdentifierManager#getIdentifiers()}.
     */
    protected StringMap getIdentifiers() {
        return values;
    }
}
