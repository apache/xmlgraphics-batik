/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class JGVTComponentAdapter implements JGVTComponentListener {

    /**
     * Called when the rendering transform
     * changes on the JGVTComponentListener
     */
    public void componentTransformChanged
        (ComponentEvent event) { }
}
