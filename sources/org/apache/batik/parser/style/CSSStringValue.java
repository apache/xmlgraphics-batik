/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents CSS string values
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSStringValue extends AbstractCSSValue {
    /**
     * The string value
     */
    protected String stringValue;

    /**
     * The unit type
     */
    protected short unitType;

    /**
     * Creates a new value.
     */
    public CSSStringValue(short unitType, String stringValue) {
	this.unitType   = unitType;
	this.stringValue = stringValue;
        this.stringValue = (unitType == CSSPrimitiveValue.CSS_IDENT)
            ? stringValue.toLowerCase().intern() : stringValue;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssValueType()}.
     */
    public short getCssValueType() {
        return CSSValue.CSS_PRIMITIVE_VALUE;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getPrimitiveType()}.
     */
    public short getPrimitiveType() {
	return unitType;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssText()}.
     */
    public String getCssText() {
        switch (unitType) {
        case CSSPrimitiveValue.CSS_URI:
            return "url(" + stringValue + ")";
        case CSSPrimitiveValue.CSS_STRING:
            // !!! See this point
            char q = (stringValue.indexOf('"') != -1) ? '\'' : '"';
            return q + stringValue + q;
        }
        return stringValue;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getStringValue()}.
     */
    public String getStringValue() throws DOMException {
        return stringValue;
    }
}
