/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.events;

import org.apache.batik.dom.DocumentWrapper;

import org.w3c.dom.Node;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

/**
 * This class implements a wrapper for an Event. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class EventListenerWrapper implements EventListener {
    
    /**
     * The associated document wrapper.
     */
    protected DocumentWrapper documentWrapper;

    /**
     * The wrapped EventListener.
     */
    protected EventListener eventListener;

    /**
     * Creates a new EventListenerWrapper.
     */
    public EventListenerWrapper(DocumentWrapper dw, EventListener e) {
        documentWrapper = dw;
        eventListener = e;
    }

    /**
     * Returns the associated EventListener.
     */
    public EventListener getEventListener() {
        return eventListener;
    }

    /**
     * <b>DOM</b>: Implements {@link EventListener#handleEvent(Event)}.
     */
    public void handleEvent(final Event ev) {
        documentWrapper.invokeEventListener(new Runnable() {
                public void run() {
                    Event e = ev;
                    if (!(ev instanceof EventWrapper)) {
                        e = documentWrapper.createEventWrapper(ev);
                    }
                    eventListener.handleEvent(e);
                }
            });
    }
}
