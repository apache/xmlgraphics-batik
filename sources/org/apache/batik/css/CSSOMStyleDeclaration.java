/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css;

import java.io.Reader;
import java.io.StringReader;
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
public class CSSOMStyleDeclaration
    implements CSSStyleDeclaration,
	       CSSValueChangeListener {
    /**
     * The default value factory map.
     */
    protected final static ValueFactoryMap EMPTY_MAP = new ValueFactoryMap() {
	public ValueFactory get(String property) { return null; }
    };

    /**
     * The CSS parser.
     */
    protected Parser parser;

    /**
     * The properties.
     */
    protected PropertyMap properties = new PropertyMap();

    /**
     * The old properties.
     */
    protected PropertyMap oldProperties;

    /**
     * The document handler.
     */
    protected DocumentHandler handler = new StyleDeclarationHandler();

    /**
     * The value factories.
     */
    protected ValueFactoryMap factories = EMPTY_MAP;

    /**
     * The parent rule
     */
    protected CSSRule parentRule;

    /**
     * The style declaration change event support.
     */
    protected CSSStyleDeclarationChangeSupport declarationChangeSupport;

    /**
     * Creates a new CSSStyleDeclaration object.
     */
    public CSSOMStyleDeclaration() {
	try {
	    parser = CSSDocumentHandler.createParser();
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    /**
     * Creates a new CSSStyleDeclaration object.
     */
    public CSSOMStyleDeclaration(CSSRule r, Parser p) {
	parser = p;
	parentRule = r;
    }

    /**
     * Sets the value factory map.
     */
    public void setValueFactoryMap(ValueFactoryMap map) {
	factories = map;
    }

    /**
     * Returns the value factory map.
     */
    public ValueFactoryMap getValueFactoryMap() {
	return factories;
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
     * org.w3c.dom.css.CSSStyleDeclaration#getCssText()}.
     */
    public String getCssText() {
	StringBuffer result = new StringBuffer();
	for (int i = properties.size() - 1; i >= 0; i--) {
	    String key = (String)properties.key(i);
	    result.append("    ").append(key).append(": ");
	    ValueEntry ve = (ValueEntry)properties.get(key);
	    if (ve.value != null) {
		result.append(((CSSValue)ve.value).getCssText());
	    }
	    result.append(ve.priority).append(";\n");
	}
	return result.toString();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#setCssText(String)}.
     */
    public void setCssText(String cssText) throws DOMException {
	fireCSSStyleDeclarationChangeStart();

	PropertyMap pm = properties;
        oldProperties = new PropertyMap(properties);
	properties = new PropertyMap();
	try {
	    Reader r = new StringReader(cssText);
	    parser.setDocumentHandler(handler);
	    parser.parseStyleDeclaration(new InputSource(r));
	} catch (DOMException e) {
	    properties = pm;
	    oldProperties = null;
	    fireCSSStyleDeclarationChangeCancel();
	    throw e;
	} catch (Exception e) {
	    properties = pm;
	    oldProperties = null;
	    fireCSSStyleDeclarationChangeCancel();
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "style.declaration.value",
		 new Object[] { cssText });
	}
	// Reset the CSSValueChangeListener of each old value.
	for (int i = 0; i < pm.size(); i++) {
	    ValueEntry ve = (ValueEntry)pm.item(i);
	    ve.value.setCSSValueChangeListener(null);
	}

	// Fire the remaining property change events
	for (int i = 0; i < oldProperties.size(); i++) {
	    ValueEntry ve = (ValueEntry)oldProperties.item(i);
	    cssValueChange((String)oldProperties.key(i), ve.value, null);
	}
	oldProperties = null;

	fireCSSStyleDeclarationChangeEnd();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyValue(String)}.
     */
    public String getPropertyValue(String propertyName) {
	ValueEntry ve;
        ve = (ValueEntry)properties.get(propertyName.toLowerCase().intern());
	return (ve == null) ? "" : ve.value.getCssText();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyCSSValue(String)}.
     */
    public CSSValue getPropertyCSSValue(String propertyName) {
	ValueEntry ve;
        ve = (ValueEntry)properties.get(propertyName.toLowerCase().intern());
	return (ve == null) ? null : ve.value;
    }

    /**
     * Sets a property value.
     */
    public void setPropertyCSSValue(String propertyName, CSSValue v,
                                    String imp) {
	CSSOMValue oldV;
	ValueEntry ve;
        ve = (ValueEntry)properties.put(propertyName,
                                        new ValueEntry((CSSOMValue)v, imp));
	oldV = (ve == null) ? null : ve.value;
	
	if (oldV == null && oldProperties != null) {
	    ve = (ValueEntry)oldProperties.remove(propertyName);
	    if (ve != null) {
		oldV = ve.value;
	    }
	}
	if (oldV != null) {
	    oldV.setCSSValueChangeListener(null);
	}
	((CSSOMValue)v).setCSSValueChangeListener(this);
	cssValueChange(propertyName, oldV, v);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#removeProperty(String)}.
     */
    public String removeProperty(String propertyName) throws DOMException {
	ValueEntry ve = (ValueEntry)properties.remove
	    (propertyName.toLowerCase().intern());
	if (ve == null) {
	    return "";
	} else {
	    CSSOMValue val = (CSSOMValue)ve.value;
	    val.setCSSValueChangeListener(null);
	    return val.getCssText();
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#getPropertyPriority(String)}.
     */
    public String getPropertyPriority(String propertyName) {
	ValueEntry ve;
        ve = (ValueEntry)properties.get(propertyName.toLowerCase().intern());
	return (ve == null) ? "" : ((ve.priority == null) ? "" : ve.priority);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSStyleDeclaration#setProperty(String,String,String)}.
     */
    public void setProperty(String propertyName, String value, String prio)
	throws DOMException {
	try {
	    ValueFactory f;
            f = factories.get(propertyName.toLowerCase().intern());
	    InputSource is = new InputSource(new StringReader(value));
	    LexicalUnit lu = parser.parsePropertyValue(is);
	    f.createCSSValue(lu, this, prio);
	} catch (Exception e) {
            e.printStackTrace();
	    throw CSSDOMExceptionFactory.createDOMException
		(DOMException.INVALID_ACCESS_ERR,
		 "property.value",
		 new Object[] { value });
	}
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
     */
    public CSSRule getParentRule() {
	return parentRule;
    }

    /**
     * To store the CSSValue of a property.
     */
    protected static class ValueEntry {
	/**
	 * The value
	 */
	public CSSOMValue value;

	/**
	 * The priority
	 */
	public String priority;

	/**
	 * Creates a new ValueEntry object.
	 */
	public ValueEntry(CSSOMValue value, String priority) {
	    this.value = value;
	    this.priority = priority;
	}
    }

    /**
     * To handle the parsing of a style declaration
     */
    protected class StyleDeclarationHandler extends DocumentHandlerAdapter {
	/**
	 * Receive notification of a declaration.
	 */
	public void property(String name, LexicalUnit value, boolean important)
	    throws CSSException {
	    String imp = (important) ? "!important" : "";
	    ValueFactory f = factories.get(name.toLowerCase().intern());

	    f.createCSSValue(value, CSSOMStyleDeclaration.this, imp);
	}
    }
}
