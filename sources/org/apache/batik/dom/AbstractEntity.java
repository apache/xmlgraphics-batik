/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Entity;
import org.w3c.dom.Node;

/**
 * This class implements the {@link org.w3c.dom.Entity} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractEntity
    extends    AbstractParentNode
    implements Entity {
    /**
     * The node name.
     */
    protected String nodeName;

    /**
     * The public id.
     */
    protected String publicId;

    /**
     * The system id.
     */
    protected String systemId;

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#ENTITY_NODE}
     */
    public short getNodeType() {
	return ENTITY_NODE;
    }

    /**
     * Sets the name of this node.
     */
    public void setNodeName(String v) {
	nodeName = v;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return {@link #nodeName}.
     */
    public String getNodeName() {
	return nodeName;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Entity#getPublicId()}.
     * @return {@link #publicId}.
     */
    public String getPublicId() {
	return publicId;
    }

    /**
     * Sets the public id.
     */
    public void setPublicId(String id) {
	publicId = id;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Entity#getSystemId()}.
     * @return {@link #systemId}.
     */
    public String getSystemId() {
	return systemId;
    }

    /**
     * Sets the system id.
     */
    public void setSystemId(String id) {
	systemId = id;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Entity#getNotationName()}.
     * @return {@link #getNodeName()}.
     */
    public String getNotationName() {
	return getNodeName();
    }

    /**
     * Sets the notation name.
     */
    public void setNotationName(String name) {
	setNodeName(name);
    }

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	AbstractEntity ae = (AbstractEntity)n;
	ae.nodeName = nodeName;
	ae.publicId = publicId;
	ae.systemId = systemId;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	AbstractEntity ae = (AbstractEntity)n;
	ae.nodeName = nodeName;
	ae.publicId = publicId;
	ae.systemId = systemId;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	AbstractEntity ae = (AbstractEntity)n;
	ae.nodeName = nodeName;
	ae.publicId = publicId;
	ae.systemId = systemId;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	AbstractEntity ae = (AbstractEntity)n;
	ae.nodeName = nodeName;
	ae.publicId = publicId;
	ae.systemId = systemId;
	return n;
    }

    /**
     * Checks the validity of a node to be inserted.
     */
    protected void checkChildType(Node n, boolean replace) {
	switch (n.getNodeType()) {
	case ELEMENT_NODE:
	case PROCESSING_INSTRUCTION_NODE:
	case COMMENT_NODE:
	case TEXT_NODE:
	case CDATA_SECTION_NODE:
	case ENTITY_REFERENCE_NODE:
	case DOCUMENT_FRAGMENT_NODE:
	    break;
	default:
	    throw createDOMException
                (DOMException.HIERARCHY_REQUEST_ERR,
                 "child.type",
                 new Object[] { new Integer(getNodeType()),
                                getNodeName(),
                                new Integer(n.getNodeType()),
                                n.getNodeName() });
	}
    }
}
