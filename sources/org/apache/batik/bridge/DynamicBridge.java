/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * Interface for Bridges which handle DOM events for the element they map to GVT
 * objects (such as <tt>GraphicsNode</tt> instances) or related objects (such as
 * <tt>java.awt.Paint</tt>)
 * 
 * @author <a href="mailto:vincent.hardy@apache.org">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface DynamicBridge extends Bridge {

    /**
     * 
     */
    public BridgeUpdateHandler getBridgeUpdateHandler();

    /**
     * 
     */
    public void setBridgeUpdateHandler(BridgeUpdateHandler handler,
                                       int handlerKey);
}
