/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * This class is responsible of deciding whether or not a repaint is needed.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class RepaintRateManager extends Thread {
    
    /**
     * The associated UpdateManager.
     */
    protected UpdateManager updateManager;

    /**
     * The expected time in ms between two repaints.
     */
    protected long targetFrameTime = 50;

    /**
     * Creates a new repaint manager.
     */
    public RepaintRateManager(UpdateManager um) {
        updateManager = um;
        setDaemon(true);
    }

    /**
     * The main method of this thread.  This needs to have a target
     * frame rate, and it needs to ensure that it changes it target
     * frame rate to ensure that it sleeps for at least a few 10s of
     * millisecs per loop (it should also see if it can increase
     * framerate because it's made the last few frames with the
     * current frame-rate easily)
     */
    public void run() {
        long lastFrameTime, currentTime, tm, sleepTime;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                lastFrameTime = System.currentTimeMillis();

                updateManager.getRepaintManager().repaint(true);

                currentTime = System.currentTimeMillis();
                tm = currentTime - lastFrameTime;
                sleepTime = targetFrameTime-tm;
                if (sleepTime > 0)
                    sleep(sleepTime);
            }
        } catch (InterruptedException e) {
        }
    }

}
