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
import org.w3c.dom.css.Rect;

/**
 * This class represents CSS rect values
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSRectValue
    extends    AbstractCSSValue
    implements Rect {
    /**
     * The top value.
     */
    protected CSSPrimitiveValue top;

    /**
     * The right value.
     */
    protected CSSPrimitiveValue right;

    /**
     * The bottom value.
     */
    protected CSSPrimitiveValue bottom;

    /**
     * The left value.
     */
    protected CSSPrimitiveValue left;

    /**
     * Creates a new rect value.
     * @param t The top length.
     * @param t The right length.
     * @param t The bottom length.
     * @param t The left length.
     */
    public CSSRectValue(CSSPrimitiveValue t,
                        CSSPrimitiveValue r,
                        CSSPrimitiveValue b,
                        CSSPrimitiveValue l) {
        top = t;
        right = r;
        bottom = b;
        left = l;
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
	return CSSPrimitiveValue.CSS_RECT;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssText()}.
     */
    public String getCssText() {
        return "rect(" + top.getCssText() + ", "
            +  right.getCssText() + ", "
            +  bottom.getCssText() + ", "
            +  left.getCssText() + ")";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getRectValue()}.
     */
    public Rect getRectValue() throws DOMException {
        return this;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Rect#getTop()}.
     */
    public CSSPrimitiveValue getTop() {
        return top;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Rect#getRight()}.
     */
    public CSSPrimitiveValue getRight() {
        return right;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Rect#getBottom()}.
     */
    public CSSPrimitiveValue getBottom() {
        return bottom;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Rect#getLeft()}.
     */
    public CSSPrimitiveValue getLeft() {
        return left;
    }
}
