/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.IOException;
import java.io.Reader;

import org.apache.batik.css.ElementWithID;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class contains methods for creating Document instances
 * from an URI using SAX2.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SAXDocumentFactory
    extends    DefaultHandler
    implements LexicalHandler,
               DocumentFactory {

    /**
     * The DOM implementation used to create the document.
     */
    protected DOMImplementation implementation;

    /**
     * The SAX2 parser classname.
     */
    protected String parserClassName;

    /**
     * The created document.
     */
    protected Document document;

    /**
     * The current node.
     */
    protected Node currentNode;

    /**
     * Whether the parser currently parses a CDATA section.
     */
    protected boolean inCDATA;

    /**
     * Whether the parser currently parses a DTD.
     */
    protected boolean inDTD;

    /**
     * Whether the document element has been parsed.
     */
    protected boolean documentElementParsed;

    /**
     * The stack used to store the namespace URIs.
     */
    protected HashTableStack namespaces;

    /**
     * Creates a new SAXDocumentFactory object.
     * @param impl The DOM implementation to use for building the DOM tree.
     * @param parser The SAX2 parser classname.
     */
    public SAXDocumentFactory(DOMImplementation impl, String parser) {
	implementation = impl;
	parserClassName = parser;
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri)
        throws IOException {
        return createDocument(ns, root, uri, new InputSource(uri));
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri, InputStream is)
        throws IOException {
        InputSource inp = new InputSource(is);
        inp.setSystemId(uri);
        return createDocument(ns, root, uri, inp);
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri, Reader r)
        throws IOException {
        InputSource inp = new InputSource(r);
        inp.setSystemId(uri);
        return createDocument(ns, root, uri, inp);
    }

    /**
     * Creates a GenericDocument.
     * @param ns The namespace URI of the root element.
     * @param root The name of the root element.
     * @param uri The document URI.
     * @param is  The document input source.
     * @exception IOException if an error occured while reading the document.
     */
    protected Document createDocument(String ns, String root, String uri,
                                      InputSource is)
	throws IOException {
	document = implementation.createDocument(ns, root, null);

	try {
            XMLReader parser = XMLReaderFactory.createXMLReader(parserClassName);

            parser.setContentHandler(this);
            parser.setDTDHandler(this);
            parser.setEntityResolver(this);
            parser.setErrorHandler(this);

            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes",
                              true);

	    parser.setProperty("http://xml.org/sax/properties/lexical-handler",
			       this);

            parser.parse(is);
	} catch (SAXException e) {
            Exception ex = e.getException();
            if (ex != null && ex instanceof InterruptedIOException) {
                throw (InterruptedIOException)ex;
            }
            throw new IOException(e.getMessage());
	}

	return document;
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#startDocument()}.
     */
    public void startDocument() throws SAXException {
        namespaces = new HashTableStack();
	namespaces.put("xml", XMLSupport.XML_NAMESPACE_URI);
	namespaces.put("xmlns", XMLSupport.XMLNS_NAMESPACE_URI);
	namespaces.put("", null);

	documentElementParsed = false;
        inCDATA = false;
        inDTD = false;
	currentNode = document;
    }

    /**
     * <b>SAX</b>: Implements {@link
     * ContentHandler#startElement(String,String,String,Attributes)}.
     */
    public void startElement(String     uri,
			     String     localName,
			     String     rawName,
			     Attributes attributes) throws SAXException {
        // Check for interruption.
        if (Thread.currentThread().isInterrupted()) {
            throw new SAXException(new InterruptedIOException());
        }

	// Namespaces resolution
	int len = attributes.getLength();
	namespaces.push();
	for (int i = 0; i < len; i++) {
	    String aname = attributes.getQName(i);
	    if (aname.equals("xmlns")) {
		namespaces.put("", attributes.getValue(i));
	    } else {
		if (aname.startsWith("xmlns:")) {
		    int idx = aname.indexOf(':');
		    namespaces.put(aname.substring(idx + 1),
				   attributes.getValue(i));
		}
	    }
	}

	// Element creation
	Element e;
	int idx = rawName.indexOf(':');
	String nsp = (idx == -1 || idx == rawName.length()-1)
	    ? ""
	    : rawName.substring(0, idx);
	String nsURI = namespaces.get(nsp);

	if (currentNode == document) {
	    e = document.getDocumentElement();
	    String lname = rawName;
	    if (idx != -1 && idx != rawName.length()-1) {
		lname = rawName.substring(idx+1);
	    }
	    if (e.getNamespaceURI() != null && nsp.length() != 0) {
		if (!e.getLocalName().equals(lname)) {
		    throw new SAXException("Bad root element");
		}
		e.setPrefix(nsp);
	    }
	    String xmlns;
	    Attr attr = (nsp.equals(""))
		? e.getAttributeNodeNS(XMLSupport.XMLNS_NAMESPACE_URI,
				       xmlns = "xmlns")
		: e.getAttributeNodeNS(XMLSupport.XMLNS_NAMESPACE_URI,
				       xmlns = "xmlns:" + nsp);
	    if (attr != null) {
		namespaces.put(nsp, attr.getValue());
	    }
	    documentElementParsed = true;
	} else {
	    e = document.createElementNS(nsURI, rawName);
	    currentNode.appendChild(e);
	}
	currentNode = e;

	// Attributes creation
	for (int i = 0; i < len; i++) {
	    String aname = attributes.getQName(i);
	    if (aname.equals("xmlns")) {
		e.setAttributeNS(XMLSupport.XMLNS_NAMESPACE_URI,
				 aname,
				 attributes.getValue(i));
	    } else {
		idx = aname.indexOf(':');
		nsURI = (idx == -1)
                    ? null
                    : namespaces.get(aname.substring(0, idx));
		e.setAttributeNS(nsURI, aname, attributes.getValue(i));
		if (attributes.getType(i).equals("ID") &&
		    e instanceof ElementWithID) {
		    String s = (idx == -1 || idx == aname.length()-1)
			? aname
			: aname.substring(idx + 1);
		    ((ElementWithID)e).setIDName(nsURI, s);
		}
	    }
	}
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#endElement(String,String,String)}.
     */
    public void endElement(String uri, String localName, String rawName)
	throws SAXException {
	currentNode = currentNode.getParentNode();
	namespaces.pop();
    }
    
    
    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#characters(char[],int,int)}.
     */
    public void characters(char ch[], int start, int length)
        throws SAXException {
	String data = new String(ch, start, length);
	Node n = (inCDATA)
	    ? document.createCDATASection(data)
	    : document.createTextNode(data);
	currentNode.appendChild(n);
    }
    
    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#processingInstruction(String,String)}.
     */
    public void processingInstruction(String target, String data)
        throws SAXException {
	if (!inDTD) {
	    Node n = document.createProcessingInstruction(target, data);
	    if (currentNode == document && !documentElementParsed) {
		currentNode.insertBefore(n, document.getDocumentElement());
	    } else {
		currentNode.appendChild(n);
	    }
	}
    }

    // LexicalHandler /////////////////////////////////////////////////////////

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ext.LexicalHandler#startDTD(String,String,String)}.
     */
    public void startDTD(String name, String publicId, String systemId)
	throws SAXException {
	inDTD = true;
    }

    /**
     * <b>SAX</b>: Implements {@link org.xml.sax.ext.LexicalHandler#endDTD()}.
     */
    public void endDTD() throws SAXException {
	inDTD = false;
    }

    /**
     * <b>SAX</b>: Implements
     * {@link org.xml.sax.ext.LexicalHandler#startEntity(String)}.
     */
    public void startEntity(String name) throws SAXException {
    }

    /**
     * <b>SAX</b>: Implements
     * {@link org.xml.sax.ext.LexicalHandler#endEntity(String)}.
     */
    public void endEntity(String name) throws SAXException {
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ext.LexicalHandler#startCDATA()}.
     */
    public void startCDATA() throws SAXException {
	inCDATA = true;
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ext.LexicalHandler#endCDATA()}.
     */
    public void endCDATA() throws SAXException {
	inCDATA = false;
    }

    /**
     * <b>SAX</b>: Implements
     * {@link org.xml.sax.ext.LexicalHandler#comment(char[],int,int)}.
     */
    public void comment(char ch[], int start, int length) throws SAXException {
	if (!inDTD) {
	    Node n = document.createComment(new String(ch, start, length));
	    if (currentNode == document && !documentElementParsed) {
		currentNode.insertBefore(n, document.getDocumentElement());
	    } else {
		currentNode.appendChild(n);
	    }
	}
    }
}
