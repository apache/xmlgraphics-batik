/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.util.UnitProcessor;
import org.apache.batik.gvt.event.EventDispatcher;

/**
 * An interface that provides access to User Agent information needed by
 * the bridge.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public interface UserAgent {
    /**
     * Returns the <code>EventDispatcher</code> used by the
     * <code>UserAgent</code> to dispatch events on GVT.
     */
    public EventDispatcher getEventDispatcher();
    
    /**
     * Displays an error message in the User Agent interface.
     */
    public void displayError(String message);

    /**
     * Returns the pixel to mm factor.
     */
    public float getPixelToMM();

}
