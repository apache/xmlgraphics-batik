/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

/**
 * This class implements {@link org.w3c.dom.DocumentFragment} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericDocumentFragment extends AbstractDocumentFragment {
    /**
     * Is this element immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new DocumentFragment object.
     */
    public GenericDocumentFragment() {
    }

    /**
     * Creates a new DocumentFragment object.
     */
    public GenericDocumentFragment(AbstractDocument owner) {
	ownerDocument = owner;
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
