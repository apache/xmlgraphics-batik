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
        CompositeGraphicsNode gn =
            (CompositeGraphicsNode)super.createGraphicsNode(ctx, e);
	if (gn == null) {
	    return null;
	}

        // 'color-rendering'
        RenderingHints hints = CSSUtilities.convertColorRendering(e, null);
        if (hints != null) {
            gn.setRenderingHints(hints);
        }

        // 'enable-background'
        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
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

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        handleElementAdded((CompositeGraphicsNode)node, 
                           e, 
                           (Element)evt.getTarget());
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleElementAdded(CompositeGraphicsNode gn, 
                                   Node parent, 
                                   Element childElt) {
        // build the graphics node
        GVTBuilder builder = ctx.getGVTBuilder();
        GraphicsNode childNode = builder.build(ctx, childElt);
        if (childNode == null) {
            return; // the added element is not a graphic element
        }
        // add the graphics node
        Node n = parent.getFirstChild();
        Node lastChild = parent.getLastChild();
        if (n == childElt) {
            // add at the beginning
            gn.add(0, childNode);
        } else if (lastChild == childElt) {
            // append at the end
            gn.add(childNode);
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
            gn.add(index, childNode);
        }
    }
}
