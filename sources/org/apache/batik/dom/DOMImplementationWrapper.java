/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.apache.batik.dom.events.EventWrapper;

import org.apache.batik.util.RunnableQueue;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import org.w3c.dom.events.Event;

/**
 * This class implements a wrapper for a DOMImplementation. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DOMImplementationWrapper implements DOMImplementation {
    
    /**
     * The RunnableQueue which runs the method calls.
     */
    protected RunnableQueue runnableQueue;

    /**
     * The wrapped DOMImplementation object.
     */
    protected DOMImplementation domImplementation;

    /**
     * Creates a new DOMImplementationWrapper associated with the given
     * RunnableQueue thread.
     */
    public DOMImplementationWrapper(RunnableQueue rq, DOMImplementation di) {
        runnableQueue = rq;
        domImplementation = di;
    }

    /**
     * <b>DOM</b>: Implements
     * {@link org.w3c.dom.DOMImplementation#hasFeature(String,String)}.
     */
    public boolean hasFeature(final String feature, final String version) {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = domImplementation.hasFeature(feature, version);
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }
    
    /**
     * <b>DOM</b>: Implements
     * {@link org.w3c.dom.DOMImplementation#createDocumentType(String,String,String)}.
     */
    public DocumentType createDocumentType(String qualifiedName, String publicId, 
                                           String systemId) throws DOMException {
        throw new RuntimeException("!!! Not Implemented");
    }

    /**
     * <b>DOM</b>: Implements
     * {@link org.w3c.dom.DOMImplementation#createDocument(String,String,DocumentType)}.
     */
    public Document createDocument(final String nsURI, final String qName, 
                                   final DocumentType doctype) throws DOMException {
        class Query implements Runnable {
            Document result;
            DOMException exception;
            public void run() {
                try {
                    result = domImplementation.createDocument(nsURI, qName, doctype);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return new DocumentWrapper(DOMImplementationWrapper.this, q.result);
    }

    /**
     * Creates an EventWrapper object.
     */
    public Event createEventWrapper(DocumentWrapper dw, Event ev)
        throws DOMException {
        return new EventWrapper(dw, ev);
    }

    /**
     * Invokes the given Runnable from the associated RunnableQueue
     * thread.
     */
    protected void invokeAndWait(Runnable r) {
        if (runnableQueue.getThread() == Thread.currentThread()) {
            r.run();
        } else {
            try {
                runnableQueue.invokeAndWait(r);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * Invokes the given Runnable from the associated RunnableQueue
     * thread.
     */
    protected void invokeLater(Runnable r) {
        if (runnableQueue.getThread() == Thread.currentThread()) {
            r.run();
        } else {
            runnableQueue.invokeLater(r);
        }
    }
}
