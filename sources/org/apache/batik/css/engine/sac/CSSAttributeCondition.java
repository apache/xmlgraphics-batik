/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import java.util.Set;

import org.w3c.dom.Element;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.AttributeCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSAttributeCondition extends AbstractAttributeCondition {
    /**
     * The attribute's local name.
     */
    protected String localName;

    /**
     * The attribute's namespace URI.
     */
    protected String namespaceURI;

    /**
     * Whether this condition applies to specified attributes.
     */
    protected boolean specified;

    /**
     * Creates a new CSSAttributeCondition object.
     */
    public CSSAttributeCondition(String localName,
                                 String namespaceURI,
                                 boolean specified,
                                 String value) {
	super(value);
	this.localName = localName;
	this.namespaceURI = namespaceURI;
	this.specified = specified;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (!super.equals(obj)) {
	    return false;
	}
	CSSAttributeCondition c = (CSSAttributeCondition)obj;
	return c.namespaceURI.equals(namespaceURI) &&
	       c.localName.equals(localName) &&
	       c.specified == specified;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Condition#getConditionType()}.
     */    
    public short getConditionType() {
	return SAC_ATTRIBUTE_CONDITION;
    }
    
    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.AttributeCondition#getNamespaceURI()}.
     */    
    public String getNamespaceURI() {
	return namespaceURI;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.AttributeCondition#getLocalName()}.
     */
    public String getLocalName() {
	return localName;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.AttributeCondition#getSpecified()}.
     */
    public boolean getSpecified() {
	return specified;
    }

    /**
     * Tests whether this condition matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	String val = getValue();
	if (val == null) {
	    return !e.getAttribute(getLocalName()).equals("");
	}
	return e.getAttribute(getLocalName()).equals(val);
    }

    /**
     * Fills the given set with the attribute names found in this selector.
     */
    public void fillAttributeSet(Set attrSet) {
        attrSet.add(localName);
    }

    /**
     * Returns a text representation of this object.
     */
    public String toString() {
	if (value == null) {
	    return "[" + localName + "]";
	}
	return "[" + localName + "=\"" + value + "\"]";
    }
}
