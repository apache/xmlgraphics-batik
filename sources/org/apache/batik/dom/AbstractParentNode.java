/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.MutationEvent;

/**
 * This class implements the Node interface with support for children.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public abstract class AbstractParentNode extends AbstractNode {

    /**
     * The children.
     */
    protected ChildNodes childNodes;

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getChildNodes()}.
     * @return {@link #childNodes}
     */
    public NodeList getChildNodes() {
	return (childNodes == null)
            ? childNodes = new ChildNodes()
            : childNodes;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getFirstChild()}.
     * @return {@link #childNodes}.firstChild
     */
    public Node getFirstChild() {
	return (childNodes == null) ? null : childNodes.firstChild;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLastChild()}.
     * @return {@link #childNodes}.lastChild
     */
    public Node getLastChild() {
	return (childNodes == null) ? null : childNodes.lastChild;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Node#insertBefore(Node, Node)}.
     */
    public Node insertBefore(Node newChild, Node refChild)
        throws DOMException {
	if (childNodes == null) {
	    throw createDOMException
		(DOMException.NOT_FOUND_ERR,
		 "child.missing",
		 new Object[] { new Integer(refChild.getNodeType()),
				refChild.getNodeName() });
	}
	checkAndRemove(newChild);

	if (newChild.getNodeType() == DOCUMENT_FRAGMENT_NODE) {
	    Node n = newChild.getFirstChild();
	    while (n != null) {
		insertBefore(n, refChild);
		n = n.getNextSibling();
	    }
	    return newChild;
	} else {
	    // Node modification
	    ExtendedNode n = childNodes.insert((ExtendedNode)newChild,
					       (ExtendedNode)refChild);
	    n.setParentNode(this);

	    // Mutation event
	    fireDOMNodeInsertedEvent(n);
	    fireDOMSubtreeModifiedEvent();
	    return n;
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Node#replaceChild(Node, Node)}.
     */
    public Node replaceChild(Node newChild, Node oldChild)
        throws DOMException {
	if (childNodes == null) {
	    throw createDOMException
		(DOMException.NOT_FOUND_ERR,
		 "child.missing",
		 new Object[] { new Integer(oldChild.getNodeType()),
				oldChild.getNodeName() });
	}
	checkAndRemove(newChild);

	if (newChild.getNodeType() == DOCUMENT_FRAGMENT_NODE) {
	    Node n  = newChild.getLastChild();
	    Node ps = oldChild.getNextSibling();
	    if (n != null) {
		replaceChild(n, oldChild);
		n = n.getPreviousSibling();
	    }
	    while (n != null) {
		insertBefore(n, ps);
		n = n.getPreviousSibling();
	    }
	    return newChild;
	} else {
	    // Mutation event
	    fireDOMNodeRemovedEvent(oldChild);

	    // Node modification
	    ExtendedNode n = (ExtendedNode)newChild;
	    ExtendedNode o = childNodes.replace(n, (ExtendedNode)oldChild);
	    n.setParentNode(this);
	    o.setParentNode(null);

	    // Mutation event
	    fireDOMNodeInsertedEvent(n);
	    fireDOMSubtreeModifiedEvent();
	    return n;
	}
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#removeChild(Node)}.
     */
    public Node removeChild(Node oldChild) throws DOMException {
	if (childNodes == null || oldChild.getParentNode() != this) {
	    throw createDOMException
		(DOMException.NOT_FOUND_ERR,
		 "child.missing",
		 new Object[] { new Integer(oldChild.getNodeType()),
				oldChild.getNodeName() });
	}
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}

	// Mutation event
	fireDOMNodeRemovedEvent(oldChild);

	// Node modification
	ExtendedNode result = childNodes.remove((ExtendedNode)oldChild);
	result.setParentNode(null);

	// Mutation event
	fireDOMSubtreeModifiedEvent();
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#appendChild(Node)}.
     */
    public Node appendChild(Node newChild) throws DOMException {
	checkAndRemove(newChild);

	if (newChild.getNodeType() == DOCUMENT_FRAGMENT_NODE) {
	    Node n = newChild.getFirstChild();
	    while (n != null) {
		appendChild(n);
		n = n.getNextSibling();
	    }
	    return newChild;
	} else {
	    if (childNodes == null) {
		childNodes = new ChildNodes();
	    }
	    // Node modification
	    ExtendedNode n = childNodes.append((ExtendedNode)newChild);
	    n.setParentNode(this);

	    // Mutation event
	    fireDOMNodeInsertedEvent(n);
	    fireDOMSubtreeModifiedEvent();
	    return n;
	}
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#hasChildNodes()}.
     * @return true if this node has children, false otherwise.
     */
    public boolean hasChildNodes() {
	return childNodes != null && childNodes.getLength() != 0;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#normalize()}.
     */
    public void normalize() {
	Node p = getFirstChild();
	if (p != null) {
	    p.normalize();
	    Node n = p.getNextSibling();
	    while (n != null) {
		if (p.getNodeType() == TEXT_NODE &&
                    n.getNodeType() == TEXT_NODE) {
		    String s = p.getNodeValue() + n.getNodeValue();
		    AbstractText at = (AbstractText)p;
		    at.setNodeValue(s);
		    removeChild(n);
		    n = p.getNextSibling();
		} else {
		    n.normalize();
		    p = n;
		    n = n.getNextSibling();
		}
	    }
	}
    }

    /**
     * Recursively fires a DOMNodeInsertedIntoDocument event.
     */
    public void fireDOMNodeInsertedIntoDocumentEvent() {
	AbstractDocument doc = getCurrentDocument();
	if (doc.getEventsEnabled()) {
	    super.fireDOMNodeInsertedIntoDocumentEvent();
	    for (Node n = getFirstChild(); n != null; n = n.getNextSibling()) {
		((AbstractNode)n).fireDOMNodeInsertedIntoDocumentEvent();
	    }
	}
    }

    /**
     * Recursively fires a DOMNodeRemovedFromDocument event.
     */
    public void fireDOMNodeRemovedFromDocumentEvent() {
	AbstractDocument doc = getCurrentDocument();
	if (doc.getEventsEnabled()) {
	    super.fireDOMNodeRemovedFromDocumentEvent();
	    for (Node n = getFirstChild(); n != null; n = n.getNextSibling()) {
		((AbstractNode)n).fireDOMNodeRemovedFromDocumentEvent();
	    }
	}
    }

    /**
     * Deeply exports this node to the given document.
     */
    protected Node deepExport(Node n, AbstractDocument d) {
	super.deepExport(n, d);
	for (Node p = getFirstChild(); p != null; p = p.getNextSibling()) {
	    Node t = ((AbstractNode)p).deepExport(p.cloneNode(false), d);
	    n.appendChild(t);
	}
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	for (Node p = getFirstChild(); p != null; p = p.getNextSibling()) {
	    Node t = p.cloneNode(true);
	    n.appendChild(t);
	}
	return n;
    }

    /**
     * Fires a DOMSubtreeModified event.
     */
    protected void fireDOMSubtreeModifiedEvent() {
	AbstractDocument doc = getCurrentDocument();
	if (doc.getEventsEnabled()) {
	    DocumentEvent de = (DocumentEvent)doc;
	    MutationEvent ev = (MutationEvent)de.createEvent("MutationEvents");
	    ev.initMutationEvent("DOMSubtreeModified",
				 true,   // canBubbleArg
				 false,  // cancelableArg
				 null,   // relatedNodeArg
				 null,   // prevValueArg
				 null,   // newValueArg
				 null,   // attrNameArg
                                 ev.MODIFICATION);
	    dispatchEvent(ev);
	}
    }

    /**
     * Fires a DOMNodeInserted event.
     */
    protected void fireDOMNodeInsertedEvent(Node node) {
	AbstractDocument doc = getCurrentDocument();
	if (doc.getEventsEnabled()) {
	    DocumentEvent de = (DocumentEvent)doc;
	    MutationEvent ev = (MutationEvent)de.createEvent("MutationEvents");
	    ev.initMutationEvent("DOMNodeInserted",
				 true,   // canBubbleArg
				 false,  // cancelableArg
				 this,   // relatedNodeArg
				 null,   // prevValueArg
				 null,   // newValueArg
				 null,   // attrNameArg
                                 ev.ADDITION);
	    AbstractNode n = (AbstractNode)node;
	    n.dispatchEvent(ev);
	    n.fireDOMNodeInsertedIntoDocumentEvent();
	}
    }

    /**
     * Fires a DOMNodeRemoved event.
     */
    protected void fireDOMNodeRemovedEvent(Node node) {
	AbstractDocument doc = getCurrentDocument();
	if (doc.getEventsEnabled()) {
	    DocumentEvent de = (DocumentEvent)doc;
	    MutationEvent ev = (MutationEvent)de.createEvent("MutationEvents");
	    ev.initMutationEvent("DOMNodeRemoved",
				 true,   // canBubbleArg
				 false,  // cancelableArg
				 this,   // relatedNodeArg
				 null,   // prevValueArg
				 null,   // newValueArg
				 null,   // attrNameArg
                                 ev.REMOVAL);
	    AbstractNode n = (AbstractNode)node;
	    n.dispatchEvent(ev);
	    n.fireDOMNodeRemovedFromDocumentEvent();
	}
    }

    /**
     * Checks the validity of a node to be inserted, and removes it from
     * the document if needed.
     */
    protected void checkAndRemove(Node n) {
	checkChildType(n);
	if (isReadonly()) {
	    throw createDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
				     "readonly.node",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}
	if (n.getOwnerDocument() != getCurrentDocument()) {
	    throw createDOMException(DOMException.WRONG_DOCUMENT_ERR,
				     "node.from.wrong.document",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName() });
	}
	for (Node pn = getParentNode(); pn != null; pn = pn.getParentNode()) {
	    if (pn == n) {
		throw createDOMException
                    (DOMException.WRONG_DOCUMENT_ERR,
                     "add.ancestor",
                     new Object[] { new Integer(getNodeType()),
                                    getNodeName() });
	    }
	}

	// Remove the node from the tree
	Node np = n.getParentNode();
	if (np != null) {
	    np.removeChild(n);
	}
    }

    /**
     * To manage a list of nodes.
     */
    protected static class Nodes implements NodeList {
	/**
	 * The table.
	 */
	protected Node[] table;

	/**
	 * The number of nodes.
	 */
	protected int size;

	/**
	 * Creates a new Nodes object.
	 */
	public Nodes() {
	}

	/**
	 * <b>DOM</b>: Implements {@link org.w3c.dom.NodeList#item(int)}.
	 */
	public Node item(int index) {
	    if (table == null || index < 0 || index > size) {
		return null;
	    }
	    return table[index];
	}

	/**
	 * <b>DOM</b>: Implements {@link org.w3c.dom.NodeList#getLength()}.
	 * @return {@link #size}.
	 */
	public int getLength() {
	    return size;
	}

	/**
	 * Appends a node to the list.
	 */
	public void append(Node n) {
	    if (table == null) {
		table = new Node[11];
	    } else if (size == table.length - 1) {
		Node[] t = new Node[table.length * 2 + 1];
		for (int i = 0; i < size; i++) {
		    t[i] = table[i];
		}
		table = t;
	    }
	    table[size++] = n;
	}
    }

    /**
     * To manage the children of this node.
     */
    protected class ChildNodes implements NodeList, Serializable {
	/**
	 * The first child.
	 */
	protected transient ExtendedNode firstChild;
	
	/**
	 * The last child.
	 */
	protected transient ExtendedNode lastChild;
	
	/**
	 * The number of children.
	 */
	protected int children;

	/**
	 * Creates a new ChildNodes object.
	 */
	public ChildNodes() {
	}

	/**
	 * <b>DOM</b>: Implements {@link org.w3c.dom.NodeList#item(int)}.
	 */
	public Node item(int index) {
	    if (index < 0 || index >= children) {
		return null;
	    }
	    if (index < (children >> 1)) {
		Node n = firstChild;
		for (int i = 0; i < index; i++) {
		    n = n.getNextSibling();
		}
		return n;
	    } else {
		Node n = lastChild;
		for (int i = children - 1; i > index; i--) {
		    n = n.getPreviousSibling();
		}
		return n;
	    }
	}

	/**
	 * <b>DOM</b>: Implements {@link org.w3c.dom.NodeList#getLength()}.
	 * @return {@link #children}.
	 */
	public int getLength() {
	    return children;
	}

	/**
	 * Appends a node to the tree.
	 * The node is assumed not to be a DocumentFragment instance.
	 */
	public ExtendedNode append(ExtendedNode n) {
	    if (lastChild == null) {
		firstChild = n;
		lastChild  = n;
		children++;
		return n;
	    }
	    lastChild.setNextSibling(n);
	    n.setPreviousSibling(lastChild);
	    lastChild = n;
	    children++;
	    return n;
	}

	/**
	 * Inserts a node in the tree.
	 */
	public ExtendedNode insert(ExtendedNode n, ExtendedNode r) {
	    if (r == null) {
		return append(n);
	    }

	    if (r == firstChild) {
		firstChild.setPreviousSibling(n);
		n.setNextSibling(firstChild);
		firstChild = n;
		children++;
		return n;
	    }

	    ExtendedNode o = firstChild;
	    while (o != null) {
		if (o == r) {
		    ExtendedNode ps = (ExtendedNode)r.getPreviousSibling();
		    ps.setNextSibling(n);
		    r.setPreviousSibling(n);
		    n.setNextSibling(r);
		    n.setPreviousSibling(ps);
		    children++;
		    return n;
		}
		o = (ExtendedNode)o.getNextSibling();
	    }
	    throw createDOMException
		(DOMException.NOT_FOUND_ERR,
		 "child.missing",
		 new Object[] { new Integer(o.getNodeType()),
				o.getNodeName() });
	}

	/**
	 * Replaces a node in the tree by an other.
	 */
	public ExtendedNode replace(ExtendedNode n, ExtendedNode o) {
	    if (o == firstChild) {
		n.setNextSibling(firstChild.getNextSibling());
		firstChild.setNextSibling(null);
		firstChild = n;
		return o;
	    }

	    if (o == lastChild) {
		n.setPreviousSibling(lastChild.getPreviousSibling());
		lastChild.setPreviousSibling(null);
		lastChild = n;
		return o;
	    }

	    ExtendedNode cn = firstChild;
	    while (cn != null) {
		if (cn == o) {
		    n.setPreviousSibling(o.getPreviousSibling());
		    n.setNextSibling(o.getNextSibling());
		    o.setPreviousSibling(null);
		    o.setNextSibling(null);
		    return o;
		}
		cn = (ExtendedNode)cn.getNextSibling();
	    }
	    throw createDOMException
		(DOMException.NOT_FOUND_ERR,
		 "child.missing",
		 new Object[] { new Integer(o.getNodeType()),
				o.getNodeName() });
	}

        /**
	 * Removes the given node from the tree.
	 */
        public ExtendedNode remove(ExtendedNode n) {
	    if (n == firstChild) {
		if (n == lastChild) {
		    firstChild = null;
		    lastChild  = null;
		    children--;
		    return n;
		}
		firstChild = (ExtendedNode)firstChild.getNextSibling();
		firstChild.setPreviousSibling(null);
		n.setNextSibling(null);
		children--;
		return n;
	    }

	    if (n == lastChild) {
		lastChild = (ExtendedNode)lastChild.getPreviousSibling();
		lastChild.setNextSibling(null);
		n.setPreviousSibling(null);
		children--;
		return n;
	    }

	    ExtendedNode o = firstChild;
	    while (o != null) {
		if (o == n) {
		    ExtendedNode ps = (ExtendedNode)n.getPreviousSibling();
		    ExtendedNode ns = (ExtendedNode)n.getNextSibling();
		    ps.setNextSibling(ns);
		    ns.setPreviousSibling(ps);
		    n.setPreviousSibling(null);
		    n.setNextSibling(null);
		    children--;
		    return n;
		}
		o = (ExtendedNode)o.getNextSibling();
	    }
	    throw createDOMException
		(DOMException.NOT_FOUND_ERR,
		 "child.missing",
		 new Object[] { new Integer(n.getNodeType()),
				n.getNodeName() });
	}

        // Serialization ///////////////////////////////////////////////////

        /**
         * Writes the object to the given stream.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
            
            for (ExtendedNode en = firstChild;
                 en != null;
                 en = (ExtendedNode)en.getNextSibling()) {
                s.writeObject(en);
            }
        }

        /**
         * Reads the object from the given stream.
         */
        private void readObject(ObjectInputStream s) 
            throws IOException, ClassNotFoundException {
            s.defaultReadObject();

            ExtendedNode prev = null;
            Document doc = null;
            if (children > 0) {
                prev = (ExtendedNode)s.readObject();
                prev.setParentNode(AbstractParentNode.this);
                doc = AbstractParentNode.this.getOwnerDocument();
                prev.setOwnerDocument(doc);
            }
            firstChild = prev;
            for (int i = children - 2; i >= 0; i--) {
                ExtendedNode en = (ExtendedNode)s.readObject();
                en.setParentNode(AbstractParentNode.this);
                en.setPreviousSibling(prev);
                en.setOwnerDocument(doc);
                prev.setNextSibling(en);
                prev = en;
            } 
            lastChild = prev;
        }
    }
}
