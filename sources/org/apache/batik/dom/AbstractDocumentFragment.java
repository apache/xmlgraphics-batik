/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/**
 * This class implements {@link org.w3c.dom.DocumentFragment} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractDocumentFragment
    extends AbstractParentNode
    implements DocumentFragment {
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return "#document-fragment".
     */
    public String getNodeName() {
	return "#document-fragment";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#DOCUMENT_FRAGMENT_NODE}
     */
    public short getNodeType() {
	return DOCUMENT_FRAGMENT_NODE;
    }

    /**
     * Checks the validity of a node to be inserted.
     */
    protected void checkChildType(Node n) {
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
