/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.InterpreterPool;

import org.apache.batik.util.Lock;
import org.apache.batik.util.RunnableQueue;

import org.w3c.dom.Document;
import org.w3c.dom.events.Event;

/**
 * This class contains the informations needed by the SVG scripting.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ScriptingEnvironment {

    private final static String EVENT_NAME = "evt";

    /**
     * The scripting lock.
     */
    protected Lock scriptingLock;

    /**
     * The update manager.
     */
    protected UpdateManager updateManager;

    /**
     * The repaint manager.
     */
    protected RepaintManager repaintManager;

    /**
     * The update runnable queue.
     */
    protected RunnableQueue updateRunnableQueue;

    /**
     * The active scripting threads.
     */
    protected List scripts = Collections.synchronizedList(new LinkedList());

    /**
     * Whether the scripts must be suspended.
     */
    protected volatile boolean suspended;

    /**
     * The suspend lock.
     */
    protected Object suspendLock = new Object();

    /**
     * Creates a new ScriptingEnvironment.
     * @param um The update manager.
     */
    public ScriptingEnvironment(UpdateManager um) {
        scriptingLock = new Lock();
        updateManager = um;
        repaintManager = um.getRepaintManager();
        updateRunnableQueue = um.getUpdateRunnableQueue();
    }

    /**
     * Runs a script.
     */
    public void runScript(String script, String lang, Event evt) {
        new ScriptingThread(script, lang, evt).start();
    }

    /**
     * Suspends the scripts.
     */
    public void suspendScripts() {
        suspended = true;
    }

    /**
     * Resumes the scripts.
     */
    public void resumeScripts() {
        synchronized (suspendLock) {
            suspended = false;
            suspendLock.notify();
        }
    }

    /**
     * Begins a script evaluation section.
     */
    public void beginScript() {
        try {
            scriptingLock.lock();
        } catch (InterruptedException e) {
            throw new StopScriptException();
        }
        synchronized (suspendLock) {
            if (suspended) {
                try {
                    suspendLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        repaintManager.disable();
        if (updateRunnableQueue.getThread() == null) {
            scriptingLock.unlock();
            throw new StopScriptException();
        }
        updateRunnableQueue.suspendExecution(true);
    }

    /**
     * Ends a script evaluation section.
     */
    public void endScript() {
        synchronized (suspendLock) {
            if (suspended) {
                try {
                    suspendLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        repaintManager.enable();
        if (updateRunnableQueue.getThread() == null) {
            scriptingLock.unlock();
            throw new StopScriptException();
        }
        updateRunnableQueue.resumeExecution();
        repaintManager.repaint(true);
        scriptingLock.unlock();
    }

    /**
     * Pauses the current script for the given amount of time.
     */
    public void pauseScript(long millis) {
        long t1 = System.currentTimeMillis();
        endScript();
        long t2 = System.currentTimeMillis();
        millis -= t2 - t1;
        if (millis < 0) {
            millis = 0;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        } finally {
            beginScript();
        }
    }

    /**
     * To run a script.
     */
    protected class ScriptingThread extends Thread {
        protected String script;
        protected Event event;
        protected UserAgent userAgent;
        protected Interpreter interpreter;
        public ScriptingThread(String script, String lang, Event evt) {
            this.script = script;
            event = evt;
            BridgeContext bc = updateManager.getBridgeContext();
            userAgent = bc.getUserAgent();
            Document doc = updateManager.getDocument();
            interpreter = bc.getInterpreterPool().getInterpreter(doc, lang);
            
            if (interpreter == null) {
                if (userAgent != null) {
                    userAgent.displayError(new Exception("unknow language: "+
                                                         lang));
                }
            }
        }
        public void run() {
            if (interpreter != null) {
                beginScript();
                scripts.add(this);
            
                if (event != null) {
                    interpreter.bindObject(EVENT_NAME, event);
                }
                try {
                    interpreter.evaluate(script);
                } catch (InterpreterException ie) {
                    Exception ex = ie.getException();
                    if (ex instanceof StopScriptException) {
                        scripts.remove(this);
                        return;
                    }
                    if (userAgent != null) {
                        userAgent.displayError((ex != null) ? ex : ie);
                    }
                }
                scripts.remove(this);
                endScript();
            }
        }
    }

    protected static class StopScriptException
        extends RuntimeException {
        public StopScriptException() {
        }
    }
}
