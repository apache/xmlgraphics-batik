/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

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

    /**
     * Returns the message of this exception. If an error message has
     * been specified, returns that one. Otherwise, return the error message
     * of enclosed exception or null if any.
     */
    public String getMessage() {
        String msg = super.getMessage();
        if (msg != null) {
            return msg;
        } else if (embedded != null) {
            return embedded.getMessage();
        } else {
            return null;
        }
    }
}
