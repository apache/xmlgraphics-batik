/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

/**
 * This class encapsulates, as events,  information about
 * document state that is either dynamic, transient, or
 * unfolding (as when state is discovered by background threads).
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id $
 */
public class DocumentPropertyEvent extends DocumentEvent {

    public static final int TITLE = 0x01;
    public static final int SIZE = 0x02;
    public static final int DESCRIPTION = 0x0f;

    public DocumentPropertyEvent(int key, Object value) {
        super(DocumentEvent.PROPERTY, key);
        o = value;
    }
}


