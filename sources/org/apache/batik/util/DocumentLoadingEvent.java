/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import org.apache.batik.dom.svg.SVGOMDocument;

/**
 * This class encapsulates, as events,  information about
 * document state that is either dynamic, transient, or
 * unfolding (as when state is discovered by background threads).
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public class DocumentLoadingEvent extends DocumentEvent {

    public static final int START_LOADING = 0x01;
    public static final int LOADED = 0x02;
    public static final int DONE = 0x0f;
    public static final int LOAD_CANCELLED = 0xfe;
    public static final int LOAD_FAILED = 0xff;

    protected Exception ex;

    public DocumentLoadingEvent(int t, SVGOMDocument doc) {
        super(DocumentEvent.LOADING, t);
        o = doc;
    }

    public DocumentLoadingEvent(int t, SVGOMDocument doc, Exception ex) {
        super(DocumentEvent.LOADING, t);
        o = doc;
        this.ex = ex;
    }

    /**
     * Returns the exception that causes an error or null if the type
     * of the event is not LOAD_FAILED.
     */
    public Exception getException() {
        return ex;
    }
}


