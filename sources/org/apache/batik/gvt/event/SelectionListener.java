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
 * The listener interface for receiving graphics node paint events.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public interface SelectionListener extends EventListener {

    /**
     * Invoked when a selection has changed
     * @param evt the selection change event
     * @see org.apache.batik.gvt.Selector
     * @see org.apache.batik.gvt.event.SelectionEvent
     * @see org.apache.batik.gvt.Selectable
     */
    void selectionChanged(SelectionEvent e);

}
