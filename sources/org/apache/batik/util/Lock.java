/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * This class represents a binary semaphore.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Lock {
    
    /**
     * Whether the lock is locked.
     */
    protected boolean locked;

    /**
     * Takes the lock.
     */
    public synchronized void lock() throws InterruptedException {
        while (locked) {
            wait();
        }
        locked = true;
    }

    /**
     * Releases the lock.
     */
    public synchronized void unlock() {
        locked = false;
        notify();
    }
}
