/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.GVTBuilder;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.bridge.Bridge;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;

/**
 * This class is responsible for creating the GVT tree from
 * the SVG Document.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ConcreteGVTBuilder implements GVTBuilder {
    /**
     * Builds a GVT tree using the specified context and SVG document.
     * @param ctx the context to use
     * @param svgDocument the DOM tree that represents an SVG document
     */
    public GraphicsNode build(BridgeContext ctx, Document svgDocument){

        Element svgRoot = svgDocument.getDocumentElement();

        // Now, build corresponding canvas

        GraphicsNodeBridge graphicsNodeBridge =
            (GraphicsNodeBridge)ctx.getBridge(svgRoot);

        if (graphicsNodeBridge == null)
            throw new IllegalArgumentException(
                 "Bridge for "+svgRoot.getTagName()+" is not registered");

        GraphicsNode treeRoot;
        treeRoot = graphicsNodeBridge.createGraphicsNode(ctx, svgRoot);

        if(graphicsNodeBridge.isContainer()){
            buildComposite(ctx, (CompositeGraphicsNode)treeRoot, svgRoot);
        }
        // Adds the Listener on Attr Modified event.
        ((EventTarget)svgRoot).
            addEventListener("DOMAttrModified",
                             new BridgeDOMAttrModifiedListener
                                 ((ConcreteBridgeContext)ctx),
                             true);

        EventListener listener;
        listener = new BridgeDOMInsertedRemovedListener
            ((ConcreteBridgeContext)ctx);
        // Adds the Listener on Attr Modified event.
        ((EventTarget)svgRoot).
            addEventListener("DOMNodeInserted", listener, true);
        // Adds the Listener on Attr Modified event.
        ((EventTarget)svgRoot).
            addEventListener("DOMNodeRemoved", listener, true);


        // <!> TODO as previous line this should be done only if we want
        // binding !!!!
        BridgeEventSupport.addGVTListener(ctx, treeRoot);

        return treeRoot;
    }

    /**
     * Creates GraphicsNode from the children of the input SVGElement and
     * appends them to the input CompositeGraphicsNode.
     * TODO: Limited implementation. Only handles groups and rectangles
     * partially.
     */
    private final void buildComposite(BridgeContext ctx,
                                      CompositeGraphicsNode composite,
                                      Element svgElement){
        List gvtChildList = composite.getChildren();

        for (Node child = svgElement.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Bridge bridge = ctx.getBridge((Element)child);
                if (bridge != null) {
                    GraphicsNodeBridge gnb = (GraphicsNodeBridge)bridge;
                    GraphicsNode childGVTNode
                        = gnb.createGraphicsNode(ctx, (Element)child);
                    
                    if (childGVTNode != null) {
                        gvtChildList.add(childGVTNode);
                        if (gnb.isContainer()) {
                            buildComposite(ctx,
                                           (CompositeGraphicsNode)childGVTNode,
                                           (Element)child);
                        }
                    }
                }
            }
        }
    }
}
