/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

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
     * Tells whether the given SVG document is dynamic.
     */
    public static boolean isDynamicDocument(Document doc) {
        return ScriptingEnvironment.isDynamicDocument(doc);
    }
    
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
     * The update RunnableQueue.
     */
    protected RunnableQueue updateRunnableQueue;

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
    protected volatile boolean running;

    /**
     * Whether the suspend() method was called.
     */
    protected boolean suspendCalled;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * The starting time.
     */
    protected long startingTime;

    /**
     * The scripting environment.
     */
    protected ScriptingEnvironment scriptingEnvironment;

    /**
     * The repaint manager.
     */
    protected RepaintManager repaintManager;

    /**
     * The repaint-rate manager.
     */
    protected RepaintRateManager repaintRateManager;

    /**
     * The update tracker.
     */
    protected UpdateTracker updateTracker;

    /**
     * The GraphicsNode whose updates are to be tracked.
     */
    protected GraphicsNode graphicsNode;

    /**
     * Creates a new update manager.
     * @param ctx The bridge context.
     * @param gn GraphicsNode whose updates are to be tracked.
     * @param doc The document to manage.
     */
    public UpdateManager(BridgeContext ctx,
                         GraphicsNode gn,
                         Document doc) {
        bridgeContext = ctx;
        bridgeContext.setUpdateManager(this);

        document = doc;

        updateRunnableQueue = RunnableQueue.createRunnableQueue();
        updateRunnableQueue.setRunHandler(this);

        graphicsNode = gn;

        scriptingEnvironment = new ScriptingEnvironment(this);
    }

    /**
     * Finishes the UpdateManager initialization.
     */
    public void manageUpdates(ImageRenderer r) {
        running = true;
        renderer = r;
        
        updateTracker = new UpdateTracker();
        RootGraphicsNode root = graphicsNode.getRoot();
        if (root != null){
            root.addTreeGraphicsNodeChangeListener(updateTracker);
        }

        repaintManager = new RepaintManager(this, renderer);
        repaintRateManager = new RepaintRateManager(this);
        repaintRateManager.start();

        scriptingEnvironment.getScriptingLock().unlock();

        fireManagerStartedEvent();
    }


    /**
     * Returns the bridge context.
     */
    public BridgeContext getBridgeContext() {
        return bridgeContext;
    }

    /**
     * Returns the update RunnableQueue.
     */
    public RunnableQueue getUpdateRunnableQueue() {
        return updateRunnableQueue;
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
     * Returns the current Document.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Returns the scripting environment.
     */
    public ScriptingEnvironment getScriptingEnvironment() {
        return scriptingEnvironment;
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
        if (running) {
            suspendCalled = true;
            updateRunnableQueue.suspendExecution(false);
        }
    }

    /**
     * Resumes the update manager.
     */
    public void resume() {
        if (!running) {
            updateRunnableQueue.resumeExecution();
        }
    }

    /**
     * Dispatches an 'SVGLoad' event to the document.
     * NOTES:
     * - This method starts the update manager threads so one can't use
     *   the update runnable queue to invoke this method.
     * - The scripting lock is taken. Is is released by a call to
     *   manageUpdates().
     */
    public void dispatchSVGLoadEvent() throws InterruptedException {
        // Locking avoid execution of scripts scheduled by calls
        // to the setTimeout() function.
        // The lock is be released by a call to manageUpdates().
        scriptingEnvironment.getScriptingLock().lock();

        scriptingEnvironment.loadScripts();
        scriptingEnvironment.dispatchSVGLoadEvent();
        
        updateRunnableQueue.resumeExecution();
        startingTime = System.currentTimeMillis();
    }

    /**
     * Dispatches an 'SVGUnLoad' event to the document.
     * This method interrupts the update manager threads.
     * NOTE: this method must be called outside the update thread.
     */
    public void dispatchSVGUnLoadEvent() {
        resume();
        updateRunnableQueue.invokeLater(new Runnable() {
                public void run() {
                    Event evt =
                        ((DocumentEvent)document).createEvent("SVGEvents");
                    evt.initEvent("SVGUnload", false, false);
                    ((EventTarget)(document.getDocumentElement())).
                        dispatchEvent(evt);
                    running = false;
                    
                    repaintRateManager.interrupt();
                    updateRunnableQueue.getThread().interrupt();
                    fireManagerStoppedEvent();
                }
            });
    }

    /**
     * Call this to let the Update Manager know that certain areas
     * in the image have been modified and need to be rerendered..
     */
    public void modifiedAreas(List areas) {
        repaintManager.modifiedAreas(areas);
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
        try {
            fireStartedEvent(renderer.getOffScreen());
            
            List l = repaintManager.updateRendering(u2d, dbr, aoi,
                                                    width, height);
            
            fireCompletedEvent(renderer.getOffScreen(), l);
        } catch (Exception e) {
            fireFailedEvent();
        }
    }

    /**
     * Updates the rendering buffer.
     * @param aoi The area of interest in the renderer space units.
     */
    public void updateRendering(List areas) {
        try {
            fireStartedEvent(renderer.getOffScreen());

            List l = repaintManager.updateRendering(areas);

            fireCompletedEvent(renderer.getOffScreen(), l);
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
        if (suspendCalled) {
            running = false;
            suspendStartTime = System.currentTimeMillis();
            if (scriptingEnvironment != null) {
                scriptingEnvironment.suspendScripts();
            }
            fireManagerSuspendedEvent();
        }
    }

    /**
     * Called when the execution of the queue has been resumed.
     */
    public void executionResumed(RunnableQueue rq) {
        if (suspendCalled && !running) {
            running = true;
            suspendCalled = false;
            suspendedTime = System.currentTimeMillis() - suspendStartTime;
            fireManagerResumedEvent();
            if (scriptingEnvironment != null) {
                scriptingEnvironment.resumeScripts();
            }
        }
    }
}
