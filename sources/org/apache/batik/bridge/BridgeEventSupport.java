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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.text.AttributedCharacterIterator;

import java.util.List;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;

import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;

import org.apache.batik.gvt.renderer.StrokingTextPainter;

import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.text.TextSpanLayout;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;

import org.w3c.dom.svg.SVGElement;

/**
 * This class is responsible of tracking GraphicsNodeMouseEvent and
 * fowarding them to the DOM as regular DOM MouseEvent.
 *
 * @author <a href="mailto:tkormann@ilog.fr>Thierry Kormann</a>
 * @version $Id$
 */
public class BridgeEventSupport implements SVGConstants {

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
                ((EventTarget)svgRoot).addEventListener
                    ("SVGUnload",
                     new GVTUnloadListener(dispatcher, listener),
                     false);
            }
        }
    }

    protected static class GVTUnloadListener implements EventListener {

        protected EventDispatcher dispatcher;
        protected Listener listener;

        public GVTUnloadListener(EventDispatcher dispatcher, 
                                 Listener listener) {
            this.dispatcher = dispatcher;
            this.listener = listener;
        }

        public void handleEvent(Event evt) {
            dispatcher.removeGraphicsNodeMouseListener(listener);
            evt.getTarget().removeEventListener("SVGUnload", this, false);
        }
    }

    /**
     * A GraphicsNodeMouseListener that dispatch DOM events accordingly.
     */
    protected static class Listener implements GraphicsNodeMouseListener {
        
        protected BridgeContext context;
        protected UserAgent ua;
        protected Element lastTargetElement;

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
            dispatchMouseEvent("mousemove", evt, false);
        }

        public void mouseMoved(GraphicsNodeMouseEvent evt) {
            Point clientXY = getClientMouseLocation(evt.getPoint2D());
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = getEventTarget(node, clientXY);
            if (lastTargetElement != targetElement) {
                if (lastTargetElement != null) {
                    dispatchMouseEvent("mouseout", 
                                       lastTargetElement, // target
                                       targetElement,     // relatedTarget
                                       clientXY,
                                       evt,
                                       true);
                }
                if (targetElement != null) {
                    dispatchMouseEvent("mouseover", 
                                       targetElement,     // target
                                       lastTargetElement, // relatedTarget
                                       clientXY,
                                       evt,
                                       true);
                }
            }
            dispatchMouseEvent("mousemove", 
                               targetElement,     // target
                               null,              // relatedTarget
                               clientXY,
                               evt,
                               false);
        }

        /**
         * Dispatches a DOM MouseEvent according to the specified
         * parameters.
         *
         * @param eventType the event type
         * @param evt the GVT GraphicsNodeMouseEvent
         * @param cancelable true means the event is cancelable
         */
        protected void dispatchMouseEvent(String eventType,
                                          GraphicsNodeMouseEvent evt,
                                          boolean cancelable) {
            Point clientXY = getClientMouseLocation(evt.getPoint2D());
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = getEventTarget(node, clientXY);
            Element relatedElement = getRelatedElement(evt);
            dispatchMouseEvent(eventType, 
                               targetElement,
                               relatedElement,
                               clientXY, 
                               evt, 
                               cancelable);
        }

        /**
         * Dispatches a DOM MouseEvent according to the specified
         * parameters.
         *
         * @param eventType the event type
         * @param targetElement the target of the event
         * @param relatedElement the related target if any
         * @param clientXY the mouse coordinates in the client space
         * @param evt the GVT GraphicsNodeMouseEvent
         * @param cancelable true means the event is cancelable
         */
        protected void dispatchMouseEvent(String eventType,
                                          Element targetElement,
                                          Element relatedElement,
                                          Point clientXY,
                                          GraphicsNodeMouseEvent evt,
                                          boolean cancelable) {
            if (targetElement == null) {
                return;
            }
            /*
            if (relatedElement != null) {
                System.out.println
                    ("dispatching "+eventType+
                     " target:"+targetElement.getLocalName()+
                     " relatedElement:"+relatedElement.getLocalName());
            } else {
                System.out.println
                    ("dispatching "+eventType+
                     " target:"+targetElement.getLocalName());

            }*/

            short button = getButton(evt);
            Point screenXY = getScreenMouseLocation(clientXY);
            // create the coresponding DOM MouseEvent
            DocumentEvent d = (DocumentEvent)targetElement.getOwnerDocument();
            MouseEvent mouseEvt = (MouseEvent)d.createEvent("MouseEvents");
            mouseEvt.initMouseEvent(eventType, 
                                    true, 
                                    cancelable, 
                                    null,
                                    evt.getClickCount(),
                                    screenXY.x, 
                                    screenXY.y,
                                    clientXY.x,
                                    clientXY.y,
                                    evt.isControlDown(), 
                                    evt.isAltDown(),
                                    evt.isShiftDown(), 
                                    evt.isMetaDown(),
                                    button, 
                                    (EventTarget)relatedElement);

            try {
                ((EventTarget)targetElement).dispatchEvent(mouseEvt);
            } catch (RuntimeException e) {
                ua.displayError(e);
            } finally {
                lastTargetElement = targetElement;
            }
        }

        /**
         * Returns the related element according to the specified event.
         *
         * @param evt the GVT GraphicsNodeMouseEvent
         */
        protected Element getRelatedElement(GraphicsNodeMouseEvent evt) {
            GraphicsNode relatedNode = evt.getRelatedNode();
            Element relatedElement = null;
            if (relatedNode != null) {
                relatedElement = context.getElement(relatedNode);
            }
            return relatedElement;
        }

        /**
         * Returns the mouse event button.
         *
         * @param evt the GVT GraphicsNodeMouseEvent
         */
        protected short getButton(GraphicsNodeMouseEvent evt) {
            short button = 1;
            if ((evt.BUTTON1_MASK & evt.getModifiers()) != 0) {
                button = 0;
            } else if ((evt.BUTTON3_MASK & evt.getModifiers()) != 0) {
                button = 2;
            }
            return button;
        }

        /**
         * Returns the client mouse coordinates using the specified
         * mouse coordinates in the GVT Tree space.
         *
         * @param coords the mouse coordinates in the GVT tree space
         */
        protected Point getClientMouseLocation(Point2D coords) {
            AffineTransform transform = ua.getTransform();
            Point2D p = coords;
            if (transform != null && !transform.isIdentity()) {
                p = transform.transform(coords, null);
            }
            return new Point((int)Math.floor(coords.getX()),
                             (int)Math.floor(coords.getY()));
        }

        /**
         * Returns the mouse coordinates on the screen using the
         * specified client mouse coordinates.
         *
         * @param coords the mouse coordinates in the client space
         */
        protected Point getScreenMouseLocation(Point coords) {
            Point screen = new Point(ua.getClientAreaLocationOnScreen());
            screen.translate(coords.x, coords.y);
            return screen;
        }

        /**
         * Returns the element that is the target of the specified
         * event or null if any.
         *
         * @param node the graphics node that received the event
         * @param coords the mouse coordinates in the GVT tree space
         */
        protected Element getEventTarget(GraphicsNode node, Point2D coords) {
            Element target = context.getElement(node);
            // Lookup inside the text element children to see if the target
            // is a tspan or textPath

            if (target != null && node instanceof TextNode) {
		TextNode textNode = (TextNode)node;
		List list = textNode.getTextRuns();
                // place coords in text node coordinate system
                try {
                    node.getGlobalTransform().createInverse().transform(coords, coords);
                } catch (NoninvertibleTransformException ex) {
                }
		for (int i = 0 ; i < list.size(); i++) {
                    StrokingTextPainter.TextRun run =
                        (StrokingTextPainter.TextRun)list.get(i);
                    AttributedCharacterIterator aci = run.getACI();
                    TextSpanLayout layout = run.getLayout();
                    float x = (float)coords.getX();
                    float y = (float)coords.getY();
                    TextHit textHit = layout.hitTestChar(x, y);
                    if (textHit != null && layout.getBounds().contains(x, y)) {
                        Object delimiter = aci.getAttribute
                            (GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
                        if (delimiter instanceof Element) {
                            return (Element)delimiter;
                        }
                    }
		}
            }
            return (Element)target;
        }
    }
}
