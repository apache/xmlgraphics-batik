/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.CSSOMReadOnlyValue;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

import org.xml.sax.SAXException;

/**
 * This class is used to resolve the URI that can be found in a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class URIResolver {
    /**
     * The reference document.
     */
    protected SVGOMDocument document;

    /**
     * The document URI.
     */
    protected String documentURI;

    /**
     * The document loader.
     */
    protected DocumentLoader documentLoader;

    /**
     * Creates a new URI resolver object.
     * @param doc The reference document.
     * @param dl The document loader.
     */
    public URIResolver(SVGDocument doc, DocumentLoader dl) {
        document = (SVGOMDocument)doc;
        documentURI = doc.getURL();
        documentLoader = dl;
    }

    /**
     * Imports the element referenced by the given URI.
     * @param uri The element URI.
     */
    public Element getElement(String uri)
        throws MalformedURLException, SAXException, IOException {
        Node n = getNode(uri);
        if (n.getNodeType() == n.DOCUMENT_NODE) {
            throw new Error("Documents not allowed");
        }
        return (Element)n;
    }

    /**
     * Returns the node referenced by the given URI.
     * @return The document or the element
     */
    public Node getNode(String uri) throws MalformedURLException,
                              SAXException, IOException {
        if (documentURI.equals(uri)) {
            return document;
        }
        if (uri.startsWith(documentURI) &&
                uri.length() > documentURI.length() + 1 &&
                uri.charAt(documentURI.length()) == '#') {
            uri = uri.substring(documentURI.length());
        }
        if (uri.startsWith("#")) {
            return document.getElementById(uri.substring(1));
        }

        URL url = new URL(((SVGOMDocument)document).getURLObject(), uri);
        Document doc = documentLoader.loadDocument(url.toString());

        String ref = url.getRef();
        if (url.getRef() == null) {
            return doc;
        } else {
            return doc.getElementById(ref);
        }
    }
}
