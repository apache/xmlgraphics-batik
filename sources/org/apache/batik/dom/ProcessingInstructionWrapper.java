/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

/**
 * This class implements a wrapper for a ProcessingInstruction. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ProcessingInstructionWrapper
    extends NodeWrapper implements ProcessingInstruction {
    
    /**
     * Creates a new ProcessingInstructionWrapper object.
     */
    public ProcessingInstructionWrapper(DocumentWrapper dw, ProcessingInstruction pi) {
        super(dw, pi);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.ProcessingInstruction#getData()}.
     */
    public String getData() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = ((ProcessingInstruction)node).getData();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.ProcessingInstruction#setData(String)}.
     */
    public void setData(final String data) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((ProcessingInstruction)node).setData(data);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Request r = new Request();
        invokeAndWait(r);
        if (r.exception != null) {
            throw r.exception;
        }
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.ProcessingInstruction#getTarget()}.
     */
    public String getTarget() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = ((ProcessingInstruction)node).getTarget();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }
}
