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

    static final long MIN_REPAINT_TIME;
    static {
        long value = 20;
        try {
            String s = System.getProperty
            ("org.apache.batik.min_repaint_time", "20");
            value = Long.parseLong(s);
        } catch (SecurityException se) {
        } catch (NumberFormatException nfe){
        } finally {
            MIN_REPAINT_TIME = value;
        }
    }

    /**
     * Tells whether the given SVG document is dynamic.
     */
    public static boolean isDynamicDocument(Document doc) {
        return BaseScriptingEnvironment.isDynamicDocument(doc);
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
     * The update RunnableQueue.
     */
    protected RunnableQueue updateRunnableQueue;

    /**
     * Whether the update manager is running.
     */
    protected boolean running;

    /**
     * Whether the suspend() method was called.
     */
    protected boolean suspendCalled;

    /**
     * The listeners.
     */
    protected List listeners = Collections.synchronizedList(new LinkedList());

    /**
     * The scripting environment.
     */
    protected ScriptingEnvironment scriptingEnvironment;

    /**
     * The repaint manager.
     */
    protected RepaintManager repaintManager;

    /**
     * The update tracker.
     */
    protected UpdateTracker updateTracker;

    /**
     * The GraphicsNode whose updates are to be tracked.
     */
    protected GraphicsNode graphicsNode;

    /**
     * Whether the manager was started.
     */
    protected boolean started;

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

        scriptingEnvironment = new ScriptingEnvironment(ctx);
    }

    /**
     * Dispatches an 'SVGLoad' event to the document.
     */
    public synchronized void dispatchSVGLoadEvent()
        throws InterruptedException {
        scriptingEnvironment.loadScripts();
        scriptingEnvironment.dispatchSVGLoadEvent();
    }

    /**
     * Dispatches an "SVGZoom" event to the document.
     */
    public void dispatchSVGZoomEvent()
        throws InterruptedException {
        scriptingEnvironment.dispatchSVGZoomEvent();
    }

    /**
     * Dispatches an "SVGZoom" event to the document.
     */
    public void dispatchSVGScrollEvent()
        throws InterruptedException {
        scriptingEnvironment.dispatchSVGScrollEvent();
    }

    /**
     * Dispatches an "SVGZoom" event to the document.
     */
    public void dispatchSVGResizeEvent()
        throws InterruptedException {
        scriptingEnvironment.dispatchSVGResizeEvent();
    }

    /**
     * Finishes the UpdateManager initialization.
     */
    public void manageUpdates(final ImageRenderer r) {
        updateRunnableQueue.preemptLater(new Runnable() {
                public void run() {
                    synchronized (UpdateManager.this) {
                        running = true;
        
                        updateTracker = new UpdateTracker();
                        RootGraphicsNode root = graphicsNode.getRoot();
                        if (root != null){
                            root.addTreeGraphicsNodeChangeListener
                                (updateTracker);
                        }

                        repaintManager =
                            new RepaintManager(r);

                        fireManagerStartedEvent();
                        started = true;
                    }
                }
            });
        updateRunnableQueue.resumeExecution();
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
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * Suspends the update manager.
     */
    public synchronized void suspend() {
        if (running) {
            suspendCalled = true;
            updateRunnableQueue.suspendExecution(false);
        }
    }

    /**
     * Resumes the update manager.
     */
    public synchronized void resume() {
        if (!running) {
            updateRunnableQueue.resumeExecution();
        }
    }

    /**
     * Interrupts the manager tasks.
     */
    public synchronized void interrupt() {
        if (updateRunnableQueue.getThread() != null) {
            if (started) {
                dispatchSVGUnLoadEvent();
            } else {
                // Invoke first to cancel the pending tasks
                updateRunnableQueue.preemptLater(new Runnable() {
                        public void run() {
                            synchronized (UpdateManager.this) {
                                running = false;
                                scriptingEnvironment.interrupt();
                                updateRunnableQueue.getThread().interrupt();
                            }
                        }
                    });
                resume();
            }
        }
    }

    /**
     * Dispatches an 'SVGUnLoad' event to the document.
     * This method interrupts the update manager threads.
     * NOTE: this method must be called outside the update thread.
     */
    public void dispatchSVGUnLoadEvent() {
        if (!started) {
            throw new IllegalStateException("UpdateManager not started.");
        }

        // Invoke first to cancel the pending tasks
        updateRunnableQueue.preemptLater(new Runnable() {
                public void run() {
                    synchronized (UpdateManager.this) {
                        Event evt =
                            ((DocumentEvent)document).createEvent("SVGEvents");
                        evt.initEvent("SVGUnload", false, false);
                        ((EventTarget)(document.getDocumentElement())).
                            dispatchEvent(evt);
                        running = false;
                    
                        scriptingEnvironment.interrupt();
                        updateRunnableQueue.getThread().interrupt();
                        fireManagerStoppedEvent();
                    }
                }
            });
        resume();
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
        repaintManager.setupRenderer(u2d,dbr,aoi,width,height);
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
            fireStartedEvent(repaintManager.getOffScreen());

            List l = repaintManager.updateRendering(areas);

            fireCompletedEvent(repaintManager.getOffScreen(), l);
        } catch (Exception e) {
            fireFailedEvent();
        }
    }

    long lastRepaint=0;

    /**
     * Repaints the dirty areas, if needed.
     */
    public void repaint() {
        long ctime = System.currentTimeMillis();
        if (updateTracker.hasChanged()) {
            if (ctime-lastRepaint < MIN_REPAINT_TIME) {
                // We very recently did a repaint check if other 
                // repaint runnables are pending.
                Iterator i = updateRunnableQueue.iterator();
                while (i.hasNext())
                    if (!(i.next() instanceof NoRepaintRunnable))
                        // have a pending repaint runnable so we
                        // will skip this repaint and we will let 
                        // the next one pick it up.
                        return;
            }
               
            List dirtyAreas = updateTracker.getDirtyAreas();
            if (dirtyAreas != null) {
                updateRendering(dirtyAreas);
            }
            updateTracker.clear();
            lastRepaint = System.currentTimeMillis();
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
        if (running && !(r instanceof NoRepaintRunnable)) {
            repaint();
        }
    }

    /**
     * Called when the execution of the queue has been suspended.
     */
    public void executionSuspended(RunnableQueue rq) {
        if (suspendCalled) {
            running = false;
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
            fireManagerResumedEvent();
        }
    }
}
