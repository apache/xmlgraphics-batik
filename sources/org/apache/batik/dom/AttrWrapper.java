/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * This class implements a wrapper for an Attr. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AttrWrapper extends NodeWrapper implements Attr {

    /**
     * Creates a new AttrWrapper object.
     */
    public AttrWrapper(DocumentWrapper dw, Attr a) {
        super(dw, a);
    }
    
    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#getName()}.
     */
    public String getName() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = ((Attr)node).getName();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#getSpecified()}.
     */
    public boolean getSpecified() {
        class Query implements Runnable {
            boolean result;
            public void run() {
                result = ((Attr)node).getSpecified();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#getValue()}.
     */
    public String getValue() {
        class Query implements Runnable {
            String result;
            public void run() {
                result = ((Attr)node).getValue();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#setValue(String)}.
     */
    public void setValue(final String value) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((Attr)node).setValue(value);
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
     * <b>DOM</b>: Implements {@link org.w3c.dom.Attr#getOwnerElement()}.
     */
    public Element getOwnerElement() {
        class Query implements Runnable {
            Element result;
            public void run() {
                result = ((Attr)node).getOwnerElement();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return createElementWrapper(q.result);
    }
}
