/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value.svg;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueManager;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.util.CSSConstants;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * This class provides a manager for the 'color-interpolation' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ColorProfileManager extends AbstractValueManager {
    
    /**
     * Implements {@link ValueManager#isInheritedProperty()}.
     */
    public boolean isInheritedProperty() {
	return true;
    }

    /**
     * Implements {@link ValueManager#getPropertyName()}.
     */
    public String getPropertyName() {
	return CSSConstants.CSS_COLOR_PROFILE_PROPERTY;
    }
    
    /**
     * Implements {@link ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return SVGValueConstants.AUTO_VALUE;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return SVGValueConstants.INHERIT_VALUE;

	case LexicalUnit.SAC_IDENT:
            String s = lu.getStringValue().toLowerCase();
            if (s.equals(CSSConstants.CSS_AUTO_VALUE)) {
                return SVGValueConstants.AUTO_VALUE;
            }
            if (s.equals(CSSConstants.CSS_SRGB_VALUE)) {
                return SVGValueConstants.SRGB_VALUE;
            }
            return new StringValue(CSSPrimitiveValue.CSS_IDENT, s);
            
        case LexicalUnit.SAC_URI:
            return new StringValue(CSSPrimitiveValue.CSS_URI,
                                   resolveURI(engine.getCSSBaseURI(),
                                              lu.getStringValue()));
        }
        throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    /**
     * Implements {@link
     * ValueManager#createStringValue(short,String,CSSEngine)}.
     */
    public Value createStringValue(short type, String value, CSSEngine engine)
	throws DOMException {
        switch (type) {
        case CSSPrimitiveValue.CSS_IDENT:
            String s = value.toLowerCase();
            if (s.equals(CSSConstants.CSS_AUTO_VALUE)) {
                return SVGValueConstants.AUTO_VALUE;
            }
            if (s.equals(CSSConstants.CSS_SRGB_VALUE)) {
                return SVGValueConstants.SRGB_VALUE;
            }
            return new StringValue(CSSPrimitiveValue.CSS_IDENT, s);

        case CSSPrimitiveValue.CSS_URI:
            return new StringValue(CSSPrimitiveValue.CSS_URI,
                                   resolveURI(engine.getCSSBaseURI(), value));
        }
        throw createInvalidStringTypeDOMException(type);
    }
}
