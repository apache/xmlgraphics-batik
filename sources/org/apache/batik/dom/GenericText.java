/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Text;

/**
 * This class provides a generic implementation of the {@link org.w3c.dom.Text}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class GenericText extends AbstractText {
    /**
     * Is this element immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new uninitialized Text object.
     */
    public GenericText() {
    }

    /**
     * Creates a new Text object.
     */
    public GenericText(String value, AbstractDocument owner) {
	ownerDocument = owner;
	setNodeValue(value);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return {@link #getNodeName()}.
     */
    public String getNodeName() {
	return "#text";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#TEXT_NODE}
     */
    public short getNodeType() {
	return TEXT_NODE;
    }

    /**
     * Tests whether this node is readonly.
     */
    public boolean isReadonly() {
	return readonly;
    }

    /**
     * Sets this node readonly attribute.
     */
    public void setReadonly(boolean v) {
	readonly = v;
    }

    /**
     * Creates a text node of the current type.
     */
    protected Text createTextNode(String text) {
	return getOwnerDocument().createTextNode(text);
    }
}
