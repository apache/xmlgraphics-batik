/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;

/**
 * This class implements the {@link org.w3c.dom.Document} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractDocument
    extends    AbstractParentNode
    implements Document,
               DocumentEvent,
               Localizable {
    /**
     * The error messages bundle class name.
     */
    protected final static String RESOURCES =
        "org.apache.batik.dom.resources.Messages";

    /**
     * The localizable support for the error messages.
     */
    protected LocalizableSupport localizableSupport =
        new LocalizableSupport(RESOURCES);

    /**
     * The DOM implementation.
     */
    protected DOMImplementation implementation;

    /**
     * Whether the event dispatching must be done.
     */
    protected boolean eventsEnabled;

    /**
     * Creates a new document.
     */
    protected AbstractDocument() {
    }

    /**
     * Creates a new document.
     */
    protected AbstractDocument(DOMImplementation impl) {
	implementation = impl;
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#setLocale(Locale)}.
     */
    public void setLocale(Locale l) {
	localizableSupport.setLocale(l);
    }

    /**
     * Implements {@link org.apache.batik.i18n.Localizable#getLocale()}.
     */
    public Locale getLocale() {
        return localizableSupport.getLocale();
    }

    /**
     * Implements {@link
     * org.apache.batik.i18n.Localizable#formatMessage(String,Object[])}.
     */
    public String formatMessage(String key, Object[] args)
        throws MissingResourceException {
        return localizableSupport.formatMessage(key, args);
    }

    /**
     * Tests whether the event dispatching must be done.
     */
    public boolean getEventsEnabled() {
	return eventsEnabled;
    }

    /**
     * Sets the eventsEnabled property.
     */
    public void setEventsEnabled(boolean b) {
	eventsEnabled = b;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return "#document".
     */
    public String getNodeName() {
	return "#document";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#DOCUMENT_NODE}
     */
    public short getNodeType() {
	return DOCUMENT_NODE;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Document#getDoctype()}.
     */
    public DocumentType getDoctype() {
	for (Node n = getFirstChild(); n != null; n = n.getNextSibling()) {
	    if (n.getNodeType() == DOCUMENT_TYPE_NODE) {
		return (DocumentType)n;
	    }
	}
	return null;
    }

    /**
     * Sets the document type node.
     */
    public void setDoctype(DocumentType dt) {
	if (dt != null) {
	    appendChild(dt);
	    ((ExtendedNode)dt).setReadonly(true);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Document#getImplementation()}.
     * @return {@link #implementation}
     */
    public DOMImplementation getImplementation() {
	return implementation;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getDocumentElement()}.
     */
    public Element getDocumentElement() {
	for (Node n = getFirstChild(); n != null; n = n.getNextSibling()) {
	    if (n.getNodeType() == ELEMENT_NODE) {
		return (Element)n;
	    }
	}
	return null;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getElementsByTagName(String)}.
     */
    public NodeList getElementsByTagName(String tagname) {
	Element e = getDocumentElement();
	if (e == null) {
	    return EMPTY_NODE_LIST;
	}
	return e.getElementsByTagName(tagname);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#importNode(Node,boolean)}.
     */
    public Node importNode(Node importedNode, boolean deep)
        throws DOMException {
	AbstractNode an = (AbstractNode)importedNode;
	return (deep)
	    ? an.deepExport(an.cloneNode(false), this)
	    : an.export(an.cloneNode(false), this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.Document#getElementsByTagNameNS(String,String)}.
     */
    public NodeList getElementsByTagNameNS(String namespaceURI,
                                           String localName) {
	Element e = getDocumentElement();
	if (e == null) {
	    return EMPTY_NODE_LIST;
	}
	return e.getElementsByTagNameNS(namespaceURI, localName);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#cloneNode(boolean)}.
     */
    public Node cloneNode(boolean deep) {
	String message = null;
	try {
	    Document n = (Document)getClass().newInstance();
	    if (deep) {
		for (Node c = getFirstChild();
                     c != null;
                     c = c.getNextSibling()) {
		    n.appendChild(n.importNode(c, deep));
		}
	    }
	    return n;
	} catch (IllegalAccessException e) {
	    message = e.getMessage();
	} catch (InstantiationException e) {
	    message = e.getMessage();
	}
	throw createDOMException(DOMException.INVALID_STATE_ERR,
				 "cloning.error",
				 new Object[] { new Integer(getNodeType()),
						getNodeName(),
						message });
    }

    // DocumentEvent /////////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.events.DocumentEvent#createEvent(String)}.
     */
    public Event createEvent(String eventType) throws DOMException {
	return EventSupport.createEvent(eventType);
    }

    /**
     * Returns the current document.
     */
    protected AbstractDocument getCurrentDocument() {
	return this;
    }

    /**
     * Exports this node to the given document.
     * @param n The clone node.
     * @param d The destination document.
     */
    protected Node export(Node n, Document d) {
	throw createDOMException(DOMException.NOT_SUPPORTED_ERR,
				 "import.document",
				 new Object[] {});
    }

    /**
     * Deeply exports this node to the given document.
     * @param n The clone node.
     * @param d The destination document.
     */
    protected Node deepExport(Node n, Document d) {
	throw createDOMException(DOMException.NOT_SUPPORTED_ERR,
				 "import.document",
				 new Object[] {});
    }

    /**
     * Copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node copyInto(Node n) {
	super.copyInto(n);
	AbstractDocument ad = (AbstractDocument)n;
	ad.implementation = implementation;
	return n;
    }

    /**
     * Deeply copy the fields of the current node into the given node.
     * @param n a node of the type of this.
     */
    protected Node deepCopyInto(Node n) {
	super.deepCopyInto(n);
	AbstractDocument ad = (AbstractDocument)n;
	ad.implementation = implementation;
	return n;
    }

    /**
     * Checks the validity of a node to be inserted.
     */
    protected void checkChildType(Node n) {
	short t = n.getNodeType();
	switch (t) {
	case ELEMENT_NODE:
	case PROCESSING_INSTRUCTION_NODE:
	case COMMENT_NODE:
	case DOCUMENT_TYPE_NODE:
	case DOCUMENT_FRAGMENT_NODE:
	    break;
	default:
	    throw createDOMException(DOMException.HIERARCHY_REQUEST_ERR,
				     "child.type",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName(),
		                                    new Integer(t),
						    n.getNodeName() });
	}
	if ((t == ELEMENT_NODE && getDocumentElement() != null) ||
	    (t == DOCUMENT_TYPE_NODE && getDoctype() != null)) {
	    throw createDOMException(DOMException.HIERARCHY_REQUEST_ERR,
				     "child.type",
				     new Object[] { new Integer(getNodeType()),
						    getNodeName(),
		                                    new Integer(t),
						    n.getNodeName() });
	}
    }
}
