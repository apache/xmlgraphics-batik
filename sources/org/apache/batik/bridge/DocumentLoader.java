/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.io.IOException;
import java.util.HashMap;

import org.apache.batik.css.engine.CSSEngine;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.dom.util.DocumentDescriptor;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import org.xml.sax.SAXException;

/**
 * This class is responsible on loading an SVG document and
 * maintaining a cache.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DocumentLoader {

    /**
     * The document factory used to create the document according a
     * DOM implementation.
     */
    protected SVGDocumentFactory documentFactory;

    /**
     * The map that contains the Document indexed by the URI.
     *
     * WARNING: tagged private as no element of this Map should be
     * referenced outise of this class
     */
    protected HashMap cacheMap = new HashMap();

    /**
     * The user agent.
     */
    protected UserAgent userAgent;

    /**
     * The bridge context.
     */
    protected BridgeContext bridgeContext;

    /**
     * Constructs a new <tt>DocumentLoader</tt>.
     */
    protected DocumentLoader() { }

    /**
     * Constructs a new <tt>DocumentLoader</tt> with the specified XML parser.
     * @param userAgent the user agent to use
     */
    public DocumentLoader(UserAgent userAgent) {
        this.userAgent = userAgent;
        documentFactory = new SAXSVGDocumentFactory
            (userAgent.getXMLParserClassName(), true);
	documentFactory.setValidating(userAgent.isXMLParserValidating());
    }

    /**
     * Returns a document from the specified uri.
     * @param uri the uri of the document
     * @exception IOException if an I/O error occured while loading
     * the document
     */
    public Document loadDocument(String uri) throws IOException {
        int n = uri.indexOf('#');
        if (n != -1) {
            uri = uri.substring(0, n);
        }
        DocumentState state = (DocumentState)cacheMap.get(uri);
        if (state == null) {
            Document document = documentFactory.createDocument(uri);
            if (bridgeContext != null) {
                bridgeContext.initializeDocument(document);
            }

            DocumentDescriptor desc = documentFactory.getDocumentDescriptor();
            state = new DocumentState(uri, document, desc);
            cacheMap.put(uri, state);
        }
        return state.document;
    }

    /**
     * Sets the bridge context.
     */
    public void setBridgeContext(BridgeContext bc) {
        bridgeContext = bc;
    }

    /**
     * Disposes and releases all resources allocated by this document loader.
     */
    public void dispose() {
        //System.out.println("purge the cache");
        cacheMap.clear();
    }

    /**
     * Returns the line in the source code of the specified element or
     * -1 if not found.
     *
     * @param e the element
     * @return -1 the document has been removed from the cache or has not
     * been loaded by this document loader.
     */
    public int getLineNumber(Element e) {
        String uri = ((SVGDocument)e.getOwnerDocument()).getURL();
        DocumentState state = (DocumentState)cacheMap.get(uri);
        if (state == null) {
            return -1;
        } else {
            return state.desc.getLocationLine(e);
        }
    }

    /**
     * A simple class that contains a Document and its number of nodes.
     */
    private static class DocumentState {

        private String uri;
        private Document document;
        private DocumentDescriptor desc;

        public DocumentState(String uri,
                             Document document,
                             DocumentDescriptor desc) {
            this.uri = uri;
            this.document = document;
            this.desc = desc;
        }

        public DocumentDescriptor getDocumentDescriptor() {
            return desc;
        }

        public String getURI() {
            return uri;
        }

        public Document getDocument() {
            return document;
        }
    }
}
