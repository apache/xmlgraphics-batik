/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.apache.batik.css.value.ImmutableInherit;
import org.apache.batik.css.value.ValueFactory;
import org.apache.batik.css.value.ValueFactoryMap;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.ViewCSS;

/**
 * This class implements the {@link org.w3c.dom.css.CSSStyleDeclaration}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMReadOnlyStyleDeclaration implements CSSStyleDeclaration {
    /**
     * To tag the properties that comes from a user agent.
     */
    public final static int USER_AGENT_ORIGIN = 0;

    /**
     * To tag the properties that comes from the user preferences.
     */
    public final static int USER_ORIGIN = 1;

    /**
     * To tag the properties that comes from the author.
     */
    public final static int AUTHOR_ORIGIN = 2;

    /**
     * The properties.
     */
    protected PropertyMap properties = new PropertyMap();

    /**
     * The ViewCSS.
     */
    protected ViewCSS viewCSS;

    /**
     * The associated parent element.
     */
    protected Element parentElement;

    /**
     * Creates a new CSSOMReadOnlyStyleDeclaration object.
     */
    public CSSOMReadOnlyStyleDeclaration(ViewCSS v, Element elt) {
        setContext(v, elt);
    }

    /**
     * Sets the declaration context.
     */
    public void setContext(ViewCSS v, Element elt) {
        viewCSS = v;
        parentElement = HiddenChildElementSupport.getParentElement(elt);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getCssText()}.
     */
    public String getCssText() {
	String result = "";
	for (int i = properties.size() - 1; i >= 0; i--) {
	    result += "    " + properties.key(i) + ": ";
	    ValueEntry ve = (ValueEntry)properties.item(i);
	    if (ve.getValue() != null) {
		result += ((CSSValue)ve.getValue()).getCssText();
	    }
	    result += ve.getPriority() + ";\n";
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#setCssText(String)}.
     * Throws a NO_MODIFICATION_ALLOWED_ERR {@link org.w3c.dom.DOMException}.
     */
    public void setCssText(String cssText) throws DOMException {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.NO_MODIFICATION_ALLOWED_ERR,
		 "readonly.declaration",
		 new Object[] {});
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyValue(String)}.
     */
    public String getPropertyValue(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = (ValueEntry)properties.get(s);
        if (ve == null) {
            return "";
        }
        if (ve.getValue() == null ||
            ve.getValue() == ImmutableInherit.INSTANCE) {
            CSSStyleDeclaration sd;
            sd = viewCSS.getComputedStyle(parentElement, null);
            return sd.getPropertyCSSValue(s).getCssText();
        } else {
            return ve.getValue().getCssText();
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyCSSValue(String)}.
     */
    public CSSValue getPropertyCSSValue(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = (ValueEntry)properties.get(s);
        if (ve == null) {
            return null;
        }
        if (ve.getValue() == null) {
            CSSStyleDeclaration sd;
            sd = viewCSS.getComputedStyle(parentElement, null);
            CSSOMReadOnlyValue v =
                (CSSOMReadOnlyValue)sd.getPropertyCSSValue(s);
            CSSOMReadOnlyValue res;
            ve.setValue(res = new CSSOMReadOnlyValue(v.getImmutableValue()));
            return res;
        } else {
            return ve.getValue();
        }
    }

    /**
     * Returns the local CSSValue.
     */
    public CSSValue getLocalPropertyCSSValue(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = (ValueEntry)properties.get(s);
        return (ve == null) ? null : ve.getValue();
    }

    /**
     * Sets a property value.
     */
    public void setPropertyCSSValue(String   propertyName,
				    CSSValue v,
				    String   imp,
				    int      orig) {
	ValueEntry ve = (ValueEntry)properties.put(propertyName,
					      createValueEntry(v, imp, orig));
    }

    /**
     * Returns a property origin.
     */
    public int getPropertyOrigin(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = (ValueEntry)properties.get(s);
        if (ve == null) {
            return AUTHOR_ORIGIN;
        }
        if (ve.getValue() == null ||
            ve.getValue() == ImmutableInherit.INSTANCE) {
            CSSStyleDeclaration sd;
            sd = viewCSS.getComputedStyle(parentElement, null);
            return ((CSSOMReadOnlyStyleDeclaration)
                    sd).getPropertyOrigin(s);
        } else {
            return ve.getOrigin();
        }
    }

    /**
     * Returns the local property origin.
     */
    public int getLocalPropertyOrigin(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = (ValueEntry)properties.get(s);
        return (ve == null) ? AUTHOR_ORIGIN : ve.getOrigin();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#removeProperty(String)}.
     */
    public String removeProperty(String propertyName) throws DOMException {
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.NO_MODIFICATION_ALLOWED_ERR,
		 "readonly.declaration",
		 new Object[] {});
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyPriority(String)}.
     */
    public String getPropertyPriority(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = (ValueEntry)properties.get(s);
        if (ve == null) {
            return "";
        }
        if (ve.getValue() == null ||
            ve.getValue() == ImmutableInherit.INSTANCE) {
            CSSStyleDeclaration sd;
            sd = viewCSS.getComputedStyle(parentElement, null);
            return ((CSSOMReadOnlyStyleDeclaration)
                    sd).getPropertyPriority(s);
        } else {
            return ve.getPriority();
        }
    }

    /**
     * Returns the local priority.
     */
    public String getLocalPropertyPriority(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = (ValueEntry)properties.get(s);
        return (ve == null) ? "" : ve.getPriority();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#setProperty(String,String,String)}.
     */
    public void setProperty(String propertyName, String value, String prio)
	throws DOMException {
	throw CSSDOMExceptionFactory.createDOMException
	    (DOMException.NO_MODIFICATION_ALLOWED_ERR,
	     "readonly.declaration",
	     new Object[] {});
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getLength()}.
     */
    public int getLength() {
	return properties.size();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#item(int)}.
     */
    public String item(int index) {
	String result = (String)properties.key(index);
	return (result == null) ? "" : result;
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
     * Creates a new value entry.
     */
    protected ValueEntry createValueEntry(CSSValue v, String s, int p) {
        switch (p) {
        case USER_AGENT_ORIGIN:
            if (s.length() == 0) {
                return new UserAgentValueEntry(v);
            } else {
                return new ImportantUserAgentValueEntry(v);
            }
        case USER_ORIGIN:
            if (s.length() == 0) {
                return new UserValueEntry(v);
            } else {
                return new ImportantUserValueEntry(v);
            }
        case AUTHOR_ORIGIN:
            if (s.length() == 0) {
                return new AuthorValueEntry(v);
            } else {
                return new ImportantAuthorValueEntry(v);
            }
        default:
            throw new RuntimeException();
        }
    }

    /**
     * This interface represents a value entry in the table.
     */
    protected interface ValueEntry {
        /**
         * Returns the CSS value.
         */
        CSSValue getValue();

        /**
         * Sets the CSS value.
         */
        void setValue(CSSValue v);

        /**
         * Returns the priority.
         */
        String getPriority();

        /**
         * Returns the value origin.
         */
        int getOrigin();
    }

    /**
     * To store an important user-agent value.
     */
    protected static class ImportantUserAgentValueEntry
        implements ValueEntry {
        /**
         * The CSS value.
         */
        protected CSSValue value;

        /**
         * Creates a new value entry.
         */
        public ImportantUserAgentValueEntry(CSSValue v) {
            value = v;
        }

        /**
         * Returns the CSS value.
         */
        public CSSValue getValue() {
            return value;
        }

        /**
         * Sets the CSS value.
         */
        public void setValue(CSSValue v) {
            value = v;
        }

        /**
         * Returns the priority.
         */
        public String getPriority() {
            return "!important";
        }

        /**
         * Returns the value origin.
         */
        public int getOrigin() {
            return USER_AGENT_ORIGIN;
        }
    }

    /**
     * To store a user-agent value.
     */
    protected static class UserAgentValueEntry
        implements ValueEntry {
        /**
         * The CSS value.
         */
        protected CSSValue value;

        /**
         * Creates a new value entry.
         */
        public UserAgentValueEntry(CSSValue v) {
            value = v;
        }

        /**
         * Returns the CSS value.
         */
        public CSSValue getValue() {
            return value;
        }

        /**
         * Sets the CSS value.
         */
        public void setValue(CSSValue v) {
            value = v;
        }

        /**
         * Returns the priority.
         */
        public String getPriority() {
            return "";
        }

        /**
         * Returns the value origin.
         */
        public int getOrigin() {
            return USER_AGENT_ORIGIN;
        }
    }

    /**
     * To store an important user value.
     */
    protected static class ImportantUserValueEntry
        implements ValueEntry {
        /**
         * The CSS value.
         */
        protected CSSValue value;

        /**
         * Creates a new value entry.
         */
        public ImportantUserValueEntry(CSSValue v) {
            value = v;
        }

        /**
         * Returns the CSS value.
         */
        public CSSValue getValue() {
            return value;
        }

        /**
         * Sets the CSS value.
         */
        public void setValue(CSSValue v) {
            value = v;
        }

        /**
         * Returns the priority.
         */
        public String getPriority() {
            return "!important";
        }

        /**
         * Returns the value origin.
         */
        public int getOrigin() {
            return USER_ORIGIN;
        }
    }

    /**
     * To store a user value.
     */
    protected static class UserValueEntry
        implements ValueEntry {
        /**
         * The CSS value.
         */
        protected CSSValue value;

        /**
         * Creates a new value entry.
         */
        public UserValueEntry(CSSValue v) {
            value = v;
        }

        /**
         * Returns the CSS value.
         */
        public CSSValue getValue() {
            return value;
        }

        /**
         * Sets the CSS value.
         */
        public void setValue(CSSValue v) {
            value = v;
        }

        /**
         * Returns the priority.
         */
        public String getPriority() {
            return "";
        }

        /**
         * Returns the value origin.
         */
        public int getOrigin() {
            return USER_ORIGIN;
        }
    }

    /**
     * To store an important author value.
     */
    protected static class ImportantAuthorValueEntry
        implements ValueEntry {
        /**
         * The CSS value.
         */
        protected CSSValue value;

        /**
         * Creates a new value entry.
         */
        public ImportantAuthorValueEntry(CSSValue v) {
            value = v;
        }

        /**
         * Returns the CSS value.
         */
        public CSSValue getValue() {
            return value;
        }

        /**
         * Sets the CSS value.
         */
        public void setValue(CSSValue v) {
            value = v;
        }

        /**
         * Returns the priority.
         */
        public String getPriority() {
            return "!important";
        }

        /**
         * Returns the value origin.
         */
        public int getOrigin() {
            return AUTHOR_ORIGIN;
        }
    }

    /**
     * To store a author value.
     */
    protected static class AuthorValueEntry
        implements ValueEntry {
        /**
         * The CSS value.
         */
        protected CSSValue value;

        /**
         * Creates a new value entry.
         */
        public AuthorValueEntry(CSSValue v) {
            value = v;
        }

        /**
         * Returns the CSS value.
         */
        public CSSValue getValue() {
            return value;
        }

        /**
         * Sets the CSS value.
         */
        public void setValue(CSSValue v) {
            value = v;
        }

        /**
         * Returns the priority.
         */
        public String getPriority() {
            return "";
        }

        /**
         * Returns the value origin.
         */
        public int getOrigin() {
            return AUTHOR_ORIGIN;
        }
    }
}
