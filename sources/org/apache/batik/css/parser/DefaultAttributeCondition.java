/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.parser;

import org.w3c.dom.Element;

/**
 * This class provides an implementation of the
 * {@link org.w3c.css.sac.AttributeCondition} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultAttributeCondition extends AbstractAttributeCondition {

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
     * Creates a new DefaultAttributeCondition object.
     */
    public DefaultAttributeCondition(String localName,
                                     String namespaceURI,
                                     boolean specified,
                                     String value) {
	super(value);
	this.localName = localName;
	this.namespaceURI = namespaceURI;
	this.specified = specified;
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
     * Returns a text representation of this object.
     */
    public String toString() {
	if (value == null) {
	    return "[" + localName + "]";
	}
	return "[" + localName + "=\"" + value + "\"]";
    }
}
