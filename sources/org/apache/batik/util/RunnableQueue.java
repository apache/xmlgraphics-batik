/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents an object which queues Runnable objects for
 * invocation in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class RunnableQueue implements Runnable {

    /**
     * whether this thread is suspended.
     */
    protected boolean suspended;

    /**
     * The Runnable objects list.
     */
    protected DoublyLinkedList list = new DoublyLinkedList();

    /**
     * The object which handle run events.
     */
    protected RunHandler runHandler;

    /**
     * The current thread.
     */
    protected Thread runnableQueueThread;

    /**
     * Creates a new RunnableQueue started in a new thread.
     * @return a RunnableQueue which is garanteed to have entered its
     *         <tt>run()</tt> method.
     */
    public static RunnableQueue createRunnableQueue() {
        RunnableQueue result = new RunnableQueue();
        synchronized (result) {
            new Thread(result).start();
            while (result.getThread() == null) {
                try { 
                    result.wait();
                } catch (InterruptedException ie) {
                }
            }
        }
        return result;
    }
    
    /**
     * Runs this queue.
     */
    public void run() {
        synchronized (this) {
            runnableQueueThread = Thread.currentThread();
            notify();
        }
        Link l;
        Runnable rable;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (this) {
                    if (suspended) {
                        if (runHandler != null) {
                            runHandler.executionSuspended(this);
                        }
                        while (suspended) {
                            wait();
                        }
                        if (runHandler != null) {
                            runHandler.executionResumed(this);
                        }
                    }

                    l = (Link)list.pop();
                    if (l == null) {
                        wait();
                        continue; // start loop over again...
                    }
                    rable = l.runnable;
                }
                rable.run();
                l.unlock();
                synchronized (this) {
                    if (runHandler != null) {
                        runHandler.runnableInvoked(this, rable);
                    }
                }
            }
        } catch (InterruptedException e) {
        } finally {
            synchronized (this) {
                runnableQueueThread = null;
            }
        }
    }

    /**
     * Returns the thread in which the RunnableQueue is currently running.
     * @return null if the RunnableQueue has not entered his
     *         <tt>run()</tt> method.
     */
    public Thread getThread() {
        return runnableQueueThread;
    }

    /**
     * Schedules the given Runnable object for a later invocation.
     * An exception is thrown if the RunnableQueue was not started.
     * @throws IllegalStateException if getThread() is null.
     */
    public synchronized void invokeLater(Runnable r) {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }
        list.push(new Link(r));
        notify();
    }

    /**
     * Waits until the given Runnable <tt>run()</tt> has returned.
     * <em>Note: <tt>invokeAndWait()</tt> must not be called from the
     * current thread (for example from the <tt>run()</tt> method of the
     * argument).
     * @throws IllegalStateException if getThread() is null or if the
     *         thread returned by getThread() is the current one.
     */
    public void invokeAndWait(Runnable r) throws InterruptedException {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }
        if (runnableQueueThread == Thread.currentThread()) {
            throw new IllegalStateException
                ("Cannot be called from the RunnableQueue thread");
        }

        LockableLink l = new LockableLink(r);
        synchronized (this) {
            list.push(l);
            notify();
        }
        l.lock();
    }

    public synchronized boolean isSuspended() { return suspended; }

    /**
     * Suspends the execution of this queue.
     * @throws IllegalStateException if getThread() is null.
     */
    public synchronized void suspendExecution() {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }
        
        suspended = true;
    }

    /**
     * Resumes the execution of this queue.
     * @throws IllegalStateException if getThread() is null.
     */
    public synchronized void resumeExecution() {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }

        if (suspended) {
            suspended = false;
            notify();
        }
    }

    /**
     * Returns the queued Runnable objects in a List.
     * <p>
     * To be garanteed to work on a valid list, be sure to lock or
     * to suspend (with <tt>suspendExecution()</tt>) the queue.
     * @throws IllegalStateException if getThread() is null.
     */
    public synchronized List getRunnableList() {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }

        List result = new LinkedList();
        Link l, h;
        l = h = (Link)list.getHead();
        if (h==null) return result;
        do {
            result.add(l.runnable);
            l = (Link)l.getNext();
        } while (l != h);

        return result;
    }

    /**
     * Sets the RunHandler for this queue.
     */
    public synchronized void setRunHandler(RunHandler rh) {
        runHandler = rh;
    }

    /**
     * Returns the RunHandler or null.
     */
    public synchronized RunHandler getRunHandler() {
        return runHandler;
    }

    /**
     * This interface must be implemented by an object which wants to
     * be notified of run events.
     */
    public interface RunHandler {

        /**
         * Called when the given Runnable has just been invoked and
         * has returned.
         */
        void runnableInvoked(RunnableQueue rq, Runnable r);

        /**
         * Called when the execution of the queue has been suspended.
         */
        void executionSuspended(RunnableQueue rq);

        /**
         * Called when the execution of the queue has been resumed.
         */
        void executionResumed(RunnableQueue rq);
    }

    /**
     * To store a Runnable.
     */
    protected static class Link extends DoublyLinkedList.Node {
        
        /**
         * The Runnable.
         */
        public Runnable runnable;

        /**
         * Creates a new link.
         */
        public Link(Runnable r) {
            runnable = r;
        }

        /**
         * unlock link and notify locker.  
         * Basic implementation does nothing.
         */
        public void unlock() throws InterruptedException { return; }
    }

    /**
     * To store a Runnable with an object waiting for him to be executed.
     */
    protected static class LockableLink extends Link {

        /**
         * Whether this link is actually locked.
         */
        protected boolean locked;

        /**
         * Creates a new link.
         */
        public LockableLink(Runnable r) {
            super(r);
        }

        /**
         * Whether the link is actually locked.
         */
        public boolean isLocked() {
            return locked;
        }

        /**
         * Locks this link.
         */
        public synchronized void lock() throws InterruptedException {
            locked = true;
            notify();
            wait();
        }

        /**
         * unlocks this link.
         */
        public synchronized void unlock() throws InterruptedException {
            while (!locked) {
                // Wait until lock is called...
                wait();
            }
            // Wake the locking thread...
            notify();
        }
    }
}
