/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;


import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.svg.SVGElement;
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
        Interpreter interpret = 
            ctx.getInterpreterPool().
            getInterpreter(((SVGElement)element).getOwnerSVGElement().
                           getContentScriptType(), element.getOwnerDocument());
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
    }

    public static void updateDOMListener(BridgeContext ctx,
                                         Element element) {
    }
    
    public static void addGVTListener(BridgeContext ctx, GraphicsNode node) {
        UserAgent ua = ctx.getUserAgent();
        if (ua != null) {
            EventDispatcher dispatcher = ua.getEventDispatcher();
            if (dispatcher != null) {
                Listener listener = new Listener(ctx);
                dispatcher.addGlobalGraphicsNodeMouseListener(listener);
            }
        }
    }
    
    public static class Listener 
        implements GraphicsNodeMouseListener {
        private BridgeContext context;
        public Listener(BridgeContext ctx) {
            context = ctx;
        }
        public void mouseClicked(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("click", evt);
        }
        public void mousePressed(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousedown", evt);
        }
        public void mouseReleased(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mouseup", evt);
        }
        public void mouseEntered(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mouseover", evt);
        }
        public void mouseExited(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mouseout", evt);
        }
        public void mouseDragged(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousemove", evt);
        }
        public void mouseMoved(GraphicsNodeMouseEvent evt) {
            dispatchMouseEvent("mousemove", evt);
        }
        private void dispatchMouseEvent(String eventType,
                                        GraphicsNodeMouseEvent evt) {
            Point2D pos = evt.getPoint2D();
            GraphicsNode node = evt.getGraphicsNode();
            Element elmt = context.getElement(node);
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
                (MouseEvent)((DocumentEvent)elmt.getOwnerDocument()).
                createEvent(org.apache.batik.dom.events.EventSupport.
                            MUTATION_EVENT_TYPE);
            mevent.initMouseEvent(eventType, true, true, null, 
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
                    ua.displayError(e.getMessage());
            }
        }
    }
}
    
