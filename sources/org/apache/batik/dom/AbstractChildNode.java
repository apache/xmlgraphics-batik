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
 * This class implements the {@link org.w3c.dom.Node} interface with support
 * for parent and siblings.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractChildNode extends AbstractNode {
    /**
     * The parent node of this node.
     */
    protected Node parentNode;

    /**
     * The previous sibling.
     */
    protected Node previousSibling;

    /**
     * Returns the next sibling.
     */
    protected Node nextSibling;

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getParentNode()}.
     * @return {@link #parentNode}
     */
    public Node getParentNode() {
	return parentNode;
    }

    /**
     * Sets the parent node.
     */
    public void setParentNode(Node v) {
	parentNode = v;
    }

    /**
     * Sets the node immediately preceding this node.
     */
    public void setPreviousSibling(Node v) {
	previousSibling = v;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getPreviousSibling()}.
     * @return {@link #previousSibling}.
     */
    public Node getPreviousSibling() {
	return previousSibling;
    }

    /**
     * Sets the node immediately following this node.
     */
    public void setNextSibling(Node v) {
	nextSibling = v;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNextSibling()}.
     * @return {@link #nextSibling}.
     */
    public Node getNextSibling() {
	return nextSibling;
    }
}
