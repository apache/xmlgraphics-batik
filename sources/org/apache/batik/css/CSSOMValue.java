/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.apache.batik.css.event.CSSValueChangeListener;
import org.apache.batik.css.value.ValueFactory;
import org.apache.batik.css.value.ImmutableValue;
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
public class CSSOMValue implements CSSPrimitiveValue, CSSValueList {
    /**
     * The value implementation.
     */
    protected ImmutableValue value;

    /**
     * The value factory
     */
    protected ValueFactory factory;

    /**
     * The value change listener.
     */
    protected CSSValueChangeListener listener;

    /**
     * Creates a new CSS value.
     */
    public CSSOMValue(ValueFactory vf, ImmutableValue v) {
        factory = vf;
        value = v;
    }

    /**
     * Associates a listener to this value.
     */
    public void setCSSValueChangeListener(CSSValueChangeListener listener) {
	this.listener = listener;
    }

    /**
     * Returns the underlying immutable value.
     */
    public ImmutableValue getImmutableValue() {
	return value;
    }

    /**
     * Returns a read-only copy of this value.
     */
    public CSSOMReadOnlyValue createReadOnlyCopy() {
	return new CSSOMReadOnlyValue(value.createReadOnlyCopy());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof CSSOMValue)) {
	    return false;
	}
	CSSOMValue v = (CSSOMValue)obj;
	return value.equals(v.value);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssText()}.
     */
    public String getCssText() {
        return value.getCssText();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSValue#setCssText(String)}.
     */
    public void setCssText(String cssText) throws DOMException {
	if (listener == null) {
	    value = factory.createValue(cssText);
	} else {
	    CSSValue old = new CSSOMReadOnlyValue(value);
	    value = factory.createValue(cssText);
	    listener.cssValueChange(factory.getPropertyName(), old, this);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSValue#getCssValueType()}.
     */
    public short getCssValueType() {
        return value.getCssValueType();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getPrimitiveType()}.
     */
    public short getPrimitiveType() {
        return value.getPrimitiveType();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
     */
    public void setFloatValue(short unitType, float floatValue)
        throws DOMException {
	if (listener == null) {
	    value = factory.createFloatValue(unitType, floatValue);
	} else {
	    CSSValue old = new CSSOMReadOnlyValue(value);
	    value = factory.createFloatValue(unitType, floatValue);
	    listener.cssValueChange(factory.getPropertyName(), old, this);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getFloatValue(short)}.
     */
    public float getFloatValue(short unitType) throws DOMException {
        return value.getFloatValue(unitType);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#setStringValue(short,String)}.
     */
    public void setStringValue(short stringType, String stringValue)
        throws DOMException {
	if (listener == null) {
	    value = factory.createStringValue(stringType, stringValue);
	} else {
	    CSSValue old = new CSSOMReadOnlyValue(value);
	    value = factory.createStringValue(stringType, stringValue);
	    listener.cssValueChange(factory.getPropertyName(), old, this);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getStringValue()}.
     */
    public String getStringValue() throws DOMException {
        return value.getStringValue();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getCounterValue()}.
     */
    public Counter getCounterValue() throws DOMException {
        return value.getCounterValue();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getRectValue()}.
     */
    public Rect getRectValue() throws DOMException {
        return value.getRectValue();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getRGBColorValue()}.
     */
    public RGBColor getRGBColorValue() throws DOMException {
        return value.getRGBColorValue();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValueList#getLength()}.
     */
    public int getLength() {
        return value.getLength();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValueList#item(int)}.
     */
    public CSSValue item(int index) {
        return value.item(index);
    }
}
