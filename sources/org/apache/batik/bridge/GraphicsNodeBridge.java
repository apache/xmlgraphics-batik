/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

/**
 * Bridge class for creating, building, and updating a <tt>GraphicsNode</tt>
 * according to an <tt>Element</tt>.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public interface GraphicsNodeBridge extends Bridge {

    /**
     * Creates a <tt>GraphicsNode</tt> according to the specified parameters.
     * This is called before children have been added to the
     * returned GraphicsNode (obviously since you construct and return it).
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    GraphicsNode createGraphicsNode(BridgeContext ctx, Element e);

    /**
     * Builds using the specified BridgeContext and element, the
     * specified graphics node.  This is called after all the children
     * of the node have been constructed and added, so it is safe to
     * do work that depends on being able to see your children nodes
     * in this method.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @param node the graphics node to build
     */
    void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node);

    /**
     * Performs an update according to the specified event.
     *
     * @param evt the event describing the update to perform
     */
    void update(BridgeMutationEvent evt);

    /**
     * Returns true if the bridge handles container element, false
     * otherwise.
     */
    boolean isComposite();

}
