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

public class CSSIdCondition extends AbstractAttributeCondition {

    /**
     * The id attribute namespace URI.
     */
    protected String namespaceURI;

    /**
     * The id attribute local name.
     */
    protected String localName;

    /**
     * Creates a new CSSAttributeCondition object.
     */
    public CSSIdCondition(String ns, String ln, String value) {
	super(value);
        namespaceURI = ns;
        localName = ln;
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Condition#getConditionType()}.
     */    
    public short getConditionType() {
	return SAC_ID_CONDITION;
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
	return true;
    }

    /**
     * Tests whether this condition matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	return (e instanceof CSSStylableElement)
	    ? ((CSSStylableElement)e).getXMLId().equals(getValue())
	    : false;
    }

    /**
     * Fills the given set with the attribute names found in this selector.
     */
    public void fillAttributeSet(Set attrSet) {
        attrSet.add(localName);
    }

    /**
     * Returns the specificity of this condition.
     */
    public int getSpecificity() {
	return 1 << 16;
    }

    /**
     * Returns a text representation of this object.
     */
    public String toString() {
	return "#" + getValue();
    }
}
