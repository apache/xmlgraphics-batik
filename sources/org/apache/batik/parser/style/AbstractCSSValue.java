/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.Rect;
import org.w3c.dom.css.RGBColor;

/**
 * This class implements the {@link org.w3c.dom.css.CSSValue},
 * {@link org.w3c.dom.css.CSSPrimitiveValue},
 * {@link org.w3c.dom.css.CSSValueList} interfaces.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractCSSValue
    implements CSSPrimitiveValue,
               CSSValueList {
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getPrimitiveType()}.
     */
    public short getPrimitiveType() {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("illegal.operation",
              null));
    }

    /**
     * <b>DOM</b>: Implements
     * {@link org.w3c.dom.css.CSSValue#setCssText(String)}.
     * Throws a NO_MODIFICATION_ALLOWED_ERR {@link org.w3c.dom.DOMException}.
     */
    public void setCssText(String cssText) throws DOMException {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("readonly.value",
              null));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
     * Throws a NO_MODIFICATION_ALLOWED_ERR {@link org.w3c.dom.DOMException}.
     */
    public void setFloatValue(short unitType, float floatValue)
        throws DOMException {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("readonly.value",
              null));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getFloatValue(short)}.
     */
    public float getFloatValue(short unitType) throws DOMException {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("illegal.operation",
              null));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#setStringValue(short,String)}.
     * Throws a NO_MODIFICATION_ALLOWED_ERR {@link org.w3c.dom.DOMException}.
     */
    public void setStringValue(short stringType, String stringValue)
        throws DOMException {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("readonly.value",
              new Object[] {}));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getStringValue()}.
     */
    public String getStringValue() throws DOMException {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("illegal.operation",
              null));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getCounterValue()}.
     */
    public Counter getCounterValue() throws DOMException {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("illegal.operation",
              null));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getRectValue()}.
     */
    public Rect getRectValue() throws DOMException {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("illegal.operation",
              null));
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getRGBColorValue()}.
     */
    public RGBColor getRGBColorValue() throws DOMException {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("illegal.operation",
              null));
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValueList#getLength()}.
     */
    public int getLength() {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("illegal.operation",
              null));
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValueList#item(int)}.
     */
    public CSSValue item(int index) {
	throw new DOMException
            (DOMException.INVALID_ACCESS_ERR,
             StyleAttributeParser.LOCALIZABLE_SUPPORT.formatMessage
             ("illegal.operation",
              null));
    }
}
