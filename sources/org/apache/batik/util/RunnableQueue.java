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

import java.util.Iterator;

/**
 * This class represents an object which queues Runnable objects for
 * invocation in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class RunnableQueue implements Runnable {

    /**
     * Type-safe enumeration of queue states.
     */
    public static class RunnableQueueState extends Object {
        final String value;
        private RunnableQueueState(String value) {
            this.value = value.intern(); }
        public String getValue() { return value; }
        public String toString() { 
            return "[RunnableQueueState: " + value + "]"; }
    }

    /**
     * The queue is in the processes of running tasks.
     */
    public static final RunnableQueueState RUNNING 
        = new RunnableQueueState("Running");

    /**
     * The queue may still be running tasks but as soon as possible
     * will go to SUSPENDED state.
     */
    public static final RunnableQueueState SUSPENDING
        = new RunnableQueueState("Suspending");

    /**
     * The queue is no longer running any tasks and will not
     * run any tasks until resumeExecution is called.
     */
    public static final RunnableQueueState SUSPENDED
        = new RunnableQueueState("Suspended");

    /**
     * The Suspension state of this thread.
     */
    protected RunnableQueueState state;

    /**
     * Object to synchronize/wait/notify for suspension
     * issues.
     */
    protected Object stateLock = new Object();


    /**
     * The Runnable objects list, also used as synchoronization point
     * for pushing/poping runables.
     */
    protected DoublyLinkedList list = new DoublyLinkedList();

    protected int preemptCount = 0;

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
            Thread t = new Thread(result, "RunnableQueue-" + threadCount++);
            t.setDaemon(true);
            t.start();
            while (result.getThread() == null) {
                try { 
                    result.wait();
                } catch (InterruptedException ie) {
                }
            }
        }
        return result;
    }
    private static int threadCount;
    
    /**
     * Runs this queue.
     */
    public void run() {
        synchronized (this) {
            runnableQueueThread = Thread.currentThread();
            // Wake the create method so it knows we are in
            // our run and ready to go.
            notify();
        }

        Link l;
        Runnable rable;
        try {
            while (!Thread.currentThread().isInterrupted()) {

                // Mutex for suspention work.
                synchronized (stateLock) {
                    if (state != RUNNING) {
                        state = SUSPENDED;

                        // notify suspendExecution in case it is
                        // waiting til we shut down.
                        stateLock.notifyAll();

                        executionSuspended();

                        while (state != RUNNING) {
                            state = SUSPENDED;
                            // Wait until resumeExecution called.
                            stateLock.wait();
                        }

                        executionResumed();
                    }
                }

                // The following seriously stress tests the class
                // for stuff happening between the two sync blocks.
                // 
                // try {
                //     Thread.sleep(1);
                // } catch (InterruptedException ie) { }

                synchronized (list) {
                    if (state == SUSPENDING)
                        continue;
                    l = (Link)list.pop();
                    if (preemptCount != 0) preemptCount--;
                    if (l == null) {
                        // No item to run, wait till there is one.
                        list.wait();
                        continue; // start loop over again...
                    }

                    rable = l.runnable;
                }

                try {
                    rable.run();
                } catch (ThreadDeath td) {
                    // Let it kill us...
                    throw td;
                } catch (Throwable t) {
                    // Might be nice to notify someone directly.
                    // But this is more or less what Swing does.
                    t.printStackTrace();
                }
                l.unlock();
                runnableInvoked(rable);
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
     * Schedules the given Runnable object for a later invocation, and
     * returns.
     * An exception is thrown if the RunnableQueue was not started.
     * @throws IllegalStateException if getThread() is null.
     */
    public void invokeLater(Runnable r) {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }
        synchronized (list) {
            list.push(new Link(r));
            list.notify();
        }
    }

    /**
     * Waits until the given Runnable's <tt>run()</tt> has returned.
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
        synchronized (list) {
            list.push(l);
            list.notify();
        }
        l.lock();
    }


    /**
     * Schedules the given Runnable object for a later invocation, and
     * returns. The given runnable preempts any runnable that is not
     * currently executing (ie the next runnable started will be the
     * one given).  An exception is thrown if the RunnableQueue was
     * not started.  
     * @throws IllegalStateException if getThread() is  null.  
     */
    public void preemptLater(Runnable r) {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }
        synchronized (list) {
            list.add(preemptCount, new Link(r));
            preemptCount++;
            list.notify();
        }
    }

    /**
     * Waits until the given Runnable's <tt>run()</tt> has returned.
     * The given runnable preempts any runnable that is not currently
     * executing (ie the next runnable started will be the one given).
     * <em>Note: <tt>preemptAndWait()</tt> must not be called from the
     * current thread (for example from the <tt>run()</tt> method of the
     * argument).
     * @throws IllegalStateException if getThread() is null or if the
     *         thread returned by getThread() is the current one.
     */
    public void preemptAndWait(Runnable r) throws InterruptedException {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }
        if (runnableQueueThread == Thread.currentThread()) {
            throw new IllegalStateException
                ("Cannot be called from the RunnableQueue thread");
        }

        LockableLink l = new LockableLink(r);
        synchronized (list) {
            list.add(preemptCount, l);
            preemptCount++;
            list.notify();
        }
        l.lock();
    }

    public RunnableQueueState getQueueState() { 
        synchronized (stateLock) {
            return state; 
        }
    }

    /**
     * Suspends the execution of this queue after the current runnable
     * completes.
     * @param waitTillSuspended if true this method will not return
     *        until the queue has suspended (no runnable in progress
     *        or about to be in progress). If resumeExecution is
     *        called while waiting will simply return (this really
     *        indicates a race condition in your code).  This may
     *        return before an associated RunHandler is notified.
     * @throws IllegalStateException if getThread() is null.  */
    public void suspendExecution(boolean waitTillSuspended) {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }
        synchronized (stateLock) {
            if (state == SUSPENDED) 
                // already suspended...
                return;

            if (state == RUNNING) {
                state = SUSPENDING;
                synchronized (list) {
                    // Wake up run thread if it is waiting for jobs,
                    // so we go into the suspended case (notifying
                    // run-handler etc...)
                    list.notify();
                }
            }

            if (waitTillSuspended)
                try {
                    stateLock.wait();
                } catch(InterruptedException ie) { }
        }
    }

    /**
     * Resumes the execution of this queue.
     * @throws IllegalStateException if getThread() is null.
     */
    public void resumeExecution() {
        if (runnableQueueThread == null) {
            throw new IllegalStateException
                ("RunnableQueue not started or has exited");
        }

        synchronized (stateLock) {
            if (state != RUNNING) {
                state = RUNNING;
                stateLock.notifyAll(); // wake it up.
            }
        }
    }

    /**
     * Returns iterator lock to use to work with the iterator
     * returned by iterator().
     */
    public Object getIteratorLock() {
        return list;
    }

    /**
     * Returns an iterator over the runnables.
     */
    public Iterator iterator() {
        return new Iterator() {
                Link head = (Link)list.getHead();
                Link link;
                public boolean hasNext() {
                    if (head == null) {
                        return false;
                    }
                    if (link == null) {
                        return true;
                    }
                    return link != head;
                }
                public Object next() {
                    if (head == null || head == link) {
                        throw new java.util.NoSuchElementException();
                    }
                    if (link == null) {
                        link = (Link)head.getNext();
                        return head.runnable;
                    }
                    Object result = link.runnable;
                    link = (Link)link.getNext();
                    return result;
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
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
     * Called when execution is being suspended.
     * Currently just notifies runHandler
     */
    protected synchronized void executionSuspended() {
        if (runHandler != null) {
            runHandler.executionSuspended(this);
        }
    }

    /**
     * Called when execution is being resumed.
     * Currently just notifies runHandler
     */
    protected synchronized void executionResumed() {
        if (runHandler != null) {
            runHandler.executionResumed(this);
        }
    }
        
    /**
     * Called when a Runnable completes.
     * Currently just notifies runHandler
     * @param rable The runnable that just completed.
     */
    protected synchronized void runnableInvoked(Runnable rable ) {
        if (runHandler != null) {
            runHandler.runnableInvoked(this, rable);
        }
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
