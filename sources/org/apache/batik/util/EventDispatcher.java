/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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
            } catch (ThreadDeath td) {
                throw td;
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
        ThreadDeath td = null;
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
                } catch (ThreadDeath t) {
                    // Keep delivering messages but remember to throw later.
                    td = t;
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (ThreadDeath t) {
            // Remember to throw later.
            td = t;
        } catch (Throwable t) {
            if (ll[ll.length-1] != null)
                dispatchEvent(dispatcher, ll, evt);
            t.printStackTrace();
        }
        if (td != null) throw td;
    }
}
