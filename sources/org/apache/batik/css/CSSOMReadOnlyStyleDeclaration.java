/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import org.apache.batik.css.event.CSSStyleDeclarationChangeListener;
import org.apache.batik.css.event.CSSStyleDeclarationChangeSupport;
import org.apache.batik.css.event.CSSValueChangeListener;
import org.apache.batik.css.value.ValueFactory;
import org.apache.batik.css.value.ValueFactoryMap;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

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
     * The style declaration change event support.
     */
    protected CSSStyleDeclarationChangeSupport declarationChangeSupport;

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getCssText()}.
     */
    public String getCssText() {
	String result = "";
	for (int i = properties.size() - 1; i >= 0; i--) {
	    result += "    " + properties.key(i) + ": ";
	    ValueEntry ve = (ValueEntry)properties.item(i);
	    if (ve.value != null) {
		result += ((CSSValue)ve.value).getCssText();
	    }
	    result += ve.priority + ";\n";
	}
	return result;
    }

    /**
     * Notifies this style declaration that a CSSValue as changed.
     */
    public void cssValueChange(String prop, CSSValue oldV, CSSValue newV) {
	if (declarationChangeSupport != null) {
	    declarationChangeSupport.fireCSSPropertyChange(prop, oldV, newV);
	}
    }

    /**
     * Adds a CSSStyleDeclarationChangeListener to the listener list.
     * @param listener The CSSStyleDeclarationChangeListener to be added
     */
    public void addCSSStyleDeclarationChangeListener
	(CSSStyleDeclarationChangeListener listener) {
	if (declarationChangeSupport == null) {
	    declarationChangeSupport =
                new CSSStyleDeclarationChangeSupport(this);
	}
	declarationChangeSupport.addCSSStyleDeclarationChangeListener
            (listener);
    }
    
    /**
     * Removes a CSSStyleDeclarationChangeListener from the listener list.
     * @param listener The CSSStyleDeclarationChangeListener to be removed
     */
    public void removeCSSStyleDeclarationChangeListener
	(CSSStyleDeclarationChangeListener listener) {
	if (declarationChangeSupport == null) {
	    return;
	}
	declarationChangeSupport.removeCSSStyleDeclarationChangeListener
            (listener);
    }

    /**
     * Reports the start of a CSSStyleDeclaration update to any registered
     * listeners.
     */
    public void fireCSSStyleDeclarationChangeStart() {
	if (declarationChangeSupport == null) {
	    return;
	}
	declarationChangeSupport.fireCSSStyleDeclarationChangeStart();
    }

    /**
     * Reports the cancellation of a CSSStyleDeclaration update to any
     * registered listeners.
     */
    public void fireCSSStyleDeclarationChangeCancel() {
	if (declarationChangeSupport == null) {
	    return;
	}
	declarationChangeSupport.fireCSSStyleDeclarationChangeCancel();
    }

    /**
     * Reports the end of a CSSStyleDeclaration update to any registered
     * listeners.
     */
    public void fireCSSStyleDeclarationChangeEnd() {
	if (declarationChangeSupport == null) {
	    return;
	}
	declarationChangeSupport.fireCSSStyleDeclarationChangeEnd();
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
	return (ve == null) ? "" : ve.value.getCssText();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyCSSValue(String)}.
     */
    public CSSValue getPropertyCSSValue(String propertyName) {
        String s = propertyName.toLowerCase().intern();
	ValueEntry ve = (ValueEntry)properties.get(s);
	return (ve == null) ? null : ve.value;
    }

    /**
     * Sets a property value.
     */
    public void setPropertyCSSValue(String   propertyName,
				    CSSValue v,
				    String   imp,
				    int      orig) {
	ValueEntry ve = (ValueEntry)properties.put(propertyName,
						 new ValueEntry(v, imp, orig));
	CSSValue oldV = (ve == null) ? null : ve.value;
	cssValueChange(propertyName, oldV, v);
    }

    /**
     * Returns a property origin.
     */
    public int getPropertyOrigin(String propertyName) {
	ValueEntry ve = (ValueEntry)properties.get(propertyName);
	return (ve == null)
	    ? CSSOMReadOnlyStyleDeclaration.USER_AGENT_ORIGIN
	    : ve.origin;
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
	return (ve == null) ? "" : ve.priority;
    }

    /**
     * The same as getPropertyPriority() but the given string is not interned.
     */
    public String getPropertyPriorityUnsafe(String propertyName) {
	ValueEntry ve = (ValueEntry)properties.get(propertyName);
	return (ve == null) ? "" : ve.priority;
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
     * To store the CSSValue of a property.
     */
    protected static class ValueEntry {
	/**
	 * The value
	 */
	public CSSValue value;

	/**
	 * The priority
	 */
	public String priority;

	/**
	 * The origin
	 */
	public int origin;

	/**
	 * Creates a new ValueEntry object.
	 */
	public ValueEntry(CSSValue value, String priority, int origin) {
	    this.value    = value;
	    this.priority = priority;
	    this.origin   = origin;
	}
    }
}
