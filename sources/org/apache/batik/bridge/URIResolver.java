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
        } else if (n.getNodeType() == Node.DOCUMENT_NODE) {
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
        // System.err.println("baseURI: " + baseURI);
        // System.err.println("URI: " + uri);
        if ((baseURI == null) &&
            (uri.startsWith("#")))
            return document.getElementById(uri.substring(1));

        ParsedURL purl = new ParsedURL(baseURI, uri);
        // System.err.println("PURL: " + purl);

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

        String purlStr = purl.toString();
        if (frag != null) {
            purlStr = purlStr.substring(0, purlStr.length()-(frag.length()+1));
        }

        Document doc = documentLoader.loadDocument(purlStr);
        if (frag != null)
            return doc.getElementById(frag);
        return doc;
    }
}
