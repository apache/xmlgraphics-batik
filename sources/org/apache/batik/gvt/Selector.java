/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import org.apache.batik.gvt.event.*;

/**
 * Interface which allows selection of GraphicsNodes and their contents.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public interface Selector extends GraphicsNodeMouseListener,
                                  GraphicsNodeKeyListener {

    /**
     * Get the contents of the current selection buffer.
     */
    public Object getSelection();

    /**
     * Reports whether the current selection contains any objects.
     */
    public boolean isEmpty();
}
