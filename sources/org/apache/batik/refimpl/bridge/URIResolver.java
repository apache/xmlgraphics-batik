/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.dom.svg.SVGOMDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.w3c.dom.svg.SVGDocument;

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
    protected SVGDocument document;

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
        document = doc;
        documentURI = doc.getURL();
        documentLoader = dl;
    }

    /**
     * Returns the node referenced by the given URI.
     * @return The document or the element 
     */
    public Node getNode(String uri) throws MalformedURLException,
                                           SAXException {
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
