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

package org.apache.batik.dom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
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
     * The created document descriptor.
     */
    protected DocumentDescriptor documentDescriptor;

    /**
     * Whether a document descriptor must be generated.
     */
    protected boolean createDocumentDescriptor;

    /**
     * The current node.
     */
    protected Node currentNode;

    /**
     * The locator.
     */
    protected Locator locator;

    /**
     * Whether the parser currently parses a CDATA section.
     */
    protected StringBuffer cdataBuffer;

    /**
     * Whether the parser currently parses a DTD.
     */
    protected boolean inDTD;

    /**
     * Whether the parser is in validating mode.
     */
    protected boolean isValidating;

    /**
     * The stack used to store the namespace URIs.
     */
    protected HashTableStack namespaces;

    /**
     * The error handler.
     */
    protected ErrorHandler errorHandler;

    protected interface PreInfo {
        public Node createNode(Document doc);
    }

    static class ProcessingInstructionInfo implements PreInfo {
        public String target, data;
        public ProcessingInstructionInfo(String target, String data) {
            this.target = target;
            this.data = data;
        }
        public Node createNode(Document doc) { 
            return doc.createProcessingInstruction(target, data);
        }
    }

    static class CommentInfo implements PreInfo {
        public String comment;
        public CommentInfo(String comment) {
            this.comment = comment;
        }
        public Node createNode(Document doc) { 
            return doc.createComment(comment);
        }
    }

    static class CDataInfo implements PreInfo {
        public String cdata;
        public CDataInfo(String cdata) {
            this.cdata = cdata;
        }
        public Node createNode(Document doc) { 
            return doc.createCDATASection(cdata);
        }
    }

    static class TextInfo implements PreInfo {
        public String text;
        public TextInfo(String text) {
            this.text = text;
        }
        public Node createNode(Document doc) { 
            return doc.createTextNode(text);
        }
    }

    /**
     * Various elements encountered prior to real document root element.
     * List of PreInfo objects.
     */
    protected List preInfo;

    /**
     * Creates a new SAXDocumentFactory object.
     * No document descriptor will be created while generating a document.
     * @param impl The DOM implementation to use for building the DOM tree.
     * @param parser The SAX2 parser classname.
     */
    public SAXDocumentFactory(DOMImplementation impl,
                              String parser) {
	implementation           = impl;
	parserClassName          = parser;
    }

    /**
     * Creates a new SAXDocumentFactory object.
     * @param impl The DOM implementation to use for building the DOM tree.
     * @param parser The SAX2 parser classname.
     * @param dd Whether a document descriptor must be generated.
     */
    public SAXDocumentFactory(DOMImplementation impl,
                              String parser,
                              boolean dd) {
	implementation           = impl;
	parserClassName          = parser;
        createDocumentDescriptor = dd;
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
     * @param uri The document URI.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String uri)
        throws IOException {
        return createDocument(new InputSource(uri));
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri, 
				   InputStream is) throws IOException {
        InputSource inp = new InputSource(is);
        inp.setSystemId(uri);
        return createDocument(ns, root, uri, inp);
    }

    /**
     * Creates a Document instance.
     * @param uri The document URI.
     * @param is The document input stream.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String uri, InputStream is) 
        throws IOException {
        InputSource inp = new InputSource(is);
        inp.setSystemId(uri);
        return createDocument(inp);
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri,
                                   Reader r) throws IOException {
        InputSource inp = new InputSource(r);
        inp.setSystemId(uri);
        return createDocument(ns, root, uri, inp);
    }

    /**
     * Creates a Document instance.
     * @param ns The namespace URI of the root element of the document.
     * @param root The name of the root element of the document.
     * @param uri The document URI.
     * @param r an XMLReaderInstance
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String ns, String root, String uri,
                                   XMLReader r) throws IOException {
        r.setContentHandler(this);
        r.setDTDHandler(this);
        r.setEntityResolver(this);
        try {
            r.parse(uri);
        } catch (SAXException e) {
            Exception ex = e.getException();
            if (ex != null && ex instanceof InterruptedIOException) {
                throw (InterruptedIOException) ex;
            }
            throw new IOException(e.getMessage());
        }
        currentNode = null;
        Document ret = document;
        document = null;
        return ret;
    }

    /**
     * Creates a Document instance.
     * @param uri The document URI.
     * @param r The document reader.
     * @exception IOException if an error occured while reading the document.
     */
    public Document createDocument(String uri, Reader r) throws IOException {
        InputSource inp = new InputSource(r);
        inp.setSystemId(uri);
        return createDocument(inp);
    }

    /**
     * Creates a Document.
     * @param ns The namespace URI of the root element.
     * @param root The name of the root element.
     * @param uri The document URI.
     * @param is  The document input source.
     * @exception IOException if an error occured while reading the document.
     */
    protected Document createDocument(String ns, String root, String uri,
                                      InputSource is)
	throws IOException {
        Document ret = createDocument(is);
        Element docElem = ret.getDocumentElement();

        String lname = root;
        String nsURI = ns;
        if (ns == null) {
            int idx = lname.indexOf(':');
            String nsp = (idx == -1 || idx == lname.length()-1)
                ? ""
                : lname.substring(0, idx);
            nsURI = namespaces.get(nsp);
            if (idx != -1 && idx != lname.length()-1) {
                lname = lname.substring(idx+1);
            }
        }


        String docElemNS = docElem.getNamespaceURI();
        if ((docElemNS != nsURI) &&
            ((docElemNS == null) || (!docElemNS.equals(nsURI))))
            throw new IOException
                ("Root element namespace does not match that requested:\n" +
                 "Requested: " + nsURI + "\n" +
                 "Found: " + docElemNS);

        if (docElemNS != null) {
            if (!docElem.getLocalName().equals(lname))
                throw new IOException
                    ("Root element does not match that requested:\n" +
                     "Requested: " + lname + "\n" +
                     "Found: " + docElem.getLocalName());
        } else {
            if (!docElem.getNodeName().equals(lname))
                throw new IOException
                    ("Root element does not match that requested:\n" +
                     "Requested: " + lname + "\n" +
                     "Found: " + docElem.getNodeName());
        }

        return ret;
    }


    /**
     * Creates a Document.
     * @param ns The namespace URI of the root element.
     * @param root The name of the root element.
     * @param uri The document URI.
     * @param is  The document input source.
     * @exception IOException if an error occured while reading the document.
     */
    protected Document createDocument(InputSource is)
	throws IOException {
	try {
            XMLReader parser =
                XMLReaderFactory.createXMLReader(parserClassName);

            parser.setContentHandler(this);
            parser.setDTDHandler(this);
            parser.setEntityResolver(this);
            parser.setErrorHandler((errorHandler == null) ?
                                   this : errorHandler);

            parser.setFeature("http://xml.org/sax/features/namespaces", 
			      true);
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes",
                              true);
	    parser.setFeature("http://xml.org/sax/features/validation",
			      isValidating);
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

        currentNode = null;
        Document ret = document;
        document = null;
	return ret;
    }

    /**
     * Returns the document descriptor associated with the latest created
     * document.
     * @return null if no document or descriptor was previously generated.
     */
    public DocumentDescriptor getDocumentDescriptor() {
        return documentDescriptor;
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#setDocumentLocator(Locator)}.
     */
    public void setDocumentLocator(Locator l) {
        locator = l;
    }

    /**
     * Sets whether or not the XML parser will validate the XML document
     * depending on the specified parameter.
     *
     * @param isValidating indicates that the XML parser will validate the XML
     * document 
     */
    public void setValidating(boolean isValidating) {
	this.isValidating = isValidating;
    }

    /**
     * Returns true if the XML parser validates the XML stream, false
     * otherwise.
     */
    public boolean isValidating() {
	return isValidating;
    }

    /**
     * Sets a custom error handler.
     */
    public void setErrorHandler(ErrorHandler eh) {
        errorHandler = eh;
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#startDocument()}.
     */
    public void startDocument() throws SAXException {
        preInfo    = new LinkedList();
        namespaces = new HashTableStack();
	namespaces.put("xml", XMLSupport.XML_NAMESPACE_URI);
	namespaces.put("xmlns", XMLSupport.XMLNS_NAMESPACE_URI);
	namespaces.put("", null);

        cdataBuffer = null;
        inDTD = false;
        currentNode = null;

        if (createDocumentDescriptor) {
            documentDescriptor = new DocumentDescriptor();
        } else {
            documentDescriptor = null;
        }
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
                String ns = attributes.getValue(i);
                if (ns.length() == 0) {
                    ns = null;
                }
		namespaces.put("", ns);
	    } else {
		if (aname.startsWith("xmlns:")) {
                    String ns = attributes.getValue(i);
                    if (ns.length() == 0) {
                        ns = null;
                    }
		    int idx = aname.indexOf(':');
		    namespaces.put(aname.substring(idx + 1), ns);
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
        if (currentNode == null) {
            document = implementation.createDocument(nsURI, rawName, null);
            Iterator i = preInfo.iterator();
            currentNode = e = document.getDocumentElement();
            while (i.hasNext()) {
                PreInfo pi = (PreInfo)i.next();
                Node n = pi.createNode(document);
                document.insertBefore(n, e);
            }
            preInfo = null;
        } else {
            e = document.createElementNS(nsURI, rawName);
            currentNode.appendChild(e);
            currentNode = e;
        }

        // Storage of the line number.
        if (createDocumentDescriptor && locator != null) {
            documentDescriptor.setLocationLine(e, locator.getLineNumber());
        }

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
	    }
	}
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ErrorHandler#fatalError(SAXParseException)}.
     */
    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ErrorHandler#error(SAXParseException)}.
     */
    public void error(SAXParseException ex) throws SAXException {
	throw ex;
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ErrorHandler#warning(SAXParseException)}.
     */
    public void warning(SAXParseException ex) throws SAXException {
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#endElement(String,String,String)}.
     */
    public void endElement(String uri, String localName, String rawName)
	throws SAXException {
        if (currentNode != null)
            currentNode = currentNode.getParentNode();
	namespaces.pop();
    }
    
    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#characters(char[],int,int)}.
     */
    public void characters(char ch[], int start, int length)
        throws SAXException {
        if (cdataBuffer != null) 
            cdataBuffer.append(ch, start, length);
        else {
            String data = new String(ch, start, length);
            if (currentNode == null) {
                preInfo.add(new TextInfo(data));
            } else {
                currentNode.appendChild(document.createTextNode(data));
            }
        }
    }
    
    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ContentHandler#processingInstruction(String,String)}.
     */
    public void processingInstruction(String target, String data)
        throws SAXException {
	if (!inDTD) {
            if (currentNode == null)
                preInfo.add(new ProcessingInstructionInfo(target, data));
            else
                currentNode.appendChild
                    (document.createProcessingInstruction(target, data));
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
        cdataBuffer = new StringBuffer();
    }

    /**
     * <b>SAX</b>: Implements {@link
     * org.xml.sax.ext.LexicalHandler#endCDATA()}.
     */
    public void endCDATA() throws SAXException {
        String data = cdataBuffer.toString();
        if (currentNode == null) {
            preInfo.add(new CDataInfo(data));
        } else {
            currentNode.appendChild(document.createCDATASection(data));
        }
        cdataBuffer = null;
    }

    /**
     * <b>SAX</b>: Implements
     * {@link org.xml.sax.ext.LexicalHandler#comment(char[],int,int)}.
     */
    public void comment(char ch[], int start, int length) throws SAXException {
	if (!inDTD) {
            String str = new String(ch, start, length);
            if (currentNode == null) {
                preInfo.add(new CommentInfo(str));
            } else {
                currentNode.appendChild
                    (document.createComment(str));
            }
	}
    }
}
