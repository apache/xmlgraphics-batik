/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*  
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.script.rhino;

import java.io.*;
import java.util.Locale;
import org.apache.batik.script.*;
import org.mozilla.javascript.*;

/**
 * A simple implementation of <code>Interpreter</code> interface to use
 * Rhino ECMAScript parser.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class RhinoInterpreter implements org.apache.batik.script.Interpreter {
    private Context ctx = null;
    private Scriptable scope = null;
    
    public RhinoInterpreter() {
        // entering a context
        ctx = Context.enter();
        // init std object with an importer
        ImporterTopLevel importer = new ImporterTopLevel();
        scope = ctx.initStandardObjects(importer);
        // import Java lang package & DOM Level 2 & SVG DOM packages
        NativeJavaPackage pkg = new NativeJavaPackage("java.lang");
        importer.importPackage(pkg);
        pkg = new NativeJavaPackage("org.w3c.dom");
        importer.importPackage(pkg);
        pkg = new NativeJavaPackage("org.w3c.dom.css");
        importer.importPackage(pkg);
        pkg = new NativeJavaPackage("org.w3c.dom.events");
        importer.importPackage(pkg);
        pkg = new NativeJavaPackage("org.w3c.dom.smil");
        importer.importPackage(pkg);
        pkg = new NativeJavaPackage("org.w3c.dom.stylesheets");
        importer.importPackage(pkg);
        pkg = new NativeJavaPackage("org.w3c.dom.svg");
        importer.importPackage(pkg);
        pkg = new NativeJavaPackage("org.w3c.dom.views");
        importer.importPackage(pkg);
    }

    // org.apache.batik.script.Intepreter implementation
    
    public Object evaluate(Reader scriptreader) 
        throws InterpreterException, IOException {
        Object rv = null;
        try {
            rv = ctx.evaluateReader(scope,
                                    scriptreader,
                                    "<SVG>",
                                    1, null);
        } catch (JavaScriptException e) {
            throw new InterpreterException(e, e.getMessage(), -1, -1);
        } catch (RuntimeException re) {
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        }
        return rv;
    }
    
    public void dispose() {
        ctx.exit();
    }
    
    public void bindObject(String name, Object object) {
        Scriptable jsObject = Context.toObject(object, scope); 
        scope.put(name, scope, jsObject);
    } 
    
    public void setOut(Writer out) {
        // no implementation of a default output function in Rhino
    }
    
    // org.apache.batik.i18n.Localizable implementation

    public Locale getLocale() {
        return ctx.getLocale();
    }

    public void setLocale(Locale locale) {
        ctx.setLocale(locale);
    }
    
    public String formatMessage(String key, Object[] args) {
        return null;
    }
}
