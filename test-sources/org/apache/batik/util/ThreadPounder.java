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
