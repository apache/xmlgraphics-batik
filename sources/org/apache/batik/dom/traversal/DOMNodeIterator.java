/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.dom.traversal;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 * This class implements the {@link org.w3c.dom.traversal.NodeIterator}
 * interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DOMNodeIterator implements NodeIterator {

    /**
     * The initial state.
     */
    protected final static short INITIAL = 0;

    /**
     * The invalid state.
     */
    protected final static short INVALID = 1;

    /**
     * The forward state.
     */
    protected final static short FORWARD = 2;

    /**
     * The backward state.
     */
    protected final static short BACKWARD = 3;

    /**
     * The document which created the iterator.
     */
    protected AbstractDocument document;

    /**
     * The root node.
     */
    protected Node root;

    /**
     * Which node types are presented via the iterator.
     */
    protected int whatToShow;

    /**
     * The NodeFilter used to screen nodes.
     */
    protected NodeFilter filter;

    /**
     * Whether the children of entity reference nodes are visible
     * to the iterator.
     */
    protected boolean expandEntityReferences;

    /**
     * The iterator state.
     */
    protected short state;

    /**
     * The reference node.
     */
    protected Node referenceNode;

    /**
     * Creates a new NodeIterator object.
     * @param doc The document which created the tree walker.
     * @param n The root node.
     * @param what Which node types are presented via the iterator.
     * @param nf The NodeFilter used to screen nodes.
     * @param exp Whether the children of entity reference nodes are visible
     *            to the iterator.
     */
    public DOMNodeIterator(AbstractDocument doc, Node n, int what,
                           NodeFilter nf, boolean exp) {
        document = doc;
        root = n;
        whatToShow = what;
        filter = nf;
        expandEntityReferences = exp;

        referenceNode = root;
    }

    /**
     * <b>DOM</b>: Implements {@link NodeIterator#getRoot()}.
     */
    public Node getRoot() {
        return root;
    }

    /**
     * <b>DOM</b>: Implements {@link NodeIterator#getWhatToShow()}.
     */
    public int getWhatToShow() {
        return whatToShow;
    }

    /**
     * <b>DOM</b>: Implements {@link NodeIterator#getFilter()}.
     */
    public NodeFilter getFilter() {
        return filter;
    }

    /**
     * <b>DOM</b>: Implements {@link NodeIterator#getExpandEntityReferences()}.
     */
    public boolean getExpandEntityReferences() {
        return expandEntityReferences;
    }

    /**
     * <b>DOM</b>: Implements {@link NodeIterator#nextNode()}.
     */
    public Node nextNode() {
        switch (state) {
        case INVALID:
            throw document.createDOMException
                (DOMException.INVALID_STATE_ERR,
                 "detached.iterator",  null);
        case BACKWARD:
        case INITIAL:
            state = FORWARD;
            return referenceNode;
        case FORWARD:
        }

        for (;;) {
            unfilteredNextNode();
            if (referenceNode == null) {
                return null;
            }
            if ((whatToShow & (1 << referenceNode.getNodeType() - 1)) != 0) {
                if (filter == null ||
                    filter.acceptNode(referenceNode) == NodeFilter.FILTER_ACCEPT) {
                    return referenceNode;
                }
            }
        }
    }
    
    /**
     * <b>DOM</b>: Implements {@link NodeIterator#previousNode()}.
     */
    public Node previousNode() {
        switch (state) {
        case INVALID:
            throw document.createDOMException
                (DOMException.INVALID_STATE_ERR,
                 "detached.iterator",  null);
        case FORWARD:
        case INITIAL:
            state = BACKWARD;
            return referenceNode;
        case BACKWARD:
        }

        for (;;) {
            unfilteredPreviousNode();
            if (referenceNode == null) {
                return referenceNode;
            }
            if ((whatToShow & (1 << referenceNode.getNodeType() - 1)) != 0) {
                if (filter == null ||
                    filter.acceptNode(referenceNode) == NodeFilter.FILTER_ACCEPT) {
                    return referenceNode;
                }
            }
        }
    }

    /**
     * <b>DOM</b>: Implements {@link NodeIterator#detach()}.
     */
    public void detach() {
        state = INVALID;
        document.detachNodeIterator(this);
    }

    /**
     * Called by the DOM when a node will be removed from the current document.
     */
    public void nodeToBeRemoved(Node removedNode) {
        if (state == INVALID) {
            return;
        }

        Node node;
        for (node = referenceNode;
             node != null && node != root;
             node = node.getParentNode()) {
            if (node == removedNode) {
                break;
            }
        }
        if (node == null || node == root) {
            return;
        }

        if (state == BACKWARD) {
            // Go to the first child
            if (node.getNodeType() != Node.ENTITY_REFERENCE_NODE ||
                expandEntityReferences) {
                Node n = node.getFirstChild();
                if (n != null) {
                    referenceNode = n;
                    return;
                }
            }

            // Go to the next sibling
            Node n = node.getNextSibling();
            if (n != null) {
                referenceNode = n;
                return;
            }

            // Go to the first sibling of one of the ancestors
            n = node;
            while ((n = n.getParentNode()) != null && n != root) {
                Node t = n.getNextSibling();
                if (t != null) {
                    referenceNode = t;
                    return;
                }
            }

            referenceNode = null;
        } else {
            Node n = node.getPreviousSibling();

            // Go to the parent of a first child
            if (n == null) {
                referenceNode = node.getParentNode();
                return;
            }

            // Go to the last child of child...
            if (n.getNodeType() != Node.ENTITY_REFERENCE_NODE ||
                expandEntityReferences) {
                Node t;
                while ((t = n.getLastChild()) != null) {
                    n = t;
                }
            }

            referenceNode = n;
        }
    }

    /**
     * Sets the reference node to the next node, unfiltered.
     */
    protected void unfilteredNextNode() {
        if (referenceNode == null) {
            return;
        }

        // Go to the first child
        if (referenceNode.getNodeType() != Node.ENTITY_REFERENCE_NODE ||
            expandEntityReferences) {
            Node n = referenceNode.getFirstChild();
            if (n != null) {
                referenceNode = n;
                return;
            }
        }

        // Go to the next sibling
        Node n = referenceNode.getNextSibling();
        if (n != null) {
            referenceNode = n;
            return;
        }

        // Go to the first sibling of one of the ancestors
        n = referenceNode;
        while ((n = n.getParentNode()) != null && n != root) {
            Node t = n.getNextSibling();
            if (t != null) {
                referenceNode = t;
                return;
            }
        }
        referenceNode = null;
    }

    /**
     * Sets the reference node to the previous node, unfiltered.
     */
    protected void unfilteredPreviousNode() {
        if (referenceNode == null) {
            return;
        }

        // The previous of root is null
        if (referenceNode == root) {
            referenceNode = null;
            return;
        }
        
        Node n = referenceNode.getPreviousSibling();

        // Go to the parent of a first child
        if (n == null) {
            referenceNode = referenceNode.getParentNode();
            return;
        }

        // Go to the last child of child...
        if (n.getNodeType() != Node.ENTITY_REFERENCE_NODE ||
            expandEntityReferences) {
            Node t;
            while ((t = n.getLastChild()) != null) {
                n = t;
            }
        }

        referenceNode = n;
    }
}
