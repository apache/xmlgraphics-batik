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

/**
 * This class represents a computed property value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ComputedValue implements Value {

    /**
     * The cascaded value.
     */
    protected Value cascadedValue;

    /**
     * The computed value.
     */
    protected Value computedValue;
    
    /**
     * Creates a new ComputedValue object.
     * @param cv The cascaded value.
     */
    public ComputedValue(Value cv) {
        cascadedValue = cv;
    }

    /**
     * Returns the computed value.
     */
    public Value getComputedValue() {
        return computedValue;
    }

    /**
     * Returns the cascaded value.
     */
    public Value getCascadedValue() {
        return cascadedValue;
    }

    /**
     * Sets the computed value.
     */
    public void setComputedValue(Value v) {
        computedValue = v;
    }

    /**
     * Implements {@link Value#getCssText()}.
     */
    public String getCssText() {
        return computedValue.getCssText();
    }

    /**
     * Implements {@link Value#getCssValueType()}.
     */
    public short getCssValueType() {
        return computedValue.getCssValueType();
    }

    /**
     * Implements {@link Value#getPrimitiveType()}.
     */
    public short getPrimitiveType() {
        return computedValue.getPrimitiveType();
    }

    /**
     * Implements {@link Value#getFloatValue()}.
     */
    public float getFloatValue() throws DOMException {
        return computedValue.getFloatValue();
    }

    /**
     * Implements {@link Value#getStringValue()}.
     */
    public String getStringValue() throws DOMException {
        return computedValue.getStringValue();
    }

    /**
     * Implements {@link Value#getRed()}.
     */
    public Value getRed() throws DOMException {
        return computedValue.getRed();
    }

    /**
     * Implements {@link Value#getGreen()}.
     */
    public Value getGreen() throws DOMException {
        return computedValue.getGreen();
    }

    /**
     * Implements {@link Value#getBlue()}.
     */
    public Value getBlue() throws DOMException {
        return computedValue.getBlue();
    }

    /**
     * Implements {@link Value#getLength()}.
     */
    public int getLength() throws DOMException {
        return computedValue.getLength();
    }

    /**
     * Implements {@link Value#item(int)}.
     */
    public Value item(int index) throws DOMException {
        return computedValue.item(index);
    }

    /**
     * Implements {@link Value#getTop()}.
     */
    public Value getTop() throws DOMException {
        return computedValue.getTop();
    }

    /**
     * Implements {@link Value#getRight()}.
     */
    public Value getRight() throws DOMException {
        return computedValue.getRight();
    }

    /**
     * Implements {@link Value#getBottom()}.
     */
    public Value getBottom() throws DOMException {
        return computedValue.getBottom();
    }

    /**
     * Implements {@link Value#getLeft()}.
     */
    public Value getLeft() throws DOMException {
        return computedValue.getLeft();
    }

    /**
     * Implements {@link Value#getIdentifier()}.
     */
    public String getIdentifier() throws DOMException {
        return computedValue.getIdentifier();
    }

    /**
     * Implements {@link Value#getListStyle()}.
     */
    public String getListStyle() throws DOMException {
        return computedValue.getListStyle();
    }

    /**
     * Implements {@link Value#getSeparator()}.
     */
    public String getSeparator() throws DOMException {
        return computedValue.getSeparator();
    }
}
