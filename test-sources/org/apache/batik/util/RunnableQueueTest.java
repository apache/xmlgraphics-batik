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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;

public class RunnableQueueTest extends AbstractTest {

    public int nThreads;
    public int activeThreads;
    public Random rand;
    public RunnableQueue rq;

    /**
     * Constructor
     * @param nThreads number of runnables to queue
     * @param sync     Should requests be made synchronously (from
     *                 different threads).
     */
    public RunnableQueueTest(int nThreads, boolean sync) {
        this.nThreads = nThreads;
    }
        
        /**
         * Returns this Test's name
         */
        public String getName() {
            return "RunnableQueue Stress Test";
        }

    /**
     * This method will only throw exceptions if some aspect
     * of the test's internal operation fails.
     */
    public TestReport runImpl() throws Exception {
        rq = RunnableQueue.createRunnableQueue();

        List l = new ArrayList(nThreads);
        rand = new Random(2345);

        // Two switch flickers to make things interesting...
        l.add(new SwitchFlicker());
        l.add(new SwitchFlicker());

        for (int i=0; i<nThreads; i++) {
            Runnable rqRable = new RQRable(i, rand.nextInt(50)+1);
            l.add(new TPRable(rq, i, rand.nextInt(4)+1,
                              rand.nextInt(500)+1, 20, rqRable));
        }

        synchronized (this) {
            ThreadPounder tp = new ThreadPounder(l);
            tp.start();
            activeThreads = nThreads;
            while (activeThreads != 0) {
                wait();
            }
        }

        System.exit(0);
        return null;
    }

    public class SwitchFlicker implements Runnable {
        public void run() {
            boolean suspendp, waitp;
            int time;
            while (true) {
                try {
                    synchronized (rand) {
                        suspendp = rand.nextBoolean();
                        waitp = rand.nextBoolean();
                        time  = rand.nextInt(500);
                    }
                    if (suspendp) {
                        // 1/2 of the time suspend, 1/2 time wait, 1/2 the
                        // time don't
                        rq.suspendExecution(waitp);
                        System.out.println("Suspended - " + 
                                           (waitp?"Wait":"Later"));
                        Thread.sleep(time/10);
                    } else {
                        // 1/2 the time resume
                        rq.resumeExecution();
                        System.out.println("Resumed");
                        Thread.sleep(time);
                    }
                } catch(InterruptedException ie) { }
            }
        }
    }

    public static final int INVOKE_LATER     = 1;
    public static final int INVOKE_AND_WAIT  = 2;
    public static final int PREEMPT_LATER    = 3;
    public static final int PREEMPT_AND_WAIT = 4;

    public class TPRable implements Runnable {

        RunnableQueue rq;
        int           idx;
        int           style;
        long          repeatDelay;
        int           count;
        Runnable      rqRable;

        TPRable(RunnableQueue rq, int idx, 
                int style,
                long    repeatDelay, int count,
                Runnable rqRable) {
            this.rq           = rq;
            this.idx          = idx;
            this.style        = style;
            this.repeatDelay  = repeatDelay;
            this.count        = count;
            this.rqRable      = rqRable;
        }

        public void run() {
            try {
                while (count-- != 0) {
                    switch (style) {
                    case INVOKE_LATER:
                        synchronized (rqRable) {
                            System.out.println("     InvL #" + idx);
                            rq.invokeLater(rqRable);
                            System.out.println("Done InvL #" + idx);
                            rqRable.wait();
                        }
                        break;
                    case INVOKE_AND_WAIT:
                        System.out.println("     InvW #" + idx);
                        rq.invokeAndWait(rqRable);
                        System.out.println("Done InvW #" + idx);
                        break;
                    case PREEMPT_LATER:
                        synchronized (rqRable) {
                            System.out.println("     PreL #" + idx);
                            rq.preemptLater(rqRable);
                            System.out.println("Done PreL #" + idx);
                            rqRable.wait();
                        }
                        break;
                    case PREEMPT_AND_WAIT:
                        System.out.println("     PreW #" + idx);
                        rq.preemptAndWait(rqRable);
                        System.out.println("Done PreW #" + idx);
                        break;
                    }
                        
                    if (repeatDelay < 0) 
                        break;
                    Thread.sleep(repeatDelay);
                }
            } catch (InterruptedException ie) {
            }
            synchronized(RunnableQueueTest.this) {
                activeThreads--;
                RunnableQueueTest.this.notify();
            }
        }
    }

    public static class RQRable implements Runnable {
        int  idx;
        long dur;

        RQRable(int idx, long dur) {
            this.idx = idx;
            this.dur = dur;
        }

        public void run() {
            try {
                System.out.println("      B Rable #" + idx);
                Thread.sleep(dur);
                System.out.println("      E Rable #" + idx);
                synchronized (this) {
                    notify();
                }
            } catch (InterruptedException ie) { }
        }
    }

    public static void main(String []args) {
        RunnableQueueTest rqt = new RunnableQueueTest(20, false);
        try {
            rqt.runImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
