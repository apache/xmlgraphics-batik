/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JDialog;

import org.apache.batik.bridge.ViewBox;

import org.apache.batik.swing.JSVGCanvas;

import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.JGVTComponent;

import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;

import org.apache.batik.util.gui.resource.ResourceManager;

import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class represents a Dialog that displays a Thumbnail of the current SVG
 * document.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class ThumbnailDialog extends JDialog {

    /**
     * The resource file name
     */
    protected final static String RESOURCES =
        "org.apache.batik.apps.svgbrowser.resources.ThumbnailDialog";

    /**
     * The resource bundle
     */
    protected static ResourceBundle bundle;

    /**
     * The resource manager
     */
    protected static ResourceManager resources;

    static {
        bundle = ResourceBundle.getBundle(RESOURCES, Locale.getDefault());
        resources = new ResourceManager(bundle);
    }

    /** The canvas that owns the SVG document to display. */
    protected JSVGCanvas svgCanvas;

    /** The canvas that displays the thumbnail. */
    protected JGVTComponent svgThumbnailCanvas;

    /** A flag bit that indicates a document has been loaded. */
    protected boolean documentChanged;

    /**
     * Constructs a new <tt>ThumbnailDialog</tt> for the specified canvas.
     *
     * @param canvas the canvas that owns the SVG document to display
     */
    public ThumbnailDialog(Frame owner, JSVGCanvas svgCanvas) {
        super(owner, resources.getString("Dialog.title"));

        addWindowListener(new ThumbnailListener());

        // register listeners to maintain consistency
        this.svgCanvas = svgCanvas;
        svgCanvas.addGVTTreeRendererListener(new ThumbnailGVTListener());
        svgCanvas.addSVGDocumentLoaderListener(new ThumbnailDocumentListener());

        // create the thumbnail
        svgThumbnailCanvas = new JGVTComponent();
        svgThumbnailCanvas.setPreferredSize(new Dimension(150, 150));
        svgThumbnailCanvas.addComponentListener(new ThumbnailComponentListener());
        getContentPane().add(svgThumbnailCanvas, BorderLayout.CENTER);
    }

    /**
     * Updates the thumbnail component.
     */
    protected void updateThumbnailGraphicsNode() {
        svgThumbnailCanvas.setGraphicsNode(svgCanvas.getGraphicsNode());
        updateThumbnailRenderingTransform();
    }

    /**
     * Updates the thumbnail component rendering transform.
     */
    protected void updateThumbnailRenderingTransform() {
        SVGDocument svgDocument = svgCanvas.getSVGDocument();
        if (svgDocument != null) {
            SVGSVGElement elt = svgDocument.getRootElement();
            Dimension dim = svgThumbnailCanvas.getSize();

            AffineTransform Tx
                = ViewBox.getViewTransform(null, elt, dim.width, dim.height);
            if (Tx.isIdentity()) {
                // no viewBox has been specified, create a scale transform
                Dimension2D docSize = svgCanvas.getSVGDocumentSize();
                double sx = dim.width / docSize.getWidth();
                double sy = dim.height / docSize.getHeight();
                double s = Math.min(sx, sy);
                Tx = AffineTransform.getScaleInstance(s, s);
            }
            svgThumbnailCanvas.setRenderingTransform(Tx);
        }
    }

    /**
     * Used to determine whether or not the GVT tree of the thumbnail has to be
     * updated.
     */
    protected class ThumbnailDocumentListener extends SVGDocumentLoaderAdapter {

        public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
            documentChanged = true;
        }
    }

    /**
     * Used to update the overlay and/or the GVT tree of the thumbnail.
     */
    protected class ThumbnailGVTListener extends GVTTreeRendererAdapter {

        public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
            if (documentChanged) {
                updateThumbnailGraphicsNode();
                documentChanged = false;
            }
        }

        public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
            svgThumbnailCanvas.setGraphicsNode(null);
            svgThumbnailCanvas.setRenderingTransform(new AffineTransform());
        }

        public void gvtRenderingFailed(GVTTreeRendererEvent e) {
            svgThumbnailCanvas.setGraphicsNode(null);
            svgThumbnailCanvas.setRenderingTransform(new AffineTransform());
        }
    }

    /**
     * Used the first time the thumbnail dialog is shown to make visible the
     * current GVT tree being displayed by the original SVG component.
     */
    protected class ThumbnailListener extends WindowAdapter {

        public void windowOpened(WindowEvent evt) {
            updateThumbnailGraphicsNode();
        }
    }

    /**
     * Used to allow the SVG document being displayed by the thumbnail to be
     * resized properly.
     */
    protected class ThumbnailComponentListener extends ComponentAdapter {

        public void componentResized(ComponentEvent e) {
            updateThumbnailRenderingTransform();
        }
    }
}
