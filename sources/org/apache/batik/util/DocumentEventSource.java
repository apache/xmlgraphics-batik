/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * An interface which should be implemented by classes which
 * produce and dispatch DocumentEvents.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public interface DocumentEventSource {

    /**
     * Fire a document event to all listeners.
     * Note that since java events are processed in the
     * firing thread, not in the AWT event thread, we must
     * wrap the event notification in an "invokeLater" or
     * "invokeAntWait" call.
     * If the delivering thread is already the AWT Event thread the
     * event is delivered directly.
     * @param e the DocumentEvent to be asynchronously delivered.
     * @param wait a boolean indicating whether we should wait for delivery
     */
    public void fireAsyncDocumentEvent(DocumentEvent e, boolean wait);

    /**
     * Associate a DocumentEventListener with this loader.
     */
    public void addDocumentListener(DocumentListener l);

    /**
     * Remove a DocumentEventListener from this loader's listener list.
     */
    public void removeDocumentListener(DocumentListener l);

}

