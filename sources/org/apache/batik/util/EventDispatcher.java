/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.awt.EventQueue;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/**
 * Generic class to dispatch events in a highly relyable way
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class EventDispatcher {

    public interface Dispatcher {
        public void dispatch(Object listener,
                             Object event);
    }


    public static void fireEvent(final Dispatcher dispatcher,
                                 final List listeners,
                                 final Object evt,
                                 final boolean useEventQueue) {
        if (useEventQueue && !EventQueue.isDispatchThread()) {
            Runnable r = new Runnable() {
                    public void run() {
                        fireEvent(dispatcher, listeners, evt, useEventQueue);
                    }
                };
            try {
                EventQueue.invokeAndWait(r);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // Assume they will get delivered????
                // be nice to wait on List but how???
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        }

        Object [] ll = null;
        Throwable err = null;
        int retryCount = 10;
        while (--retryCount != 0) {
            // If the thread has been interrupted this can 'mess up'
            // the class loader and cause this otherwise safe code to
            // throw errors.
            try {
                synchronized (listeners) {
                    if (listeners.size() == 0)
                        return;
                    ll = listeners.toArray();
                    break;
                }
            } catch(Throwable t) {
                err = t;
            }
        }
        if (ll == null) {
            if (err != null)
                err.printStackTrace();
            return;
        }
        dispatchEvent(dispatcher, ll, evt);
    }

    protected static void dispatchEvent(final Dispatcher dispatcher,
                                        final Object [] ll,
                                        final Object evt) {
        try {
            for (int i = 0; i < ll.length; i++) {
                try {
                    Object l;
                    synchronized (ll) {
                        l = ll[i];
                        if (l == null) continue;
                        ll[i] = null;
                    }
                    dispatcher.dispatch(l, evt);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Throwable t) {
            if (ll[ll.length-1] != null)
                dispatchEvent(dispatcher, ll, evt);
            t.printStackTrace();
        }
    }
}
