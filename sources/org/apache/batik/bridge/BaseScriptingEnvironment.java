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

import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import java.util.jar.Manifest;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XMLBaseSupport;

import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;
import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.ScriptHandler;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import org.w3c.dom.svg.SVGSVGElement;

/**
 * This class is the base class for SVG scripting.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class BaseScriptingEnvironment {

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
    

    protected final static String EVENT_NAME = "event";
    protected final static String ALTERNATE_EVENT_NAME = "evt";

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
     * Creates a new BaseScriptingEnvironment.
     * @param ctx the bridge context
     */
    public BaseScriptingEnvironment(BridgeContext ctx) {
        bridgeContext = ctx;
        document = ctx.getDocument();
        userAgent     = bridgeContext.getUserAgent();
    }

    /**
     * Creates a new Window object.
     */
    public org.apache.batik.script.Window createWindow
        (Interpreter interp, String lang) {
        return new Window(interp, lang);
    }

    /**
     * Creates a new Window object.
     */
    public org.apache.batik.script.Window createWindow() {
        return createWindow(null, null);
    }

    /**
     * Initializes the environment of the given interpreter.
     */
    public void initializeEnvironment(Interpreter interp, String lang) {
        interp.bindObject("window", createWindow(interp, lang));
    }

    /**
     * Loads the scripts contained in the <script> elements.
     */
    public void loadScripts() {
        org.apache.batik.script.Window window = null;

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

            if (type.length() == 0) {
                type = "text/ecmascript";
            }

            //
            // Java code invocation.
            //
            if (type.equals("application/java-archive")) {
                try {
                    String href = XLinkSupport.getXLinkHref(script);
                    URL url;
                    url = new URL(XMLBaseSupport.getCascadedXMLBase(script));
                    url = new URL(url, href);
                    URLClassLoader cll = new URLClassLoader(new URL[] { url });

                    // Get the 'Script-Handler' entry in the manifest.
                    url = cll.findResource("META-INF/MANIFEST.MF");
                    if (url == null) {
                        continue;
                    }
                    Manifest man = new Manifest(url.openStream());
                    String sh;
                    sh = man.getMainAttributes().getValue("Script-Handler");
                    if (sh == null) {
                        continue;
                    }

                    // Run the script handler.
                    ScriptHandler h;
                    h = (ScriptHandler)cll.loadClass(sh).newInstance();

                    if (window == null) {
                        window = createWindow();
                    }

                    h.run(document, window);
                    
                } catch (Exception e) {
                    if (userAgent != null) {
                        userAgent.displayError(e);
                    }
                }
                continue;
            }

            //
            // Scripting language invocation.
            //
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
                    URL url;
                    url = new URL(XMLBaseSupport.getCascadedXMLBase(script));
                    url = new URL(url, href);
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
            l = new EventListener() {
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
         * Creates a new Window.
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
            return null;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setInterval(Runnable,long)}.
         */
        public Object setInterval(final Runnable r, long interval) {
            return null;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#clearInterval(Object)}.
         */
        public void clearInterval(Object interval) {
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setTimeout(String,long)}.
         */
        public Object setTimeout(final String script, long timeout) {
            return null;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setTimeout(Runnable,long)}.
         */
        public Object setTimeout(final Runnable r, long timeout) {
            return null;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#clearTimeout(Object)}.
         */
        public void clearTimeout(Object timeout) {
        }

        /**
         * Parses the given XML string into a DocumentFragment of the
         * given document.
         * @return The document fragment or null on error.
         */
        public DocumentFragment parseXML(String text, Document doc) {
            return null;
        }

        /**
         * Gets data from the given URI.
         * @param uri The URI where the data is located.
         * @param h A handler called when the data is available.
         */
        public void getURL(String uri, GetURLHandler h) {
        }

        /**
         * Gets data from the given URI.
         * @param uri The URI where the data is located.
         * @param h A handler called when the data is available.
         * @param enc The character encoding of the data.
         */
        public void getURL(String uri, GetURLHandler h, String enc) {
        }

        /**
         * Displays an alert dialog box.
         */
        public void alert(String message) {
        }

        /**
         * Displays a confirm dialog box.
         */
        public boolean confirm(String message) {
            return false;
        }

        /**
         * Displays an input dialog box.
         */
        public String prompt(String message) {
            return null;
        }

        /**
         * Displays an input dialog box, given the default value.
         */
        public String prompt(String message, String defVal) {
            return null;
        }

        /**
         * Returns the current BridgeContext.
         */
        public BridgeContext getBridgeContext() {
            return bridgeContext;
        }

        /**
         * Returns the associated interpreter.
         */
        public Interpreter getInterpreter() {
            return interpreter;
        }

    }
}
