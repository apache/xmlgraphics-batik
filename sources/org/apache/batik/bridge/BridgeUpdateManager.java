/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeMutationEvent;

import org.w3c.dom.Element;

/**
 * This class is responsible to update the GraphicsNode that are modified.
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public class BridgeUpdateManager  {
    
    private BridgeContext context;
    
    /**
     * Creates a BridgeUpdateManager.
     */
    public BridgeUpdateManager(BridgeContext context) {
        this.context = context;
    }
    
    /** Adds a dirty node to the update manager.
     * @param node the GraphicsNode to be updated.
     * @param event the mutation event describing the modification.
     */
    public void addDirtyNode(GraphicsNode node, BridgeMutationEvent event){
        //for the moment simply call the update method of the bridge
        //and repaint the node with the manager.
        GraphicsNodeBridge bridge = (GraphicsNodeBridge)context.getBridge((Element)event.getSource());
        bridge.update(event);
    }
}
