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
public class GenericElementNS extends AbstractElementNS {
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
    protected GenericElementNS() {
    }

    /**
     * Creates a new Element object.
     * @param nsURI The element namespace URI.
     * @param name  The element qualified name.
     * @param owner The owner document.
     * @exception DOMException
     *    INVALID_CHARACTER_ERR: Raised if the specified qualified name 
     *   contains an illegal character.
     *   <br> NAMESPACE_ERR: Raised if the <code>qualifiedName</code> is 
     *   malformed, if the <code>qualifiedName</code> has a prefix and the 
     *   <code>namespaceURI</code> is <code>null</code> or an empty string, 
     *   or if the <code>qualifiedName</code> has a prefix that is "xml" and 
     *   the <code>namespaceURI</code> is different from 
     *   "http://www.w3.org/XML/1998/namespace"  .
     */
    public GenericElementNS(String nsURI, String name,
                            AbstractDocument owner) {
	super(nsURI, name, owner);
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

    // ExtendedNode ///////////////////////////////////////////////////

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
	GenericElementNS ge = (GenericElementNS)super.export(n, d);
	ge.nodeName = nodeName;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	GenericElementNS ge = (GenericElementNS)super.deepExport(n, d);
	ge.nodeName = nodeName;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	GenericElementNS ge = (GenericElementNS)super.copyInto(n);
	ge.nodeName = nodeName;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	GenericElementNS ge = (GenericElementNS)super.deepCopyInto(n);
	ge.nodeName = nodeName;
	return n;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new GenericElementNS();
    }
}
