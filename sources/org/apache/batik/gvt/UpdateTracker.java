/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Shape;

/**
 * This class tracks the changes on a GVT tree
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class UpdateTracker {
    
    /**
     * Tells whether the GVT tree has changed.
     */
    public boolean hasChanged() {
        return false;
    }

    /**
     * Returns the dirty area on GVT.
     */
    public Shape getDirtyArea() {
        return null;
    }

    /**
     * Clears the tracker.
     */
    public void clear() {
    }
}
