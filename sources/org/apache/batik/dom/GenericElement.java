/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * This class implements the {@link org.w3c.dom.Element} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericElement extends AbstractElement {
    /**
     * The node name.
     */
    protected String nodeName;

    /**
     * Is this element immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new Element object.
     */
    public GenericElement() {
    }

    /**
     * Creates a new Element object.
     * @param name  The element name for validation purposes.
     * @param owner The owner document.
     * @exception DOMException
     *   INVALID_CHARACTER_ERR: if name contains invalid characters,
     */
    public GenericElement(String name, AbstractDocument owner)
	throws DOMException {
	super(name, owner);
	nodeName = name;
    }

    /**
     * Sets the name of this node.
     */
    public void setNodeName(String v) {
	nodeName = v;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return {@link #nodeName}
     */
    public String getNodeName() {
	return nodeName;
    }

    // ExtendedNode //////////////////////////////////////////////////
    
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
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	GenericElement ge = (GenericElement)n;
	ge.nodeName = nodeName;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	GenericElement ge = (GenericElement)n;
	ge.nodeName = nodeName;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	GenericElement ge = (GenericElement)super.copyInto(n);
	ge.nodeName = nodeName;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	GenericElement ge = (GenericElement)super.deepCopyInto(n);
	ge.nodeName = nodeName;
	return n;
    }
}
