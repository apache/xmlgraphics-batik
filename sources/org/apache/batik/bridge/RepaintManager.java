/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.List;

import org.apache.batik.gvt.UpdateTracker;

/**
 * This class manages the rendering of a GVT tree.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class RepaintManager {
    
    /**
     * The associated UpdateManager.
     */
    protected UpdateManager updateManager;

    /**
     * Whether or not the manager is active.
     */
    protected boolean enabled;

    /**
     * Creates a new repaint manager.
     */
    public RepaintManager(UpdateManager um) {
        updateManager = um;
    }
    
    /**
     * Provokes a repaint, if needed.
     * @param b If true, waits until the repaint has finished.
     */
    public void repaint(boolean b) {
        if (!enabled) {
            return;
        }
        final UpdateTracker ut = updateManager.getUpdateTracker();
        Runnable r = new Runnable() {
                public void run() {
                    if (ut.hasChanged()) {
                        List dirtyAreas = ut.getDirtyAreas();
                        if (dirtyAreas != null) {
                            updateManager.modifiedAreas(dirtyAreas);
                            updateManager.updateRendering(dirtyAreas);
                        }
                        ut.clear();
                    }
                }
            };
        if (updateManager.getUpdateRunnableQueue().getThread() == null) {
            return;
        }
        if (b) {
            try {
                updateManager.getUpdateRunnableQueue().invokeAndWait(r);
            } catch (InterruptedException e) {
            }
        } else {
            updateManager.getUpdateRunnableQueue().invokeLater(r);
        }
    }

    /**
     * Suspends the repaint management.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Suspends the repaint management.
     */
    public void enable() {
        enabled = true;
    }
    
}
