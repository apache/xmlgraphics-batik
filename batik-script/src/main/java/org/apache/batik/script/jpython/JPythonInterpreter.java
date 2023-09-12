/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.script.jpython;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;

import org.apache.batik.script.InterpreterException;
import org.python.util.PythonInterpreter;

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

    /**
     * Returns the content types of the scripting languages this interpreter
     * handles.
     */
    public String[] getMimeTypes() {
        return JPythonInterpreterFactory.JPYTHON_MIMETYPES;
    }

    public Object evaluate(Reader scriptreader)
        throws IOException {
        return evaluate(scriptreader, "");
    }

    public Object evaluate(Reader scriptreader, String description)
        throws IOException {

        // oups jpython doesn't accept reader in its eval method :-(
        StringBuilder sbuffer = new StringBuilder();
        char[] buffer = new char[1024];
        int val = 0;
        while ((val = scriptreader.read(buffer)) != -1) {
            sbuffer.append(buffer,0, val);
        }
        String str = sbuffer.toString();
        return evaluate(str);
    }

    public Object evaluate(String script) {
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
