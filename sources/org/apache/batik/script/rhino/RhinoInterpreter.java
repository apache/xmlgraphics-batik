/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

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
import org.mozilla.javascript.WrappedException;

/**
 * A simple implementation of <code>Interpreter</code> interface to use
 * Rhino ECMAScript interpreter.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class RhinoInterpreter implements Interpreter {
    private Scriptable scope = null;
    private Context context = null;

    private static String[] TO_BE_IMPORTED = {
        "java.lang",
        "org.w3c.dom",
        "org.w3c.dom.css",
        "org.w3c.dom.events",
        "org.w3c.dom.smil",
        "org.w3c.dom.stylesheets",
        "org.w3c.dom.svg",
        "org.w3c.dom.views"
    };

    /**
     * Build a <code>Interpreter</code> for ECMAScript using Rhino.
     * @see org.apache.batik.script.Interpreter
     * @see org.apache.batik.script.InterpreterPool
     */
    public RhinoInterpreter() {
        // entering a context
        context = Context.enter();
        try {
            // init std object with an importer
            ImporterTopLevel importer = new ImporterTopLevel();
            scope = context.initStandardObjects(importer);
            // import Java lang package & DOM Level 2 & SVG DOM packages
            NativeJavaPackage[] p= new NativeJavaPackage[TO_BE_IMPORTED.length];
            for (int i = 0; i < TO_BE_IMPORTED.length; i++) {
                p[i] = new NativeJavaPackage(TO_BE_IMPORTED[i]);
            }
            importer.importPackage(context, scope, p, null);
            context.setWrapHandler(new EventTargetWrapHandler(this));
        } finally {
            Context.exit();
        }
    }

    // org.apache.batik.script.Intepreter implementation

    /**
     * This method evaluates a piece of ECMA script.
     * @param scriptreader a <code>java.io.Reader</code> on the piece of script
     * @return if no exception is thrown during the call, should return the
     * value of the last expression evaluated in the script
     */
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
            // exception from JavaScript (possibly wrapping a Java Ex)
            if (e.getValue() instanceof Exception) {
                Exception ex = (Exception)e.getValue();
                throw new InterpreterException(ex, ex.getMessage(), -1, -1);
            } else
                throw new InterpreterException(e, e.getMessage(), -1, -1);
        } catch (WrappedException we) {
            // main Rhino RuntimeException
            throw
                new InterpreterException((Exception)we.getWrappedException(),
                                         we.getWrappedException().getMessage(),
                                         -1, -1);
        } catch (RuntimeException re) {
            // other RuntimeExceptions
            throw new InterpreterException(re, re.getMessage(), -1, -1);
        } finally {
            Context.exit();
        }
        return rv;
    }

    /**
     * For <code>RhinoInterpreter</code> this method does nothing.
     */
    public void dispose() {
    }

    /**
     * This method registers a particular Java <code>Object</code> in
     * the environment of the interpreter.
     * @param name the name of the script object to create
     * @param object the Java object
     */
    public void bindObject(String name, Object object) {
        Scriptable jsObject =  Context.toObject(object, scope);
        scope.put(name, scope, jsObject);
    }

    /**
     * To be used by <code>EventTargetWrapper</code>.
     */
    void callHandler(Function handler,
                     Object arg)
        throws JavaScriptException {
        Context ctx = Context.enter(context);
        try {
            arg = Context.toObject(arg, scope);
            Object[] args = {arg};
            handler.call(ctx, scope, scope, args);
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

    /**
     * By default Rhino has no output method in its language. That's why
     * this method does nothing.
     * @param output the new out <code>Writer</code>.
     */
    public void setOut(Writer out) {
        // no implementation of a default output function in Rhino
    }

    // org.apache.batik.i18n.Localizable implementation

    /**
     * Provides a way to the user to specify a locale which override the
     * default one. If null is passed to this method, the used locale
     * becomes the global one.
     * @param l The locale to set.
     */
    public Locale getLocale() {
        // <!> TODO : in Rhino the local is for a thread not a scope..
        return null;
    }

    /**
     * Returns the current locale or null if the locale currently used is
     * the default one.
     */
    public void setLocale(Locale locale) {
        // <!> TODO : in Rhino the local is for a thread not a scope..
    }

    /**
     * Creates and returns a localized message, given the key of the message
     * in the resource bundle and the message parameters.
     * The messages in the resource bundle must have the syntax described in
     * the java.text.MessageFormat class documentation.
     * @param key  The key used to retreive the message from the resource
     *             bundle.
     * @param args The objects that compose the message.
     * @exception MissingResourceException if the key is not in the bundle.
     */
    public String formatMessage(String key, Object[] args) {
        return null;
    }
}
