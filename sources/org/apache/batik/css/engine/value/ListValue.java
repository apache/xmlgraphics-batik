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
