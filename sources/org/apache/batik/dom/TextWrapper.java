/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

/**
 * This class implements a wrapper for a Text. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TextWrapper extends CharacterDataWrapper implements Text {
    
    /**
     * Creates a new TextWrapper object.
     */
    public TextWrapper(DocumentWrapper dw, Text t) {
        super(dw, t);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Text#splitText(int)}.
     */
    public Text splitText(final int offset) throws DOMException {
        class Query implements Runnable {
            DOMException exception;
            Text result;
            public void run() {
                try {
                    result = ((Text)node).splitText(offset);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        if (q.exception != null) {
            throw q.exception;
        }
        return createTextWrapper(q.result);
    }
}
