/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * This is the base class for document events (usually used by
 * threads for asynchronous notification of changes to document
 * creating and loading state.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public abstract class DocumentEvent {

    private static final int LOADING_KEYVAL = 1;
    private static final int PROPERTY_KEYVAL = 2;

    public static final DocumentEvent.Key LOADING =
        new DocumentEvent.Key(LOADING_KEYVAL);
    public static final DocumentEvent.Key PROPERTY =
        new DocumentEvent.Key(PROPERTY_KEYVAL);

    public final DocumentEvent.Key classid;
    public final int type;
    public Object o;

    /**
     * Constructor (called only by subclasses)
     * required to initialize final variables.
     */
    protected DocumentEvent(DocumentEvent.Key classid, int type) {
        this.classid = classid;
        this.type = type;
    }

    /**
     * Overloaded by derived classes to allow event data to be
     * encapsulated.
     */
    public Object getValue() {
        return o;
    }

    /**
     * Inner class for class and key values.
     */
    public final static class Key {
        int keyval;

        public Key(int keyval) {
            this.keyval = keyval;
        }
    }

}


