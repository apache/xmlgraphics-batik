/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving composite graphics node events.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface CompositeGraphicsNodeListener extends EventListener {

    /**
     * Invoked when a graphics node has been added.
     * @param evt the composite graphics node event
     */
    void graphicsNodeAdded(CompositeGraphicsNodeEvent evt);

    /**
     * Invoked when a graphics node has been removed.
     * @param evt the composite graphics node event
     */
    void graphicsNodeRemoved(CompositeGraphicsNodeEvent evt);
}
