/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

/**
 * An exception that will be thrown when a problem is encountered in the
 * script by an <code>Interpreter</code> interface implementation.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class InterpreterException extends Exception {
    private int line = -1; // -1 when unknow
    private int column = -1; // -1 when unknow
    private Exception embedded = null; // null when unknown

    /**
     * Builds an instance of <code>InterpreterException</code>.
     * @param message the <code>Exception</code> message.
     * @param lineno the number of the line the error occurs.
     * @param columno the number of the column the error occurs.
     */
    public InterpreterException(String message, int lineno, int columnno) {
        super(message);
        line = lineno;
        column = columnno;
    }

    /**
     * Builds an instance of <code>InterpreterException</code>.
     * @param exception the embedded exception.
     * @param message the <code>Exception</code> message.
     * @param lineno the number of the line the error occurs.
     * @param columno the number of the column the error occurs.
     */
    public InterpreterException(Exception exception,
                                String message, int lineno, int columnno) {
        this(message, lineno, columnno);
        embedded = exception;
    }

    /**
     * Returns the line number where the error occurs. If this value is not
     * known, returns -1.
     */
    public int getLineNumber() {
        return line;
    }

    /**
     * Returns the column number where the error occurs. If this value is not
     * known, returns -1.
     */
    public int getColumnNumber() {
        return column;
    }

    /**
     * Returns the embedded exception. If no embedded exception is set,
     * returns null.
     */
    public Exception getException() {
        return embedded;
    }
}
