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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.InterpreterPool;

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
    

    private final static String EVENT_NAME = "event";
    private final static String ALTERNATE_EVENT_NAME = "evt";

    /**
     * The timer for periodic or delayed tasks.
     */
    Timer timer = new Timer(true);

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
     * The bridge context.
     */
    protected BridgeContext bridgeContext;

    /**
     * The user-agent.
     */
    protected UserAgent userAgent;
    
    /**
     * The document to manage.
     */
    protected Document document;

    /**
     * Creates a new ScriptingEnvironment.
     * @param um The update manager.
     */
    public ScriptingEnvironment(UpdateManager um) {
        updateManager = um;
        bridgeContext = updateManager.getBridgeContext();
        userAgent     = bridgeContext.getUserAgent();
        document      = updateManager.getDocument();
        updateRunnableQueue = um.getUpdateRunnableQueue();
    }

    /**
     * Creates a new Window object.
     */
    public Window createWindow(Interpreter interp, String lang) {
        return new Window(interp, lang);
    }

    /**
     * Creates a new Window object.
     */
    public Window createWindow() {
        return new Window(null, null);
    }

    /**
     * Initializes the environment of the given interpreter.
     */
    public void initializeEnvironment(Interpreter interp, String lang) {
        interp.bindObject("window", new Window(interp, lang));
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
                if (userAgent != null) {
                    userAgent.displayError(e);
                }
                return;
            } catch (InterpreterException e) {
                handleInterpreterException(e);
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

        Event ev;
        DocumentEvent de = (DocumentEvent)elt.getOwnerDocument();
        ev = de.createEvent("SVGEvents");
        ev.initEvent("SVGLoad", false, false);
        EventTarget t = (EventTarget)elt;

        final String s =
            elt.getAttributeNS(null, SVGConstants.SVG_ONLOAD_ATTRIBUTE);
        EventListener l = null;
        if (s.length() > 0) {
            l = new UnwrappedEventListener() {
                    public void handleEvent(Event evt) {
                        try {
                            interp.bindObject(EVENT_NAME, evt);
                            interp.bindObject(ALTERNATE_EVENT_NAME, evt);
                            interp.evaluate(new StringReader(s));
                        } catch (IOException io) {
                        } catch (InterpreterException e) {
                            handleInterpreterException(e);
                        }
                    }
                };
            t.addEventListener("SVGLoad", l, false);
        }
        t.dispatchEvent(ev);
        if (s.length() > 0) {
            t.removeEventListener("SVGLoad", l, false);
        }
    }

    /**
     * Handles the given exception.
     */
    protected void handleInterpreterException(InterpreterException ie) {
        if (userAgent != null) {
            Exception ex = ie.getException();
            userAgent.displayError((ex == null) ? ie : ex);
        }
    }

    /**
     * To wrap an event listener.
     */
    protected class EventListenerWrapper implements UnwrappedEventListener {

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
    public class Window implements org.apache.batik.script.Window {

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
            javax.swing.JOptionPane.showMessageDialog
                (null, "Script alert:\n" + message);
        }

        /**
         * Displays a confirm dialog box.
         */
        public boolean confirm(String message) {
            return javax.swing.JOptionPane.showConfirmDialog
                    (null, "Script confirm:\n" + message,
                     "Confirm",javax.swing.JOptionPane.YES_NO_OPTION) ==
                    javax.swing.JOptionPane.YES_OPTION;
        }

        /**
         * Displays an input dialog box.
         */
        public String prompt(String message) {
            return javax.swing.JOptionPane.showInputDialog
                ("Script prompt:\n" + message);
        }

        /**
         * Displays an input dialog box, given the default value.
         */
        public String prompt(String message, String defVal) {
            return (String)javax.swing.JOptionPane.showInputDialog
                (null,
                 "Script prompt:\n" + message,
                 "Prompt",
                 javax.swing.JOptionPane.PLAIN_MESSAGE,
                 null, null, defVal);
        }

        /**
         * Returns the associated interpreter.
         */
        public Interpreter getInterpreter() {
            return interpreter;
        }
    }
}
