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
import org.w3c.dom.events.EventTarget;

/**
 * This class implements a wrapper for an Event. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class EventWrapper implements Event {
    
    /**
     * The associated document wrapper.
     */
    protected DocumentWrapper documentWrapper;

    /**
     * The wrapped Event.
     */
    protected Event event;

    /**
     * Creates a new EventWrapper.
     */
    public EventWrapper(DocumentWrapper dw, Event ev) {
        documentWrapper = dw;
        event = ev;
    }

    /**
     * Returns the wrapped event.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * <b>DOM</b>: Implements {@link Event#getType()};
     */
    public String getType() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = event.getType();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return q.result;
    }
    
    /**
     * <b>DOM</b>: Implements {@link Event#getTarget()};
     */
    public EventTarget getTarget() {
        class Query implements Runnable {
            EventTarget result;
            public void run() {
                result = event.getTarget();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return (EventTarget)documentWrapper.createNodeWrapper((Node)q.result);
    }
    
    /**
     * <b>DOM</b>: Implements {@link Event#getCurrentTarget()};
     */
    public EventTarget getCurrentTarget() {
        class Query implements Runnable {
            EventTarget result;
            public void run() {
                result = event.getCurrentTarget();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return (EventTarget)documentWrapper.createNodeWrapper((Node)q.result);
    }
    
    /**
     * <b>DOM</b>: Implements {@link Event#getEventPhase()};
     */
    public short getEventPhase() {
        class Query implements Runnable {
            short result;
            public void run() {
                result = event.getEventPhase();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link Event#getBubbles()};
     */
    public boolean getBubbles() {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = event.getBubbles();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link Event#getCancelable()};
     */
    public boolean getCancelable() {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = event.getCancelable();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link Event#getTimeStamp()};
     */
    public long getTimeStamp() {
        class Query implements Runnable {
            long result;
            public void run() {
                result = event.getTimeStamp();
            }
        }
        Query q = new Query();
        documentWrapper.invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link Event#stopPropagation()};
     */
    public void stopPropagation() {
        class Request implements Runnable {
            public void run() {
                event.stopPropagation();
            }
        }
        documentWrapper.invokeLater(new Request());
    }

    /**
     * <b>DOM</b>: Implements {@link Event#preventDefault()};
     */
    public void preventDefault() {
        class Request implements Runnable {
            public void run() {
                event.preventDefault();
            }
        }
        documentWrapper.invokeLater(new Request());
    }

    /**
     * <b>DOM</b>: Implements {@link Event#initEvent(String,boolean,boolean)};
     */
    public void initEvent(final String type,
                          final boolean bubble,
                          final boolean cancel) {
        class Request implements Runnable {
            public void run() {
                event.initEvent(type, bubble, cancel);
            }
        }
        documentWrapper.invokeLater(new Request());
    }
}
