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
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import org.w3c.dom.events.EventTarget;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaPackage;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

/**
 * A simple implementation of <code>Interpreter</code> interface to use
 * Rhino ECMAScript interpreter.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class RhinoInterpreter implements Interpreter {
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

    private static class Entry {
        String str;
        Script script;
        Entry(String str, Script script) {
            this.str = str;
            this.script = script;
        }
    };

    // store last 32 precompiled objects.
    private static final int MAX_CACHED_SCRIPTS = 32;

    private Context context = null;
    private ScriptableObject globalObject = null;
    private LinkedList compiledScripts = new LinkedList();

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
            // building the importer automatically initialize the
            // context with it since Rhino1.5R3
            ImporterTopLevel importer = new ImporterTopLevel(context);
            globalObject = importer;
            // import Java lang package & DOM Level 2 & SVG DOM packages
            NativeJavaPackage[] p= new NativeJavaPackage[TO_BE_IMPORTED.length];
            for (int i = 0; i < TO_BE_IMPORTED.length; i++) {
                p[i] = new NativeJavaPackage(TO_BE_IMPORTED[i]);
            }
            importer.importPackage(context, globalObject, p, null);
            context.setWrapHandler(new EventTargetWrapHandler(this));
        } finally {
            Context.exit();
        }
    }

    /**
     * This method returns the ECMAScript global object used by this interpreter.
     */
    protected ScriptableObject getGlobalObject() {
        return globalObject;
    }

    /**
     * This method returns the default context in which the interpreter runs.
     */
    protected Context getContext() {
        return context;
    }


    // org.apache.batik.script.Intepreter implementation

    /**
     * This method evaluates a piece of ECMAScript.
     * @param scriptreader a <code>java.io.Reader</code> on the piece of script
     * @return if no exception is thrown during the call, should return the
     * value of the last expression evaluated in the script.
     */
    public Object evaluate(Reader scriptreader)
        throws InterpreterException, IOException {
        Object rv = null;
        Context ctx = Context.enter(context);
        try {
            rv = ctx.evaluateReader(globalObject,
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
     * This method evaluates a piece of ECMA script.
     * The first time a String is passed, it is compiled and evaluated.
     * At next call, the piece of script will only be evaluated to
     * prevent from recompiling it.
     * @param scriptstr the piece of script
     * @return if no exception is thrown during the call, should return the
     * value of the last expression evaluated in the script.
     */
    public Object evaluate(String scriptstr)
        throws InterpreterException {
        System.out.println("evaluate "+scriptstr);
        Context ctx = Context.enter(context);
        Script script = null;
        Entry et = null;
        Iterator it = compiledScripts.iterator();
        // between nlog(n) and log(n) because it is
        // an AbstractSequentialList
        while (it.hasNext()) {
            if ((et = (Entry)(it.next())).str == scriptstr) {
                // if it is not at the end, remove it because
                // it will change from place (it is faster
                // to remove it now)
                script = et.script;
                it.remove();
                break;
            }
        }
        if (script == null) {
            // this script has not been compiled yet or has been fogotten
            // since the compilation:
            // compile it and store it for future use.
            try {
                script = ctx.compileReader(globalObject,
                                           new StringReader(scriptstr),
                                           "<SVG>",
                                           1, null);
            } catch (IOException io) {
                // can't happen because we use a String...
            }
            if (compiledScripts.size()+1 > MAX_CACHED_SCRIPTS) {
                // too many cached items - we should delete the oldest entry.
                // all of this is very fast on linkedlist
                compiledScripts.removeFirst();
            }
            // stroring is done here:
            compiledScripts.addLast(new Entry(scriptstr, script));
        } else {
            // this script has been compiled before,
            // just update it's index so it won't get deleted soon.
            compiledScripts.addLast(et);
        }
        Object rv = null;
        try {
            System.out.println("ctx "+ctx);
            System.out.println("global "+globalObject);
            rv = script.exec(ctx, globalObject);
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
            re.printStackTrace();
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
        Scriptable jsObject =  Context.toObject(object, globalObject);
        globalObject.put(name, globalObject, jsObject);
    }

    /**
     * To be used by <code>EventTargetWrapper</code>.
     */
    void callHandler(Function handler,
                     Object arg)
        throws JavaScriptException {
        Context ctx = Context.enter(context);
        try {
            arg = Context.toObject(arg, globalObject);
            Object[] args = {arg};
            handler.call(ctx, globalObject, globalObject, args);
        } finally {
            Context.exit();
        }
    }

    /**
     * Build the wrapper for objects implement <code>EventTarget</code>.
     */
    Scriptable buildEventTargetWrapper(EventTarget obj) {
        return new EventTargetWrapper(globalObject, obj, this);
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
