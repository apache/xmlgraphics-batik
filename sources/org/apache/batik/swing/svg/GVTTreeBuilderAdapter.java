/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.svg;

/**
 * An adapter class that represents a listener to the GVTTreeBuilderEvent
 * events.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class GVTTreeBuilderAdapter implements GVTTreeBuilderListener {

    /**
     * Called when a build started.
     * The data of the event is initialized to the old document.
     */
    public void gvtBuildStarted(GVTTreeBuilderEvent e) {}

    /**
     * Called when a build was completed.
     */
    public void gvtBuildCompleted(GVTTreeBuilderEvent e) {}

    /**
     * Called when a build was cancelled.
     */
    public void gvtBuildCancelled(GVTTreeBuilderEvent e) {}

    /**
     * Called when a build failed.
     */
    public void gvtBuildFailed(GVTTreeBuilderEvent e) {}

}
