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

package org.apache.batik.gvt;

import java.util.List;

/**
 * <tt>GVTTreeWalker</tt> objects are used to navigate a GVT tree or subtree.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class GVTTreeWalker {

    /** The GVT root into which text is searched. */
    protected GraphicsNode gvtRoot;

    /** The current GraphicsNode. */
    protected GraphicsNode currentNode;

    /**
     * Constructs a new <tt>GVTTreeWalker</tt>.
     *
     * @param gvtRoot the graphics node root
     */
    public GVTTreeWalker(GraphicsNode gvtRoot) {
        this.gvtRoot = gvtRoot;
        currentNode = gvtRoot;
    }

    /**
     * Returns the root graphics node.
     */
    public GraphicsNode getRoot() {
        return gvtRoot;
    }

    /**
     * Sets the current GraphicsNode to the specified node.
     *
     * @param node the new current graphics node
     * @exception IllegalArgumentException if the node is not part of the GVT Tree
     *                                     this walker is dedicated to
     */
    public void setCurrentGraphicsNode(GraphicsNode node) {
        if (node.getRoot() != gvtRoot) {
            throw new IllegalArgumentException
                ("The node "+node+" is not part of the document "+gvtRoot);
        }
        currentNode = node;
    }

    /**
     * Returns the current <tt>GraphicsNode</tt>.
     */
    public GraphicsNode getCurrentGraphicsNode() {
        return currentNode;
    }

    /**
     * Returns the previous <tt>GraphicsNode</tt>. If the current graphics node
     * does not have a previous node, returns null and retains the current node.
     */
    public GraphicsNode previousGraphicsNode() {
        GraphicsNode result = getPreviousGraphicsNode(currentNode);
        if (result != null) {
            currentNode = result;
        }
        return result;
    }

    /**
     * Returns the next <tt>GraphicsNode</tt>. If the current graphics node does
     * not have a next node, returns null and retains the current node.
     */
    public GraphicsNode nextGraphicsNode() {
        GraphicsNode result = getNextGraphicsNode(currentNode);
        if (result != null) {
            currentNode = result;
        }
        return result;
    }

    /**
     * Returns the parent of the current <tt>GraphicsNode</tt>. If the current
     * graphics node has no parent, returns null and retains the current node.
     */
    public GraphicsNode parentGraphicsNode() {
        GraphicsNode result = currentNode.getParent();
        if (result != null) {
            currentNode = result;
        }
        return result;
    }

    /**
     * Returns the next sibling of the current <tt>GraphicsNode</tt>. If the
     * current graphics node does not have a next sibling, returns null and
     * retains the current node.
     */
    public GraphicsNode getNextSibling() {
        GraphicsNode result = getNextSibling(currentNode);
        if (result != null) {
            currentNode = result;
        }
        return result;
    }

    /**
     * Returns the next previous of the current <tt>GraphicsNode</tt>. If the
     * current graphics node does not have a previous sibling, returns null and
     * retains the current node.
     */
    public GraphicsNode getPreviousSibling() {
        GraphicsNode result = getPreviousSibling(currentNode);
        if (result != null) {
            currentNode = result;
        }
        return result;
    }

    /**
     * Returns the first child of the current <tt>GraphicsNode</tt>. If the
     * current graphics node does not have a first child, returns null and
     * retains the current node.
     */
    public GraphicsNode firstChild() {
        GraphicsNode result = getFirstChild(currentNode);
        if (result != null) {
            currentNode = result;
        }
        return result;
    }

    /**
     * Returns the last child of the current <tt>GraphicsNode</tt>. If the
     * current graphics node does not have a last child, returns null and
     * retains the current node.
     */
    public GraphicsNode lastChild() {
        GraphicsNode result = getLastChild(currentNode);
        if (result != null) {
            currentNode = result;
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private GraphicsNode getNextGraphicsNode(GraphicsNode node) {
        if (node == null) {
            return null;
        }
        // Go to the first child
        GraphicsNode n = getFirstChild(node);
        if (n != null) {
            return n;
        }

        // Go to the next sibling
        n = getNextSibling(node);
        if (n != null) {
            return n;
        }

        // Go to the first sibling of one of the ancestors
        n = node;
        while ((n = n.getParent()) != null && n != gvtRoot) {
            GraphicsNode t = getNextSibling(n);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    private GraphicsNode getPreviousGraphicsNode(GraphicsNode node) {
        if (node == null) {
            return null;
        }

        // The previous of root is null
        if (node == gvtRoot) {
            return null;
        }

        GraphicsNode n = getPreviousSibling(node);

        // Go to the parent of a first child
        if (n == null) {
            return node.getParent();
        }

        // Go to the last child of child...
        GraphicsNode t;
        while ((t = getLastChild(n)) != null) {
            n = t;
        }
        return n;
    }

    private static GraphicsNode getLastChild(GraphicsNode node) {
        if (!(node instanceof CompositeGraphicsNode)) {
            return null;
        }
        CompositeGraphicsNode parent = (CompositeGraphicsNode)node;
        List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        if (children.size() >= 1) {
            return (GraphicsNode)children.get(children.size()-1);
        } else {
            return null;
        }
    }

    private static GraphicsNode getPreviousSibling(GraphicsNode node) {
        CompositeGraphicsNode parent = node.getParent();
        if (parent == null) {
            return null;
        }
        List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        int index = children.indexOf(node);
        if (index-1 >= 0) {
            return (GraphicsNode)children.get(index-1);
        } else {
            return null;
        }
    }

    private static GraphicsNode getFirstChild(GraphicsNode node) {
        if (!(node instanceof CompositeGraphicsNode)) {
            return null;
        }
        CompositeGraphicsNode parent = (CompositeGraphicsNode)node;
        List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        if (children.size() >= 1) {
            return (GraphicsNode)children.get(0);
        } else {
            return null;
        }
    }

    private static GraphicsNode getNextSibling(GraphicsNode node) {
        CompositeGraphicsNode parent = node.getParent();
        if (parent == null) {
            return null;
        }
        List children = parent.getChildren();
        if (children == null) {
            return null;
        }
        int index = children.indexOf(node);
        if (index+1 < children.size()) {
            return (GraphicsNode)children.get(index+1);
        } else {
            return null;
        }
    }
}
