/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.script.JavaFunction;

import org.apache.batik.util.Lock;
import org.apache.batik.util.RunnableQueue;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class contains the informations needed by the SVG scripting.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ScriptingEnvironment {

    /**
     * Tells whether the given SVG document is dynamic.
     */
    public static boolean isDynamicDocument(Document doc) {
        Element elt = doc.getDocumentElement();
        if (elt.getNamespaceURI().equals(SVGConstants.SVG_NAMESPACE_URI)) {
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONABORT_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONERROR_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONRESIZE_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONUNLOAD_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONSCROLL_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONZOOM_ATTRIBUTE).length() > 0) {
                return true;
            }
            return isDynamicElement(doc.getDocumentElement());
        }
        return false;
    }
    
    /**
     * Tells whether the given SVG element is dynamic.
     */
    public static boolean isDynamicElement(Element elt) {
        if (elt.getNamespaceURI().equals(SVGConstants.SVG_NAMESPACE_URI)) {
            String name = elt.getLocalName();
            if (name.equals(SVGConstants.SVG_SCRIPT_TAG)) {
                return true;
            }
            if (name.startsWith("animate") || name.equals("set")) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONERROR_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONACTIVATE_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONCLICK_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONFOCUSIN_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONFOCUSOUT_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEDOWN_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEMOVE_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEOUT_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEOVER_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEUP_ATTRIBUTE).length() > 0) {
                return true;
            }
        
            for (Node n = elt.getFirstChild();
                 n != null;
                 n = n.getNextSibling()) {
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (isDynamicElement((Element)n)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    

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
     * The bridge context.
     */
    protected BridgeContext bridgeContext;
    
    /**
     * The suspend lock.
     */
    protected Object suspendLock = new Object();

    /**
     * The document to manage.
     */
    protected Document document;

    /**
     * The alert function.
     */
    protected JavaFunction alertFunction;

    /**
     * The live scripting threads.
     */
    protected List liveThreads =
        Collections.synchronizedList(new LinkedList());

    /**
     * Whether this environment has been interrupted.
     */
    protected boolean interrupted;

    /**
     * Creates a new ScriptingEnvironment.
     * @param um The update manager.
     */
    public ScriptingEnvironment(UpdateManager um) {
        scriptingLock = new Lock();
        updateManager = um;
        bridgeContext = updateManager.getBridgeContext();
        document = updateManager.getDocument();
    }

    /**
     * Initializes the environment of the given interpreter.
     */
    public void initializeEnvironment(Interpreter interp, String lang) {
        interp.bindObject("alert", getAlertFunction());
        interp.bindObject("setTimeout", getSetTimeoutFunction(lang));
    }

    /**
     * Returns the scripting lock.
     */
    public Lock getScriptingLock() {
        return scriptingLock;
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
    public void runFunction(String function,
                            Object[] args,
                            String lang,
                            long delay) {
        new FunctionCallThread(function, args, lang, delay).start();
    }

    /**
     * Interrupts the scripts.
     */
    public void interruptScripts() {
        synchronized (liveThreads) {
            interrupted = true;
            Iterator it = liveThreads.iterator();
            while (it.hasNext()) {
                scriptingLock.unlock();
                ((Thread)it.next()).interrupt();
            }
        }
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
            suspendLock.notifyAll();
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
                    throw new StopScriptException();
                }
            }
        }

        if (repaintManager == null) {
            repaintManager = updateManager.getRepaintManager();
            if (repaintManager == null) {
                throw new StopScriptException();
            }
            updateRunnableQueue = updateManager.getUpdateRunnableQueue();
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
                    throw new StopScriptException();
                }
            }
        }
        repaintManager.enable();
        if (updateRunnableQueue.getThread() == null) {
            scriptingLock.unlock();
            throw new StopScriptException();
        }
        updateRunnableQueue.resumeExecution();
    
        try {
            repaintManager.repaint(true);
        } catch (InterruptedException e) {
            throw new StopScriptException();
        }
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
     * Loads the scripts contained in the <script> elements.
     */
    public void loadScripts() {
        NodeList scripts = document.getElementsByTagNameNS
            (SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_SCRIPT_TAG);
        int len = scripts.getLength();

        if (len == 0) {
            return;
        }

        Set languages = new HashSet();

        for (int i = 0; i < len; i++) {
            Element script = (Element)scripts.item(i);
            String type = script.getAttributeNS
                (null, SVGConstants.SVG_TYPE_ATTRIBUTE);
            Interpreter interpreter = bridgeContext.getInterpreter(type);

            if (interpreter == null) {
                UserAgent ua = bridgeContext.getUserAgent();
                if (ua != null) {
                    ua.displayError(new Exception("Unknown language: "+type));
                }
                return;
            }

            if (!languages.contains(type)) {
                languages.add(type);
                initializeEnvironment(interpreter, type);
            }

            try {
                String href = XLinkSupport.getXLinkHref(script);
                Reader reader;
                if (href.length() > 0) {
                    // External script.
                    URL url = new URL(((SVGOMDocument)document).getURLObject(),
                                      href);
                    reader = new InputStreamReader(url.openStream());
                } else {
                    // Inline script.
                    Node n = script.getFirstChild();
                    if (n != null) {
                        StringBuffer sb = new StringBuffer();
                        while (n != null) {
                            sb.append(n.getNodeValue());
                            n = n.getNextSibling();
                        }
                        reader = new StringReader(sb.toString());
                    } else {
                        continue;
                    }
                }

                interpreter.evaluate(reader);

            } catch (IOException e) {
                UserAgent ua = bridgeContext.getUserAgent();
                if (ua != null) {
                    ua.displayError(e);
                }
            } catch (InterpreterException e) {
                UserAgent ua = bridgeContext.getUserAgent();
                if (ua != null) {
                    Exception ex = e.getException();
                    ua.displayError((ex == null) ? e : ex);
                }
                return;
            }
        }
    }

    /**
     * Recursively dispatch the SVG 'onload' event.
     */
    public void dispatchSVGLoadEvent() {
        SVGSVGElement root =
            (SVGSVGElement)document.getDocumentElement();
        String lang = root.getContentScriptType();
        Interpreter interp = bridgeContext.getInterpreter(lang);
        if (interp == null) {
            UserAgent ua = bridgeContext.getUserAgent();
            if (ua != null) {
                ua.displayError(new Exception("Unknown language: " + lang));
            }
            return;
        }
        dispatchSVGLoad(root, interp);
    }

    /**
     * Auxiliary method for dispatchSVGLoad.
     */
    protected void dispatchSVGLoad(Element elt, final Interpreter interp) {
        for (Node n = elt.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            if (n.getNodeType() == n.ELEMENT_NODE) {
                dispatchSVGLoad((Element)n, interp);
            }
        }

        final String s =
            elt.getAttributeNS(null, SVGConstants.SVG_ONLOAD_ATTRIBUTE);
        if (s.length() > 0) {
            Event ev;
            DocumentEvent de = (DocumentEvent)elt.getOwnerDocument();
            ev = de.createEvent("SVGEvents");
            ev.initEvent("SVGLoad", false, false);
            EventTarget t = (EventTarget)elt;
            EventListener l = new EventListener() {
                    public void handleEvent(Event evt) {
                        try {
                            interp.bindObject(EVENT_NAME, evt);
                            interp.evaluate(new StringReader(s));
                        } catch (IOException io) {
                        } catch (InterpreterException e) {
                            UserAgent ua = bridgeContext.getUserAgent();
                            if (ua != null) {
                                Exception ex = e.getException();
                                ua.displayError((ex == null) ? e : ex);
                            }
                        }
                    }
                };
            t.addEventListener("SVGLoad", l, false);
            t.dispatchEvent(ev);
            t.removeEventListener("SVGLoad", l, false);
        }
    }

    /**
     * Returns the 'alert' function.
     */
    protected JavaFunction getAlertFunction() {
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


    protected final static Class[] ST_PARAMS = { String.class, Long.TYPE };

    /**
     * Returns the 'setTimeout' function.
     */
    protected JavaFunction getSetTimeoutFunction(final String lang) {
        return new JavaFunction() {
                public Class[] getParameterTypes() {
                    return ST_PARAMS;
                }
                public Object call(Object[] args) {
                    Object[] fargs = new Object[args.length - 2];
                    for (int i = 0; i < fargs.length; i++) {
                        fargs[i] = args[i + 2];
                    }
                    runFunction((String)args[0], fargs, lang,
                                ((Long)args[1]).longValue());
                    return null;
                }
            };
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
            try {
                liveThreads.add(this);
                if (interrupted) {
                    return;
                }
                if (interpreter != null) {
                    try {
                        beginScript();
                    } catch (StopScriptException e) {
                        return;
                    }
                    
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
                    }
                    try {
                        endScript();
                    } catch (StopScriptException e) {
                    }
                }
            } finally {
                liveThreads.remove(this);
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
        protected long delay;

        /**
         * Creates a new FunctionCallThread.
         */
        public FunctionCallThread(String fname,
                                  Object[] args,
                                  String lang,
                                  long delay) {
            super(lang);
            function = fname;
            arguments = args;
            this.delay = delay;
        }

        /**
         * The main method.
         */
        public void run() {
            if (delay > 0) {
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
            }
            super.run();
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
