/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.ParseException;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * The base bridge class for SVG graphics node. By default, the namespace URI is
 * the SVG namespace. Override the <tt>getNamespaceURI</tt> if you want to add
 * custom <tt>GraphicsNode</tt> with a custom namespace.
 *
 * <p>This class handles various attributes that are defined on most
 * of the SVG graphic elements as described in the SVG
 * specification.</p>
 *
 * <ul>
 * <li>clip-path</li>
 * <li>filter</li>
 * <li>mask</li>
 * <li>opacity</li>
 * <li>transform</li>
 * <li>visibility</li>
 * </ul>
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractGraphicsNodeBridge extends AbstractSVGBridge
    implements BridgeUpdateHandler, GraphicsNodeBridge, ErrorConstants {

    /**
     * The element that has been handled by this bridge.
     */
    protected Element e;

    /**
     * The graphics node constructed by this bridge.
     */
    protected GraphicsNode node;

    /**
     * The bridge context to use for dynamic updates.
     */
    protected BridgeContext ctx;

    /**
     * Constructs a new abstract bridge.
     */
    protected AbstractGraphicsNodeBridge() {}

    /**
     * Creates a <tt>GraphicsNode</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }

        GraphicsNode node = instantiateGraphicsNode();
        // 'transform'
        String s = e.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            node.setTransform
                (SVGUtilities.convertTransform(e, SVG_TRANSFORM_ATTRIBUTE, s));
        }
        // 'visibility'
        node.setVisible(CSSUtilities.convertVisibility(e));
        return node;
    }

    /**
     * Creates the GraphicsNode depending on the GraphicsNodeBridge
     * implementation.
     */
    protected abstract GraphicsNode instantiateGraphicsNode();

    /**
     * Builds using the specified BridgeContext and element, the
     * specified graphics node.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @param node the graphics node to build
     */
    public void buildGraphicsNode(BridgeContext ctx,
                                  Element e,
                                  GraphicsNode node) {

        // push 'this' as the current BridgeUpdateHandler for subbridges
        if (ctx.isDynamic()) {
            ctx.pushBridgeUpdateHandler(this);
        }

        // 'opacity'
        node.setComposite(CSSUtilities.convertOpacity(e));
        // 'filter'
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        // 'mask'
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        // 'clip-path'
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
        // 'pointer-events'
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));

        if (ctx.isDynamic()) {
            this.e = e;
            this.node = node;
            this.ctx = ctx;
            initializeDynamicSupport();
            // 'this' is no more the current BridgeUpdateHandler
            ctx.popBridgeUpdateHandler();
        }

        // Handle children elements such as <title>
        SVGUtilities.bridgeChildren(ctx, e);
    }

    // dynamic support

    /**
     * This method is invoked during the build phase if the document
     * is dynamic. The responsability of this method is to ensure that
     * any dynamic modifications of the element this bridge is
     * dedicated to, happen on its associated GVT product.
     */
    protected void initializeDynamicSupport() {
        ((EventTarget)e).addEventListener("DOMAttrModified", 
                                          new DOMAttrModifiedEventListener(),
                                          false);
        ctx.bind(e, node);
        BridgeEventSupport.addDOMListener(ctx, e);
    }

    /**
     * Invoked when a bridge update is starting.
     *
     * @param evt the evt that describes the incoming update
     */
    public void bridgeUpdateStarting(BridgeUpdateEvent evt) {
        System.out.println("("+e.getLocalName()+" "+node+") update started "+
                           evt.getHandlerKey());
    }

    /**
     * Invoked when a bridge update is completed.
     *
     * @param evt the evt that describes the update
     */
    public void bridgeUpdateCompleted(BridgeUpdateEvent evt) {
        System.out.println("("+e.getLocalName()+" "+node+") update completed "+
                           evt.getHandlerKey());
    }

    /**
     * Handles DOMAttrModified events.
     *
     * @param evt the DOM mutation event
     */
    protected void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_TRANSFORM_ATTRIBUTE)) {
            BridgeUpdateEvent be = new BridgeUpdateEvent(this);
            fireBridgeUpdateStarting(be);
            
            String s = evt.getNewValue();
            AffineTransform at = GraphicsNode.IDENTITY;
            if (s.length() != 0) {
                at = SVGUtilities.convertTransform
                    (e, SVG_TRANSFORM_ATTRIBUTE, s);
            }
            node.setTransform(at);
            fireBridgeUpdateCompleted(be);
        } else {
            System.out.println("Unsupported attribute modification: "+attrName+
                               " on "+e.getLocalName());
        }
    }

    /**
     * The listener class for 'DOMAttrModified' event.
     */
    protected class DOMAttrModifiedEventListener
        implements UnwrappedEventListener {

        /**
         * Handles 'DOMAttrModfied' events and deleguates to the
         * 'handleDOMAttrModifiedEvent' method any changes to the
         * GraphicsNode if any.
         *
         * @param evt the DOM event
         */
        public void handleEvent(Event evt) {
            if (evt.getTarget() != e) {
                return;
            }
            handleDOMAttrModifiedEvent((MutationEvent)evt);
        }
    }
}
