/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

/**
 * This class implements the {@link org.w3c.dom.Entity} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericEntity extends AbstractEntity {
    /**
     * Is this node immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new Entity object.
     */
    public GenericEntity() {
    }

    /**
     * Creates a new Entity object.
     */
    public GenericEntity(String           name,
			 String           pubId,
			 String           sysId,
			 AbstractDocument owner) {
	ownerDocument = owner;
	setNodeName(name);
	setPublicId(pubId);
	setSystemId(sysId);
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
