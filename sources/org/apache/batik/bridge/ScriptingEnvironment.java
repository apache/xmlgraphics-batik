/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;

import org.apache.batik.util.RunnableQueue;

import org.w3c.dom.Document;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

/**
 * This class contains the informations needed by the SVG scripting.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ScriptingEnvironment extends BaseScriptingEnvironment {

    /**
     * The timer for periodic or delayed tasks.
     */
    protected Timer timer = new Timer(true);

    /**
     * The update manager.
     */
    protected UpdateManager updateManager;

    /**
     * The update runnable queue.
     */
    protected RunnableQueue updateRunnableQueue;

    /**
     * Creates a new ScriptingEnvironment.
     * @param ctx the bridge context
     */
    public ScriptingEnvironment(BridgeContext ctx) {
        super(ctx);
        updateManager = ctx.getUpdateManager();
        updateRunnableQueue = updateManager.getUpdateRunnableQueue();
    }

    /**
     * Creates a new Window object.
     */
    public org.apache.batik.script.Window createWindow
        (Interpreter interp, String lang) {
        return new Window(interp, lang);
    }

    /**
     * Runs an event handler.
     */
    public void runEventHandler(String script, Event evt, String lang) {
        Interpreter interpreter = bridgeContext.getInterpreter(lang);
        if (interpreter == null) {
            if (userAgent != null) {
                userAgent.displayError
                    (new Exception("unknow language: " + lang));
            }
        }


        interpreter.bindObject(EVENT_NAME, evt);
        interpreter.bindObject(ALTERNATE_EVENT_NAME, evt);
            
        try {
            interpreter.evaluate(script);
        } catch (InterpreterException ie) {
            handleInterpreterException(ie);
        }
    }

    /**
     * Interrupts the periodic tasks.
     */
    public void interrupt() {
        timer.cancel();
    }

    /**
     * To wrap an event listener.
     */
    protected class EventListenerWrapper implements EventListener {

        /**
         * The wrapped event listener.
         */
        protected EventListener eventListener;

        /**
         * Creates a new EventListenerWrapper.
         */
        public EventListenerWrapper(EventListener el) {
            eventListener = el;
        }

        public void handleEvent(final Event evt) {
            eventListener.handleEvent(evt);
        }
    }

    /**
     * To interpret a script.
     */
    protected class EvaluateRunnable implements Runnable {
        protected Interpreter interpreter;
        protected String script;
        public EvaluateRunnable(String s, Interpreter interp) {
            interpreter = interp;
            script = s;
        }
        public void run() {
            try {
                interpreter.evaluate(script);
            } catch (InterpreterException ie) {
                handleInterpreterException(ie);
            }
        }
    }

    /**
     * To interpret a script.
     */
    protected class EvaluateIntervalRunnable implements Runnable {
        /**
         * Incremented each time this runnable is added to the queue.
         */
        public int count;

        protected Interpreter interpreter;
        protected String script;

        public EvaluateIntervalRunnable(String s, Interpreter interp) {
            interpreter = interp;
            script = s;
        }
        public void run() {
            count--;
            try {
                interpreter.evaluate(script);
            } catch (InterpreterException ie) {
                handleInterpreterException(ie);
            }
        }
    }

    /**
     * To call a Runnable.
     */
    protected class EvaluateRunnableRunnable implements Runnable {
        /**
         * Incremented each time this runnable is put in the queue.
         */
        public int count;

        protected Runnable runnable;

        public EvaluateRunnableRunnable(Runnable r) {
            runnable = r;
        }
        public void run() {
            count--;
            try {
                runnable.run();
            } catch (Exception e) {
                if (userAgent != null) {
                    userAgent.displayError(e);
                }
            }
        }
    }

    /**
     * Represents the window object of this environment.
     */
    protected class Window implements org.apache.batik.script.Window {

        /**
         * The associated interpreter.
         */
        protected Interpreter interpreter;

        /**
         * The associated language.
         */
        protected String language;

        /**
         * Creates a new Window for the given language.
         */
        public Window(Interpreter interp, String lang) {
            interpreter = interp;
            language = lang;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setInterval(String,long)}.
         */
        public Object setInterval(final String script, long interval) {
            TimerTask tt = new TimerTask() {
                    EvaluateIntervalRunnable eir =
                        new EvaluateIntervalRunnable(script, interpreter);
                    public void run() {
                        if (eir.count > 1) {
                            return;
                        }
                        eir.count++;
                        updateRunnableQueue.invokeLater(eir);
                    }
                };

            timer.schedule(tt, interval, interval);
            return tt;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setInterval(Runnable,long)}.
         */
        public Object setInterval(final Runnable r, long interval) {
            TimerTask tt = new TimerTask() {
                    EvaluateRunnableRunnable eihr =
                        new EvaluateRunnableRunnable(r);
                    public void run() {
                        if (eihr.count > 1) {
                            return;
                        }
                        eihr.count++;
                        updateRunnableQueue.invokeLater(eihr);
                    }
                };

            timer.schedule(tt, interval, interval);
            return tt;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#clearInterval(Object)}.
         */
        public void clearInterval(Object interval) {
            ((TimerTask)interval).cancel();
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setTimeout(String,long)}.
         */
        public Object setTimeout(final String script, long timeout) {
            TimerTask tt = new TimerTask() {
                    public void run() {
                        updateRunnableQueue.invokeLater
                            (new EvaluateRunnable(script, interpreter));
                    }
                };

            timer.schedule(tt, timeout);
            return tt;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setTimeout(Runnable,long)}.
         */
        public Object setTimeout(final Runnable r, long timeout) {
            TimerTask tt = new TimerTask() {
                    public void run() {
                        updateRunnableQueue.invokeLater(new Runnable() {
                                public void run() {
                                    try {
                                        r.run();
                                    } catch (Exception e) {
                                        if (userAgent != null) {
                                            userAgent.displayError(e);
                                        }
                                    }
                                }
                            });
                    }
                };

            timer.schedule(tt, timeout);
            return tt;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#clearTimeout(Object)}.
         */
        public void clearTimeout(Object timeout) {
            ((TimerTask)timeout).cancel();
        }

        /**
         * Displays an alert dialog box.
         */
        public void alert(String message) {
            if (userAgent != null) {
                userAgent.showAlert(message);
            }
        }

        /**
         * Displays a confirm dialog box.
         */
        public boolean confirm(String message) {
            if (userAgent != null) {
                return userAgent.showConfirm(message);
            }
            return false;
        }

        /**
         * Displays an input dialog box.
         */
        public String prompt(String message) {
            if (userAgent != null) {
                return userAgent.showPrompt(message);
            }
            return null;
        }

        /**
         * Displays an input dialog box, given the default value.
         */
        public String prompt(String message, String defVal) {
            if (userAgent != null) {
                return userAgent.showPrompt(message, defVal);
            }
            return null;
        }

        /**
         * Returns the associated interpreter.
         */
        public Interpreter getInterpreter() {
            return interpreter;
        }
    }
}
