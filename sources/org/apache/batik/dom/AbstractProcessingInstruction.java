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
import org.w3c.dom.ProcessingInstruction;

/**
 * This class implements the {@link org.w3c.dom.ProcessingInstruction}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractProcessingInstruction
    extends    AbstractChildNode
    implements ProcessingInstruction {
    /**
     * The data.
     */
    protected String data;

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return {@link #getTarget()}.
     */
    public String getNodeName() {
	return getTarget();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#PROCESSING_INSTRUCTION_NODE}
     */
    public short getNodeType() {
	return PROCESSING_INSTRUCTION_NODE;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeValue()}.
     * @return {@link #getData()}.
     */
    public String getNodeValue() throws DOMException {
	return getData();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#setNodeValue(String)}.
     * @return {@link #setData(String)}.
     */
    public void setNodeValue(String nodeValue) throws DOMException {
	setData(nodeValue);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.ProcessingInstruction#getData()}.
     * @return {@link #data}.
     */
    public String getData() {
	return data;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.ProcessingInstruction#setData(String)}.
     */
    public void setData(String data) throws DOMException {
	if (isReadonly()) {
            throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                     "readonly.node",
                                     new Object[] { new Integer(getNodeType()),
                                                    getNodeName() });
	}
	String val = this.data;
	this.data = data;

	// Mutation event
	fireDOMCharacterDataModifiedEvent(val, this.data);
	if (getParentNode() != null) {
	    ((AbstractParentNode)getParentNode()).
                fireDOMSubtreeModifiedEvent();
	}
    }

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	AbstractProcessingInstruction p;
	p = (AbstractProcessingInstruction)super.export(n, d);
	p.data = data;
	return p;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	AbstractProcessingInstruction p;
	p = (AbstractProcessingInstruction)super.deepExport(n, d);
	p.data = data;
	return p;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	AbstractProcessingInstruction p;
	p = (AbstractProcessingInstruction)super.copyInto(n);
	p.data = data;
	return p;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	AbstractProcessingInstruction p;
	p = (AbstractProcessingInstruction)super.deepCopyInto(n);
	p.data = data;
	return p;
    }
}
