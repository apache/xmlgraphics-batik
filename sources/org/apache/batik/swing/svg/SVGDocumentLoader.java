/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.awt.EventQueue;

import java.io.InterruptedIOException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.DocumentLoader;

import org.w3c.dom.svg.SVGDocument;

import org.xml.sax.SAXException;

/**
 * This class represents an object which loads asynchroneaously a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGDocumentLoader extends Thread {

    /**
     * The URL of the document,
     */
    protected String url;

    /**
     * The document loader.
     */
    protected DocumentLoader loader;

    /**
     * The exception thrown.
     */
    protected Exception exception;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * Creates a new SVGDocumentLoader.
     * @param u The URL of the document.
     * @param l The document loader to use
     */
    public SVGDocumentLoader(String u, DocumentLoader l) {
        url = u;
        loader = l;
    }

    /**
     * Runs this loader.
     */
    public void run() {
        try {
            fireStartedEvent();
            SVGDocument svgDocument = (SVGDocument)loader.loadDocument(url);
            fireCompletedEvent(svgDocument);
        } catch (InterruptedIOException e) {
            fireCancelledEvent();
        } catch (Exception e) {
            exception = e;
            fireFailedEvent();
        }
        loader.dispose();
    }

    /**
     * Returns the exception, if any occured.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Adds a SVGDocumentLoaderListener to this SVGDocumentLoader.
     */
    public void addSVGDocumentLoaderListener(SVGDocumentLoaderListener l) {
        listeners.add(l);
    }

    /**
     * Removes a SVGDocumentLoaderListener from this SVGDocumentLoader.
     */
    public void removeSVGDocumentLoaderListener(SVGDocumentLoaderListener l) {
        listeners.remove(l);
    }

    /**
     * Fires a SVGDocumentLoaderEvent.
     */
    protected void fireStartedEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final SVGDocumentLoaderEvent ev = new SVGDocumentLoaderEvent(this, null);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    SVGDocumentLoaderListener dl = (SVGDocumentLoaderListener)dll[i];
                    dl.documentLoadingStarted(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            SVGDocumentLoaderListener dl =
                                (SVGDocumentLoaderListener)dll[i];
                            dl.documentLoadingStarted(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a SVGDocumentLoaderEvent.
     */
    protected void fireCompletedEvent(SVGDocument doc) {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final SVGDocumentLoaderEvent ev = new SVGDocumentLoaderEvent(this, doc);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    SVGDocumentLoaderListener dl = (SVGDocumentLoaderListener)dll[i];
                    dl.documentLoadingCompleted(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            SVGDocumentLoaderListener dl =
                                (SVGDocumentLoaderListener)dll[i];
                            dl.documentLoadingCompleted(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a SVGDocumentLoaderEvent.
     */
    protected void fireFailedEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final SVGDocumentLoaderEvent ev = new SVGDocumentLoaderEvent(this, null);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    SVGDocumentLoaderListener dl = (SVGDocumentLoaderListener)dll[i];
                    dl.documentLoadingFailed(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            SVGDocumentLoaderListener dl =
                                (SVGDocumentLoaderListener)dll[i];
                            dl.documentLoadingFailed(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a SVGDocumentLoaderEvent.
     */
    protected void fireCancelledEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final SVGDocumentLoaderEvent ev = new SVGDocumentLoaderEvent(this, null);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    SVGDocumentLoaderListener dl = (SVGDocumentLoaderListener)dll[i];
                    dl.documentLoadingCancelled(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            SVGDocumentLoaderListener dl =
                                (SVGDocumentLoaderListener)dll[i];
                            dl.documentLoadingCancelled(ev);
                        }
                    }
                });
            }
        }
    }
}
