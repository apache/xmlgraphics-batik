/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.io.InterruptedIOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.util.EventDispatcher;
import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.w3c.dom.svg.SVGDocument;

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
     * Boolean indicating if this thread has ever been interrupted.
     */
    protected boolean beenInterrupted;

    /**
     * Creates a new SVGDocumentLoader.
     * @param u The URL of the document.
     * @param l The document loader to use
     */
    public SVGDocumentLoader(String u, DocumentLoader l) {
        url = u;
        loader = l;
        beenInterrupted = false;
    }

    public boolean getBeenInterrupted() {
        synchronized (this) { return beenInterrupted; }
    }

    /**
     * Runs this loader.
     */
    public void run() {
        SVGDocumentLoaderEvent evt;
        evt = new SVGDocumentLoaderEvent(this, null);
        try {
            fireEvent(startedDispatcher, evt);
            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, evt);
                return;
            }

            SVGDocument svgDocument = (SVGDocument)loader.loadDocument(url);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, evt);
                return;
            }

            evt = new SVGDocumentLoaderEvent(this, svgDocument);
            fireEvent(completedDispatcher, evt);
        } catch (InterruptedIOException e) {
            fireEvent(cancelledDispatcher, evt);
        } catch (Exception e) {
            exception = e;
            fireEvent(failedDispatcher, evt);
        } catch (Throwable t) {
            t.printStackTrace();
            exception = new Exception(t.getMessage());
            fireEvent(failedDispatcher, evt);
        }
    }

    public void interrupt() {
        super.interrupt();
        synchronized (this) {
            beenInterrupted = true;
        }
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

    public void fireEvent(Dispatcher dispatcher, Object event) {
        EventDispatcher.fireEvent(dispatcher, listeners, event, true);
    }

    static Dispatcher startedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGDocumentLoaderListener)listener).documentLoadingStarted
                    ((SVGDocumentLoaderEvent)event);
            }
        };
            
            static Dispatcher completedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGDocumentLoaderListener)listener).documentLoadingCompleted
                 ((SVGDocumentLoaderEvent)event);
            }
        };

    static Dispatcher cancelledDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGDocumentLoaderListener)listener).documentLoadingCancelled
                 ((SVGDocumentLoaderEvent)event);
            }
        };

    static Dispatcher failedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGDocumentLoaderListener)listener).documentLoadingFailed
                 ((SVGDocumentLoaderEvent)event);
            }
        };
}
