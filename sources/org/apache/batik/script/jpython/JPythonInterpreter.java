/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.jpython;

import java.io.*;
import java.util.Locale;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;

import org.python.util.*;

/**
 * A simple implementation of <code>Interpreter</code> interface to use
 * JPython python parser.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class JPythonInterpreter implements org.apache.batik.script.Interpreter {
    private PythonInterpreter interpreter = null;

    public JPythonInterpreter() {
        interpreter = new PythonInterpreter();
    }

    // org.apache.batik.script.Intepreter implementation

    public Object evaluate(Reader scriptreader)
        throws InterpreterException, IOException {
        // oups jpython doesn't accept reader in its eval method :-(
        StringBuffer sbuffer = new StringBuffer();
        char[] buffer = new char[1024];
        int val = 0;
        while ((val = scriptreader.read(buffer)) != -1) {
            sbuffer.append(buffer,0, val);
        }
        String str = sbuffer.toString();
        return evaluate(str);
    }

    public Object evaluate(String script)
        throws InterpreterException {
        try {
            interpreter.exec(script);
        } catch (org.python.core.PyException e) {
            throw new InterpreterException(e, e.getMessage(), -1, -1);
        } catch (RuntimeException re) {
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        }
        return null;
    }

    public void dispose() {
    }

    public void bindObject(String name, Object object) {
        interpreter.set(name, object);
    }

    public void setOut(Writer out) {
        interpreter.setOut(out);
    }

    // org.apache.batik.i18n.Localizable implementation

    public Locale getLocale() {
        return null;
    }

    public void setLocale(Locale locale) {
    }

    public String formatMessage(String key, Object[] args) {
        return null;
    }
}
