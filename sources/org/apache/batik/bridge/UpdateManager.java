/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Rectangle;
import java.awt.Shape;

import java.awt.geom.AffineTransform;

import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.dom.DocumentWrapper;
import org.apache.batik.dom.DOMImplementationWrapper;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.UpdateTracker;

import org.apache.batik.gvt.renderer.ImageRenderer;

import org.apache.batik.util.RunnableQueue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.svg.SVGSVGElement;

import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * This class provides features to manage the update of an SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class UpdateManager implements RunnableQueue.RunHandler {
    
    /**
     * The bridge context.
     */
    protected BridgeContext bridgeContext;
    
    /**
     * The document to manage.
     */
    protected Document document;

    /**
     * The renderer used to paint.
     */
    protected ImageRenderer renderer;

    /**
     * The document usable from the scripting thread.
     */
    protected DocumentWrapper scriptingDocument;

    /**
     * The update RunnableQueue.
     */
    protected RunnableQueue updateRunnableQueue;

    /**
     * The scripting RunnableQueue.
     */
    protected RunnableQueue scriptingRunnableQueue;

    /**
     * The initial time.
     */
    protected long initialTime;

    /**
     * The time elapsed in suspended state.
     */
    protected long suspendedTime;

    /**
     * The starting time of the current pause.
     */
    protected long suspendStartTime;

    /**
     * Whether the update manager is running.
     */
    protected boolean running;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * The starting time.
     */
    protected long startingTime;

    /**
     * The repaint manager.
     */
    protected RepaintManager repaintManager;

    /**
     * The update tracker.
     */
    protected UpdateTracker updateTracker;

    /**
     * Creates a new update manager.
     * @param ctx The bridge context.
     * @param gn GraphicsNode whose updates are to be tracked.
     * @param doc The document to manage.
     * @param r The renderer.
     */
    public UpdateManager(BridgeContext ctx,
                         GraphicsNode gn,
                         Document doc,
                         ImageRenderer r) {
        bridgeContext = ctx;
        bridgeContext.setUpdateManager(this);

        document = doc;
        renderer = r;

        updateRunnableQueue = RunnableQueue.createRunnableQueue();
        scriptingRunnableQueue = RunnableQueue.createRunnableQueue();

        updateRunnableQueue.setRunHandler(this);

        DOMImplementationWrapper iw;
        iw = new DOMImplementationWrapper(updateRunnableQueue,
                                          scriptingRunnableQueue,
                                          document.getImplementation());
        scriptingDocument = new DocumentWrapper(iw, document);

        updateTracker = new UpdateTracker();

        RootGraphicsNode root = gn.getRoot();
        if (root != null){
            root.addTreeGraphicsNodeChangeListener(getUpdateTracker());
        }

        repaintManager = new RepaintManager(this);
        repaintManager.start();
    }

    /**
     * Returns the update RunnableQueue.
     */
    public RunnableQueue getUpdateRunnableQueue() {
        return updateRunnableQueue;
    }

    /**
     * Returns the scripting RunnableQueue.
     */
    public RunnableQueue getScriptingRunnableQueue() {
        return scriptingRunnableQueue;
    }

    /**
     * Returns the repaint manager.
     */
    public RepaintManager getRepaintManager() {
        return repaintManager;
    }

    /**
     * Returns the GVT update tracker.
     */
    public UpdateTracker getUpdateTracker() {
        return updateTracker;
    }

    /**
     * Returns a Document usable from the scripting thread.
     */
    public DocumentWrapper getScriptingDocument() {
        return scriptingDocument;
    }

    /**
     * Tells whether the update manager is currently running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns the presentation time, in milliseconds.
     */
    public long getPresentationTime() {
        if (running) {
            return System.currentTimeMillis() - initialTime - suspendedTime;
        } else {
            return suspendStartTime - initialTime - suspendedTime;
        }
    }

    /**
     * Suspends the update manager.
     */
    public void suspend() {
        scriptingRunnableQueue.suspendExecution(true);
        updateRunnableQueue.suspendExecution(true);
        running = false;
        suspendStartTime = System.currentTimeMillis();
    }

    /**
     * Resumes the update manager.
     */
    public void resume() {
        scriptingRunnableQueue.resumeExecution();
        updateRunnableQueue.resumeExecution();
        running = true;
        suspendedTime = System.currentTimeMillis() - suspendStartTime;
    }

    /**
     * Dispatches an 'SVGLoad' event to the document.
     * NOTE: This method starts the update manager threads so one can't use
     * the update runnable queue to invoke this method.
     */
    public void dispatchSVGLoad() {
        updateRunnableQueue.resumeExecution();
        scriptingRunnableQueue.resumeExecution();

        updateRunnableQueue.invokeLater(new Runnable() {
                public void run() {
                    running = true;
                    startingTime = System.currentTimeMillis();

                    fireManagerStartedEvent();

                    SVGSVGElement svgElement =
                        (SVGSVGElement)document.getDocumentElement();
                    String language = svgElement.getContentScriptType();

                    BridgeEventSupport.loadScripts(bridgeContext, svgElement);

                    BridgeEventSupport.dispatchOnLoad(bridgeContext,
                                                      svgElement,
                                                      language);
                }
            });
    }

    /**
     * Dispatches an 'SVGUnLoad' event to the document.
     * This method interrupts the update manager threads.
     * NOTE: this method must be called outside the update thread.
     */
    public void dispatchSVGUnLoad() {
        try {
            updateRunnableQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        Event evt = ((DocumentEvent)document).createEvent("SVGEvents");
                        evt.initEvent("SVGUnload", false, false);
                        ((EventTarget)(document.getDocumentElement())).
                            dispatchEvent(evt);
                        running = false;

                        fireManagerStoppedEvent();
                    }
                });
        } catch (InterruptedException e) {
        }
        repaintManager.interrupt();
        scriptingRunnableQueue.getThread().interrupt();
        updateRunnableQueue.getThread().interrupt();
    }

    /**
     * Updates the rendering buffer.
     * @param u2d The user to device transform.
     * @param dbr Whether the double buffering should be used.
     * @param aoi The area of interest in the renderer space units.
     * @param width&nbsp;height The offscreen buffer size.
     */
    public void updateRendering(AffineTransform u2d,
                                boolean dbr,
                                Shape aoi,
                                int width,
                                int height) {
        renderer.setTransform(u2d);
        renderer.setDoubleBuffered(dbr);
        renderer.updateOffScreen(width, height);
        renderer.clearOffScreen();
        List l = new ArrayList(1);
        l.add(aoi);
        updateRendering(l);
    }

    /**
     * Updates the rendering buffer.
     * @param aoi The area of interest in the renderer space units.
     */
    public void updateRendering(List areas) {
        try {
            fireStartedEvent(renderer.getOffScreen());
            Iterator i = areas.iterator();
            List rects = new ArrayList(areas.size());
            AffineTransform at = renderer.getTransform();

            while (i.hasNext()) {
                Shape s = (Shape)i.next();
                renderer.flush(s.getBounds());
                Rectangle r = at.createTransformedShape(s).getBounds();
                rects.add(r);
            }

            renderer.setTransform(renderer.getTransform());
            renderer.repaint(areas);
            fireCompletedEvent(renderer.getOffScreen(), rects);
        } catch (Exception e) {
            fireFailedEvent();
        }
    }

    /**
     * Adds a UpdateManagerListener to this UpdateManager.
     */
    public void addUpdateManagerListener(UpdateManagerListener l) {
        listeners.add(l);
    }

    /**
     * Removes a UpdateManagerListener from this UpdateManager.
     */
    public void removeUpdateManagerListener(UpdateManagerListener l) {
        listeners.remove(l);
    }

    /**
     * Fires a UpdateManagerEvent to notify that the manager was started.
     */
    protected void fireManagerStartedEvent() {
        Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            UpdateManagerEvent ev = new UpdateManagerEvent(this, null, null);
            for (int i = 0; i < dll.length; i++) {
                ((UpdateManagerListener)dll[i]).managerStarted(ev);
            }
        }
    }

    /**
     * Fires a UpdateManagerEvent to notify that the manager was stopped.
     */
    protected void fireManagerStoppedEvent() {
        Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            UpdateManagerEvent ev = new UpdateManagerEvent(this, null, null);
            for (int i = 0; i < dll.length; i++) {
                ((UpdateManagerListener)dll[i]).managerStopped(ev);
            }
        }
    }

    /**
     * Fires a UpdateManagerEvent to notify that the manager was suspended.
     */
    protected void fireManagerSuspendedEvent() {
        Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            UpdateManagerEvent ev = new UpdateManagerEvent(this, null, null);
            for (int i = 0; i < dll.length; i++) {
                ((UpdateManagerListener)dll[i]).managerSuspended(ev);
            }
        }
    }

    /**
     * Fires a UpdateManagerEvent to notify that the manager was resumed.
     */
    protected void fireManagerResumedEvent() {
        Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            UpdateManagerEvent ev = new UpdateManagerEvent(this, null, null);
            for (int i = 0; i < dll.length; i++) {
                ((UpdateManagerListener)dll[i]).managerResumed(ev);
            }
        }
    }

    /**
     * Fires a UpdateManagerEvent in the starting phase of an update.
     */
    protected void fireStartedEvent(BufferedImage bi) {
        Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            UpdateManagerEvent ev = new UpdateManagerEvent(this, bi, null);
            for (int i = 0; i < dll.length; i++) {
                ((UpdateManagerListener)dll[i]).updateStarted(ev);
            }
        }
    }

    /**
     * Fires a UpdateManagerEvent when an update completed.
     */
    protected void fireCompletedEvent(BufferedImage bi, List rects) {
        Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            UpdateManagerEvent ev = new UpdateManagerEvent(this, bi, rects);
            for (int i = 0; i < dll.length; i++) {
                ((UpdateManagerListener)dll[i]).updateCompleted(ev);
            }
        }
    }

    /**
     * Fires a UpdateManagerEvent when an update failed.
     */
    protected void fireFailedEvent() {
        Object[] dll = listeners.toArray();

        if (dll.length > 0) {
            UpdateManagerEvent ev = new UpdateManagerEvent(this, null, null);
            for (int i = 0; i < dll.length; i++) {
                ((UpdateManagerListener)dll[i]).updateFailed(ev);
            }
        }
    }

    // RunnableQueue.RunHandler /////////////////////////////////////////

    /**
     * Called when the given Runnable has just been invoked and
     * has returned.
     */
    public void runnableInvoked(RunnableQueue rq, Runnable r) {
    }

    /**
     * Called when the execution of the queue has been suspended.
     */
    public void executionSuspended(RunnableQueue rq) {
        fireManagerSuspendedEvent();
    }

    /**
     * Called when the execution of the queue has been resumed.
     */
    public void executionResumed(RunnableQueue rq) {
        fireManagerResumedEvent();
    }

    //
    //
    //
    //
    //
    //
    //
    // !!! TODO: revisit
    //
    //
    //
    //
    //
    //
    //

    /**
     * To listen to the DOM mutation events.
     */
    protected static class MutationListener implements EventListener {
        
        BridgeContext bridgeContext;

        public MutationListener(BridgeContext bridgeContext) {
            this.bridgeContext = bridgeContext;
        }

        public void handleEvent(Event evt) {
            BridgeMutationEvent bme;
            Element target = (Element)evt.getTarget();
            bme = new BridgeMutationEvent
                (target,
                 bridgeContext,
                 BridgeMutationEvent.PROPERTY_MUTATION_TYPE);

            MutationEvent me = (MutationEvent)evt;

            bme.setAttrName(me.getAttrName());
            bme.setAttrNewValue(me.getNewValue());

            GraphicsNodeBridge bridge;
            bridge = (GraphicsNodeBridge)bridgeContext.getBridge(target);
            
            bridge.update(bme);
        }
    }
}
