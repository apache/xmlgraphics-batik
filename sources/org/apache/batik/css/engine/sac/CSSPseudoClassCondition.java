/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import java.util.Set;

import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.Element;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.AttributeCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSPseudoClassCondition extends AbstractAttributeCondition {
    /**
     * The namespaceURI.
     */
    protected String namespaceURI;

    /**
     * Creates a new CSSAttributeCondition object.
     */
    public CSSPseudoClassCondition(String namespaceURI, String value) {
	super(value);
	this.namespaceURI = namespaceURI;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     */
    public boolean equals(Object obj) {
	if (!super.equals(obj)) {
	    return false;
	}
	CSSPseudoClassCondition c = (CSSPseudoClassCondition)obj;
	return c.namespaceURI.equals(namespaceURI);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Condition#getConditionType()}.
     */    
    public short getConditionType() {
	return SAC_PSEUDO_CLASS_CONDITION;
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
	return null;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.AttributeCondition#getSpecified()}.
     */
    public boolean getSpecified() {
	return false;
    }

    /**
     * Tests whether this selector matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	return (e instanceof CSSStylableElement)
	    ? ((CSSStylableElement)e).isPseudoInstanceOf(getValue())
	    : false;
    }

    /**
     * Fills the given set with the attribute names found in this selector.
     */
    public void fillAttributeSet(Set attrSet) {
    }

    /**
     * Returns a text representation of this object.
     */
    public String toString() {
	return ":" + getValue();
    }
}
