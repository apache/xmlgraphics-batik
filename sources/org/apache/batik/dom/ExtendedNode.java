/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;

/**
 * This interface provides an access to the non DOM methods implemented by
 * all the nodes in this implementation.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public interface ExtendedNode extends EventTarget, NodeEventTarget {
    /**
     * Sets the name of this node.
     */
    void setNodeName(String v);

    /**
     * Tests whether this node is readonly.
     */
    boolean isReadonly();

    /**
     * Sets this node readonly attribute.
     */
    void setReadonly(boolean v);

    /**
     * Sets the parent node.
     */
    void setParentNode(Node v);

    /**
     * Sets the node immediately preceding this node.
     */
    void setPreviousSibling(Node n);

    /**
     * Sets the node immediately following this node.
     */
    void setNextSibling(Node n);

    /**
     * Sets the value of the specified attribute. This method only applies
     * to Attr objects.
     */
    void setSpecified(boolean v);
}
