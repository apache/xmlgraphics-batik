/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser.style;

import org.w3c.dom.css.CSSValue;

/**
 * This class represents CSS list of values
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSValueListValue extends AbstractCSSValue {
    /**
     * The table that contains the values.
     */
    protected CSSValue[] table = new CSSValue[5];

    /**
     * The list length.
     */
    protected int length;

    /**
     * The separator character.
     */
    protected char separator;

    /**
     * Creates a new list value.
     * @param c The list separator.
     */
    public CSSValueListValue(char c) {
        separator = c;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssValueType()}.
     */
    public short getCssValueType() {
        return CSSValue.CSS_VALUE_LIST;
    }
    
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssText()}.
     */
    public String getCssText() {
        String result = "";
        if (length > 0) {
            result += table[0].getCssText();
        }
        for (int i = 1; i < length; i++) {
            result += separator + table[i].getCssText();
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValueList#getLength()}.
     */
    public int getLength() {
        return length;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValueList#item(int)}.
     */
    public CSSValue item(int index) {
        if (index < 0 || index > length) {
            return null;
        }
        return table[index];
    }

    /**
     * Appends an item to the list.
     */
    public void append(CSSValue item) {
        if (table.length == length) {
            CSSValue[] old = table;
            table = new CSSValue[length * 2 + 1];
            for (int i = 0; i < length; i++) {
                table[i] = old[i];
            }
        }
        table[length++] = item;
    }
}
