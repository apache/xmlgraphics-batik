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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class is responsible on loading an SVG document.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DocumentLoader {

    /**
     * The default size of the cache in terms of number of nodes.
     */
    public static final int DEFAULT_MAX_CACHED_NODE_COUNT = 2000;

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
    private HashMap documentMap = new HashMap();

    /**
     * A list of the <tt>DocumentState</tt>. Each time a document is
     * loaded, the first item becomes this document.
     *
     * WARNING: tagged private as no element of this List should be
     * referenced outside of this class.
     */
    private List documentList = new LinkedList();

    /**
     * The current number of cached nodes.
     */
    private int currentCachedNodeCount = 0;

    /**
     * The size of the cache.
     */
    protected int size;

    /**
     * Constructs a new <tt>DocumentLoader</tt>.
     */
    protected DocumentLoader() { }

    /**
     * Constructs a new <tt>DocumentLoader</tt> with the specified XML parser.
     * @param parser The SAX2 parser classname.
     */
    public DocumentLoader(String parser) {
        this(parser, DEFAULT_MAX_CACHED_NODE_COUNT);
    }

    /**
     * Constructs a new <tt>DocumentLoader</tt> with the specified XML parser.
     * @param parser The SAX2 parser classname.
     * @param size the size of the cache
     */
    public DocumentLoader(String parser, int size) {
        this.documentFactory = new SAXSVGDocumentFactory(parser);
        this.size = size;
    }

    /**
     * Returns a document from the specified uri.
     * @param uri the uri of the document
     * @exception IOException if an I/O error occured while loading the document
     */
    public Document loadDocument(String uri) throws IOException {
        int n = uri.indexOf('#');
        if (n != -1) {
            uri = uri.substring(0, n);
        }
        Document document = (Document) documentMap.get(uri);
        if (document != null) {
            //System.out.println("reusing: "+uri);
            DocumentState state = getDocumentState(document);
            documentList.remove(state);
            documentList.add(0, state);
        } else {
            //System.out.println("loading: "+uri);
            // load the document
            document = documentFactory.createDocument(uri);
            // update the cache
            int num = getNodeCount(document.getDocumentElement());
            while ((currentCachedNodeCount + num) > size &&
                    documentList.size() > 0) {
                // remove the oldest document loaded
                int i = documentList.size()-1;
                DocumentState state = (DocumentState) documentList.get(i);
                documentList.remove(i);
                documentMap.remove(state.uri);
                currentCachedNodeCount -= state.nodeCount;
            }
            currentCachedNodeCount += num;
            // add the new loaded document to the cache
            DocumentState state = new DocumentState(uri, document, num);
            documentList.add(0, state);
            documentMap.put(uri, document);
        }
        return document;
    }

    /**
     * Disposes and releases all resources allocated by this document loader.
     */
    public void dispose() {
        //System.out.println("disposing...");
        documentMap.clear();
        documentList.clear();
    }

    /**
     * Returns the <tt>DocumentState</tt> of the specified Document.
     * @param document the document
     */
    protected DocumentState getDocumentState(Document document) {
        for (Iterator i = documentList.iterator(); i.hasNext();) {
            DocumentState state = (DocumentState) i.next();
            if (state.document == document) {
                return state;
            }
        }
        return null;
    }

    /**
     * Returns the number of nodes in the specified document.
     */
    protected int getNodeCount(Node n) {
        int num = 1;
        for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling()) {
            num += getNodeCount(c);
        }
        return num;
    }

    /**
     * A simple class that contains a Document and its number of nodes.
     */
    private static class DocumentState {

        private String uri;
        private Document document;
        private int nodeCount;

        public DocumentState(String uri, Document document, int nodeCount) {
            this.uri = uri;
            this.document = document;
            this.nodeCount = nodeCount;
        }

        public String getURI() {
            return uri;
        }

        public Document getDocument() {
            return document;
        }

        public int getNodeCount() {
            return nodeCount;
        }
    }
}
