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
import org.apache.batik.script.JavaFunction;

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
    private final static String ARG_NAME = "arg__";

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
     * Whether the scripts must be suspended.
     */
    protected volatile boolean suspended;

    /**
     * The suspend lock.
     */
    protected Object suspendLock = new Object();

    /**
     * The alert function.
     */
    protected JavaFunction alertFunction;

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
     * Sets the environment of the given interpreter.
     */
    public void setEnvironment(Interpreter interp, String lang) {
        interp.bindObject("alert", createAlertFunction());
        interp.bindObject("setTimeout", createSetTimeoutFunction(lang));
    }

    private JavaFunction createAlertFunction() {
        if (alertFunction == null) {
            alertFunction = new JavaFunction() {
                    public Class[] getParameterTypes() {
                        return new Class[] { String.class };
                    }
                    public Object call(Object[] arguments) {
                        javax.swing.JOptionPane.showMessageDialog
                            (null, (String)arguments[0]);
                        return null;
                    }
                };
        }
        return alertFunction;
    }


    private final static Class[] parameters = { String.class, Long.TYPE };
    private JavaFunction createSetTimeoutFunction(final String lang) {
        return new JavaFunction() {
                public Class[] getParameterTypes() {
                    return parameters;
                }
                public Object call(Object[] args) {
                    pauseScript(((Long)args[1]).longValue());
                    Object[] fargs = new Object[args.length - 2];
                    for (int i = 0; i < fargs.length; i++) {
                        fargs[i] = args[i + 2];
                    }
                    runFunction((String)args[0], fargs, lang);
                    return null;
                }
            };
    }

    /**
     * Runs an event handler.
     */
    public void runEventHandler(String script, Event evt, String lang) {
        new EventHandlerThread(script, evt, lang).start();
    }

    /**
     * Runs a function.
     */
    public void runFunction(String function, Object[] args, String lang) {
        new FunctionCallThread(function, args, lang).start();
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
     * To run a piece of script.
     */
    protected abstract class ScriptingThread extends Thread {

        protected UserAgent userAgent;
        protected Interpreter interpreter;

        /**
         * Creates a new scripting thread.
         */
        public ScriptingThread(String lang) {
            BridgeContext bc = updateManager.getBridgeContext();
            userAgent = bc.getUserAgent();
            Document doc = updateManager.getDocument();
            interpreter = bc.getInterpreter(lang);
            if (interpreter == null) {
                if (userAgent != null) {
                    userAgent.displayError
                        (new Exception("unknow language: " + lang));
                }
            }
        }

        /**
         * The main method.
         */
        public void run() {
            if (interpreter != null) {
                beginScript();

                try {
                    interpreter.evaluate(getScript());
                } catch (InterpreterException ie) {
                    Exception ex = ie.getException();
                    if (ex instanceof StopScriptException) {
                        return;
                    }
                    if (userAgent != null) {
                        userAgent.displayError((ex != null) ? ex : ie);
                    }
                } finally {
                    try {
                        endScript();
                    } catch (StopScriptException e) {
                    }
                }
            }
        }

        /**
         * Returns the script to execute.
         */
        protected abstract String getScript();

    }

    /**
     * To run a function.
     */
    protected class FunctionCallThread extends ScriptingThread {
        protected String function;
        protected Object[] arguments;

        /**
         * Creates a new FunctionCallThread.
         */
        public FunctionCallThread(String fname, Object[] args, String lang) {
            super(lang);
            function = fname;
            arguments = args;
        }

        /**
         * Returns the script to execute.
         */
        protected String getScript() {
            if (function.endsWith("()")) {
                function = function.substring(0, function.length() - 2);
            }
            StringBuffer sb = new StringBuffer(function);

            sb.append("(");
            if (arguments.length > 0) {
                String s = ARG_NAME + 0;
                sb.append(s);
                interpreter.bindObject(s, arguments[0]);
                for (int i = 1; i < arguments.length; i++) {
                    s = ARG_NAME + i;
                    sb.append(",");
                    sb.append(s);
                    interpreter.bindObject(s, arguments[i]);
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }

    /**
     * To run an event handler.
     */
    protected class EventHandlerThread extends ScriptingThread {
        protected String script;
        protected Event event;

        /**
         * Creates a new EventHandlerThread.
         */
        public EventHandlerThread(String script, Event evt, String lang) {
            super(lang);
            this.script = script;
            event = evt;
        }

        /**
         * Returns the script to execute.
         */
        protected String getScript() {
            interpreter.bindObject(EVENT_NAME, event);
            return script;
        }
    }

    protected static class StopScriptException
        extends RuntimeException {
        public StopScriptException() {
        }
    }
}
