/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

/**
 * This interface represents a listener to the GVTTreeRendererEvent events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface GVTTreeRendererListener {

    /**
     * Called when a rendering is in its preparing phase.
     */
    void gvtRenderingPrepare(GVTTreeRendererEvent e);

    /**
     * Called when a rendering started.
     */
    void gvtRenderingStarted(GVTTreeRendererEvent e);

    /**
     * Called when a rendering was completed.
     */
    void gvtRenderingCompleted(GVTTreeRendererEvent e);

    /**
     * Called when a rendering was cancelled.
     */
    void gvtRenderingCancelled(GVTTreeRendererEvent e);

    /**
     * Called when a rendering failed.
     */
    void gvtRenderingFailed(GVTTreeRendererEvent e);

}
