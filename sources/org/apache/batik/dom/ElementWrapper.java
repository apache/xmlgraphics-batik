/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class implements a wrapper for an Element. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ElementWrapper extends NodeWrapper implements Element {
    
    /**
     * Creates a new ElementWrapper object.
     */
    public ElementWrapper(DocumentWrapper dw, Element e) {
        super(dw, e);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Element#getTagName()}.
     */
    public String getTagName() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = ((Element)node).getTagName();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Element#hasAttribute(String)}.
     */
    public boolean hasAttribute(final String name) {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = ((Element)node).hasAttribute(name);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Element#getAttribute(String)}.
     */
    public String getAttribute(final String name) {
        class Query implements Runnable {
            String result;
            public void run() {
                result = ((Element)node).getAttribute(name);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#setAttribute(String,String)}.
     */
    public void setAttribute(final String name, final String value)
        throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((Element)node).setAttribute(name, value);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#removeAttribute(String)}.
     */
    public void removeAttribute(final String name) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((Element)node).removeAttribute(name);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getAttributeNode(String)}.
     */
    public Attr getAttributeNode(final String name) {
        class Query implements Runnable {
            Attr result;
            public void run() {
                result = ((Element)node).getAttributeNode(name);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createAttrWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#setAttributeNode(Attr)}.
     */
    public Attr setAttributeNode(final Attr newAttr) throws DOMException {
        class Query implements Runnable {
            Attr result;
            DOMException exception;
            public void run() {
                try {
                    result = ((Element)node).setAttributeNode
                        ((Attr)((NodeWrapper)newAttr).node);
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
        return createAttrWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#removeAttributeNode(Attr)}.
     */
    public Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
        class Query implements Runnable {
            Attr result;
            DOMException exception;
            public void run() {
                try {
                    result = ((Element)node).removeAttributeNode
                        ((Attr)((NodeWrapper)oldAttr).node);
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
        return createAttrWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getElementsByTagName(String)}.
     */
    public NodeList getElementsByTagName(final String name) {
        class Query implements Runnable {
            NodeList result;
            public void run() {
                result = ((Element)node).getElementsByTagName(name);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#hasAttributeNS(String,String)}.
     */
    public boolean hasAttributeNS(final String namespaceURI, final String localName) {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = ((Element)node).hasAttributeNS(namespaceURI,
                                                        localName);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getAttributeNS(String,String)}.
     */
    public String getAttributeNS(final String namespaceURI, final String localName) {
        class Query implements Runnable {
            String result;
            public void run() {
                result = ((Element)node).getAttributeNS(namespaceURI,
                                                        localName);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#setAttributeNS(String,String,String)}.
     */
    public void setAttributeNS(final String namespaceURI, 
                               final String qualifiedName, 
                               final String value) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((Element)node).setAttributeNS(namespaceURI,
                                                   qualifiedName,
                                                   value);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#removeAttributeNS(String,String)}.
     */
    public void removeAttributeNS(final String namespaceURI, 
                                  final String localName) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((Element)node).removeAttributeNS(namespaceURI,
                                                      localName);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getAttributeNodeNS(String,String)}.
     */
    public Attr getAttributeNodeNS(final String namespaceURI, 
                                   final String localName) {
        class Query implements Runnable {
            Attr result;
            public void run() {
                result = ((Element)node).getAttributeNodeNS(namespaceURI,
                                                            localName);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createAttrWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#setAttributeNodeNS(Attr)}.
     */
    public Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
        class Query implements Runnable {
            Attr result;
            DOMException exception;
            public void run() {
                try {
                    result = ((Element)node).setAttributeNodeNS
                        ((Attr)((NodeWrapper)newAttr).node);
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
        return createAttrWrapper(q.result);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Element#getElementsByTagNameNS(String,String)}.
     */
    public NodeList getElementsByTagNameNS(final String namespaceURI,
                                           final String localName) {
        class Query implements Runnable {
            NodeList result;
            public void run() {
                result = ((Element)node).getElementsByTagNameNS(namespaceURI,
                                                                localName);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.result == null) {
            return null;
        }
        return createNodeListWrapper(q.result);
    }

}
