/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.UpdateTracker;

/**
 * This class manages the rendering of a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class RepaintManager extends Thread {
    
    /**
     * The associated UpdateManager.
     */
    protected UpdateManager updateManager;

    /**
     * Creates a new repaint manager.
     */
    public RepaintManager(UpdateManager um) {
        updateManager = um;
        setDaemon(true);
    }

    /**
     * The main method of this thread.  This needs to have a target
     * frame rate, and it needs to ensure that it changes it target
     * frame rate to ensure that it sleeps for at least a few 10s of
     * millisecs per loop (it should also see if it can increase
     * framerate because it's made the last few frames with the
     * current frame-rate easily) */
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                final UpdateTracker ut = updateManager.getUpdateTracker();
                if (ut.hasChanged()) {
                    updateManager.getUpdateRunnableQueue().invokeAndWait
                        (new Runnable() {
                            public void run() {
                                    updateManager.updateRendering
                                        (ut.getDirtyAreas());
                                ut.clear();
                            }
                        });
                }
                sleep(40);
            }
        } catch (InterruptedException e) {
        }
    }

}
