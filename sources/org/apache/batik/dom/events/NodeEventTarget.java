/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

/**
 * A Node that uses an EventSupport for its event registration and
 * dispatch.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 */
public interface NodeEventTarget extends Node, EventTarget {

    /**
     * Returns the event support instance for this node, or null if any.
     */
    EventSupport getEventSupport();

}
