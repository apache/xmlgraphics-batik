/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine.sac;

import org.w3c.dom.Element;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.AttributeCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOneOfAttributeCondition extends CSSAttributeCondition {
    /**
     * Creates a new CSSAttributeCondition object.
     */
    public CSSOneOfAttributeCondition(String localName,
                                      String namespaceURI,
                                      boolean specified,
                                      String value) {
	super(localName, namespaceURI, specified, value);
    }

    /**
     * <b>SAC</b>: Implements {@link
     * org.w3c.css.sac.Condition#getConditionType()}.
     */    
    public short getConditionType() {
	return SAC_ONE_OF_ATTRIBUTE_CONDITION;
    }
    
    /**
     * Tests whether this condition matches the given element.
     */
    public boolean match(Element e, String pseudoE) {
	String attr = e.getAttribute(getLocalName());
	String val = getValue();
	int i = attr.indexOf(val);
	if (i == -1) {
	    return false;
	}
	if (i != 0 && !Character.isSpaceChar(attr.charAt(i - 1))) {
	    return false;
	}
	int j = i + val.length();
	return (j == attr.length() ||
		(j < attr.length() && Character.isSpaceChar(attr.charAt(j))));
    }

    /**
     * Returns a text representation of this object.
     */
    public String toString() {
	return "[" + getLocalName() + "~=\"" + getValue() + "\"]";
    }
}
