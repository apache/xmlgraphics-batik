/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XMLBaseSupport;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
        documentLoader = dl;
    }

    /**
     * Imports the Element referenced by the given URI on Element
     * <tt>ref</tt>.
     * @param uri The element URI.
     * @param ref The Element in the DOM tree to evaluate <tt>uri</tt>
     *            from.  
     * @return The referenced element or null if element can't be found.
     */
    public Element getElement(String uri, Element ref)
        throws MalformedURLException, IOException {

        Node n = getNode(uri, ref);
        if (n == null) {
            return null;
        } else if (n.getNodeType() == n.DOCUMENT_NODE) {
            throw new IllegalArgumentException();
        } else {
            return (Element)n;
        }
    }

    /**
     * Imports the Node referenced by the given URI on Element
     * <tt>ref</tt>.
     * @param uri The element URI.
     * @param ref The Element in the DOM tree to evaluate <tt>uri</tt>
     *            from. 
     * @return The referenced Node/Document or null if element can't be found.
     */
    public Node getNode(String uri, Element ref)
        throws MalformedURLException, IOException, SecurityException {

        String baseURI = XMLBaseSupport.getCascadedXMLBase(ref);
        if ((baseURI == null) &&
            (uri.startsWith("#")))
            return document.getElementById(uri.substring(1));

        ParsedURL purl = new ParsedURL(baseURI, uri);

        if (documentURI == null)
            documentURI = document.getURL();

        String    frag  = purl.getRef();
        if ((frag != null) && (documentURI != null)) {
            ParsedURL pDocURL = new ParsedURL(documentURI);
            // System.out.println("doc: " + pDocURL);
            // System.out.println("Purl: " + purl);
            if (pDocURL.sameFile(purl)) {
                // System.out.println("match");
                return document.getElementById(frag);
            }
        }

        // uri is not a reference into this document, so load the 
        // document it does reference after doing a security 
        // check with the UserAgent
        ParsedURL pDocURL = null;
        if (documentURI != null) {
            pDocURL = new ParsedURL(documentURI);
        }

        UserAgent userAgent = documentLoader.getUserAgent();
        userAgent.checkLoadExternalResource(purl, pDocURL);
        
        Document doc = documentLoader.loadDocument(purl.toString());
        if (frag != null)
            return doc.getElementById(frag);
        return doc;
    }
}
