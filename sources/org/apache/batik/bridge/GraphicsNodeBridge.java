/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.w3c.dom.Element;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * Factory class for vending <tt>GraphicsNode</tt> objects.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface GraphicsNodeBridge extends Bridge {

    /**
     * Creates a <tt>GraphicsNode</tt> using the specified context and element.
     * @param ctx the context to use
     * @param elem the Element that describes the GraphicsNode to build
     * @return a GraphicsNode object representing the Element
     */
    GraphicsNode createGraphicsNode(BridgeContext ctx, Element element);

    /**
     * Builds the specified <tt>GraphicsNode</tt> using the specified
     * context and element.
     * @param node the node to build
     * @param ctx the context to use
     * @param elem the Element that describes the GraphicsNode to build
     * @return a GraphicsNode object representing the Element
     */
    void buildGraphicsNode(GraphicsNode node, 
                           BridgeContext ctx,
                           Element element);

    /**
     * Updates an Element coresponding to the specified BridgeMutationEvent.
     * @param evt the event that describes the modification to perform
     */
    void update(BridgeMutationEvent evt);

    /**
     * Returns true if this bridge is a container, false otherwise.
     */
    boolean isContainer();
}
