/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.DynamicGVTBuilder;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.InterruptedBridgeException;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.EventDispatcher;
import org.apache.batik.util.EventDispatcher.Dispatcher;
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
     * Boolean indicating if this thread has ever been interrupted.
     */
    protected boolean beenInterrupted;

    /**
     * Creates a new GVTTreeBuilder.
     */
    public GVTTreeBuilder(SVGDocument   doc,
                          BridgeContext bc) {
        svgDocument = doc;
        bridgeContext = bc;
        beenInterrupted = false;
    }

    public boolean getBeenInterrupted() {
        synchronized (this) { return beenInterrupted; }
    }

    /**
     * Runs this builder.
     */
    public void run() {
        GVTTreeBuilderEvent ev;
        ev = new GVTTreeBuilderEvent(this, null);
        try {
            fireEvent(startedDispatcher, ev);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }
            GVTBuilder builder = null;

            if (bridgeContext.isDynamic()) {
                builder = new DynamicGVTBuilder();
            } else {
                builder = new GVTBuilder();
            }
            GraphicsNode gvtRoot = builder.build(bridgeContext, svgDocument);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }

            ev = new GVTTreeBuilderEvent(this, gvtRoot);
            fireEvent(completedDispatcher, ev);
        } catch (InterruptedBridgeException e) {
            fireEvent(cancelledDispatcher, ev);
        } catch (BridgeException e) {
            exception = e;
            ev = new GVTTreeBuilderEvent(this, e.getGraphicsNode());
            fireEvent(failedDispatcher, ev);
        } catch (Exception e) {
            exception = e;
            fireEvent(failedDispatcher, ev);
        } catch (Throwable t) {
            t.printStackTrace();
            exception = new Exception(t.getMessage());
            fireEvent(failedDispatcher, ev);
        } finally {
            bridgeContext.getDocumentLoader().dispose();
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

    public void fireEvent(Dispatcher dispatcher, Object event) {
        EventDispatcher.fireEvent(dispatcher, listeners, event, true);
    }

    static Dispatcher startedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeBuilderListener)listener).gvtBuildStarted
                 ((GVTTreeBuilderEvent)event);
            }
        };

    static Dispatcher completedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeBuilderListener)listener).gvtBuildCompleted
                 ((GVTTreeBuilderEvent)event);
            }
        };

    static Dispatcher cancelledDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeBuilderListener)listener).gvtBuildCancelled
                 ((GVTTreeBuilderEvent)event);
            }
        };

    static Dispatcher failedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((GVTTreeBuilderListener)listener).gvtBuildFailed
                 ((GVTTreeBuilderEvent)event);
            }
        };
}
