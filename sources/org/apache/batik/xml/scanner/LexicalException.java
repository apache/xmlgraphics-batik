/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.xml.scanner;

/**
 * Signals that a lexical exception of some sort has occurred.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LexicalException extends Exception {
    
    /**
     * @serial The embedded exception if tunnelling, or null.
     */    
    protected Exception exception;
    
    /**
     * @serial The line number.
     */
    protected int lineNumber;

    /**
     * @serial The column number.
     */
    protected int columnNumber;

    /**
     * Creates a new LexicalException.
     * @param message The error or warning message.
     * @param line The line of the last parsed character.
     * @param column The column of the last parsed character.
     */
    public LexicalException (String message, int line, int column) {
        super(message);
        exception = null;
        lineNumber = line;
        columnNumber = column;
    }
    
    /**
     * Creates a new LexicalException wrapping an existing exception.
     *
     * <p>The existing exception will be embedded in the new
     * one, and its message will become the default message for
     * the LexicalException.
     * @param e The exception to be wrapped in a LexicalException.
     */
    public LexicalException (Exception e) {
        exception = e;
        lineNumber = -1;
        columnNumber = -1;
    }
    
    /**
     * Creates a new LexicalException from an existing exception.
     *
     * <p>The existing exception will be embedded in the new
     * one, but the new exception will have its own message.
     * @param message The detail message.
     * @param e The exception to be wrapped in a SAXException.
     */
    public LexicalException (String message, Exception e) {
        super(message);
        this.exception = e;
    }
    
    /**
     * Return a detail message for this exception.
     *
     * <p>If there is a embedded exception, and if the ParseException
     * has no detail message of its own, this method will return
     * the detail message from the embedded exception.
     * @return The error or warning message.
     */
    public String getMessage () {
        String message = super.getMessage();
        
        if (message == null && exception != null) {
            return exception.getMessage();
        } else {
            return message;
        }
    }
    
    /**
     * Return the embedded exception, if any.
     * @return The embedded exception, or null if there is none.
     */
    public Exception getException () {
        return exception;
    }

    /**
     * Returns the line of the last parsed character.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the column of the last parsed character.
     */
    public int getColumnNumber() {
        return columnNumber;
    }
}
