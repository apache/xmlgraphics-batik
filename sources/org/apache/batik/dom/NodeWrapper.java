/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import java.util.HashMap;
import java.util.Map;

import org.apache.batik.dom.events.EventListenerWrapper;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.events.EventWrapper;

import org.apache.batik.dom.util.HashTable;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * This class implements a wrapper for a Document. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class NodeWrapper
    implements Node,
               EventTarget {
    
    /**
     * The owner document wrapper.
     */
    protected DocumentWrapper documentWrapper;

    /**
     * The wrapped node.
     */
    protected Node node;

    /**
     * The capturing listeners table.
     */
    protected HashTable capturingListeners;

    /**
     * The bubbling listeners table.
     */
    protected HashTable bubblingListeners;

    /**
     * Creates a new NodeWrapper.
     */
    public NodeWrapper(DocumentWrapper dw, Node n) {
        documentWrapper = dw;
        node = n;
    }

    /**
     * Returns the wrapped node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Called when the object is collectable.
     */
    protected void finalize() throws Throwable {
        documentWrapper.nodeWrapperFinalized(this);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     */
    public String getNodeName() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = node.getNodeName();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     */
    public short getNodeType() {
        // Assumes that getNodeType() is thread safe.
        return node.getNodeType();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeValue()}.
     */
    public String getNodeValue() throws DOMException {
        class Query implements Runnable {
            String result;
            DOMException exception;
            public void run() {
                try {
                    result = node.getNodeValue();
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#setNodeValue(String)}.
     */
    public void setNodeValue(final String nodeValue) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    node.setNodeValue(nodeValue);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Request r = new Request();
        invokeAndWait(r);
        if (r.exception != null) {
            throw r.exception;
        }
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getParentNode()}.
     */
    public Node getParentNode() {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = node.getParentNode();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getChildNodes()}.
     */
    public NodeList getChildNodes() {
        class Query implements Runnable {
            NodeList result;
            public void run() {
                result = node.getChildNodes();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.result == null) {
            return null;
        }
        return createNodeListWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getFirstChild()}.
     */
    public Node getFirstChild() {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = node.getFirstChild();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLastChild()}.
     */
    public Node getLastChild() {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = node.getLastChild();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getPreviousSibling()}.
     */
    public Node getPreviousSibling() {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = node.getPreviousSibling();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNextSibling()}.
     */
    public Node getNextSibling() {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = node.getNextSibling();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#hasAttributes()}.
     */
    public boolean hasAttributes() {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = node.hasAttributes();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getAttributes()}.
     */
    public NamedNodeMap getAttributes() {
        class Query implements Runnable {
            NamedNodeMap result;
            public void run() {
                result = node.getAttributes();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.result == null) {
            return null;
        }
        return createNamedNodeMapWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getOwnerDocument()}.
     */
    public Document getOwnerDocument() {
        return documentWrapper;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNamespaceURI()}.
     */
    public String getNamespaceURI() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = node.getNamespaceURI();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Node#insertBefore(Node, Node)}.
     */
    public Node insertBefore(final Node newChild,
                             final Node refChild) throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = node.insertBefore(((NodeWrapper)newChild).node,
                                               ((NodeWrapper)refChild).node);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Node#replaceChild(Node, Node)}.
     */
    public Node replaceChild(final Node newChild,
                             final Node oldChild) throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = node.replaceChild(((NodeWrapper)newChild).node,
                                               ((NodeWrapper)oldChild).node);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#removeChild(Node)}.
     */
    public Node removeChild(final Node oldChild) throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = node.removeChild(((NodeWrapper)oldChild).node);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#appendChild(Node)}.
     */
    public Node appendChild(final Node newChild) throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = node.appendChild(((NodeWrapper)newChild).node);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#hasChildNodes()}.
     */
    public boolean hasChildNodes() {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = node.hasChildNodes();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#cloneNode(boolean)}.
     */
    public Node cloneNode(final boolean deep) {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = node.cloneNode(deep);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#normalize()}.
     */
    public void normalize() {
        invokeLater(new Runnable() {
                public void run() {
                    node.normalize();
                }
            });
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Node#isSupported(String,String)}.
     */
    public boolean isSupported(final String feature, final String version) {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = node.isSupported(feature, version);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getPrefix()}.
     */
    public String getPrefix() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = node.getPrefix();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#setPrefix(String)}.
     */
    public void setPrefix(final String prefix) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    node.setPrefix(prefix);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Request r = new Request();
        invokeAndWait(r);
        if (r.exception != null) {
            throw r.exception;
        }
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = node.getLocalName();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    // EventTarget /////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * EventTarget#addEventListener(String,EventListener,boolean)}.
     */
    public void addEventListener(final String type, 
                                 EventListener listener, 
                                 final boolean useCapture) {
        final EventListener l = new EventListenerWrapper(documentWrapper, listener);

	HashTable listeners;
	if (useCapture) {
	    if (capturingListeners == null) {
		capturingListeners = new HashTable(3);
	    }
	    listeners = capturingListeners;
	} else {
	    if (bubblingListeners == null) {
		bubblingListeners = new HashTable(3);
	    }
	    listeners = bubblingListeners;
	}
        HashTable ht = (HashTable)listeners.get(type);
        if (ht == null) {
            ht = new HashTable(3);
            listeners.put(type, ht);
        }
        ht.put(listeners, l);

        class Request implements Runnable {
            public void run() {
                ((EventTarget)node).addEventListener(type, l, useCapture);
            }
        }
        invokeLater(new Request());
    }

    /**
     * <b>DOM</b>: Implements {@link
     * EventTarget#removeEventListener(String,EventListener,boolean)}.
     */
    public void removeEventListener(final String type,
                                    EventListener listener,
                                    final boolean useCapture) {
	HashTable listeners = (useCapture) ? capturingListeners : bubblingListeners;
        if (listeners == null) {
            return;
        }
        HashTable ht = (HashTable)listeners.get(type);
        if (ht == null) {
            return;
        }
        
        final EventListener l = (EventListener)ht.remove(listeners);
        if (l == null) {
            return;
        }

        class Request implements Runnable {
            public void run() {
                ((EventTarget)node).removeEventListener(type, l, useCapture);
            }
        }
        invokeLater(new Request());
    }

    /**
     * <b>DOM</b>: Implements {@link EventTarget#dispatchEvent(Event)}.
     */
    public boolean dispatchEvent(final Event evt) throws EventException {
        class Query implements Runnable {
            boolean result;
            EventException exception;
            public void run() {
                try {
                    result = ((EventTarget)node).dispatchEvent
                        (((EventWrapper)evt).getEvent());
                } catch (EventException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return q.result;
    }

    /**
     * Invokes the given Runnable from the associated RunnableQueue
     * thread.
     */
    protected void invokeAndWait(Runnable r) {
        documentWrapper.invokeAndWait(r);
    }

    /**
     * Invokes the given Runnable from the associated RunnableQueue
     * thread.
     */
    protected void invokeLater(Runnable r) {
        documentWrapper.invokeLater(r);
    }

    /**
     * Creates a wrapper for the given node.
     */
    protected Node createNodeWrapper(Node n) {
        return documentWrapper.createNodeWrapper(n);
    }

    /**
     * Creates a wrapper for the given Attr.
     */
    protected Attr createAttrWrapper(Attr n) {
        return documentWrapper.createAttrWrapper(n);
    }

    /**
     * Creates a wrapper for the given Text.
     */
    protected Text createTextWrapper(Text t) {
        return documentWrapper.createTextWrapper(t);
    }

    /**
     * Creates a wrapper for the given Element.
     */
    protected Element createElementWrapper(Element n) {
        return documentWrapper.createElementWrapper(n);
    }

    /**
     * Creates a wrapper for the given node list.
     */
    protected NodeList createNodeListWrapper(NodeList nl) {
        return documentWrapper.createNodeListWrapper(nl);
    }

    /**
     * Creates a wrapper for the given node map.
     */
    protected NamedNodeMap createNamedNodeMapWrapper(NamedNodeMap nm) {
        return documentWrapper.createNamedNodeMapWrapper(nm);
    }
}
