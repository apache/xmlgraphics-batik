/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.awt.EventQueue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.UpdateManager;

import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.svg.SVGDocument;

/**
 * This class dispatches the SVGLoadEvent event on a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGLoadEventDispatcher extends Thread {

    /**
     * The SVG document to give to the bridge.
     */
    protected SVGDocument svgDocument;

    /**
     * The root graphics node.
     */
    protected GraphicsNode root;

    /**
     * The bridge context to use.
     */
    protected BridgeContext bridgeContext;

    /**
     * The update manager.
     */
    protected UpdateManager updateManager;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * The exception thrown.
     */
    protected Exception exception;

    /**
     * Creates a new SVGLoadEventDispatcher.
     */
    public SVGLoadEventDispatcher(GraphicsNode gn,
                                  SVGDocument doc,
                                  BridgeContext bc,
                                  UpdateManager um) {
        svgDocument = doc;
        root = gn;
        bridgeContext = bc;
        updateManager = um;
    }

    /**
     * Runs the dispatcher.
     */
    public void run() {
        try {
            fireStartedEvent();

            updateManager.dispatchSVGLoadEvent();

            fireCompletedEvent();
        } catch (InterruptedException e) {
            fireCancelledEvent();
        } catch (Exception e) {
            exception = e;
            fireFailedEvent();
        }
    }

    /**
     * Returns the update manager.
     */
    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    /**
     * Returns the exception, if any occured.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Adds a SVGLoadEventDispatcherListener to this SVGLoadEventDispatcher.
     */
    public void addSVGLoadEventDispatcherListener(SVGLoadEventDispatcherListener l) {
        listeners.add(l);
    }

    /**
     * Removes a SVGLoadEventDispatcherListener from this SVGLoadEventDispatcher.
     */
    public void removeSVGLoadEventDispatcherListener
        (SVGLoadEventDispatcherListener l) {
        listeners.remove(l);
    }

    /**
     * Fires a SVGLoadEventDispatcherEvent.
     */
    protected void fireStartedEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final SVGLoadEventDispatcherEvent ev =
                new SVGLoadEventDispatcherEvent(this, root);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    SVGLoadEventDispatcherListener dl =
                        (SVGLoadEventDispatcherListener)dll[i];
                    dl.svgLoadEventDispatchStarted(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            SVGLoadEventDispatcherListener dl =
                                (SVGLoadEventDispatcherListener)dll[i];
                            dl.svgLoadEventDispatchStarted(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a SVGLoadEventDispatcherEvent.
     */
    protected void fireCompletedEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final SVGLoadEventDispatcherEvent ev =
                new SVGLoadEventDispatcherEvent(this, root);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    SVGLoadEventDispatcherListener dl =
                        (SVGLoadEventDispatcherListener)dll[i];
                    dl.svgLoadEventDispatchCompleted(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            SVGLoadEventDispatcherListener dl =
                                (SVGLoadEventDispatcherListener)dll[i];
                            dl.svgLoadEventDispatchCompleted(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a SVGLoadEventDispatcherEvent.
     */
    protected void fireFailedEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final SVGLoadEventDispatcherEvent ev =
                new SVGLoadEventDispatcherEvent(this, root);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    SVGLoadEventDispatcherListener dl =
                        (SVGLoadEventDispatcherListener)dll[i];
                    dl.svgLoadEventDispatchFailed(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            SVGLoadEventDispatcherListener dl =
                                (SVGLoadEventDispatcherListener)dll[i];
                            dl.svgLoadEventDispatchFailed(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a SVGLoadEventDispatcherEvent.
     */
    protected void fireCancelledEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final SVGLoadEventDispatcherEvent ev =
                new SVGLoadEventDispatcherEvent(this, root);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    SVGLoadEventDispatcherListener dl =
                        (SVGLoadEventDispatcherListener)dll[i];
                    dl.svgLoadEventDispatchCancelled(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            SVGLoadEventDispatcherListener dl =
                                (SVGLoadEventDispatcherListener)dll[i];
                            dl.svgLoadEventDispatchCancelled(ev);
                        }
                    }
                });
            }
        }
    }

}
