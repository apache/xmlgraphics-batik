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
import org.w3c.dom.Text;

/**
 * This class implements the {@link org.w3c.dom.Text} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractText
    extends    AbstractCharacterData
    implements Text {
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Text#splitText(int)}.
     */
    public Text splitText(int offset) throws DOMException {
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}
	String v = getNodeValue();
	if (offset < 0 || offset >= v.length()) {
	    throw createDOMException(DOMException.INDEX_SIZE_ERR,
				     "offset",
				     new Object[] { new Integer(offset) });
	}
	Node n = getParentNode();
	if (n == null) {
	    throw createDOMException(DOMException.INDEX_SIZE_ERR,
				     "need.parent",
				     new Object[] {});
	}
	String t1 = v.substring(offset, v.length());
	Text t = createTextNode(t1);
	Node ns = getNextSibling();
	if (ns != null) {
	    n.insertBefore(t, ns);
	} else {
	    n.appendChild(t);
	}
	setNodeValue(v.substring(0, offset));
	return t;
    }

    /**
     * Creates a text node of the current type.
     */
    protected abstract Text createTextNode(String text);
}
