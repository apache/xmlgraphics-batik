/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

/**
 * This interface represents a listener to the GVTTreeBuilderEvent events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface GVTTreeBuilderListener {

    /**
     * Called when a build started.
     * The data of the event is initialized to the old document.
     */
    void gvtBuildStarted(GVTTreeBuilderEvent e);

    /**
     * Called when a build was completed.
     */
    void gvtBuildCompleted(GVTTreeBuilderEvent e);

    /**
     * Called when a build was cancelled.
     */
    void gvtBuildCancelled(GVTTreeBuilderEvent e);

    /**
     * Called when a build failed.
     */
    void gvtBuildFailed(GVTTreeBuilderEvent e);

}
