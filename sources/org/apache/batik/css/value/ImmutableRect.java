/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMValue;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.Rect;

/**
 * This class represents immutable CSS Rect values
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImmutableRect extends AbstractImmutablePrimitiveValue
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
     * Creates a new Rect value.
     */
    public ImmutableRect(CSSPrimitiveValue t,
			 CSSPrimitiveValue r,
			 CSSPrimitiveValue b,
			 CSSPrimitiveValue l) {
	top = t;
	right = r;
	bottom = b;
	left = l;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof ImmutableRect)) {
	    return false;
	}
	ImmutableRect v = (ImmutableRect)obj;
	return
	    top.equals(v.top) &&
	    right.equals(v.right) &&
	    bottom.equals(v.bottom) &&
	    left.equals(v.left);
    }

    /**
     * Returns a deep read-only copy of this object.
     */
    public ImmutableValue createReadOnlyCopy() {
	return new ImmutableRect(((CSSOMValue)top).createReadOnlyCopy(),
				 ((CSSOMValue)right).createReadOnlyCopy(),
				 ((CSSOMValue)bottom).createReadOnlyCopy(),
				 ((CSSOMValue)left).createReadOnlyCopy());
    }

    /**
     *  A string representation of the current value. 
     */
    public String getCssText() {
	return "rect(" + top.getCssText() + ", "
	    +  right.getCssText() + ", "
	    +  bottom.getCssText() + ", "
	    +  left.getCssText() + ")";
    }

    /**
     *  This method is used to get the Rect value. If this CSS value doesn't 
     * contain a rect value, a <code>DOMException</code> is raised. 
     * Modification to the corresponding style property can be achieved using 
     * the <code>Rect</code> interface. 
     * @return The Rect value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the CSS value doesn't contain a Rect 
     *   value.  (e.g. this is not <code>CSS_RECT</code>). 
     */
    public Rect getRectValue() throws DOMException {
	return this;
    }

    /**
     * The type of the value as defined by the constants specified in
     * CSSPrimitiveValue.
     */
    public short getPrimitiveType() {
	return CSSPrimitiveValue.CSS_RECT;
    }

    /**
     *  This attribute is used for the top of the rect. 
     */
    public CSSPrimitiveValue getTop() {
	return top;
    }

    /**
     *  This attribute is used for the right of the rect. 
     */
    public CSSPrimitiveValue getRight() {
	return right;
    }

    /**
     *  This attribute is used for the bottom of the rect. 
     */
    public CSSPrimitiveValue getBottom() {
	return bottom;
    }

    /**
     *  This attribute is used for the left of the rect. 
     */
    public CSSPrimitiveValue getLeft() {
	return left;
    }
}
