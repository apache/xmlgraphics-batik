/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.css.engine.CSSEngineEvent;
import org.w3c.dom.events.MutationEvent;

/**
 * Interface for objects interested in being notified of updates.
 * 
 * @author <a href="mailto:vincent.hardy@apache.org">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface BridgeUpdateHandler {
    
    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    void handleDOMAttrModifiedEvent(MutationEvent evt);

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    void handleDOMNodeInsertedEvent(MutationEvent evt);

    /**
     * Invoked when an MutationEvent of type 'DOMNodeRemoved' is fired.
     */
    void handleDOMNodeRemovedEvent(MutationEvent evt);

    /**
     * Invoked when an CSSEngineEvent is fired.
     */
    void handleCSSEngineEvent(CSSEngineEvent evt);

}
