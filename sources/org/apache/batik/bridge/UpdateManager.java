/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.UpdateTracker;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.util.EventDispatcher;
import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.apache.batik.util.RunnableQueue;
import org.w3c.dom.Document;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

/**
 * This class provides features to manage the update of an SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class UpdateManager  {

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
        RunnableQueue.RunHandler runHandler = createRunHandler();
        updateRunnableQueue.setRunHandler(runHandler);

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

                        fireEvent(startedDispatcher,
                                  new UpdateManagerEvent(this, null, null));
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

                        // Now shut everything down and disconnect
                        // everything before we send the 
                        // UpdateMangerStopped event.
                        scriptingEnvironment.interrupt();
                        updateRunnableQueue.getThread().interrupt();
                        bridgeContext.dispose();

                        // Send the UpdateManagerStopped event.
                        fireEvent(stoppedDispatcher,
                                  new UpdateManagerEvent(this, null, null));
                    }
                }
            });
        resume();
    }

    /**
     * Updates the rendering buffer.  Only to be called from the
     * update thread.
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
    protected void updateRendering(List areas) {
        try {
            fireEvent(updateStartedDispatcher,new UpdateManagerEvent
                      (this, repaintManager.getOffScreen(), null));

            List l = repaintManager.updateRendering(areas);

            fireEvent(updateCompletedDispatcher,new UpdateManagerEvent
                      (this, repaintManager.getOffScreen(), l));
        } catch (ThreadDeath td) {
            fireEvent(updateFailedDispatcher,
                      new UpdateManagerEvent(this, null, null));
            throw td;
        } catch (Throwable t) {
            fireEvent(updateFailedDispatcher,
                      new UpdateManagerEvent(this, null, null));
        }
    }

    long lastRepaint=0;

    /**
     * Repaints the dirty areas, if needed.
     */
    protected void repaint() {
        if (!updateTracker.hasChanged()) 
            return;
        long ctime = System.currentTimeMillis();
        if (ctime-lastRepaint < MIN_REPAINT_TIME) {
            // We very recently did a repaint check if other 
            // repaint runnables are pending.
            synchronized (updateRunnableQueue.getIteratorLock()) {
                Iterator i = updateRunnableQueue.iterator();
                while (i.hasNext())
                    if (!(i.next() instanceof NoRepaintRunnable))
                        // have a pending repaint runnable so we
                        // will skip this repaint and we will let 
                        // the next one pick it up.
                        return;
                
            }
        }
        
        List dirtyAreas = updateTracker.getDirtyAreas();
        updateTracker.clear();
        if (dirtyAreas != null) {
            updateRendering(dirtyAreas);
        }
        lastRepaint = System.currentTimeMillis();
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

    protected void fireEvent(Dispatcher dispatcher, Object event) {
        EventDispatcher.fireEvent(dispatcher, listeners, event, false);
    }


    /**
     * Dispatches a UpdateManagerEvent to notify that the manager was
     * started
     */
    static Dispatcher startedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((UpdateManagerListener)listener).managerStarted
                    ((UpdateManagerEvent)event);
            }
        };

    /**
     * Dispatches a UpdateManagerEvent to notify that the manager was
     * stopped.
     */
    static Dispatcher stoppedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((UpdateManagerListener)listener).managerStopped
                    ((UpdateManagerEvent)event);
            }
        };

    /**
     * Dispatches a UpdateManagerEvent to notify that the manager was
     * suspended.
     */
    static Dispatcher suspendedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((UpdateManagerListener)listener).managerSuspended
                    ((UpdateManagerEvent)event);
            }
        };

    /**
     * Dispatches a UpdateManagerEvent to notify that the manager was
     * resumed.
     */
    static Dispatcher resumedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((UpdateManagerListener)listener).managerResumed
                    ((UpdateManagerEvent)event);
            }
        };

    /**
     * Dispatches a UpdateManagerEvent to notify that an update
     * started
     */
    static Dispatcher updateStartedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((UpdateManagerListener)listener).updateStarted
                    ((UpdateManagerEvent)event);
            }
        };

    /**
     * Dispatches a UpdateManagerEvent to notify that an update
     * completed
     */
    static Dispatcher updateCompletedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((UpdateManagerListener)listener).updateCompleted
                    ((UpdateManagerEvent)event);
            }
        };

    /**
     * Dispatches a UpdateManagerEvent to notify that an update
     * failed
     */
    static Dispatcher updateFailedDispatcher = new Dispatcher() {
            public void dispatch(Object listener,
                                 Object event) {
                ((UpdateManagerListener)listener).updateFailed
                    ((UpdateManagerEvent)event);
            }
        };



    // RunnableQueue.RunHandler /////////////////////////////////////////
    protected RunnableQueue.RunHandler createRunHandler() {
        return new UpdateManagerRunHander();
    }

    protected class UpdateManagerRunHander 
        implements RunnableQueue.RunHandler {

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
                fireEvent(suspendedDispatcher, 
                          new UpdateManagerEvent(this, null, null));
            }
        }
        
        /**
         * Called when the execution of the queue has been resumed.
         */
        public void executionResumed(RunnableQueue rq) {
            if (suspendCalled && !running) {
                running = true;
                
                suspendCalled = false;
                fireEvent(resumedDispatcher, 
                          new UpdateManagerEvent(this, null, null));
            }
        }
    }
}
