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

import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.util.DocumentDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

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

    public Document checkCache(String uri) {
        int n = uri.lastIndexOf('/');
        if (n == -1) 
            n = 0;
        n = uri.indexOf('#', n);
        if (n != -1) {
            uri = uri.substring(0, n);
        }
        DocumentState state = (DocumentState)cacheMap.get(uri);
        if (state != null)
            return state.document;
        return null;
    }

    /**
     * Returns a document from the specified uri.
     * @param uri the uri of the document
     * @exception IOException if an I/O error occured while loading
     * the document
     */
    public Document loadDocument(String uri) throws IOException {
        Document ret = checkCache(uri);
        if (ret != null)
            return ret;

        SVGDocument document = documentFactory.createSVGDocument(uri);

        DocumentDescriptor desc = documentFactory.getDocumentDescriptor();
        DocumentState state = new DocumentState(uri, document, desc);
        cacheMap.put(uri, state);

        return state.document;
    }

    /**
     * Returns a document from the specified uri.
     * @param uri the uri of the document
     * @exception IOException if an I/O error occured while loading
     * the document
     */
    public Document loadDocument(String uri, InputStream is) 
        throws IOException {
        Document ret = checkCache(uri);
        if (ret != null)
            return ret;

        SVGDocument document = documentFactory.createSVGDocument(uri, is);

        DocumentDescriptor desc = documentFactory.getDocumentDescriptor();
        DocumentState state = new DocumentState(uri, document, desc);
        cacheMap.put(uri, state);

        return state.document;
    }

    /**
     * Returns the userAgent used by this DocumentLoader
     */
    public UserAgent getUserAgent(){
        return userAgent;
    }

    /**
     * Disposes and releases all resources allocated by this document loader.
     */
    public void dispose() {
        // new Exception("purge the cache").printStackTrace();
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
