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
import org.apache.batik.bridge.InterruptedBridgeException;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.EventDispatcher;
import org.apache.batik.util.EventDispatcher.Dispatcher;
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
     * Boolean indicating if this thread has ever been interrupted.
     */
    protected boolean beenInterrupted;

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
        beenInterrupted = false;
    }

    public boolean getBeenInterrupted() {
        synchronized (this) { return beenInterrupted; }
    }

    /**
     * Runs the dispatcher.
     */
    public void run() {
        SVGLoadEventDispatcherEvent ev;
        ev = new SVGLoadEventDispatcherEvent(this, root);
        try {
            fireEvent(startedDispatcher, ev);

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }

            updateManager.dispatchSVGLoadEvent();

            if (getBeenInterrupted()) {
                fireEvent(cancelledDispatcher, ev);
                return;
            }

            fireEvent(completedDispatcher, ev);
        } catch (InterruptedException e) {
            fireEvent(cancelledDispatcher, ev);
        } catch (InterruptedBridgeException e) {
            fireEvent(cancelledDispatcher, ev);
        } catch (Exception e) {
            exception = e;
            fireEvent(failedDispatcher, ev);
        } catch (Throwable t) {
            t.printStackTrace();
            exception = new Exception(t.getMessage());
            fireEvent(failedDispatcher, ev);
        }
    }

    public void interrupt() {
        super.interrupt();
        synchronized (this) {
            beenInterrupted = true;
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
    public void addSVGLoadEventDispatcherListener
        (SVGLoadEventDispatcherListener l) {
        listeners.add(l);
    }

    /**
     * Removes a SVGLoadEventDispatcherListener from this
     * SVGLoadEventDispatcher.
     */
    public void removeSVGLoadEventDispatcherListener
        (SVGLoadEventDispatcherListener l) {
        listeners.remove(l);
    }

    public void fireEvent(Dispatcher dispatcher, Object event) {
        EventDispatcher.fireEvent(dispatcher, listeners, event, true);
    }

    static Dispatcher startedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGLoadEventDispatcherListener)listener).
                    svgLoadEventDispatchStarted
                    ((SVGLoadEventDispatcherEvent)event);
            }
        };

    static Dispatcher completedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGLoadEventDispatcherListener)listener).
                    svgLoadEventDispatchCompleted
                    ((SVGLoadEventDispatcherEvent)event);
            }
        };

    static Dispatcher cancelledDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGLoadEventDispatcherListener)listener).
                    svgLoadEventDispatchCancelled
                    ((SVGLoadEventDispatcherEvent)event);
            }
        };

    static Dispatcher failedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((SVGLoadEventDispatcherListener)listener).
                    svgLoadEventDispatchFailed
                    ((SVGLoadEventDispatcherEvent)event);
            }
        };
}
