/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

import org.apache.batik.bridge.DocumentLoader;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * A buffered document loader. This loader caches documents depending
 * on the total number of nodes of each document. If the total number
 * of cached nodes is greater than <tt>MAX_CACHED_NODE_COUNT</tt>, the
 * oldest document is removed from the cache.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class BufferedDocumentLoader implements DocumentLoader {

    /**
     * The maximum number of cachable nodes.
     */
    public static final int MAX_CACHED_NODE_COUNT = 800;

    /**
     * The enclosed document loader used to load the document when needed.
     */
    protected DocumentLoader documentLoader;

    /**
     * The map that contains the Document indexed by the URI.
     */
    protected HashMap documentMap = new HashMap();

    /**
     * A list of the <tt>DocumentState</tt>. Each time a document is
     * loaded, the first item becomes this document.
     */
    protected List documentList = new LinkedList();

    /**
     * The current number of cached nodes.
     */
    protected int currentCachedNodeCount = 0;

    /**
     * Constructs a new DocumentLoader using the specified document
     * loader to load a document.
     * @param documentLoader the document loader used to load a document
     */
    public BufferedDocumentLoader(DocumentLoader documentLoader) {
        this.documentLoader = documentLoader;
    }

    /**
     * Returns the document associated to the specified URI. First
     * checks in the cache if the document has not already been
     * loaded. If not, use the enclosed document loader to physically
     * load it and update the cache.
     *
     * @param uri the uri of the document to return
     * @exception InterruptedException is thrown if this thread is interrupted.
     */
    public Document loadDocument(String uri) throws DOMException, SAXException,
                                       InterruptedException {
        int n = uri.indexOf('#');
        if (n != -1) {
            uri = uri.substring(0, n);
        }
        Document document = (Document) documentMap.get(uri);
        if (document != null) {
            DocumentState state = getDocumentState(document);
            documentList.remove(state);
            documentList.add(0, state);
        } else {
            // load the document
            document = documentLoader.loadDocument(uri);
            // update the cache
            int num = getNodeCount(document.getDocumentElement());
            //System.out.println("*** CACHE *** load "+uri+" ["+num+"]");
            while ((currentCachedNodeCount + num) > MAX_CACHED_NODE_COUNT &&
                    documentList.size() > 0) {
                // remove the oldest document loaded
                int i = documentList.size()-1;
                DocumentState state = (DocumentState) documentList.get(i);
                documentList.remove(i);
                documentMap.remove(state.uri);
                currentCachedNodeCount -= state.nodeCount;
                //System.out.println("*** CACHE *** remove "+state.uri+" ["+state.nodeCount+"]");
            }
            currentCachedNodeCount += num;
            //System.out.println("*** CACHE *** total cached nodes : "+currentCachedNodeCount+"/"+MAX_CACHED_NODE_COUNT);
            // add the new loaded document to the cache
            DocumentState state = new DocumentState(uri, document, num);
            documentList.add(0, state);
            documentMap.put(uri, document);
        }
        return document;
    }

    public void dispose() {
        //System.out.println("*** CACHE *** dispose");
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
        public String uri;
        public Document document;
        public int nodeCount;

        public DocumentState(String uri, Document document, int nodeCount) {
            this.uri = uri;
            this.document = document;
            this.nodeCount = nodeCount;
        }
    }
}
