/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.applet;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import javax.swing.JApplet;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.css.CSSDocumentHandler;
import org.apache.batik.dom.svg.DefaultSVGContext;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.refimpl.gvt.event.ConcreteEventDispatcher;
import org.apache.batik.refimpl.util.JSVGCanvas;
import org.apache.batik.util.DocumentEvent;
import org.apache.batik.util.DocumentListener;
import org.apache.batik.util.DocumentLoadRunnable;
import org.apache.batik.util.DocumentLoadRunnable;
import org.apache.batik.util.DocumentLoadingEvent;
import org.apache.batik.util.DocumentPropertyEvent;

import org.w3c.dom.svg.SVGAElement;
import org.w3c.dom.svg.SVGDocument;

/**
 * An applet that displays SVG document.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class JSVGApplet extends JApplet implements UserAgent, DocumentListener {

    /**
     * The current processing thread
     */
    protected Thread thread;

    /**
     * The user languages.
     */
    protected String userLanguages = "en";

    /**
     * The user style sheet URI.
     */
    protected String userStyleSheetURI;

    /**
     * The SVG canvas.
     */
    protected JSVGCanvas canvas;

    /**
     * The factory that creates new SVG Document instances.
     */
    protected SVGDocumentFactory df;

    /**
     * The event dispatcher.
     */
    protected EventDispatcher eventDispatcher;

    /**
     * The default cursor.
     */
    protected final static Cursor DEFAULT_CURSOR =
        new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * The wait cursor.
     */
    protected final static Cursor WAIT_CURSOR =
        new Cursor(Cursor.WAIT_CURSOR);

    public void init() {
        CSSDocumentHandler.setParserClassName("org.w3c.flute.parser.Parser");
        df = new SVGDocumentFactory(getXMLParserClassName());
        canvas = new JSVGCanvas(this);
        eventDispatcher = 
            new ConcreteEventDispatcher(
                canvas.getRendererFactory().getRenderContext());

        canvas.setPreferredSize(new Dimension(600, 400));
        getContentPane().add(canvas, BorderLayout.CENTER);
        String uri = getParameter("svg");
        try {
            loadDocument(uri);
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem item = new JMenuItem("Zoom In");
        item.addActionListener(canvas.getAction(JSVGCanvas.ZOOM_IN_ACTION));
        popup.add(item);
        item = new JMenuItem("Zoom Out");
        item.addActionListener(canvas.getAction(JSVGCanvas.ZOOM_OUT_ACTION));
        popup.add(item);
        item = new JMenuItem("Zoom 1:1");
        item.addActionListener(canvas.getAction(JSVGCanvas.UNZOOM_ACTION));
        popup.add(item);
        canvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                int mods = e.getModifiers();
                if (!((mods & e.CTRL_MASK) != 0) && e.isPopupTrigger()) {
                    popup.show(e.getComponent(),
                               e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Loads the given document.
     * @param s The document name.
     */
    public void loadDocument(String uri) {
        if (uri != null) {
            // interrupt any document load already underway
            if ((thread != null) && thread.isAlive()) {
                thread.interrupt();
            }
            Thread t = DocumentLoadRunnable.createLoaderThread(uri,
                                                               this,
                                                               df);
            runThread(t);
        }
    }

    // DocumentLoadRunnable //////////////////////////////////////////////

    /**
     * Take action on receipt of a document event.
     */
    public void processDocumentEvent(DocumentEvent e) {

        // perhaps keying on public field is faster than instanceof ?

        if (e.classid == DocumentEvent.LOADING) {
            processDocumentLoadingEvent((DocumentLoadingEvent)e);
        } else if (e.classid == DocumentEvent.PROPERTY) {
            processDocumentPropertyEvent((DocumentPropertyEvent)e);
        }
    }

    /**
     * Take action on receipt of a document loading event.
     */
    public void processDocumentLoadingEvent(DocumentLoadingEvent e) {
        switch (e.type) {
        case (DocumentLoadingEvent.START_LOADING):
            setCursor(WAIT_CURSOR);
            break;
        case (DocumentLoadingEvent.LOADED):
            // New doc has been loaded, prepare for new view
            DefaultSVGContext dc = new DefaultSVGContext() {
                    public float getPixelToMM() {
                        return JSVGApplet.this.getPixelToMM();
                    }
                    public float getViewportWidth() {
                        return (float)canvas.getSize().getWidth();
                    }
                    public float getViewportHeight() {
                        return (float)canvas.getSize().getHeight();
                    }
                };
            SVGOMDocument doc = (SVGOMDocument) e.getValue();
            dc.setUserStyleSheetURI(userStyleSheetURI);
            doc.setSVGContext(dc);
            canvas.setSVGDocument(null);
            break;
        case (DocumentLoadingEvent.DONE):
            doc = (SVGOMDocument) e.getValue();
            canvas.setSVGDocument(doc);
            setCursor(DEFAULT_CURSOR);
            break;
        case (DocumentLoadingEvent.LOAD_CANCELLED):
            break;
        case (DocumentLoadingEvent.LOAD_FAILED):
            displayError("Document.failed");
        }
    }

    /**
     * Take action on receipt of a document property change.
     */
    public void processDocumentPropertyEvent(DocumentPropertyEvent e) {
        switch (e.type) {
        case (DocumentPropertyEvent.TITLE) :
            break;
        case (DocumentPropertyEvent.SIZE) :
            break;
        case (DocumentPropertyEvent.DESCRIPTION) :
            break;
        }
    }

    // UserAgent ///////////////////////////////////////////////////

    /**
     * Returns the default size of the viewport of this user agent.
     */
    public Dimension2D getViewportSize() {
        return getSize();
    }

    /**
     * Returns the <code>EventDispatcher</code> used by the
     * <code>UserAgent</code> to dispatch events on GVT.
     */
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * Displays an error message in the User Agent interface.
     */
    public void displayError(String message) {
        System.err.println(message);
    }

    /**
     * Displays an error resulting from the specified Exception.
     */
    public void displayError(Exception ex) {
        System.err.println(ex);
    }

    /**
     * Displays a message in the User Agent interface.
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Returns the pixel to mm factor.
     */
    public float getPixelToMM() {
        return 0.33f;
    }

    /**
     * Returns the language settings.
     */
    public String getLanguages() {
        return userLanguages;
    }

    /**
     * Returns the user stylesheet uri.
     * @return null if no user style sheet was specified.
     */
    public String getUserStyleSheetURI() {
        return userStyleSheetURI;
    }

    /**
     * Opens a link.
     * @param elt The activated link element.
     */
    public void openLink(SVGAElement elt) {
        //application.openLink(this, elt);
    }

    /**
     * Informs the user agent to change the cursor.
     * @param cursor the new cursor
     */
    public void setSVGCursor(Cursor cursor) {
        setCursor(cursor);
    }

    /**
     * Runs the given thread.
     */
    public synchronized void runThread(Thread t) {
        thread = t;
        thread.start();
    }

    /**
     * Returns the class name of the XML parser.
     */
    public String getXMLParserClassName() {
        return "org.apache.crimson.parser.XMLReaderImpl";
    }

    /**
     * Returns the <code>AffineTransform</code> currently
     * applied to the drawing by the UserAgent.
     */
    public AffineTransform getTransform() {
        return canvas.getTransform();
    }

    /**
     * Returns the location on the screen of the
     * client area in the UserAgent.
     */
    public Point getClientAreaLocationOnScreen() {
        return canvas.getLocationOnScreen();
    }
}
