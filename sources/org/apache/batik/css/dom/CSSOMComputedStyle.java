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

package org.apache.batik.css.dom;

import java.util.HashMap;
import java.util.Map;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents the computed style of an element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMComputedStyle implements CSSStyleDeclaration {

    /**
     * The CSS engine used to compute the values.
     */
    protected CSSEngine cssEngine;

    /**
     * The associated element.
     */
    protected CSSStylableElement element;

    /**
     * The optional pseudo-element.
     */
    protected String pseudoElement;

    /**
     * The CSS values.
     */
    protected Map values = new HashMap();

    /**
     * Creates a new computed style.
     */
    public CSSOMComputedStyle(CSSEngine e,
                              CSSStylableElement elt,
                              String pseudoElt) {
        cssEngine = e;
        element = elt;
        pseudoElement = pseudoElt;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getCssText()}.
     */
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cssEngine.getNumberOfProperties(); i++) {
            sb.append(cssEngine.getPropertyName(i));
            sb.append(": ");
            sb.append(cssEngine.getComputedStyle(element, pseudoElement,
                                                 i).getCssText());
            sb.append(";\n");
        }
        return sb.toString();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#setCssText(String)}.
     * Throws a NO_MODIFICATION_ALLOWED_ERR {@link org.w3c.dom.DOMException}.
     */
    public void setCssText(String cssText) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyValue(String)}.
     */
    public String getPropertyValue(String propertyName) {
        int idx = cssEngine.getPropertyIndex(propertyName);
        if (idx == -1) {
            return "";
        }
        Value v = cssEngine.getComputedStyle(element, pseudoElement, idx);
        return v.getCssText();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyCSSValue(String)}.
     */
    public CSSValue getPropertyCSSValue(String propertyName) {
        CSSValue result = (CSSValue)values.get(propertyName);
        if (result == null) {
            int idx = cssEngine.getPropertyIndex(propertyName);
            if (idx != -1) {
                result = createCSSValue(idx);
                values.put(propertyName, result);
            }
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#removeProperty(String)}.
     */
    public String removeProperty(String propertyName) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyPriority(String)}.
     */
    public String getPropertyPriority(String propertyName) {
        return "";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#setProperty(String,String,String)}.
     */
    public void setProperty(String propertyName, String value, String prio)
	throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getLength()}.
     */
    public int getLength() {
        return cssEngine.getNumberOfProperties();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#item(int)}.
     */
    public String item(int index) {
        if (index < 0 || index >= cssEngine.getNumberOfProperties()) {
            return "";
        }
        return cssEngine.getPropertyName(index);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getParentRule()}.
     * @return null.
     */
    public CSSRule getParentRule() {
        return null;
    }

    /**
     * Creates a CSSValue to manage the value at the given index.
     */
    protected CSSValue createCSSValue(int idx) {
        return new ComputedCSSValue(idx);
    }

    /**
     * To manage a computed CSSValue.
     */
    public class ComputedCSSValue
        extends CSSOMValue
        implements CSSOMValue.ValueProvider {
        
        /**
         * The index of the associated value.
         */
        protected int index;

        /**
         * Creates a new ComputedCSSValue.
         */
        public ComputedCSSValue(int idx) {
            super(null);
            valueProvider = this;
            index = idx;
        }

        /**
         * Returns the Value associated with this object.
         */
        public Value getValue() {
            return cssEngine.getComputedStyle(element, pseudoElement, index);
        }
    }
}
