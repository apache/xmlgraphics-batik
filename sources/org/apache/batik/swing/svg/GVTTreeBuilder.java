/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.DynamicGVTBuilder;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.InterruptedBridgeException;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.svg.SVGDocument;

/**
 * This class represents an object which builds asynchroneaously
 * a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GVTTreeBuilder extends Thread {

    /**
     * The SVG document to give to the bridge.
     */
    protected SVGDocument svgDocument;

    /**
     * The bridge context to use.
     */
    protected BridgeContext bridgeContext;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * The exception thrown.
     */
    protected Exception exception;

    /**
     * Creates a new GVTTreeBuilder.
     */
    public GVTTreeBuilder(SVGDocument   doc,
                          BridgeContext bc) {
        svgDocument = doc;
        bridgeContext = bc;
    }

    /**
     * Runs this builder.
     */
    public void run() {
        try {
            fireStartedEvent();

            GVTBuilder builder = null;

            if (bridgeContext.isDynamic()) {
                builder = new DynamicGVTBuilder();
            } else {
                builder = new GVTBuilder();
            }
            GraphicsNode gvtRoot = builder.build(bridgeContext, svgDocument);

            fireCompletedEvent(gvtRoot);
        } catch (InterruptedBridgeException e) {
            fireCancelledEvent();
        } catch (BridgeException e) {
            exception = e;
            fireFailedEvent(e.getGraphicsNode());
        } catch (Exception e) {
            exception = e;
            fireFailedEvent(null);
        }
        bridgeContext.getDocumentLoader().dispose();
    }

    /**
     * Returns the exception, if any occured.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Adds a GVTTreeBuilderListener to this GVTTreeBuilder.
     */
    public void addGVTTreeBuilderListener(GVTTreeBuilderListener l) {
        listeners.add(l);
    }

    /**
     * Removes a GVTTreeBuilderListener from this GVTTreeBuilder.
     */
    public void removeGVTTreeBuilderListener(GVTTreeBuilderListener l) {
        listeners.remove(l);
    }

    /**
     * Fires a GVTTreeBuilderEvent.
     */
    protected void fireStartedEvent() throws InterruptedException {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeBuilderEvent ev = new GVTTreeBuilderEvent(this, null);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    GVTTreeBuilderListener dl = (GVTTreeBuilderListener)dll[i];
                    dl.gvtBuildStarted(ev);
                }
            } else {
                try {
                    EventQueue.invokeAndWait(new Runnable() {
                            public void run() {
                                for (int i = 0; i < dll.length; i++) {
                                    GVTTreeBuilderListener dl =
                                        (GVTTreeBuilderListener)dll[i];
                                    dl.gvtBuildStarted(ev);
                                }
                            }
                        });
                } catch (InvocationTargetException e) {
                }
            }
        }
    }

    /**
     * Fires a GVTTreeBuilderEvent.
     */
    protected void fireCompletedEvent(GraphicsNode root) {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeBuilderEvent ev = new GVTTreeBuilderEvent(this, root);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    GVTTreeBuilderListener dl = (GVTTreeBuilderListener)dll[i];
                    dl.gvtBuildCompleted(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            GVTTreeBuilderListener dl =
                                (GVTTreeBuilderListener)dll[i];
                            dl.gvtBuildCompleted(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a GVTTreeBuilderEvent.
     */
    protected void fireFailedEvent(GraphicsNode root) {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeBuilderEvent ev = new GVTTreeBuilderEvent(this, root);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    GVTTreeBuilderListener dl = (GVTTreeBuilderListener)dll[i];
                    dl.gvtBuildFailed(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            GVTTreeBuilderListener dl =
                                (GVTTreeBuilderListener)dll[i];
                            dl.gvtBuildFailed(ev);
                        }
                    }
                });
            }
        }
    }

    /**
     * Fires a GVTTreeBuilderEvent.
     */
    protected void fireCancelledEvent() {
        final Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            final GVTTreeBuilderEvent ev = new GVTTreeBuilderEvent(this, null);

            if (EventQueue.isDispatchThread()) {
                for (int i = 0; i < dll.length; i++) {
                    GVTTreeBuilderListener dl = (GVTTreeBuilderListener)dll[i];
                    dl.gvtBuildCancelled(ev);
                }
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < dll.length; i++) {
                            GVTTreeBuilderListener dl =
                                (GVTTreeBuilderListener)dll[i];
                            dl.gvtBuildCancelled(ev);
                        }
                    }
                });
            }
        }
    }
}
