/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

import java.awt.EventQueue;
import java.awt.Shape;

import java.awt.image.BufferedImage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.gvt.renderer.ImageRenderer;

/**
 * This class represents an object which renders asynchroneaously
 * a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GVTTreeRenderer extends Thread {
    
    /**
     * The renderer used to paint.
     */
    protected ImageRenderer renderer;

    /**
     * The area of interest.
     */
    protected Shape areaOfInterest;

    /**
     * The buffer width.
     */
    protected int width;

    /**
     * The buffer height.
     */
    protected int height;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * Creates a new GVTTreeRenderer.
     * @param r The renderer to use to paint.
     * @param aoi The area of interest in the renderer space units.
     * @param width&nbsp;height The offscreen buffer size.
     */
    public GVTTreeRenderer(ImageRenderer r, Shape aoi, int width, int height) {
        renderer = r;
        areaOfInterest = aoi;
        this.width = width;
        this.height = height;
    }

    /**
     * Runs this renderer.
     */
    public void run() {
        try {
            firePrepareEvent();

            renderer.updateOffScreen(width, height);
            renderer.clearOffScreen();

            fireStartedEvent(renderer.getOffScreen());

            renderer.repaint(areaOfInterest);

            if (Thread.currentThread().isInterrupted()) {
                fireCancelledEvent(renderer.getOffScreen());
            } else {
                fireCompletedEvent(renderer.getOffScreen());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fireFailedEvent();
        }
    }

    /**
     * Adds a GVTTreeRendererListener to this GVTTreeRenderer.
     */
    public void addGVTTreeRendererListener(GVTTreeRendererListener l) {
        listeners.add(l);
    }

    /**
     * Removes a GVTTreeRendererListener from this GVTTreeRenderer.
     */
    public void removeGVTTreeRendererListener(GVTTreeRendererListener l) {
        listeners.remove(l);
    }

    /**
     * Fires a GVTTreeRendererEvent in the preparing phase.
     */
    protected void firePrepareEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeRendererEvent ev = new GVTTreeRendererEvent(this, null);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    ((GVTTreeRendererListener)dll[i]).gvtRenderingPrepare(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            ((GVTTreeRendererListener)dll[i]).gvtRenderingPrepare(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a GVTTreeRendererEvent in the starting phase.
     */
    protected void fireStartedEvent(BufferedImage bi) {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeRendererEvent ev = new GVTTreeRendererEvent(this, bi);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    ((GVTTreeRendererListener)dll[i]).gvtRenderingStarted(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            ((GVTTreeRendererListener)dll[i]).gvtRenderingStarted(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a GVTTreeRendererEvent when cancelled.
     */
    protected void fireCancelledEvent(BufferedImage bi) {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeRendererEvent ev = new GVTTreeRendererEvent(this, bi);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    ((GVTTreeRendererListener)dll[i]).gvtRenderingCancelled(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            ((GVTTreeRendererListener)dll[i]).gvtRenderingCancelled(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a GVTTreeRendererEvent when completed.
     */
    protected void fireCompletedEvent(BufferedImage bi) {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeRendererEvent ev = new GVTTreeRendererEvent(this, bi);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    ((GVTTreeRendererListener)dll[i]).gvtRenderingCompleted(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            ((GVTTreeRendererListener)dll[i]).gvtRenderingCompleted(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a GVTTreeRendererEvent when failed.
     */
    protected void fireFailedEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeRendererEvent ev = new GVTTreeRendererEvent(this, null);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    ((GVTTreeRendererListener)dll[i]).gvtRenderingFailed(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            ((GVTTreeRendererListener)dll[i]).gvtRenderingFailed(ev);
                        }
                    }
                });
            }
        }
    }
}
