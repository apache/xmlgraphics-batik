/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.awt.Dimension;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.File;
import java.io.Reader;
import java.net.URL;

import java.util.Iterator;
import java.util.Vector;
import java.util.List;

import java.util.zip.GZIPInputStream;

import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.apache.batik.dom.svg.DefaultSVGContext;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XSLTransformer;

import org.w3c.dom.svg.SVGSVGElement;

//import org.apache.batik.util.SVGUtilities;

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
public class DocumentLoadRunnable implements Runnable {


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
     */
    public DocumentLoadRunnable(String uri) {
        documentURI = uri;
    }

    /**
     * Creates a new thread which runs a new DocumentLoader runnable.
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
     * wrap the event notification in an "invokeLater" call.
     */
    protected void fireDocumentEvent(DocumentEvent e) {
        Iterator iter = listeners.iterator();
        DocumentListener listener;

        /* Implementation note:  arguably it would be more efficient to
         * wrap all notifications in one invokeLater() runnable, but
         * this provides better execution granularity.
         */
        while (iter.hasNext()) {
            listener = (DocumentListener)iter.next();
            SwingUtilities.invokeLater(
		new DocumentEventAsyncDispatch(e, listener, 
					getDocumentFactory()));
        }
    }

    private class DocumentEventAsyncDispatch implements Runnable {
        private DocumentEvent e;
        private DocumentListener l;
        private SVGDocumentFactory df;

        public DocumentEventAsyncDispatch(DocumentEvent e,
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
            fireDocumentEvent(
                new DocumentLoadingEvent(
		    DocumentLoadingEvent.START_LOADING, null));

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
            Reader r = new InputStreamReader(is);

            checkInterrupt();
            doc = df.createDocument(documentURI, new InputSource(r));
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
                r = new InputStreamReader(is);
                r = XSLTransformer.transform(r, l);
                doc = df.createDocument(documentURI, new InputSource(r));
            }

            long t2 = System.currentTimeMillis();

            checkInterrupt();
            fireDocumentEvent(
                new DocumentLoadingEvent(
                        DocumentLoadingEvent.LOADED, doc));

            System.out.println("--------------------------------");
            System.out.println(" Document loading time: " +
                                   (t2 - t1) + " ms");
            System.out.println("--------------------------------");

            String title = doc.getTitle();

            checkInterrupt();
            fireDocumentEvent(new DocumentPropertyEvent(
                DocumentPropertyEvent.TITLE, title));

            // Set the panel preferred size.
            SVGSVGElement elt = doc.getRootElement();
            float w = elt.getWidth().getBaseVal().getValue();
            float h = elt.getHeight().getBaseVal().getValue();

            checkInterrupt();
            fireDocumentEvent(new DocumentPropertyEvent(
                DocumentPropertyEvent.SIZE, new Dimension((int)w, (int)h)));

            t1 = System.currentTimeMillis();

            System.out.println("--------------------------------");
            System.out.println(" Tree construction time: " +
                                    (t1 - t2) + " ms");
            System.out.println("--------------------------------");

            String description =
                        SVGUtilities.getDescription(doc.getRootElement());

            fireDocumentEvent(new DocumentPropertyEvent(
                DocumentPropertyEvent.DESCRIPTION, description));

            fireDocumentEvent(new DocumentLoadingEvent(
                DocumentLoadingEvent.DONE, doc));

        } catch (InterruptedException e) {
            System.out.println("Document loading thread interrupted.");
            fireDocumentEvent(new DocumentLoadingEvent(
			DocumentLoadingEvent.LOAD_CANCELLED, null));
        } catch (InterruptedIOException iioe) {
            System.out.println("Interrupted during document I/O.");
            fireDocumentEvent(new DocumentLoadingEvent(
			DocumentLoadingEvent.LOAD_CANCELLED, null));
        } catch (IOException e) {
	    System.out.println("I/O Exception loading document: "
						+e.getMessage());
            fireDocumentEvent(new DocumentLoadingEvent(
			DocumentLoadingEvent.LOAD_FAILED, null));
        } catch (SAXException e) {
            fireDocumentEvent(new DocumentLoadingEvent(
			DocumentLoadingEvent.LOAD_FAILED, null));
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
