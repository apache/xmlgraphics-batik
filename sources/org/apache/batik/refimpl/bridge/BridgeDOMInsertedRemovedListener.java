/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.MutationEvent;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.StyleReference;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Iterator;

/**
 * This class listens to DOMNodeInserted and DOMNodeRemoved
 * events in the SVG tree.
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class BridgeDOMInsertedRemovedListener implements EventListener {

    private static final String DOM_NODE_INSERTED_TYPE = "DOMNodeInserted";
    private static final String DOM_NODE_REMOVED_TYPE = "DOMNodeRemoved";

    private ConcreteBridgeContext context;

    /**
     * Creates the listener.
     */
    public BridgeDOMInsertedRemovedListener(ConcreteBridgeContext context) {
        this.context = context;
    }

    /**
     * Handles the mutation event.
     * @param evt the DOM event
     */
    public void handleEvent(Event evt) {
        MutationEvent event = (MutationEvent)evt;

        Node child  = (Node)event.getTarget(); // The inserted or removed elmt
        Element parent = (Element)event.getRelatedNode(); // The parent

        if (child.getNodeType() == Node.ELEMENT_NODE) {
            GraphicsNode gn = context.getGraphicsNode(parent);

            if (gn != null) {
                // May be this should be done in the BridgeUpdateManager

                if (event.getType().equals(DOM_NODE_INSERTED_TYPE)) {
                    GraphicsNodeBridge graphicsNodeBridge =
                        (GraphicsNodeBridge)context.getBridge((Element)child);
                    // it means we know how to go from DOM to GVT for this node
                    if (graphicsNodeBridge != null) {
                        GraphicsNode childGVTNode =
                            graphicsNodeBridge.
                            createGraphicsNode(context,
                                               (Element)child);
                        ((CompositeGraphicsNode)gn).getChildren().
                            add(childGVTNode);
                    }
                } else { // DOM_NODE_REMOVED_TYPE
                    GraphicsNode childGVTNode =
                        context.getGraphicsNode((Element)child);
                    ((CompositeGraphicsNode)gn).getChildren().
                        remove(childGVTNode);
                    context.unbind((Element)child);
                }
            }
        }
    }
}
