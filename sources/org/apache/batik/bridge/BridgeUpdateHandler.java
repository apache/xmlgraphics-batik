/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * Interface for objects interesed in being notified of updates
 * by a <tt>DynamicBridge</tt>.
 * 
 * @author <a href="mailto:vincent.hardy@apache.org">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface BridgeUpdateHandler {
    
    /**
     *
     */
    public void bridgeUpdateStarting(BridgeUpdateEvent e);

    /**
     * 
     */
    public void bridgeUpdateCompleted(BridgeUpdateEvent e);
}
