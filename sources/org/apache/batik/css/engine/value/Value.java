/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;

/**
 * This interface represents a property value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Value {
    
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
     *  This method is used to get the float value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a float
     *    value. 
     */
    float getFloatValue() throws DOMException;

    /**
     *  This method is used to get the string value.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a string
     *    value. 
     */
    String getStringValue() throws DOMException;

    /**
     * The red value of the RGB color. 
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a RGB
     *    color value. 
     */
    Value getRed() throws DOMException;

    /**
     * The green value of the RGB color. 
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a RGB
     *    color value. 
     */
    Value getGreen() throws DOMException;

    /**
     * The blue value of the RGB color. 
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a RGB
     *    color value. 
     */
    Value getBlue() throws DOMException;

    /**
     * The number of <code>CSSValues</code> in the list. The range of valid 
     * values of the indices is <code>0</code> to <code>length-1</code> 
     * inclusive.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a list
     *    value. 
     */
    int getLength() throws DOMException;

    /**
     * Used to retrieve a rule by ordinal index.
     * @return The style rule at the <code>index</code> position in the 
     *   list, or <code>null</code> if that is not a valid index.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a list
     *    value. 
     */
    Value item(int index) throws DOMException;

    /**
     * The top value of the rect. 
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a Rect
     *    value. 
     */
    Value getTop() throws DOMException;

    /**
     * The right value of the rect. 
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a Rect
     *    value. 
     */
    Value getRight() throws DOMException;

    /**
     * The bottom value of the rect. 
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a Rect
     *    value. 
     */
    Value getBottom() throws DOMException;

    /**
     * The left value of the rect. 
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a Rect
     *    value. 
     */
    Value getLeft() throws DOMException;

    /**
     * The identifier value of the counter.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a Counter
     *    value. 
     */
    String getIdentifier() throws DOMException;

    /**
     * The listStyle value of the counter.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a Counter
     *    value. 
     */
    String getListStyle() throws DOMException;

    /**
     * The separator value of the counter.
     * @exception DOMException
     *    INVALID_ACCESS_ERR: Raised if the value doesn't contain a Counter
     *    value. 
     */
    String getSeparator() throws DOMException;
}
