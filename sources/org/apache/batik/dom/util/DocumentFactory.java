/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

import java.io.IOException;
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
public class DocumentFactory
    extends    DefaultHandler
    implements LexicalHandler {

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
    protected HashTableStack namespaces = new HashTableStack();
    {
	namespaces.put("xml", XMLSupport.XML_NAMESPACE_URI);
	namespaces.put("xmlns", XMLSupport.XMLNS_NAMESPACE_URI);
	namespaces.put("", null);
    }

    /**
     * Creates a new DocumentFactory object.
     * @param impl The DOM implementation to use for building the DOM tree.
     * @param parser The SAX2 parser classname.
     */
    public DocumentFactory(DOMImplementation impl, String parser) {
	implementation = impl;
	parserClassName = parser;
    }

    /**
     * Creates a GenericDocument.
     * @param ns The namespace URI of the root element.
     * @param root The name of the root element.
     * @param uri The document URI.
     * @param is  The document input source.
     * @exception DOMException if an error occured when building the document.
     * @exception SAXException if an error occured when reading the document.
     * @exception InterruptedException if the current thread is interrupted.
     */
    public Document createDocument(String ns, String root, String uri,
                                   InputSource is)
	throws DOMException, SAXException, IOException {
	documentElementParsed = false;
	document = implementation.createDocument(ns, root, null);

	XMLReader parser = XMLReaderFactory.createXMLReader(parserClassName);
	parser.setContentHandler(this);
	parser.setDTDHandler(this);
	parser.setEntityResolver(this);
	parser.setErrorHandler(this);

	parser.setFeature("http://xml.org/sax/features/namespaces", false);
	parser.setFeature("http://xml.org/sax/features/namespace-prefixes",
                          true);

	try {
	    parser.setProperty("http://xml.org/sax/properties/lexical-handler",
			       this);
	} catch (SAXException e) {
	}

        parser.parse(is);
	
	return document;
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#startDocument()}.
     */
    public void startDocument() throws SAXException {
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
