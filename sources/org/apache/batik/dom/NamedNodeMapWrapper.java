/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class implements a wrapper for a NamedNodeMap. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class NamedNodeMapWrapper implements NamedNodeMap {
    
    /**
     * The associated document wrapper.
     */
    protected DocumentWrapper documentWrapper;

    /**
     * The wrapped NamedNodeMap.
     */
    protected NamedNodeMap namedNodeMap;

    /**
     * Creates a new NamedNodeMapWrapper.
     */
    public NamedNodeMapWrapper(DocumentWrapper dw, NamedNodeMap nnm) {
        documentWrapper = dw;
        namedNodeMap = nnm;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.NamedNodeMap#getNamedItem(String)}.
     */
    public Node getNamedItem(final String name) {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = namedNodeMap.getNamedItem(name);
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return documentWrapper.createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.NamedNodeMap#setNamedItem(Node)}.
     */
    public Node setNamedItem(final Node arg) throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = namedNodeMap.setNamedItem(((NodeWrapper)arg).node);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return documentWrapper.createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.NamedNodeMap#removeNamedItem(String)}.
     */
    public Node removeNamedItem(final String name) throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = namedNodeMap.removeNamedItem(name);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return documentWrapper.createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.NamedNodeMap#item(int)}.
     */
    public Node item(final int index) {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = namedNodeMap.item(index);
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return documentWrapper.createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.NamedNodeMap#getLength()}.
     */
    public int getLength() {
        class Query implements Runnable {
            int result;
            public void run() {
                result = namedNodeMap.getLength();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.NamedNodeMap#getNamedItemNS(String,String)}.
     */
    public Node getNamedItemNS(final String namespaceURI, final String localName) {
        class Query implements Runnable {
            Node result;
            public void run() {
                result = namedNodeMap.getNamedItemNS(namespaceURI, localName);
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return documentWrapper.createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.NamedNodeMap#setNamedItemNS(Node)}.
     */
    public Node setNamedItemNS(final Node arg) throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = namedNodeMap.setNamedItemNS(((NodeWrapper)arg).node);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return documentWrapper.createNodeWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.NamedNodeMap#removeNamedItemNS(String,String)}.
     */
    public Node removeNamedItemNS(final String namespaceURI, final String localName)
        throws DOMException {
        class Query implements Runnable {
            Node result;
            DOMException exception;
            public void run() {
                try {
                    result = namedNodeMap.removeNamedItemNS(namespaceURI, localName);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return documentWrapper.createNodeWrapper(q.result);
    }
}
