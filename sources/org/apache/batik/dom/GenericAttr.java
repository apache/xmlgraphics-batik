/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.DOMException;

/**
 * This class implements the {@link org.w3c.dom.Attr} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericAttr extends AbstractAttr {
    /**
     * Is this attribute immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new Attr object.
     */
    public GenericAttr() {
    }

    /**
     * Creates a new Attr object.
     * @param name  The attribute name for validation purposes.
     * @param owner The owner document.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: if name contains invalid characters,
     */
    public GenericAttr(String name, AbstractDocument owner)
        throws DOMException {
	super(name, owner);
	setNodeName(name);
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
}
