/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Random;

/**
 * The purpose of this class is to invoke a series of runnables as
 * closely to synchronously as possible.  It does this by starting 
 * a thread for each one, getting the threads into there run method,
 * then quickly running through (in random order) and notifying each
 * thread.
 */
public class ThreadPounder {
    List runnables;
    Object [] threads;
    Object lock = new Object();

    public ThreadPounder(List runnables)  
        throws InterruptedException {
        this(runnables, new Random(1234));
    }

    public ThreadPounder(List runnables, Random rand) 
        throws InterruptedException {
        this.runnables = new ArrayList(runnables);
        Collections.shuffle(this.runnables, rand);
        threads = new Object[this.runnables.size()];
        int i=0;
        Iterator iter= this.runnables.iterator();
        synchronized (lock) {
            while (iter.hasNext()) {
                Thread t = new SyncThread((Runnable)iter.next());
                t.start();
                lock.wait();
                threads[i] = t;
                i++;
            }
        }
    }

    public void start() {
        synchronized(this) {
            this.notifyAll();
        }

    }

    class SyncThread extends Thread {
        Runnable toRun;
        public long runTime;
        public SyncThread(Runnable toRun) {
            this.toRun = toRun;
        }

        public void run() {
            try {
                synchronized (ThreadPounder.this) {
                    synchronized (lock) {
                        // Let pounder know I'm ready to go
                        lock.notify();
                    }
                    // Wait for pounder to wake me up.
                    ThreadPounder.this.wait();
                }
                toRun.run();
            } catch (InterruptedException ie) {
            }
        }
    }

    public static void main(String [] str) { 
        List l = new ArrayList(20);
        for (int i=0; i<20; i++) {
            final int x = i;
            l.add(new Runnable() {
                    public void run() {
                        System.out.println("Thread " + x);
                    }
                });
        }

        try { 
            ThreadPounder tp = new ThreadPounder(l);
            System.out.println("Starting:" );
            tp.start();
            System.out.println("All Started:" );
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
