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

package org.apache.batik.script.jacl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;

import org.apache.batik.script.InterpreterException;

import tcl.lang.Interp;
import tcl.lang.ReflectObject;
import tcl.lang.TclException;

/**
 * A simple implementation of <code>Interpreter</code> interface to use
 * JACL Tcl parser.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class JaclInterpreter implements org.apache.batik.script.Interpreter {
    private Interp interpreter = null;

    public JaclInterpreter() {
        interpreter = new Interp();
        try {
            interpreter.eval("package require java", 0);
        } catch (TclException e) {
        }
    }

    // org.apache.batik.script.Intepreter implementation

    public Object evaluate(Reader scriptreader)
        throws InterpreterException, IOException {
        return evaluate(scriptreader, "");
    }

    public Object evaluate(Reader scriptreader, String description) throws IOException, InterpreterException {
        // oups jacl doesn't accept reader in its eval method :-(
        StringBuffer sbuffer = new StringBuffer();
        char[] buffer = new char[1024];
        int val = 0;
        while ((val = scriptreader.read(buffer)) != -1) {
            sbuffer.append(buffer, 0, val);
        }
        String str = sbuffer.toString();
        return evaluate(str);
    }

    public Object evaluate(String script)
        throws InterpreterException {
        try {
            interpreter.eval(script, 0);
        } catch (TclException e) {
            throw new InterpreterException(e, e.getMessage(), -1, -1);
        } catch (RuntimeException re) {
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        }
        return interpreter.getResult();
    }

    public void dispose() {
        interpreter.dispose();
    }

    public void bindObject(String name, Object object) {
        try {
            interpreter.
                setVar(name,
                       ReflectObject.
                       newInstance(interpreter, object.getClass(), object),
                       0);
        } catch (TclException e) {
            // should not happened we just register an object
        }
    }

    public void setOut(Writer out) {
        // no implementation of a default output function in Jacl
    }

    // org.apache.batik.i18n.Localizable implementation

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public void setLocale(Locale locale) {
    }

    public String formatMessage(String key, Object[] args) {
        return null;
    }
}
