/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.awt.Dimension;
import java.awt.EventQueue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.File;
import java.io.Reader;

import java.net.URL;

import java.lang.reflect.InvocationTargetException;

import java.util.Iterator;
import java.util.Vector;
import java.util.List;

import java.util.zip.GZIPInputStream;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.dom.svg.DefaultSVGContext;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XSLTransformer;

import org.w3c.dom.svg.SVGSVGElement;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class implements a document loading thread "runnable",
 * allowing background loading of documents, and asynchronous notification
 * of DocumentEvents.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public class DocumentLoadRunnable implements Runnable, DocumentEventSource {


    /**
     * A flag indicating whether there is an active loader thread...
     */
    private boolean isRunning = false;

    /**
     * The document URI.
     */
    protected String documentURI;

    /**
     * The SVG document factory to use when creating SVG Documents.
     */
    protected SVGDocumentFactory df;

    /**
     * The DocumentEventListener list.
     * Note: we use Vector (which provide synchronized access)
     * since outside threads may attempt to add listeners!
     */
    protected Vector listeners = new Vector();

    /**
     * Create a new DocumentLoadRunnable for a given URI.
     * @param uri a String representing the document's URI.
     */
    public DocumentLoadRunnable(String uri) {
        documentURI = uri;
    }

    /**
     * Creates a new thread which runs a new DocumentLoader runnable.
     * @param uri a String representing the document's URI.
     * @param df the SVGDocumentFactory for creating the document
     * @param l a DocumentListener which should listen to this loader's events.
     */
    public static Thread createLoaderThread(String uri, DocumentListener l,
                                            SVGDocumentFactory df) {
        DocumentLoadRunnable loader = new DocumentLoadRunnable(uri);
        loader.addDocumentListener(l);
        loader.setDocumentFactory(df);
        Thread t = new Thread(loader);
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    }

    /**
     * Associate a DocumentEventListener with this loader.
     */
    public void addDocumentListener(DocumentListener l) {
        listeners.add(l);
    }

    /**
     * Remove a DocumentEventListener from this loader's listener list.
     */
    public void removeDocumentListener(DocumentListener l) {
        listeners.remove(l);
    }

    /**
     * Associate an SVGDocumentFactory with this loader.
     */
    public synchronized void setDocumentFactory(SVGDocumentFactory df) {
        this.df = df;
    }

    /**
     * Return the associated SVGDocumentFactory for this loader.
     */
    public synchronized SVGDocumentFactory getDocumentFactory() {
        return df;
    }


    /**
     * Fire a document event to all listeners.
     * Note that since java events are processed in the
     * firing thread, not in the AWT event thread, we must
     * wrap the event notification in an "invokeLater" or
     * "invokeAndWait" call.
     * If the delivering thread is already the AWT Event thread the
     * event is delivered directly.
     * @param e the DocumentEvent to be asynchronously delivered.
     * @param wait a boolean indicating whether we should wait for delivery
     */
    public void fireAsyncDocumentEvent(DocumentEvent e, boolean wait) {
        Iterator iter = listeners.iterator();
        DocumentListener listener;

        /* Implementation note:  arguably it would be more efficient to
         * wrap all notifications in one invokeLater() runnable, but
         * this provides better execution granularity.
         */
        while (iter.hasNext()) {
            listener = (DocumentListener)iter.next();
            DocumentEventDispatch dispatchRunnable =
                new DocumentEventDispatch(e, listener, getDocumentFactory());
            if (!EventQueue.isDispatchThread()) {
                if (wait) {
                    try {
                        EventQueue.invokeAndWait(dispatchRunnable);
                    } catch (InterruptedException ie) {
                        ; // ignore if interrupted, don't complete dispatch
                    } catch (InvocationTargetException ite) {
                        ite.printStackTrace();
                    }
                } else {
                    EventQueue.invokeLater(dispatchRunnable);
                }
            } else {
                dispatchRunnable.run(); // execute in current thread
            }
        }
    }

    private class DocumentEventDispatch implements Runnable {
        private DocumentEvent e;
        private DocumentListener l;
        private SVGDocumentFactory df;

        public DocumentEventDispatch(DocumentEvent e,
                                DocumentListener l, SVGDocumentFactory df) {
            this.e = e;
            this.l = l;
            this.df = df;

        }

        public void run() { // this gets run in the AWT thread
            l.processDocumentEvent(e);
        }

    }

    /**
     * The main Runnable method.
     */
    public void run() {

        SVGOMDocument doc = null;
        long t1 = System.currentTimeMillis();

        try {
            fireAsyncDocumentEvent(
                new DocumentLoadingEvent(
                    DocumentLoadingEvent.START_LOADING, null), false);

            // Load requested document.

            checkInterrupt();
            URL url = new URL(documentURI);
            InputStream is = url.openStream();
            checkInterrupt();
            try {
                is = new GZIPInputStream(is);
            } catch (InterruptedIOException iioe) {
                is.close();
                throw new InterruptedException();
            } catch (IOException e) {
                is.close();
                is = url.openStream();
            }

            checkInterrupt();
            try {  // Hack to catch bad things that can happen when parser is interrupted
                doc = df.createDocument(documentURI, is);
            } catch (NoClassDefFoundError e) {
                throw new InterruptedException("Parser interrupted?");
            }
            checkInterrupt();
            List l = XSLTransformer.getStyleSheets(doc.getFirstChild(),
                                                   documentURI);
            if (l.size() > 0) {
                // XSL transformations
                is.close();
                is = url.openStream();
                try {
                    is = new GZIPInputStream(is);
                } catch (InterruptedIOException iioe) {
                    is.close();
                    throw new InterruptedException();
                } catch (IOException e) {
                    is.close();
                    is = url.openStream();
                }
                Reader r = XSLTransformer.transform(new InputStreamReader(is), l);
                doc = df.createDocument(documentURI, r);
            }

            long t2 = System.currentTimeMillis();

            checkInterrupt();
            fireAsyncDocumentEvent(
                new DocumentLoadingEvent(
                        DocumentLoadingEvent.LOADED, doc), true);
/*
            System.out.println("---- Document loading time ---- " +
                                   (t2 - t1) + " ms");
*/
            String title = doc.getTitle();

            checkInterrupt();
            fireAsyncDocumentEvent(new DocumentPropertyEvent(
                DocumentPropertyEvent.TITLE, title), false);

            // Set the panel preferred size.
            SVGSVGElement elt = doc.getRootElement();
            float w, h;

            // <!> FIXME : use UserAgent.getDefaultViewport.
            try {
                w = elt.getWidth().getBaseVal().getValue();
                h = elt.getHeight().getBaseVal().getValue();
            } catch (IllegalArgumentException ex) {
                w = 400;
                h = 400;
            }
            checkInterrupt();
            String description =
                        SVGUtilities.getDescription(doc.getRootElement());

            fireAsyncDocumentEvent(new DocumentPropertyEvent(
                DocumentPropertyEvent.DESCRIPTION, description), false);

            fireAsyncDocumentEvent(new DocumentLoadingEvent(
                DocumentLoadingEvent.DONE, doc), false);

            fireAsyncDocumentEvent(new DocumentPropertyEvent(
                DocumentPropertyEvent.SIZE,
                new Dimension((int)w, (int)h)), false);

        } catch (InterruptedException e) {
            System.out.println("Document loading thread interrupted.");
            fireAsyncDocumentEvent(new DocumentLoadingEvent(
                        DocumentLoadingEvent.LOAD_CANCELLED, null), false);
        } catch (InterruptedIOException iioe) {
            System.out.println("Loading interrupted during document I/O.");
            fireAsyncDocumentEvent(new DocumentLoadingEvent(
                        DocumentLoadingEvent.LOAD_CANCELLED, null), false);
        } catch (IOException e) {
            //System.out.println("I/O Exception loading document: " +e.getMessage());
            fireAsyncDocumentEvent(new DocumentLoadingEvent(
                        DocumentLoadingEvent.LOAD_FAILED, null, e), false);
        } catch (SAXException e) {
            //System.out.println("Malformed XML document: " +e.getMessage());
            fireAsyncDocumentEvent(new DocumentLoadingEvent(
                        DocumentLoadingEvent.LOAD_FAILED, null, e), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     private void checkInterrupt() throws InterruptedException {
          if (Thread.currentThread().isInterrupted()) {
              throw new InterruptedException();
          }
     }
}
