/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import org.apache.batik.css.engine.value.Value;

/**
 * This class represents a collection of CSS property values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StyleDeclaration {

    protected final static int INITIAL_LENGTH = 8;
    
    /**
     * The values.
     */
    protected Value[] values = new Value[INITIAL_LENGTH];

    /**
     * The value indexes.
     */
    protected int[] indexes = new int[INITIAL_LENGTH];

    /**
     * The value priorities.
     */
    protected boolean[] priorities = new boolean[INITIAL_LENGTH];

    /**
     * The number of values in the declaration.
     */
    protected int count;

    /**
     * Returns the number of values in the declaration.
     */
    public int size() {
        return count;
    }

    /**
     * Returns the value at the given index.
     */
    public Value getValue(int idx) {
        return values[idx];
    }

    /**
     * Returns the property index of a value.
     */
    public int getIndex(int idx) {
        return indexes[idx];
    }

    /**
     * Tells whether a value is important.
     */
    public boolean getPriority(int idx) {
        return priorities[idx];
    }

    /**
     * Removes the value at the given index.
     */
    public void remove(int idx) {
        count--;
        for (int i = idx; i < count; i++) {
            values[i] = values[i + 1];
            indexes[i] = indexes[i + 1];
            priorities[i] = priorities[i + 1];
        }
    }

    /**
     * Sets a value within the declaration.
     */
    public void put(int idx, Value v, int i, boolean prio) {
        values[idx]     = v;
        indexes[idx]    = i;
        priorities[idx] = prio;
    }

    /**
     * Appends a value to the declaration.
     */
    public void append(Value v, int idx, boolean prio) {
        if (values.length == count) {
            Value[]   newval  = new Value[count * 2];
            int[]     newidx  = new int[count * 2];
            boolean[] newprio = new boolean[count * 2];
            for (int i = 0; i < count; i++) {
                newval[i]  = values[i];
                newidx[i]  = indexes[i];
                newprio[i] = priorities[i];
            }
            values     = newval;
            indexes    = newidx;
            priorities = newprio;
        }
        values[count]     = v;
        indexes[count]    = idx;
        priorities[count] = prio;
        count++;
    }

    /**
     * Returns a printable representation of this style rule.
     */
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append(eng.getPropertyName(indexes[i]));
            sb.append(": ");
            sb.append(values[i]);
            sb.append(";\n");
        }
        return sb.toString();
    }
}
