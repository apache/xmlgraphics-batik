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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
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

    private BridgeEventSupport() {}

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
                // add an unload listener on the SVGDocument to remove
                // that listener for dispatching events
                ((EventTarget)svgRoot).
                    addEventListener("SVGUnload",
                                     new GVTUnloadListener(dispatcher,
                                                           listener),
                                     false);
            }
        }
    }

    private static class GVTUnloadListener implements EventListener {
        private EventDispatcher dispatcher;
        private Listener listener;
        GVTUnloadListener(EventDispatcher dispatcher, Listener listener) {
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
            MouseEvent mevent = (MouseEvent)
                // DOM Level 2 6.5 cast from Document to DocumentEvent is ok
                ((DocumentEvent)elmt.getOwnerDocument()).createEvent("MouseEvents");
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
                ua.displayError(new Exception("scripting error in event handling: "+
                                              e.getMessage()));
            }
        }
    }
}
