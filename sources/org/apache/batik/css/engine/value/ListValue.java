/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents a list of values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ListValue extends AbstractValue {
    
    /**
     * The length of the list.
     */
    protected int length;

    /**
     * The items.
     */
    protected Value[] items = new Value[5];

    /**
     * The list separator.
     */
    protected char separator = ',';

    /**
     * Creates a ListValue.
     */
    public ListValue() {
    }

    /**
     * Creates a ListValue with the given separator.
     */
    public ListValue(char s) {
        separator = s;
    }

    /**
     * Returns the separator used for this list.
     */
    public char getSeparatorChar() {
        return separator;
    }

    /**
     * Implements {@link Value#getCssValueType()}.
     */
    public short getCssValueType() {
        return CSSValue.CSS_VALUE_LIST;
    }

    /**
     *  A string representation of the current value. 
     */
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        if (length > 0) {
            sb.append(items[0].getCssText());
        }
        for (int i = 1; i < length; i++) {
            sb.append(separator);
            sb.append(items[i].getCssText());
        }
        return sb.toString();
    }

    /**
     * Implements {@link Value#getLength()}.
     */
    public int getLength() throws DOMException {
        return length;
    }

    /**
     * Implements {@link Value#item(int)}.
     */
    public Value item(int index) throws DOMException {
        return items[index];
    }

    /**
     * Returns a printable representation of this value.
     */
    public String toString() {
        return getCssText();
    }

    /**
     * Appends an item to the list.
     */
    public void append(Value v) {
        if (length == items.length) {
            Value[] t = new Value[length * 2];
            for (int i = 0; i < length; i++) {
                t[i] = items[i];
            }
            items = t;
        }
        items[length++] = v;
    }
}
