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
     * The lock used to wait for Runnable objects to be available.
     */
    protected Object invocationLock = new Object();
    
    /**
     * The lock used to suspend the queue execution.
     */
    protected Object suspendLock = new Object();

    /**
     * whether this thread is suspended.
     */
    protected boolean suspended;

    /**
     * The Runnable objects list's head.
     */
    protected Link head;

    /**
     * The Runnable objects list's tail.
     */
    protected Link tail;

    /**
     * The object which handle run events.
     */
    protected RunHandler runHandler;

    /**
     * The current thread.
     */
    protected volatile Thread runnableQueueThread;

    /**
     * Creates a new RunnableQueue started in a new thread.
     * @return a RunnableQueue which is garanteed to have entered its
     *         <tt>run()</tt> method.
     */
    public static RunnableQueue createRunnableQueue() {
        RunnableQueue result = new RunnableQueue();
        new Thread(result).start();
        while (result.getThread() == null) {
            Thread.yield();
        }
        return result;
    }
    
    /**
     * Runs this queue.
     */
    public void run() {
        runnableQueueThread = Thread.currentThread();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (suspended) {
                    if (runHandler != null) {
                        runHandler.executionSuspended(this);
                    }
                    synchronized (suspendLock) {
                        while (suspended) {
                            suspendLock.wait();
                        }
                    }
                    if (runHandler != null) {
                        runHandler.executionResumed(this);
                    }
                }
                if (head == null) {
                    synchronized (invocationLock) {
                        invocationLock.wait();
                    }
                }
                Link l = head;
                synchronized (this) {
                    head = head.next;
                }
                l.runnable.run();
                if (l.isLock()) {
                    LockedLink ll = (LockedLink)l;
                    synchronized (l) {
                        while (!ll.isLocked()) {
                            l.wait();
                        }
                        l.notify();
                    }
                }
                if (runHandler != null) {
                    runHandler.runnableInvoked(this, l.runnable);
                }
            }
        } catch (InterruptedException e) {
        } finally {
            runnableQueueThread = null;
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
            throw new IllegalStateException("RunnableQueue not started");
        }

        if (head == null) {
            head = tail = new UnlockedLink(r);
            synchronized (invocationLock) {
                invocationLock.notify();
            }
        } else {
            tail.next = new UnlockedLink(r);
            tail = tail.next;
        }
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
            throw new IllegalStateException("RunnableQueue not started");
        }
        if (runnableQueueThread == Thread.currentThread()) {
            throw new IllegalStateException
                ("Cannot be called from the RunnableQueue thread");
        }

        LockedLink l;
        synchronized (this) {
            if (head == null) {
                l = new LockedLink(r);
                head = tail = l;
                synchronized (invocationLock) {
                    invocationLock.notify();
                }
            } else {
                l = new LockedLink(r);
                tail.next = l;
                tail = tail.next;
            }
        }
        l.lock();
    }

    /**
     * Suspends the execution of this queue.
     * @throws IllegalStateException if getThread() is null.
     */
    public void suspendExecution() {
        if (runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started");
        }

        suspended = true;
    }

    /**
     * Resumes the execution of this queue.
     * @throws IllegalStateException if getThread() is null.
     */
    public void resumeExecution() {
        if (runnableQueueThread == null) {
            throw new IllegalStateException("RunnableQueue not started");
        }

        synchronized (suspendLock) {
            if (suspended) {
                suspended = false;
                suspendLock.notify();
            }
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
            throw new IllegalStateException("RunnableQueue not started");
        }

        List result = new LinkedList();
        Link l = head;
        while (l != null) {
            result.add(l.runnable);
            l = l.next;
        }
        return result;
    }

    /**
     * Sets the RunHandler for this queue.
     */
    public void setRunHandler(RunHandler rh) {
        runHandler = rh;
    }

    /**
     * Returns the RunHandler or null.
     */
    public RunHandler getRunHandler() {
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
    protected  abstract static class Link {
        
        /**
         * The Runnable.
         */
        public Runnable runnable;

        /**
         * The next link.
         */
        public Link next;

        /**
         * Creates a new link.
         */
        public Link(Runnable r) {
            runnable = r;
        }

        /**
         * Whether the link is a lock.
         */
        public abstract boolean isLock();
    }

    /**
     * To store a Runnable to invoke later.
     */
    protected static class UnlockedLink extends Link {

        /**
         * Creates a new link.
         */
        public UnlockedLink(Runnable r) {
            super(r);
        }

        /**
         * Whether the link is a lock.
         */
        public boolean isLock() {
            return false;
        }
    }

    /**
     * To store a Runnable with an object waiting for him to be executed.
     */
    protected static class LockedLink extends Link {

        /**
         * Whether this link is actually locked.
         */
        protected boolean locked;

        /**
         * Creates a new link.
         */
        public LockedLink(Runnable r) {
            super(r);
        }

        /**
         * Whether the link is a lock.
         */
        public boolean isLock() {
            return true;
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
    }
}
