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
