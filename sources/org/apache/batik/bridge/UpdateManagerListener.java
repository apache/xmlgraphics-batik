/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * This interface represents a listener to the UpdateManagerEvent events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface UpdateManagerListener {

    /**
     * Called when the manager was started.
     */
    void managerStarted(UpdateManagerEvent e);

    /**
     * Called when the manager was suspended.
     */
    void managerSuspended(UpdateManagerEvent e);

    /**
     * Called when the manager was resumed.
     */
    void managerResumed(UpdateManagerEvent e);

    /**
     * Called when the manager was stopped.
     */
    void managerStopped(UpdateManagerEvent e);

    /**
     * Called when an update started.
     */
    void updateStarted(UpdateManagerEvent e);

    /**
     * Called when an update was completed.
     */
    void updateCompleted(UpdateManagerEvent e);

    /**
     * Called when an update failed.
     */
    void updateFailed(UpdateManagerEvent e);

}
