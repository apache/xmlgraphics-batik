/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Node;

/**
 * This class implements the {@link
 * org.w3c.dom.ProcessingInstruction} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class GenericProcessingInstruction
    extends AbstractProcessingInstruction {
    /**
     * The target.
     */
    protected String target;

    /**
     * Is this node immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new ProcessingInstruction object.
     */
    protected GenericProcessingInstruction() {
    }

    /**
     * Creates a new ProcessingInstruction object.
     */
    public GenericProcessingInstruction(String           target,
					String           data,
					AbstractDocument owner) {
	ownerDocument = owner;
	setTarget(target);
	setData(data);
    }

    /**
     * Sets the node name.
     */
    public void setNodeName(String v) {
	setTarget(v);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.ProcessingInstruction#getTarget()}.
     * @return {@link #target}.
     */
    public String getTarget() {
	return target;
    }

    /**
     * Sets the target value.
     */
    public void setTarget(String v) {
	target = v;
    }

    /**
     * Exports this node to the given document.
     */
    protected Node export(Node n, AbstractDocument d) {
	GenericProcessingInstruction p;
	p = (GenericProcessingInstruction)super.export(n, d);
	p.setTarget(getTarget());
	return p;
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	GenericProcessingInstruction p;
	p = (GenericProcessingInstruction)super.deepExport(n, d);
	p.setTarget(getTarget());
	return p;
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	GenericProcessingInstruction p;
	p = (GenericProcessingInstruction)super.copyInto(n);
	p.setTarget(getTarget());
	return p;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	GenericProcessingInstruction p;
	p = (GenericProcessingInstruction)super.deepCopyInto(n);
	p.setTarget(getTarget());
	return p;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new GenericProcessingInstruction();
    }
}
