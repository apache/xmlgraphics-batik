/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * This class implements a wrapper for a CharacterData. All the methods
 * of the underlying document are called in a single thread.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CharacterDataWrapper extends NodeWrapper implements CharacterData {
    
    /**
     * Creates a new CharacterDataWrapper object.
     */
    public CharacterDataWrapper(DocumentWrapper dw, CharacterData cd) {
        super(dw, cd);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.CharacterData#getData()}.
     */
    public String getData() throws DOMException {
        class Query implements Runnable {
            String result;
            DOMException exception;
            public void run() {
                try {
                    result = ((CharacterData)node).getData();
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
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#setData(String)}.
     */
    public void setData(final String data) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((CharacterData)node).setData(data);
                } catch (DOMException e) {
                    exception = e;
                }
            }
        }
        Request r = new Request();
        r.run();
        if (r.exception != null) {
            throw r.exception;
        }
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.CharacterData#getLength()}.
     */
    public int getLength() {
        class Query implements Runnable {
            int result;
            public void run() {
                result = ((CharacterData)node).getLength();
            }
        }
        Query q = new Query();
        invokeAndWait(q);
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#substringData(int,int)}.
     */
    public String substringData(final int offset, final int count) throws DOMException {
        class Query implements Runnable {
            String result;
            DOMException exception;
            public void run() {
                try {
                    result = ((CharacterData)node).substringData(offset, count);
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
        return q.result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#appendData(String)}.
     */
    public void appendData(final String arg) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((CharacterData)node).appendData(arg);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#insertData(int,String)}.
     */
    public void insertData(final int offset, final String arg) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((CharacterData)node).insertData(offset, arg);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#deleteData(int,int)}.
     */
    public void deleteData(final int offset, final int count) throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((CharacterData)node).deleteData(offset, count);
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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.CharacterData#replaceData(int,int,String)}.
     */
    public void replaceData(final int offset, final int count, final String arg)
        throws DOMException {
        class Request implements Runnable {
            DOMException exception;
            public void run() {
                try {
                    ((CharacterData)node).replaceData(offset, count, arg);
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
}
