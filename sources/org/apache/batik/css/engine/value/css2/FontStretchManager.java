/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value.css2;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.util.CSSConstants;

/**
 * This class provides a manager for the 'font-stretch' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FontStretchManager extends IdentifierManager {
    
    /**
     * The identifier values.
     */
    protected final static StringMap values = new StringMap();
    static {
	values.put(CSSConstants.CSS_CONDENSED_VALUE,
                   ValueConstants.CONDENSED_VALUE);
	values.put(CSSConstants.CSS_EXPANDED_VALUE,
                   ValueConstants.EXPANDED_VALUE);
	values.put(CSSConstants.CSS_EXTRA_CONDENSED_VALUE,
                   ValueConstants.EXTRA_CONDENSED_VALUE);
	values.put(CSSConstants.CSS_EXTRA_EXPANDED_VALUE,
                   ValueConstants.EXTRA_EXPANDED_VALUE);
	values.put(CSSConstants.CSS_NARROWER_VALUE,
                   ValueConstants.NARROWER_VALUE);
	values.put(CSSConstants.CSS_NORMAL_VALUE,
                   ValueConstants.NORMAL_VALUE);
	values.put(CSSConstants.CSS_SEMI_CONDENSED_VALUE,
                   ValueConstants.SEMI_CONDENSED_VALUE);
	values.put(CSSConstants.CSS_SEMI_EXPANDED_VALUE,
                   ValueConstants.SEMI_EXPANDED_VALUE);
	values.put(CSSConstants.CSS_ULTRA_CONDENSED_VALUE,
                   ValueConstants.ULTRA_CONDENSED_VALUE);
	values.put(CSSConstants.CSS_ULTRA_EXPANDED_VALUE,
                   ValueConstants.ULTRA_EXPANDED_VALUE);
	values.put(CSSConstants.CSS_WIDER_VALUE,
                   ValueConstants.WIDER_VALUE);
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
	return CSSConstants.CSS_FONT_STRETCH_PROPERTY;
    }
    
    /**
     * Implements {@link
     * org.apache.batik.css.engine.value.ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return ValueConstants.NORMAL_VALUE;
    }

    /**
     * Implements {@link
     * ValueManager#computeValue(CSSStylableElement,String,CSSEngine,int,StyleMap,Value)}.
     */
    public Value computeValue(CSSStylableElement elt,
                              String pseudo,
                              CSSEngine engine,
                              int idx,
                              StyleMap sm,
                              Value value) { 
        if (value == ValueConstants.NARROWER_VALUE) {
            sm.putParentRelative(idx, true);

            CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
            if (p == null) {
                return ValueConstants.SEMI_CONDENSED_VALUE;
            }
            Value v = engine.getComputedStyle(p, pseudo, idx);
            if (v == ValueConstants.NORMAL_VALUE) {
                return ValueConstants.SEMI_CONDENSED_VALUE;
            }
            if (v == ValueConstants.CONDENSED_VALUE) {
                return ValueConstants.EXTRA_CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXPANDED_VALUE) {
                return ValueConstants.SEMI_EXPANDED_VALUE;
            }
            if (v == ValueConstants.SEMI_EXPANDED_VALUE) {
                return ValueConstants.NORMAL_VALUE;
            }
            if (v == ValueConstants.SEMI_CONDENSED_VALUE) {
                return ValueConstants.CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXTRA_CONDENSED_VALUE) {
                return ValueConstants.ULTRA_CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXTRA_EXPANDED_VALUE) {
                return ValueConstants.EXPANDED_VALUE;
            }
            if (v == ValueConstants.ULTRA_CONDENSED_VALUE) {
                return ValueConstants.ULTRA_CONDENSED_VALUE;
            }
            return ValueConstants.EXTRA_EXPANDED_VALUE;
        } else if (value == ValueConstants.WIDER_VALUE) {
            sm.putParentRelative(idx, true);

            CSSStylableElement p = CSSEngine.getParentCSSStylableElement(elt);
            if (p == null) {
                return ValueConstants.SEMI_CONDENSED_VALUE;
            }
            Value v = engine.getComputedStyle(p, pseudo, idx);
            if (v == ValueConstants.NORMAL_VALUE) {
                return ValueConstants.SEMI_EXPANDED_VALUE;
            }
            if (v == ValueConstants.CONDENSED_VALUE) {
                return ValueConstants.SEMI_CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXPANDED_VALUE) {
                return ValueConstants.EXTRA_EXPANDED_VALUE;
            }
            if (v == ValueConstants.SEMI_EXPANDED_VALUE) {
                return ValueConstants.EXPANDED_VALUE;
            }
            if (v == ValueConstants.SEMI_CONDENSED_VALUE) {
                return ValueConstants.NORMAL_VALUE;
            }
            if (v == ValueConstants.EXTRA_CONDENSED_VALUE) {
                return ValueConstants.CONDENSED_VALUE;
            }
            if (v == ValueConstants.EXTRA_EXPANDED_VALUE) {
                return ValueConstants.ULTRA_EXPANDED_VALUE;
            }
            if (v == ValueConstants.ULTRA_CONDENSED_VALUE) {
                return ValueConstants.EXTRA_CONDENSED_VALUE;
            }
            return ValueConstants.ULTRA_EXPANDED_VALUE;
        }
        return value;
    }

    /**
     * Implements {@link IdentifierManager#getIdentifiers()}.
     */
    protected StringMap getIdentifiers() {
        return values;
    }
}
