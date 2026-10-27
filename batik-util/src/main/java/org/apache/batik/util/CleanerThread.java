/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.lang.ref.PhantomReference;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org">l449433</a>
 * @version $Id$
 */
public class CleanerThread extends Thread {

    static volatile ReferenceQueue<?> queue = null;
    static CleanerThread thread = null;
    /* latch to signal CleanerThread shutdown request */
    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    public static ReferenceQueue<?> getReferenceQueue() {

        synchronized (CleanerThread.class) {
            if ( queue == null ) {
                queue = new ReferenceQueue<>();
                thread = new CleanerThread();
            }
        }
        return queue;
    }

    /**
     * If objects registered with the reference queue associated with
     * this class implement this interface then the 'cleared' method
     * will be called when the reference is queued.
     */
    public interface ReferenceCleared {
        /* Called when the reference is cleared */
        void cleared();
    }

    /**
     * A SoftReference subclass that automatically registers with
     * the cleaner ReferenceQueue.
     */
    public abstract static class SoftReferenceCleared extends SoftReference
      implements ReferenceCleared {
        public SoftReferenceCleared(Object o) {
            super (o, CleanerThread.getReferenceQueue());
        }
    }

    /**
     * A WeakReference subclass that automatically registers with
     * the cleaner ReferenceQueue.
     */
    public abstract static class WeakReferenceCleared extends WeakReference
      implements ReferenceCleared {
        public WeakReferenceCleared(Object o) {
            super (o, CleanerThread.getReferenceQueue());
        }
    }

    /**
     * A PhantomReference subclass that automatically registers with
     * the cleaner ReferenceQueue.
     */
    public abstract static class PhantomReferenceCleared
        extends PhantomReference
        implements ReferenceCleared {
        public PhantomReferenceCleared(Object o) {
            super (o, CleanerThread.getReferenceQueue());
        }
    }

    protected CleanerThread() {
        super("Batik CleanerThread");
        start();
    }

    @Override
    public void run() {
        try {
            while(!shutdownLatch.await(100, TimeUnit.SECONDS)) {
                Reference<?> ref;
                do {
                    ref = queue.poll();
                    // System.err.println("Cleaned: " + ref);
                    if (ref != null && ref instanceof ReferenceCleared) {
                        ReferenceCleared rc = (ReferenceCleared)ref;
                        rc.cleared();
                    }
                } while (ref != null);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request CleanerThread shutdown and wait for it to finish.
     */
    public static void shutdown() throws InterruptedException {
        synchronized (CleanerThread.class) {
            if (thread != null) {
                thread.shutdownLatch.countDown();
                thread.join();
                queue = null;
                thread = null;
            }
        }
    }
}
