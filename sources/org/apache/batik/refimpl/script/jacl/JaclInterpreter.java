/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*  
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.script.jacl;

import java.io.*;
import java.util.Locale;
import org.apache.batik.script.*;
import tcl.lang.*;


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
        // import Java lang package & DOM Level 2 & SVG DOM packages
        //        JavaImportCmd import = new JavaImportCmd();
        //        import.cmdProc();
        //        pkg = new NativeJavaPackage("org.w3c.dom");
        //        pkg = new NativeJavaPackage("org.w3c.dom.css");
        //        pkg = new NativeJavaPackage("org.w3c.dom.events");
        //        pkg = new NativeJavaPackage("org.w3c.dom.smil");
        //        pkg = new NativeJavaPackage("org.w3c.dom.stylesheets");
        //        pkg = new NativeJavaPackage("org.w3c.dom.svg");
        //        pkg = new NativeJavaPackage("org.w3c.dom.views");
    }

    // org.apache.batik.script.Intepreter implementation
    
    public Object evaluate(Reader scriptreader) 
        throws InterpreterException, IOException {
        try {
            // oups jacl doesn't accept reader in its eval method :-(
            StringBuffer sbuffer = new StringBuffer();
            char[] buffer = new char[1024];
            int val = 0;
            while ((val = scriptreader.read(buffer)) != -1) {
                sbuffer.append(buffer, 0, val);
            }
            String str = sbuffer.toString();
            interpreter.eval(str, 0);
        } catch (TclException e) {
            e.printStackTrace();
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
                       ReflectObject.newInstance(interpreter, object.getClass(), object),
                       0);
        } catch (TclException e) {
            // 
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
