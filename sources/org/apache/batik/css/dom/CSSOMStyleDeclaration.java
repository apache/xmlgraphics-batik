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

import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

/**
 * This class represents a style declaration.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMStyleDeclaration implements CSSStyleDeclaration {

    /**
     * The associated value.
     */
    protected ValueProvider valueProvider;

    /**
     * The modifications handler.
     */
    protected ModificationHandler handler;

    /**
     * The parent rule.
     */
    protected CSSRule parentRule;

    /**
     * The values.
     */
    protected Map values;

    /**
     * Creates a new style declaration.
     */
    public CSSOMStyleDeclaration(ValueProvider vp, CSSRule parent) {
        valueProvider = vp;
        parentRule = parent;
    }

    /**
     * Sets the modification handler of this value.
     */
    public void setModificationHandler(ModificationHandler h) {
        handler = h;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getCssText()}.
     */
    public String getCssText() {
        return valueProvider.getText();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#setCssText(String)}.
     */
    public void setCssText(String cssText) throws DOMException {
	if (handler == null) {
            throw new DOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
	} else {
            values = null;
            handler.textChanged(cssText);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyValue(String)}.
     */
    public String getPropertyValue(String propertyName) {
        Value value = valueProvider.getValue(propertyName);
        if (value == null) {
            return "";
        }
        return value.getCssText();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyCSSValue(String)}.
     */
    public CSSValue getPropertyCSSValue(String propertyName) {
        Value value = valueProvider.getValue(propertyName);
        if (value == null) {
            return null;
        }
        return getCSSValue(propertyName);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#removeProperty(String)}.
     */
    public String removeProperty(String propertyName) throws DOMException {
        String result = getPropertyValue(propertyName);
        if (result.length() > 0) {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                if (values != null) {
                    values.remove(propertyName);
                }
                handler.propertyRemoved(propertyName);
            }
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyPriority(String)}.
     */
    public String getPropertyPriority(String propertyName) {
        return (valueProvider.isImportant(propertyName)) ? "important" : "";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#setProperty(String,String,String)}.
     */
    public void setProperty(String propertyName, String value, String prio)
	throws DOMException {
        if (handler == null) {
            throw new DOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
        } else {
            handler.propertyChanged(propertyName, value, prio);
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getLength()}.
     */
    public int getLength() {
        return valueProvider.getLength();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#item(int)}.
     */
    public String item(int index) {
        return valueProvider.item(index);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getParentRule()}.
     */
    public CSSRule getParentRule() {
        return parentRule;
    }

    /**
     * Gets the CSS value associated with the given property.
     */
    protected CSSValue getCSSValue(String name) {
        CSSValue result = null;
        if (values != null) {
            result = (CSSValue)values.get(name);
        }
        if (result == null) {
            result = createCSSValue(name);
            if (values == null) {
                values = new HashMap(11);
            }
            values.put(name, result);
        }
        return result;
    }

    /**
     * Creates the CSS value associated with the given property.
     */
    protected CSSValue createCSSValue(String name) {
        return new StyleDeclarationValue(name);
    }

    /**
     * To provides the values.
     */
    public interface ValueProvider {

        /**
         * Returns the current value associated with this object.
         */
        Value getValue(String name);

        /**
         * Tells whether the given property is important.
         */
        boolean isImportant(String name);

        /**
         * Returns the text of the declaration.
         */
        String getText();

        /**
         * Returns the length of the declaration.
         */
        int getLength();

        /**
         * Returns the value at the given.
         */
        String item(int idx);

    }

    /**
     * To manage the modifications on a CSS value.
     */
    public interface ModificationHandler {

        /**
         * Called when the value text has changed.
         */
        void textChanged(String text) throws DOMException;

        /**
         * Called when a property was removed.
         */
        void propertyRemoved(String name) throws DOMException;

        /**
         * Called when a property was changed.
         */
        void propertyChanged(String name, String value, String prio)
            throws DOMException;

    }

    /**
     * This class represents a CSS value returned by this declaration.
     */
    public class StyleDeclarationValue
        extends CSSOMValue
        implements CSSOMValue.ValueProvider {
        
        /**
         * The property name.
         */
        protected String property;

        /**
         * Creates a new StyleDeclarationValue.
         */
        public StyleDeclarationValue(String prop) {
            super(null);
            this.valueProvider = this;
            this.setModificationHandler(new AbstractModificationHandler() {
                    protected Value getValue() {
                        return StyleDeclarationValue.this.getValue();
                    }
                    public void textChanged(String text) throws DOMException {
                        if (values == null ||
                            values.get(this) == null ||
                            StyleDeclarationValue.this.handler == null) {
                            throw new DOMException
                                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
                        }
                        String prio = getPropertyPriority(property);
                        CSSOMStyleDeclaration.this.
                            handler.propertyChanged(property, text, prio);
                    }
                });

            property = prop;
        }

        // ValueProvider ///////////////////////////////

        /**
         * Returns the current value associated with this object.
         */
        public Value getValue() {
            return CSSOMStyleDeclaration.this.valueProvider.getValue(property);
        }

    }
}
