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

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JDialog;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;
import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a Dialog that displays a Thumbnail of the current SVG
 * document.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class ThumbnailDialog extends JDialog implements GVTTreeRendererListener {

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
    protected JSVGCanvas svgThumbnailCanvas;

    /**
     * Constructs a new <tt>ThumbnailDialog</tt> for the specified canvas.
     *
     * @param canvas the canvas that owns the SVG document to display
     */
    public ThumbnailDialog(Frame owner, JSVGCanvas svgCanvas) {
        super(owner, resources.getString("Dialog.title"));
        this.svgCanvas = svgCanvas;
        svgThumbnailCanvas = new JSVGCanvas();
        svgThumbnailCanvas.setPreferredSize(new Dimension(150, 150));
        getContentPane().add(svgThumbnailCanvas, BorderLayout.CENTER);

        svgCanvas.addGVTTreeRendererListener(this);
    }

    /**
     * Called when a rendering is in its preparing phase.
     */
    public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
        System.out.println("gvtRenderingPrepare");
        svgThumbnailCanvas.setGraphicsNode(svgCanvas.getGraphicsNode());
    }

    /**
     * Called when a rendering started.
     */
    public void gvtRenderingStarted(GVTTreeRendererEvent e) {
        System.out.println("gvtRenderingStarted");
        svgThumbnailCanvas.setGraphicsNode(svgCanvas.getGraphicsNode());
    }

    /**
     * Called when a rendering was completed.
     */
    public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
        System.out.println("gvtRenderingCompleted");
        svgThumbnailCanvas.setGraphicsNode(svgCanvas.getGraphicsNode());
    }

    /**
     * Called when a rendering was cancelled.
     */
    public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
        System.out.println("gvtRenderingCancelled");
        svgThumbnailCanvas.setGraphicsNode(svgCanvas.getGraphicsNode());
    }

    /**
     * Called when a rendering failed.
     */
    public void gvtRenderingFailed(GVTTreeRendererEvent e) {
        System.out.println("gvtRenderingFailed");
        svgThumbnailCanvas.setGraphicsNode(svgCanvas.getGraphicsNode());
    }
}
