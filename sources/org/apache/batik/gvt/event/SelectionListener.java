/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.event;

import java.util.EventListener;

/**
 * The listener interface for receiving selection events.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public interface SelectionListener extends EventListener {

    /**
     * Invoked when a selection has changed.
     * @param evt the selection change event
     */
    void selectionChanged(SelectionEvent evt);

    /**
     * Invoked when a selection is done.
     * @param evt the selection change event
     */
    void selectionDone(SelectionEvent evt);

    /**
     * Invoked when a selection is cleared.
     * @param evt the selection change event
     */
    void selectionCleared(SelectionEvent evt);

    /**
     * Invoked when a selection started.
     * @param evt the selection change event
     */
    void selectionStarted(SelectionEvent evt);

}
