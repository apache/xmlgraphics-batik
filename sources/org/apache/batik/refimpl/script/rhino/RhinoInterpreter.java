/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.script.rhino;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;

import org.w3c.dom.events.EventTarget;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaPackage;
import org.mozilla.javascript.Scriptable;


/**
 * A simple implementation of <code>Interpreter</code> interface to use
 * Rhino ECMAScript parser.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class RhinoInterpreter implements Interpreter {
    private Scriptable scope = null;
    private Context context = null;

    public RhinoInterpreter() {
        // entering a context
        context = Context.enter();
        // init std object with an importer
        ImporterTopLevel importer = new ImporterTopLevel();
        scope = context.initStandardObjects(importer);
        // import Java lang package & DOM Level 2 & SVG DOM packages
        NativeJavaPackage[] p={new NativeJavaPackage("java.lang"),
                               new NativeJavaPackage("org.w3c.dom"),
                               new NativeJavaPackage("org.w3c.dom.css"),
                               new NativeJavaPackage("org.w3c.dom.events"),
                               new NativeJavaPackage("org.w3c.dom.smil"),
                               new NativeJavaPackage("org.w3c.dom.stylesheets"),
                               new NativeJavaPackage("org.w3c.dom.svg"),
                               new NativeJavaPackage("org.w3c.dom.views")};
        importer.importPackage(context, scope, p, null);
        context.setWrapHandler(new EventTargetWrapHandler(this));
        Context.exit();
    }

    // org.apache.batik.script.Intepreter implementation

    public Object evaluate(Reader scriptreader)
        throws InterpreterException, IOException {
        Object rv = null;
        Context ctx = Context.enter(context);
        try {
            rv = ctx.evaluateReader(scope,
                                    scriptreader,
                                    "<SVG>",
                                    1, null);
        } catch (JavaScriptException e) {
            throw new InterpreterException(e, e.getMessage(), -1, -1);
        } catch (RuntimeException re) {
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        } finally {
            Context.exit();
        }
        return rv;
    }

    public void dispose() {
    }

    public void bindObject(String name, Object object) {
        Scriptable jsObject =  Context.toObject(object, scope);
        scope.put(name, scope, jsObject);
    }

    /**
     * To be used by <code>EventTargetWrapper</code>.
     */
    void callHandler(Function handler,
                     Object arg)
        throws InterpreterException {
        Context ctx = Context.enter(context);
        arg = Context.toObject(arg, scope);
        Object[] args = {arg};
        try {
            handler.call(ctx, scope, scope, args);
        } catch (JavaScriptException e) {
            throw new InterpreterException(e, e.getMessage(), -1, -1);
        } finally {
            Context.exit();
        }
    }

    /**
     * Build the wrapper for objects implement <code>EventTarget</code>.
     */
    Scriptable buildEventTargetWrapper(EventTarget obj) {
        return new EventTargetWrapper(scope, obj, this);
    }

    public void setOut(Writer out) {
        // no implementation of a default output function in Rhino
    }

    // org.apache.batik.i18n.Localizable implementation

    public Locale getLocale() {
        // <!> TODO : in Rhino the local is for a thread not a scope..
        return null;
    }

    public void setLocale(Locale locale) {
        // <!> TODO : in Rhino the local is for a thread not a scope..
    }

    public String formatMessage(String key, Object[] args) {
        return null;
    }
}
