/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;g> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGGElementBridge extends AbstractGraphicsNodeBridge {

    /**
     * Constructs a new bridge for the &lt;g> element.
     */
    public SVGGElementBridge() {}

    /**
     * Returns 'g'.
     */
    public String getLocalName() {
        return SVG_G_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGGElementBridge();
    }

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

        CompositeGraphicsNode gn =
            (CompositeGraphicsNode)super.createGraphicsNode(ctx, e);

        // 'color-rendering'
        Map colorHints = CSSUtilities.convertColorRendering(e);
        if (colorHints != null) {
            gn.setRenderingHints(new RenderingHints(colorHints));
        }

        // 'enable-background'
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);
        Rectangle2D r = CSSUtilities.convertEnableBackground(e, uctx);
        if (r != null) {
            gn.setBackgroundEnable(r);
        }
        return gn;
    }

    /**
     * Creates a <tt>CompositeGraphicsNode</tt>.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new CompositeGraphicsNode();
    }

    /**
     * Returns true as the &lt;g> element is a container.
     */
    public boolean isComposite() {
        return true;
    }

    // dynamic support

    /**
     * This method is invoked during the build phase if the document
     * is dynamic. The responsability of this method is to ensure that
     * any dynamic modifications of the element this bridge is
     * dedicated to, happen on its associated GVT product.
     */
    protected void initializeDynamicSupport() {
        super.initializeDynamicSupport();
        ((EventTarget)e).addEventListener("DOMNodeInserted", 
                                          new DOMNodeInsertedEventListener(),
                                          false);
        ((EventTarget)e).addEventListener("DOMNodeRemoved", 
                                          new DOMNodeRemovedEventListener(),
                                          false);
    }

    /**
     * Handles DOMNodeInserted events.
     *
     * @param evt the DOM mutation event
     */
    protected void handleDOMNodeInserted(MutationEvent evt) {
        //System.out.println("handleDOMNodeInserted "+e.getLocalName());
        Element childElt = (Element)evt.getTarget();
        // build the graphics node
        GVTBuilder builder = ctx.getGVTBuilder();
        GraphicsNode childNode = builder.build(ctx, childElt);
        if (childNode == null) {
            return; // the added element is not a graphic element
        }
        // add the graphics node
        Node n = e.getFirstChild();
        Node lastChild = e.getLastChild();
        if (n == childElt) {
            // add at the beginning
            ((CompositeGraphicsNode)node).add(0, childNode);
        } else if (lastChild == childElt) {
            // append at the end
            ((CompositeGraphicsNode)node).add(childNode);
        } else {
            // find the index of the GraphicsNode to add
            int index = 0;
            while (n != lastChild && n != childElt) {
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (ctx.hasGraphicsNodeBridge((Element)n)) {
                        index++;
                    }
                }
                n = n.getNextSibling();
            }
            // insert at the index
            ((CompositeGraphicsNode)node).add(index, childNode);
        }
    }

    /**
     * Handles DOMNodeRemoved events.
     *
     * @param evt the DOM mutation event
     */
    protected void handleDOMNodeRemoved(MutationEvent evt) {
        //System.out.println("handleDOMNodeRemoved "+e.getLocalName());
        Element childElt = (Element)evt.getTarget();
        if (!ctx.hasGraphicsNodeBridge(childElt)) {
            return; // the removed element is not a graphic element
        }
        Node n = e.getFirstChild();
        Node lastChild = e.getLastChild();
        if (n == childElt) {
            // remove first
            ((CompositeGraphicsNode)node).remove(0);
        } else if (lastChild == childElt) {
            // remove last
            CompositeGraphicsNode cgn = (CompositeGraphicsNode)node;
            cgn.remove(cgn.size()-1);
        } else {
            // find the index of the GraphicsNode to remove
            int index = 0;
            while (n != lastChild && n != childElt) {
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (ctx.hasGraphicsNodeBridge((Element)n)) {
                        index++;
                    }
                }
                n = n.getNextSibling();
            }
            // remove at the index
            ((CompositeGraphicsNode)node).remove(index);
        }
    }

    /**
     * The listener class for 'DOMNodeInserted' event.
     */
    protected class DOMNodeInsertedEventListener
        implements UnwrappedEventListener {

        /**
         * Handles 'DOMNodeInserted' events and deleguates to the
         * 'handleDOMNodeInserted' method any changes to the
         * GraphicsNode if any.
         *
         * @param evt the DOM event
         */
        public void handleEvent(Event evt) {
            if (((MutationEvent)evt).getRelatedNode() != e) {
                return;
            }
            handleDOMNodeInserted((MutationEvent)evt);
        }
    }

    /**
     * The listener class for 'DOMNodeRemoved' event.
     */
    protected class DOMNodeRemovedEventListener
        implements UnwrappedEventListener {

        /**
         * Handles 'DOMNodeRemoved' events and deleguates to the
         * 'handleDOMNodeRemoved' method any changes to the
         * GraphicsNode if any.
         *
         * @param evt the DOM event
         */
        public void handleEvent(Event evt) {
            if (((MutationEvent)evt).getRelatedNode() != e) {
                return;
            }
            handleDOMNodeRemoved((MutationEvent)evt);
        }
    }
}
