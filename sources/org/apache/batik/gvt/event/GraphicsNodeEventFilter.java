/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import org.apache.batik.gvt.GraphicsNode;

/**
 * The interface for filtering graphics node events.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface GraphicsNodeEventFilter {

    /**
     * Returns true if the specified node accepts the specified event,
     * false otherwise.
     * @param node the targetted node
     * @param evt the event to check
     */
    public boolean accept(GraphicsNode target, GraphicsNodeEvent evt);

}
