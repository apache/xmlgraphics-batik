/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.DocumentEvent;

import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

import org.apache.batik.gvt.GraphicsNode;

import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.script.InterpreterException;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.UserAgent;

import java.io.StringReader;
import java.io.IOException;

import java.awt.geom.Point2D;

/**
 * A class to attach listeners on the <code>Document</code> to
 * call pieces of script when necessary and to attach a listener
 * on the GVT root to propagate GVT events to the DOM.
 * @author <a href="mailto:cjolif@ilog.fr>Christophe Jolif</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
class BridgeEventSupport {
    private static String[] EVENT_ATTRIBUTES = {
        // all
        "onfocusin",
        "onfocusout",
        "onactivate",
        "onclick",
        "onmousedown",
        "onmouseup",
        "onmouseover",
        "onmouseout",
        "onmousemove",
        "onload",

        // document
        "onunload",
        "onabort",
        "onerror",
        "onresize",
        "onscroll",
        "onzoom",

        // animation
        "onbegin",
        "onend",
        "onrepeat"
    };

    private static String[] EVENT_NAMES = {
        // all
        "focusin",
        "focusout",
        "activate",
        "click",
        "mousedown",
        "mouseup",
        "mouseover",
        "mouseout",
        "mousemove",
        "SVGLoad",

        // document
        "SVGUnload",
        "SVGAbort",
        "SVGError",
        "SVGResize",
        "SVGScroll",
        "SVGZoom",

        // animation
        "beginEvent",
        "endEvent",
        "repeatEvent"
    };

    private BridgeEventSupport() {}

    /**
     * Creates and add a listener on the element to call script
     * when necessary.
     * @param ctx the <code>BridgeContext</code> containing useful
     * information.
     * @param element the DOM element corresponding to the node. It should
     * also be an event target.
     * @param node the <code>GraphicsNode</code>.
     */
    public static void addDOMListener(BridgeContext ctx,
                                      Element element) {
        // ability for scripts to be called
        EventTarget target = (EventTarget)element;
        String script = null;
        // <!> HACK (the cast) should be modified : call the method
        // with SVGElement's only
        SVGSVGElement svgElement = (SVGSVGElement)
            ((SVGElement)element).getOwnerSVGElement();
        if (svgElement == null) {
            if (element instanceof SVGSVGElement) {
                svgElement = (SVGSVGElement)element;
            } else {
                // disable scripting
                return;
            }
        }
        String language = svgElement.getContentScriptType();
        Interpreter interpret =
            ctx.getInterpreterPool().
            getInterpreter(language);
        if (interpret != null) {
            // <!> TODO we need to memo listeners to be able to remove
            // them later.
            // <!> TODO be smarter : don't look for doc attr on other
            // elements.
            for (int i = 0; i < EVENT_ATTRIBUTES.length; i++) {
                if (!(script = element.getAttribute(EVENT_ATTRIBUTES[i])).
                    equals("")) {
                    target.
                        addEventListener(EVENT_NAMES[i],
                                         new ScriptCaller(ctx.getUserAgent(),
                                                          script, interpret),
                                         false);
                }
            }
        } else {
            UserAgent ua = ctx.getUserAgent();
            if (ua != null)
                ua.displayError("unknow language: "+language);
        }
    }

    public static void updateDOMListener(BridgeContext ctx,
                                         Element element) {
    }

    public static void addGVTListener(BridgeContext ctx, Element svgRoot) {
        UserAgent ua = ctx.getUserAgent();
        if (ua != null) {
            EventDispatcher dispatcher = ua.getEventDispatcher();
            if (dispatcher != null) {
                final Listener listener = new Listener(ctx);
                dispatcher.addGlobalGraphicsNodeMouseListener(listener);
                ((EventTarget)svgRoot).
                    addEventListener("SVGUnload",
                                     new UnloadListener(dispatcher, listener),
                                     false);
            }
        }
    }

    public static void loadScripts(BridgeContext ctx, Document doc) {
        NodeList list = doc.getElementsByTagName("script");
        final UserAgent ua = ctx.getUserAgent();
        String language = null;
        Element selement = null;
        for (int i = 0; i < list.getLength(); i++) {
            language = (selement = (Element)list.item(i)).
                getAttribute("type");
            final Interpreter interpret =
                ctx.getInterpreterPool().
                getInterpreter(language);
            if (interpret != null) {
                final StringBuffer script = new StringBuffer();
                for (Node n = selement.getFirstChild(); n != null;
                     n = n.getNextSibling()) {
                    script.append(n.getNodeValue());
                }
                try {
                    interpret.evaluate
                        (new StringReader(script.toString()));
                } catch (IOException io) {
                    // will never appeared we don't use a file
                } catch (InterpreterException e) {
                    if (ua != null)
                        ua.displayError("scripting error: " +
                                        e.getMessage());
                }
            } else
                if (ua != null)
                    ua.displayError("unknown language: "+language);
        }
    }

    private static class UnloadListener
        implements EventListener {
        private EventDispatcher dispatcher;
        private Listener listener;
        UnloadListener(EventDispatcher dispatcher, Listener listener)
        {
            this.dispatcher = dispatcher;
            this.listener = listener;
        }
        public void handleEvent(Event evt) {
            dispatcher.removeGlobalGraphicsNodeMouseListener(listener);
            evt.getTarget().removeEventListener("SVGUnload", this, false);
        }
    }

    private static class Listener
        implements GraphicsNodeMouseListener {
        private BridgeContext context;
        public Listener(BridgeContext ctx) {
            context = ctx;
        }
        public void mouseClicked(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("click", evt, true);
        }
        public void mousePressed(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousedown", evt, true);
        }
        public void mouseReleased(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mouseup", evt, true);
        }
        public void mouseEntered(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mouseover", evt, true);
        }
        public void mouseExited(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mouseout", evt, true);
        }
        public void mouseDragged(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousemove", evt, true);
        }
        public void mouseMoved(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousemove", evt, false);
        }
        private void dispatchMouseEvent(String eventType,
                                        GraphicsNodeMouseEvent evt,
                                        boolean cancelok) {
            Point2D pos = evt.getPoint2D();
            GraphicsNode node = evt.getGraphicsNode();
            Element elmt = context.getElement(node);
            if (elmt == null) // should not appeared if binding on
                return;
            EventTarget target = (EventTarget)elmt;
            short button = 1;
            if ((evt.BUTTON1_MASK & evt.getModifiers()) != 0)
                button = 0;
            else
                if ((evt.BUTTON3_MASK & evt.getModifiers()) != 0)
                    button = 2;
            // <!> TODO some stuff to transform pos
            MouseEvent mevent =
                // DOM Level 2 6.5 cast form Document to DocumentEvent is ok
                (MouseEvent)org.apache.batik.dom.events.EventSupport.
                createEvent(org.apache.batik.dom.events.EventSupport.
                            MOUSE_EVENT_TYPE);
            mevent.initMouseEvent(eventType, true, cancelok, null,
                                  evt.getClickCount(), 0, 0,
                                  (int)Math.floor(pos.getX()),
                                  (int)Math.floor(pos.getY()),
                                  evt.isControlDown(), evt.isAltDown(),
                                  evt.isShiftDown(), evt.isMetaDown(),
                                  button, target);
            target.dispatchEvent(mevent);
        }
    }


    public static class ScriptCaller implements EventListener {
        private static String EVENT_NAME = "evt";

        private String script = null;
        private Interpreter interpreter = null;
        private UserAgent ua = null;

        public ScriptCaller(UserAgent agent,
                            String str, Interpreter interpret) {
            script = str;
            interpreter = interpret;
            ua = agent;
        }

        public void handleEvent(Event evt) {
            interpreter.bindObject(EVENT_NAME, evt);
            try {
                interpreter.evaluate(new StringReader(script));
            } catch (IOException io) {
                // will never appeared we don't use a file
            } catch (InterpreterException e) {
                if (ua != null)
                    ua.displayError("scripting error: " +
                                    e.getMessage());
            }
        }
    }
}

