/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.StringReader;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

    // <!> FIXME: TO BE REMOVED
    // <!> FIXME: TO BE REMOVED
    // <!> FIXME: TO BE REMOVED
    // <!> FIXME: TO BE REMOVED
    // <!> FIXME: TO BE REMOVED

/**
 * A class to attach listeners on the <code>Document</code> to
 * call pieces of script when necessary and to attach a listener
 * on the GVT root to propagate GVT events to the DOM.
 * @author <a href="mailto:cjolif@ilog.fr>Christophe Jolif</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
class BridgeEventSupport implements SVGConstants {
    private static final String[] EVENT_ATTRIBUTES_GRAPHICS = {
        // graphics + svg
        "onfocusin",
        "onfocusout",
        "onactivate",
        "onclick",
        "onmousedown",
        "onmouseup",
        "onmouseover",
        "onmouseout",
        "onmousemove",
        "onload"
    };

    private static final int FIRST_SVG_EVENT = 10;

    private static final String[] EVENT_ATTRIBUTES_SVG = {
        // document
        "onunload",
        "onabort",
        "onerror",
        "onresize",
        "onscroll",
        "onzoom"
    };

    private static final int FIRST_ANIMATION_EVENT = 16;

    private static final String[] EVENT_ATTRIBUTES_ANIMATION = {
        // animation
        "onbegin",
        "onend",
        "onrepeat"
    };

    private static final String[] EVENT_NAMES = {
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
     * @param element the DOM SVGElement corresponding to the node. It should
     * also be an instance of <code>EventTarget</code> otherwise no listener
     * will be added.
     * @param node the <code>GraphicsNode</code>.
     */
    public static void addDOMListener(BridgeContext ctx,
                                      Element eee) {
        SVGElement element = null;
        EventTarget target = null;
        try {
            element = (SVGElement)eee;
            // ability for scripts to be called
            target = (EventTarget)element;
        } catch (ClassCastException e) {
            // will not work on this one!
            return;
        }
        SVGSVGElement svgElement = (SVGSVGElement)element.getOwnerSVGElement();
        if (svgElement == null) {
            if (element.getLocalName().equals(SVG_SVG_TAG)) {
                svgElement = (SVGSVGElement)element;
            } else {
                // something goes wrong => disable scripting
                return;
            }
        }
        String language = svgElement.getContentScriptType();
        Interpreter interpret = null;
        String script = null;
        // <!> TODO we need to memo listeners to be able to remove
        // them later when deconnecting the bridge binding...
        if (element.getLocalName().equals(SVG_SVG_TAG)) {
            for (int i = 0; i < EVENT_ATTRIBUTES_SVG.length; i++) {
                if (!(script = element.getAttribute(EVENT_ATTRIBUTES_SVG[i])).
                    equals("")) {
                    if (interpret == null) {
                        // try to get the intepreter only if we have
                        // a reason to do it!
                        interpret = ctx.getInterpreterPool().
                            getInterpreter(element.getOwnerDocument(), language);
                        // the interpreter is not avaible => stop it now!
                        if (interpret == null) {
                            UserAgent ua = ctx.getUserAgent();
                            if (ua != null)
                                ua.displayError(new Exception("unknow language: "+language));
                            break;
                        }
                    }
                    target.
                        addEventListener(EVENT_NAMES[i+FIRST_SVG_EVENT],
                                         new ScriptCaller(ctx.getUserAgent(),
                                                          script, interpret),
                                         false);
                }
            }
            // continue
        } else
            if (element.getLocalName().equals("set") ||
                element.getLocalName().startsWith("animate")) {
                for (int i = 0; i < EVENT_ATTRIBUTES_ANIMATION.length; i++) {
                    if (!(script =
                          element.getAttribute(EVENT_ATTRIBUTES_ANIMATION[i])).
                        equals("")) {
                        if (interpret == null) {
                            // try to get the intepreter only if we have
                            // a reason to do it!
                            interpret = ctx.getInterpreterPool().
                                getInterpreter(element.getOwnerDocument(),
                                               language);
                            // the interpreter is not avaible => stop it now!
                            if (interpret == null) {
                                UserAgent ua = ctx.getUserAgent();
                                if (ua != null)
                                    ua.displayError(new Exception("unknow language: "+
                                                                  language));
                                break;
                            }
                        }
                        target.
                            addEventListener(EVENT_NAMES[i+
                                                        FIRST_ANIMATION_EVENT],
                                             new ScriptCaller(ctx.getUserAgent(),
                                                              script, interpret),
                                             false);
                    }
                }
                // not other stuff to do on this kind of events
                return;
            }
        for (int i = 0; i < EVENT_ATTRIBUTES_GRAPHICS.length; i++) {
            if (!(script = element.getAttribute(EVENT_ATTRIBUTES_GRAPHICS[i])).
                equals("")) {
                if (interpret == null) {
                    // try to get the intepreter only if we have
                    // a reason to do it!
                    interpret = ctx.getInterpreterPool().
                        getInterpreter(element.getOwnerDocument(), language);
                    // the interpreter is not avaible => stop it now!
                    if (interpret == null) {
                        UserAgent ua = ctx.getUserAgent();
                        if (ua != null)
                            ua.displayError(new Exception("unknow language: "+language));
                        break;
                    }
                }
                target.
                    addEventListener(EVENT_NAMES[i],
                                     new ScriptCaller(ctx.getUserAgent(),
                                                      script, interpret),
                                     false);
            }
        }
    }


    public static void updateDOMListener(BridgeContext ctx,
                                         SVGElement element) {
    }

    /**
     * Is called only for the root element in order to dispatch GVT
     * events to the DOM.
     */
    public static void addGVTListener(BridgeContext ctx, Element svgRoot) {
        UserAgent ua = ctx.getUserAgent();
        if (ua != null) {
            EventDispatcher dispatcher = ua.getEventDispatcher();
            if (dispatcher != null) {
                final Listener listener = new Listener(ctx, ua);
                dispatcher.addGraphicsNodeMouseListener(listener);
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
                ctx.getInterpreterPool().getInterpreter(doc, language);
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
                        ua.displayError(new Exception("scripting error: " +e.getMessage()));
                }
            } else
                if (ua != null)
                    ua.displayError(new Exception("unknown language: "+language));
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
            dispatcher.removeGraphicsNodeMouseListener(listener);
            evt.getTarget().removeEventListener("SVGUnload", this, false);
        }
    }

    private static class Listener
        implements GraphicsNodeMouseListener {
        private BridgeContext context;
        private UserAgent ua;
        private GraphicsNode lastTarget;
        public Listener(BridgeContext ctx, UserAgent u) {
            context = ctx;
            ua = u;
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
            GraphicsNode node = evt.getRelatedNode();
            GraphicsNodeMouseEvent evt2 = null;
            if (lastTarget != node) {
                if (lastTarget != null) {
                    evt2 = new GraphicsNodeMouseEvent(lastTarget,
                                                      evt.MOUSE_EXITED,
                                                      evt.getWhen(),
                                                      evt.getModifiers(),
                                                      evt.getX(),
                                                      evt.getY(),
                                                      evt.getClickCount(),
                                                      lastTarget);
                    dispatchMouseEvent("mouseout",
                                       evt2,
                                       true);
                }
                if (node != null) {
                    evt2 = new GraphicsNodeMouseEvent(node,
                                                      evt.MOUSE_ENTERED,
                                                      evt.getWhen(),
                                                      evt.getModifiers(),
                                                      evt.getX(),
                                                      evt.getY(),
                                                      evt.getClickCount(),
                                                      lastTarget);
                    dispatchMouseEvent("mouseover",
                                       evt2,
                                       true);
                }
            }
            try {
                if (node != null) {
                    evt2 = new GraphicsNodeMouseEvent(node,
                                                      evt.MOUSE_MOVED,
                                                      evt.getWhen(),
                                                      evt.getModifiers(),
                                                      evt.getX(),
                                                      evt.getY(),
                                                      evt.getClickCount(),
                                                      null);
                    dispatchMouseEvent("mousemove",
                                       evt2,
                                       true);
                }
            } finally {
                lastTarget = node;
            }
        }
        public void mouseMoved(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousemove", evt, false);
        }
        private void dispatchMouseEvent(String eventType,
                                        GraphicsNodeMouseEvent evt,
                                        boolean cancelok) {
            Point2D pos = evt.getPoint2D();
            AffineTransform transform = ua.getTransform();
            if (transform != null && !transform.isIdentity())
                transform.transform(pos, pos);
            Point screen = ua.getClientAreaLocationOnScreen();
            screen.translate((int)Math.floor(pos.getX()),
                             (int)Math.floor(pos.getY()));
            // compute screen coordinates
            GraphicsNode node = evt.getGraphicsNode();
            Element elmt = context.getElement(node);
            if (elmt == null) // should not appeared if binding on
                return;
            EventTarget target = (EventTarget)elmt;
            // <!> TODO dispatch it only if pointers-event property ask for
            short button = 1;
            if ((evt.BUTTON1_MASK & evt.getModifiers()) != 0)
                button = 0;
            else
                if ((evt.BUTTON3_MASK & evt.getModifiers()) != 0)
                    button = 2;
            MouseEvent mevent =
                // DOM Level 2 6.5 cast from Document to DocumentEvent is ok
                (MouseEvent)org.apache.batik.dom.events.EventSupport.
                createEvent(org.apache.batik.dom.events.EventSupport.
                            MOUSE_EVENT_TYPE);
            // deal with the related node/target
            node = evt.getRelatedNode();
            EventTarget relatedTarget =
                (EventTarget)context.getElement(node);
            mevent.initMouseEvent(eventType, true, cancelok, null,
                                  evt.getClickCount(),
                                  screen.x, screen.y,
                                  (int)Math.floor(pos.getX()),
                                  (int)Math.floor(pos.getY()),
                                  evt.isControlDown(), evt.isAltDown(),
                                  evt.isShiftDown(), evt.isMetaDown(),
                                  button, relatedTarget);
            try {
                target.dispatchEvent(mevent);
            } catch (RuntimeException e) {
                // runtime exceptions may appear we need to display them...
                ua.displayError(new Exception("scripting error in event handling: "+e.getMessage()));
            }
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
                    ua.displayError(new Exception("scripting error: " +e.getMessage()));
            }
        }
    }
}

