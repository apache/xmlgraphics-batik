/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.traversal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;

/**
 * This class provides support for traversal.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TraversalSupport {
    
    /**
     * The iterators list.
     */
    protected List iterators;

    /**
     * Creates a new TraversalSupport.
     */
    public TraversalSupport() {
    }

    /**
     * Creates a new tree walker.
     */
    public static TreeWalker createTreeWalker(AbstractDocument doc,
                                              Node root,
                                              int whatToShow, 
                                              NodeFilter filter, 
                                              boolean entityReferenceExpansion) {
        if (root == null) {
            throw doc.createDOMException
                (DOMException.NOT_SUPPORTED_ERR, "null.root",  null);
        }
        return new DOMTreeWalker(root, whatToShow, filter,
                                 entityReferenceExpansion);
    }

    /**
     * Creates a new node iterator.
     */
    public NodeIterator createNodeIterator(AbstractDocument doc,
                                           Node root,
                                           int whatToShow, 
                                           NodeFilter filter, 
                                           boolean entityReferenceExpansion)
        throws DOMException {
        if (root == null) {
            throw doc.createDOMException
                (DOMException.NOT_SUPPORTED_ERR, "null.root",  null);
        }
        NodeIterator result = new DOMNodeIterator(doc, root, whatToShow,
                                                  filter,
                                                  entityReferenceExpansion);
        if (iterators == null) {
            iterators = new LinkedList();
        }
        iterators.add(result);

        return result;
    }

    /**
     * Called by the DOM when a node will be removed from the current document.
     */
    public void nodeToBeRemoved(Node removedNode) {
        if (iterators != null) {
            Iterator it = iterators.iterator();
            while (it.hasNext()) {
                ((DOMNodeIterator)it.next()).nodeToBeRemoved(removedNode);
            }
        }
    }

    /**
     * Detaches the given node iterator.
     */
    public void detachNodeIterator(NodeIterator it) {
        iterators.remove(it);
    }
}
