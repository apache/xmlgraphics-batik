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
 * This class provides a factory for the 'filter' property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class FilterManager extends AbstractValueManager {
    
    /**
     * Implements {@link ValueManager#isInheritedProperty()}.
     */
    public boolean isInheritedProperty() {
	return false;
    }

    /**
     * Implements {@link ValueManager#getPropertyName()}.
     */
    public String getPropertyName() {
	return CSSConstants.CSS_FILTER_PROPERTY;
    }
    
    /**
     * Implements {@link ValueManager#getDefaultValue()}.
     */
    public Value getDefaultValue() {
        return SVGValueConstants.NONE_VALUE;
    }

    /**
     * Implements {@link ValueManager#createValue(LexicalUnit,CSSEngine)}.
     */
    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
	switch (lu.getLexicalUnitType()) {
	case LexicalUnit.SAC_INHERIT:
	    return SVGValueConstants.INHERIT_VALUE;

        case LexicalUnit.SAC_URI:
            return new StringValue(CSSPrimitiveValue.CSS_URI,
                                   resolveURI(engine.getCSSBaseURI(),
                                              lu.getStringValue()));

        case LexicalUnit.SAC_IDENT:
	    if (lu.getStringValue().equalsIgnoreCase
                (CSSConstants.CSS_NONE_VALUE)) {
		return SVGValueConstants.NONE_VALUE;
	    }
            throw createInvalidIdentifierDOMException(lu.getStringValue());
        }
        throw createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
    }

    /**
     * Implements {@link
     * ValueManager#createStringValue(short,String,CSSEngine)}.
     */
    public Value createStringValue(short type, String value, CSSEngine engine)
        throws DOMException {
	if (type == CSSPrimitiveValue.CSS_IDENT) {
            if (value.equalsIgnoreCase(CSSConstants.CSS_NONE_VALUE)) {
                return SVGValueConstants.NONE_VALUE;
            }
            throw createInvalidIdentifierDOMException(value);
	}
	if (type == CSSPrimitiveValue.CSS_URI) {
	    return new StringValue(type, resolveURI(engine.getCSSBaseURI(),
                                                    value));
	}
        throw createInvalidStringTypeDOMException(type);
    }
}
