/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import org.apache.batik.css.CSSOMValue;
import org.w3c.dom.css.CSSValue;

/**
 * This class implements an immutable value list.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImmutableValueList extends AbstractImmutableValue {
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
    protected char separator = ',';

    /**
     * Creates a new ImmutableValueList object.
     */
    public ImmutableValueList() {
    }

    /**
     * Creates a new ImmutableValueList object.
     */
    public ImmutableValueList(char c) {
	separator = c;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (obj == null || !(obj instanceof ImmutableValueList)) {
	    return false;
	}
	ImmutableValueList v = (ImmutableValueList)obj;
	if (length != v.length) {
	    return false;
	}
	for (int i = 0; i < length; i++) {
	    CSSValue v1 = table[i];
	    CSSValue v2 = v.table[i];
	    if (v1 == null) {
		return v2 == null;
	    }
	    if (!v1.equals(v2)) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Returns a deep read-only copy of this object.
     */
    public ImmutableValue createReadOnlyCopy() {
	ImmutableValueList result = new ImmutableValueList(separator);
	for (int i = 0; i < length; i++) {
	    result.append(((CSSOMValue)table[i]).createReadOnlyCopy());
	}
	return result;
    }

    /**
     * A string representation of the current value. 
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
     * A code defining the type of the value. 
     */
    public short getCssValueType() {
	return CSSValue.CSS_VALUE_LIST;
    }

    /**
     * The number of <code>CSSValues</code> in the list. The range of valid 
     * values of the indices is <code>0</code> to <code>length-1</code> 
     * inclusive.
     */
    public int getLength() {
	return length;
    }

    /**
     * Used to retrieve a CSS rule by ordinal index. The order in this 
     * collection represents the order of the values in the CSS style 
     * property.
     * @param index Index into the collection.
     * @return The style rule at the <code>index</code> position in the 
     *   <code>CSSValueList</code>, or <code>null</code> if that is not a 
     *   valid index.
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
