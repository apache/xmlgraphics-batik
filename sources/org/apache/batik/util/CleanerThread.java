/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.ref.PhantomReference;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class CleanerThread extends Thread {

    static ReferenceQueue queue = null;
    static CleanerThread  thread = null;

    public static ReferenceQueue getReferenceQueue() { 
        if (queue != null) 
            return queue;
        
        queue = new ReferenceQueue();
        thread = new CleanerThread();
        return queue; 
    }

    /**
     * If objects registered with the reference queue associated with
     * this class implement this interface then the 'cleared' method
     * will be called when the reference is queued.
     */
    public static interface ReferenceCleared {
        /* Called when the reference is cleared */
        public void cleared();
    }

    /**
     * A SoftReference subclass that automatically registers with 
     * the cleaner ReferenceQueue.
     */
    public static abstract class SoftReferenceCleared extends SoftReference 
      implements ReferenceCleared {
        public SoftReferenceCleared(Object o) {
            super (o, CleanerThread.getReferenceQueue());
        }
    }

    /**
     * A WeakReference subclass that automatically registers with 
     * the cleaner ReferenceQueue.
     */
    public static abstract class WeakReferenceCleared extends WeakReference 
      implements ReferenceCleared {
        public WeakReferenceCleared(Object o) {
            super (o, CleanerThread.getReferenceQueue());
        }
    }

    /**
     * A PhantomReference subclass that automatically registers with 
     * the cleaner ReferenceQueue.
     */
    public static abstract class PhantomReferenceCleared 
        extends PhantomReference 
        implements ReferenceCleared {
        public PhantomReferenceCleared(Object o) {
            super (o, CleanerThread.getReferenceQueue());
        }
    }
            
    protected CleanerThread() {
        setDaemon(true);
        start();
    }

    public void run() {
        while(true) {
            Reference ref;
            try {
                ref = queue.remove();
                // System.err.println("Cleaned: " + ref);
            } catch (InterruptedException ie) {
                continue;
            }

            if (ref instanceof ReferenceCleared) {
                ReferenceCleared rc = (ReferenceCleared)ref;
                rc.cleared();
            }
        }
    }
};
