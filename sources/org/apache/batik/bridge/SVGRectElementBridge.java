/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.apache.batik.parser.ParseException;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;

import org.w3c.dom.Element;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;rect> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGRectElementBridge extends SVGShapeElementBridge {

    /**
     * Constructs a new bridge for the &lt;rect> element.
     */
    public SVGRectElementBridge() {}

    /**
     * Returns 'rect'.
     */
    public String getLocalName() {
        return SVG_RECT_TAG;
    }

    /**
     * Constructs a rectangle according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes a rect element
     * @param shapeNode the shape node to initialize
     */
    protected void buildShape(BridgeContext ctx,
                              Element e,
                              ShapeNode shapeNode) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);
        String s;

        // 'x' attribute - default is 0
        s = e.getAttributeNS(null, SVG_X_ATTRIBUTE);
        float x = 0;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_X_ATTRIBUTE, uctx);
        }

        // 'y' attribute - default is 0
        s = e.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        float y = 0;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_Y_ATTRIBUTE, uctx);
        }

        // 'width' attribute - required
        s = e.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        float w;
        if (s.length() != 0) {
            w = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, SVG_WIDTH_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_WIDTH_ATTRIBUTE, s});
        }
	// A value of zero disables rendering of the element
	if (w == 0) {
	    return;
	}

        // 'height' attribute - required
        s = e.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
        float h;
        if (s.length() != 0) {
            h = UnitProcessor.svgVerticalLengthToUserSpace
                (s, SVG_HEIGHT_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_HEIGHT_ATTRIBUTE, s});
        }
	// A value of zero disables rendering of the element
	if (h == 0) {
	    return;
	}

        // 'rx' attribute - default is 0
        s = e.getAttributeNS(null, SVG_RX_ATTRIBUTE);
        boolean rxs = (s.length() != 0);
        float rx = 0;
        if (rxs) {
            rx = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, SVG_RX_ATTRIBUTE, uctx);
        }
        rx = (rx > w / 2) ? w / 2 : rx;

        // 'ry' attribute - default is 0
        s = e.getAttributeNS(null, SVG_RY_ATTRIBUTE);
        boolean rys = (s.length() != 0);
        float ry = 0;
        if (rys) {
            ry = UnitProcessor.svgVerticalLengthToUserSpace
                (s, SVG_RY_ATTRIBUTE, uctx);
        }
        ry = (ry > h / 2) ? h / 2 : ry;

        Shape shape = null;
        if (rxs && rys) {
            if (rx == 0 || ry == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, rx*2, ry*2);
            }
        } else if (rxs) {
            if (rx == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, rx*2, rx*2);
            }
        } else if (rys) {
            if (ry == 0) {
                shape = new Rectangle2D.Float(x, y, w, h);
            } else {
                shape = new RoundRectangle2D.Float(x, y, w, h, ry*2, ry*2);
            }
        } else {
            shape = new Rectangle2D.Float(x, y, w, h);
        }
        shapeNode.setShape(shape);
    }

    /**
     * Extension of the <tt>SVGRectElementBridge</tt> that can 
     * handle updates in the corresponding <tt>&lt;rect&gt;</tt>
     * element.
     */
    public static class Dynamic extends SVGRectElementBridge 
        implements DynamicBridge, EventListener{
        private BridgeContext ctx;
        private BridgeUpdateHandler handler;
        private int handlerKey;
        private ShapeNode node;

        public Bridge getInstance(){
            return new SVGRectElementBridge();
        }

        /**
         * Creates a graphics node using the specified BridgeContext and
         * for the specified element.
         *
         * @param ctx the bridge context to use
         * @param e the element that describes the graphics node to build
         * @return a graphics node that represents the specified element
         */
        public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
            // System.out.println("Calling createGraphicsNode");
            ((EventTarget)e).addEventListener("DOMAttrModified",
                                              this,
                                              false);
            node = (ShapeNode)super.createGraphicsNode(ctx, e);
            this.ctx = ctx;
            return node;
        }

        /**
         *
         */
        public void handleEvent(Event evt){
            
            BridgeUpdateEvent be = new BridgeUpdateEvent();
            be.setHandlerKey(handlerKey);

            // Notify handler if any.
            if(handler != null){
                handler.bridgeUpdateStarting(be);
            }

            Rectangle2D r = (Rectangle2D)node.getShape();
            // System.out.println("Old r: " + r);

            MutationEvent mevt = (MutationEvent)evt;
            if (mevt.getAttrName().equals("x")){
                r.setRect(Float.parseFloat(((MutationEvent)evt).getNewValue()),
                          r.getY(),
                          r.getWidth(),
                          r.getHeight());
            } else if (mevt.getAttrName().equals("y")){
                r.setRect(r.getX(),
                          Float.parseFloat(((MutationEvent)evt).getNewValue()),
                          r.getWidth(),
                          r.getHeight());

            } else if (mevt.getAttrName().equals("width")){
                r.setRect(r.getX(),
                          r.getY(),
                          Float.parseFloat(((MutationEvent)evt).getNewValue()),
                          r.getHeight());

            }else if (mevt.getAttrName().equals("height")){
                r.setRect(r.getX(),
                          r.getY(),
                          r.getWidth(),
                          Float.parseFloat(((MutationEvent)evt).getNewValue()));

            }
            // System.out.println("New r: " + r);

            node.setShape(r);

            if(handler != null){
                handler.bridgeUpdateCompleted(be);
            }
        }

        /**
         * 
         */
        public BridgeUpdateHandler getBridgeUpdateHandler(){
            return handler;
        }
        
        /**
         * 
         */
        public void setBridgeUpdateHandler(BridgeUpdateHandler handler,
                                           int handlerKey){
            System.out.println("SVGRectElementBridge.Dynamic, I have been called: " + handler );
            this.handler = handler;
            this.handlerKey = handlerKey;
        }
        
    }
}
