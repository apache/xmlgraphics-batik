/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
