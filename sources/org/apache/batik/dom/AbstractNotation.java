/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Notation;

/**
 * This class implements the {@link org.w3c.dom.Notation} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractNotation
    extends    AbstractNode
    implements Notation {
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
     * @return {@link org.w3c.dom.Node#NOTATION_NODE}
     */
    public short getNodeType() {
	return NOTATION_NODE;
    }

    /**
     * Sets the name of this node.
     */
    public void setNodeName(String v) {
	nodeName = v;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     */
    public String getNodeName() {
	return nodeName;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Notation#getPublicId()}.
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
     * <b>DOM</b>: Implements {@link org.w3c.dom.Notation#getSystemId()}.
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
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	super.export(n, d);
	AbstractNotation an = (AbstractNotation)n;
	an.nodeName = nodeName;
	an.publicId = publicId;
	an.systemId = systemId;
	return n;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	AbstractNotation an = (AbstractNotation)n;
	an.nodeName = nodeName;
	an.publicId = publicId;
	an.systemId = systemId;
	return n;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	AbstractNotation an = (AbstractNotation)n;
	an.nodeName = nodeName;
	an.publicId = publicId;
	an.systemId = systemId;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	AbstractNotation an = (AbstractNotation)n;
	an.nodeName = nodeName;
	an.publicId = publicId;
	an.systemId = systemId;
	return n;
    }
}
