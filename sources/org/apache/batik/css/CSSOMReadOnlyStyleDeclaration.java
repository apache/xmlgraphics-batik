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
    protected AbstractViewCSS viewCSS;

    /**
     * The associated parent element.
     */
    protected Element parentElement;

    /**
     * Creates a new CSSOMReadOnlyStyleDeclaration object.
     */
    public CSSOMReadOnlyStyleDeclaration(AbstractViewCSS v, Element elt) {
        setContext(v, elt);
    }

    /**
     * Sets the declaration context.
     */
    public void setContext(AbstractViewCSS v, Element elt) {
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
	    ValueEntry ve = properties.item(i);
	    if (ve.value != null) {
		result += ve.value.getCssText();
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
	ValueEntry ve = properties.get(s);
        if (ve == null) {
            return "";
        }
        if (ve.value == null) { // ||
            //ve.value.getImmutableValue() == ImmutableInherit.INSTANCE) {
            CSSStyleDeclaration sd;
            sd = viewCSS.getComputedStyle(parentElement, null);
            return sd.getPropertyCSSValue(s).getCssText();
        } else {
            return ve.value.getCssText();
        }
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyCSSValue(String)}.
     */
    public CSSValue getPropertyCSSValue(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = properties.get(s);
        if (ve == null) {
            return null;
        }
        if (ve.value == null) {
            CSSStyleDeclaration sd;
            sd = viewCSS.getComputedStyle(parentElement, null);
            CSSOMReadOnlyValue v =
                (CSSOMReadOnlyValue)sd.getPropertyCSSValue(s);
            return ve.value = new CSSOMReadOnlyValue(v.getImmutableValue());
        } else {
            return ve.value;
        }
    }

    /**
     * Internal version of getPropertyCSSValue().
     */
    public CSSOMReadOnlyValue getPropertyCSSValueInternal(String propertyName) {
	ValueEntry ve = properties.get(propertyName);
        if (ve == null) {
            return null;
        }
        if (ve.value == null) {
            CSSOMReadOnlyStyleDeclaration sd;
            sd = viewCSS.getComputedStyleInternal(parentElement, null);
            CSSOMReadOnlyValue v = sd.getPropertyCSSValueInternal(propertyName);
            return ve.value = new CSSOMReadOnlyValue(v.getImmutableValue());
        } else {
            return ve.value;
        }
    }

    /**
     * Returns the local CSSValue.
     */
    public CSSValue getLocalPropertyCSSValue(String propertyName) {
	ValueEntry ve = properties.get(propertyName);
        return (ve == null) ? null : ve.value;
    }

    /**
     * Sets a property value.
     */
    public void setPropertyCSSValue(String   propertyName,
				    CSSValue v,
				    String   imp,
				    int      orig) {
	/*ValueEntry ve = (ValueEntry)*/
        properties.put(propertyName, createValueEntry((CSSOMReadOnlyValue)v,
                                                      imp, orig));
    }

    /**
     * Returns a property origin.
     */
    public int getPropertyOrigin(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = properties.get(s);
        if (ve == null) {
            return AUTHOR_ORIGIN;
        }
        if (ve.value == null) { // ||
            //ve.value.getImmutableValue() == ImmutableInherit.INSTANCE) {
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
	ValueEntry ve = properties.get(propertyName);
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
	ValueEntry ve = properties.get(s);
        if (ve == null) {
            return "";
        }
        if (ve.value == null) { // ||
            //ve.value.getImmutableValue() == ImmutableInherit.INSTANCE) {
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
	ValueEntry ve = properties.get(propertyName);
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
	String result = properties.key(index);
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
    protected ValueEntry createValueEntry(CSSOMReadOnlyValue v, String s, int p) {
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
    protected abstract static class ValueEntry {

        /**
         * Returns the priority.
         */
        public abstract String getPriority();

        /**
         * Returns the value origin.
         */
        public abstract int getOrigin();

	/**
	 * The hash code
	 */
	public int hash;

	/**
	 * The key
	 */
	public String key;

        /**
         * The value.
         */
        public CSSOMReadOnlyValue value;

	/**
	 * The next entry
	 */
	public ValueEntry next;

        /**
         * Initializes the value.
         */
        public void initialize(int h, String k, ValueEntry n) {
            hash = h;
            key = k;
            next = n;
        }
    }

    /**
     * To store an important user-agent value.
     */
    protected static class ImportantUserAgentValueEntry
        extends ValueEntry {

        /**
         * Creates a new value entry.
         */
        public ImportantUserAgentValueEntry(CSSOMReadOnlyValue v) {
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
        extends ValueEntry {

        /**
         * Creates a new value entry.
         */
        public UserAgentValueEntry(CSSOMReadOnlyValue v) {
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
        extends ValueEntry {

        /**
         * Creates a new value entry.
         */
        public ImportantUserValueEntry(CSSOMReadOnlyValue v) {
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
        extends ValueEntry {

        /**
         * Creates a new value entry.
         */
        public UserValueEntry(CSSOMReadOnlyValue v) {
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
        extends ValueEntry {

        /**
         * Creates a new value entry.
         */
        public ImportantAuthorValueEntry(CSSOMReadOnlyValue v) {
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
        extends ValueEntry {

        /**
         * Creates a new value entry.
         */
        public AuthorValueEntry(CSSOMReadOnlyValue v) {
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

    /**
     * To store the values.
     */
    protected static class PropertyMap {
	    
        /**
         * The initial capacity
         */
        protected final static int INITIAL_CAPACITY = 11;

        /**
         * The underlying array
         */
        protected ValueEntry[] table;
	    
        /**
         * The number of entries
         */
        protected int count;
	    
        /**
         * Creates a new table.
         */
        public PropertyMap() {
            table = new ValueEntry[INITIAL_CAPACITY];
        }

        /**
         * Returns the size of this table.
         */
        public int size() {
            return count;
        }
    
        /**
         * Gets the value of a variable
         * @return the value or null
         */
        public ValueEntry get(String key) {
            int hash  = key.hashCode() & 0x7FFFFFFF;
            int index = hash % table.length;
            
            for (ValueEntry e = table[index]; e != null; e = e.next) {
                if ((e.hash == hash) && e.key == key) {
                    return e;
                }
            }
            return null;
        }
    
        /**
         * Sets a new value for the given variable
         * @return the old value or null
         */
        public void put(String key, ValueEntry valueEntry) {
            int hash  = key.hashCode() & 0x7FFFFFFF;
            int index = hash % table.length;
	
            for (ValueEntry e = table[index]; e != null; e = e.next) {
                if ((e.hash == hash) && e.key == key) {
                    e.value = valueEntry.value;
                }
            }
	
            // The key is not in the hash table
            int len = table.length;
            if (count++ >= (len * 3) >>> 2) {
                rehash();
                index = hash % table.length;
            }
	
            valueEntry.initialize(hash, key, table[index]);
            table[index] = valueEntry;
        }

        /**
         * Returns the key at the given position or null.
         */
        public String key(int index) {
            if (index < 0 || index >= count) {
                return null;
            }
            int j = 0;
            for (int i = 0; i < table.length; i++) {
                ValueEntry e = table[i];
                if (e == null) {
                    continue;
                }
                do {
                    if (j++ == index) {
                        return e.key;
                    }
                    e = e.next;
                } while (e != null);
            }
            return null;
        }

        /**
         * Returns the item at the given position.
         */
        public ValueEntry item(int index) {
            if (index < 0 || index >= count) {
                return null;
            }
            int j = 0;
            for (int i = 0; i < table.length; i++) {
                ValueEntry e = table[i];
                if (e == null) {
                    continue;
                }
                do {
                    if (j++ == index) {
                        return e;
                    }
                    e = e.next;
                } while (e != null);
            }
            return null;
        }

        /**
         * Rehash the table
         */
        protected void rehash () {
            ValueEntry[] oldTable = table;
	
            table     = new ValueEntry[oldTable.length * 2 + 1];
	
            for (int i = oldTable.length-1; i >= 0; i--) {
                for (ValueEntry old = oldTable[i]; old != null;) {
                    ValueEntry e = old;
                    old = old.next;
		
                    int index = e.hash % table.length;
                    e.next = table[index];
                    table[index] = e;
                }
            }
        }
    }
}
