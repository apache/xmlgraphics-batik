/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * This class provides a task scheduler which runs concurrently,
 * executing a schdeuled instance of TimerTask's run() method
 * at regular intervals.
 * [This class is provided to avoid dependencies on jdk1.3's
 * Timer class, and is intended to provide similar functionality.]
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */

public class Timer extends Thread {

    long delay;
    long interval;
    TimerTask task;

    // This implementation only supports a single task per timer, sorry ;-)
    // TODO: complete implementation of jdk1.3 java.util.Timer

    /**
     * Create a Timer instance.  
     * @param isDaemon (see java.util.Timer)
     */
    public Timer(boolean isDaemon) {
    }

    public void schedule(TimerTask task, long delay, long interval) {
        this.task = task;
        this.delay = delay;
        this.interval = interval;
    }

    public void run() {
        try {
            sleep(delay);
            while (true) {
                task.run();
                sleep(interval);
 	    }
        } catch (InterruptedException ie) {
            ;
        }
    }
}