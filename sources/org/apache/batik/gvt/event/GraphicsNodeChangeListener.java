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
 * The interface for listening to changes on graphics nodes.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public interface GraphicsNodeChangeListener extends EventListener {

    /**
     * Invoked when a change has started on a graphics node, but before
     * any changes occure in the graphics node it's self.
     * @param evt the graphics node change event
     */
    void changeStarted  (GraphicsNodeChangeEvent gnce);

    /**
     * Invoked when a change on a graphics node has completed
     * @param evt the graphics node change event
     */
    void changeCompleted(GraphicsNodeChangeEvent gnce);
}
