/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.Rect;
import org.w3c.dom.css.RGBColor;

/**
 * This interface represents an immutable CSS value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface ImmutableValue {
    /**
     * Returns a deep read-only copy of this object.
     */
    ImmutableValue createReadOnlyCopy();

    /**
     *  A string representation of the current value. 
     */
    String getCssText();

    /**
     * A code defining the type of the value. 
     */
    short getCssValueType();

    /**
     * The type of the value.
     */
    short getPrimitiveType();

    /**
     *  A method to get the float value with a specified unit.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the attached property doesn't support 
     *   the float value or the unit type.
     */
    float getFloatValue(short unitType) throws DOMException;

    /**
     *  This method is used to get the string value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the CSS value doesn't contain a string
     *    value. 
     */
    String getStringValue() throws DOMException;

    /**
     *  This method is used to get the Counter value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the CSS value doesn't contain a 
     *   Counter value (e.g. this is not <code>CSS_COUNTER</code>). 
     */
    Counter getCounterValue() throws DOMException;

    /**
     *  This method is used to get the Rect value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the CSS value doesn't contain a Rect 
     *   value.  (e.g. this is not <code>CSS_RECT</code>). 
     */
    Rect getRectValue() throws DOMException;

    /**
     *  This method is used to get the RGB color.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the attached property can't return a 
     *   RGB color value (e.g. this is not <code>CSS_RGBCOLOR</code>). 
     */
    RGBColor getRGBColorValue() throws DOMException;

    /**
     * The number of <code>CSSValues</code> in the list. The range of valid 
     * values of the indices is <code>0</code> to <code>length-1</code> 
     * inclusive.
     */
    int getLength();

    /**
     * Used to retrieve a CSS rule by ordinal index.
     * @return The style rule at the <code>index</code> position in the 
     *   <code>CSSValueList</code>, or <code>null</code> if that is not a 
     *   valid index.
     */
    CSSValue item(int index);
}
