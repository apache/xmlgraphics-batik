/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class implements a wrapper for a NodeListWrapper. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class NodeListWrapper implements NodeList {
    
    /**
     * The owner document wrapper.
     */
    protected DocumentWrapper documentWrapper;

    /**
     * The wrapped list.
     */
    protected NodeList nodeList;

    /**
     * Creates a new NodeListWrapper.
     */
    public NodeListWrapper(DocumentWrapper dw, NodeList nl) {
        documentWrapper = dw;
        nodeList = nl;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.NodeList#item(int)}.
     */
    public Node item(final int index) {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = nodeList.item(index);
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return documentWrapper.createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.NodeList#getLength()}.
     */
    public int getLength() {
        class Query implements Runnable {
            int result;
            public void run() {
                result = nodeList.getLength();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return q.result;
    }
}
