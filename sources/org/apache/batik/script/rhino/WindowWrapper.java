/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

import java.io.IOException;
import java.io.StringReader;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.PropertyException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.Wrapper;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.Window;

import org.w3c.dom.Document;

/**
 * This class wraps a Window object to expose it to the interpreter.
 * This will be the Global Object of our interpreter.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class WindowWrapper extends ImporterTopLevel {

    private final static Object[] EMPTY_ARGUMENTS = new Object[0];

    /**
     * The rhino interpreter.
     */
    protected RhinoInterpreter interpreter;

    /**
     * The wrapped window.
     */
    protected Window window;

    /**
     * Creates a new WindowWrapper.
     */
    public WindowWrapper(Context context) {
        super(context);
        String[] names = { "setInterval", "setTimeout", "clearInterval", "clearTimeout",
                           "parseXML", "getURL", "alert", "confirm", "prompt" };
        try {
            this.defineFunctionProperties(names, WindowWrapper.class,
                                          ScriptableObject.DONTENUM);
        } catch (PropertyException e) {
            throw new Error();  // should never happen
        }
    }

    public String getClassName() {
        return "Window";
    }

    public String toString() {
        return "[object Window]";
    }

    /**
     * Wraps the 'setInterval' methods of the Window interface.
     */
    public static Object setInterval(Context cx,
                                     Scriptable thisObj,
                                     Object[] args,
                                     Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        long to = ((Long)NativeJavaObject.coerceType
                   (Long.TYPE, args[1])).longValue();
        if (args[0] instanceof Function) {
            RhinoInterpreter interp =
                (RhinoInterpreter)window.getInterpreter();
            FunctionWrapper fw;
            fw = new FunctionWrapper(interp, (Function)args[0],
                                     EMPTY_ARGUMENTS);
            return window.setInterval(fw, to);
        }
        String script =
            (String)NativeJavaObject.coerceType(String.class, args[0]);
        return window.setInterval(script, to);
    }

    /**
     * Wraps the 'setTimeout' methods of the Window interface.
     */
    public static Object setTimeout(Context cx,
                                    Scriptable thisObj,
                                    Object[] args,
                                    Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        long to = ((Long)NativeJavaObject.coerceType
                   (Long.TYPE, args[1])).longValue();
        if (args[0] instanceof Function) {
            RhinoInterpreter interp =
                (RhinoInterpreter)window.getInterpreter();
            FunctionWrapper fw;
            fw = new FunctionWrapper(interp, (Function)args[0],
                                     EMPTY_ARGUMENTS);
            return window.setTimeout(fw, to);
        }
        String script =
            (String)NativeJavaObject.coerceType(String.class, args[0]);
        return window.setTimeout(script, to);
    }

    /**
     * Wraps the 'clearInterval' method of the Window interface.
     */
    public static void clearInterval(Context cx,
                                     Scriptable thisObj,
                                     Object[] args,
                                     Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 1) {
            window.clearInterval(NativeJavaObject.coerceType
                                 (Object.class, args[0]));
        }
    }

    /**
     * Wraps the 'clearTimeout' method of the Window interface.
     */
    public static void clearTimeout(Context cx,
                                    Scriptable thisObj,
                                    Object[] args,
                                    Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 1) {
            window.clearTimeout(NativeJavaObject.coerceType
                                (Object.class, args[0]));
        }
    }

    /**
     * Wraps the 'parseXML' method of the Window interface.
     */
    public static Object parseXML(Context cx,
                                  Scriptable thisObj,
                                  final Object[] args,
                                  Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }

        AccessControlContext acc =
            ((RhinoInterpreter)window.getInterpreter()).getAccessControlContext();

        return AccessController.doPrivileged( new PrivilegedAction() {
                public Object run() {
                    return window.parseXML
                        ((String)NativeJavaObject.coerceType(String.class, args[0]),
                         (Document)NativeJavaObject.coerceType(Document.class, args[1]));
                }
            }, acc);
    }

    /**
     * Wraps the 'getURL' method of the Window interface.
     */
    public static void getURL(Context cx,
                              Scriptable thisObj,
                              final Object[] args,
                              Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        final Window window = ww.window;
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        RhinoInterpreter interp =
            (RhinoInterpreter)window.getInterpreter();
        final String uri = (String)NativeJavaObject.coerceType(String.class, args[0]);
        Window.GetURLHandler urlHandler = null;
        if (args[1] instanceof Function) {
          urlHandler = new GetURLFunctionWrapper(interp, (Function)args[1], ww);
        } else {
          urlHandler = new GetURLObjectWrapper(interp, (NativeObject)args[1], ww);
        }
        final Window.GetURLHandler fw = urlHandler;

        AccessControlContext acc =
            ((RhinoInterpreter)window.getInterpreter()).getAccessControlContext();

        if (len == 2) {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run(){
                        window.getURL(uri, fw);
                        return null;
                    }
                }, acc);
        } else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        window.getURL
                            (uri, fw,
                             (String)NativeJavaObject.coerceType(String.class, args[2]));
                        return null;
                    }
                }, acc);
        }
    }

    /**
     * Wraps the 'alert' method of the Window interface.
     */
    public static void alert(Context cx,
                             Scriptable thisObj,
                             Object[] args,
                             Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 1) {
            String message =
                (String)NativeJavaObject.coerceType(String.class, args[0]);
            window.alert(message);
        }
    }

    /**
     * Wraps the 'confirm' method of the Window interface.
     */
    public static boolean confirm(Context cx,
                                  Scriptable thisObj,
                                  Object[] args,
                                  Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 1) {
            String message =
                (String)NativeJavaObject.coerceType(String.class, args[0]);
            return window.confirm(message);
        }
        return false;
    }

    /**
     * Wraps the 'prompt' method of the Window interface.
     */
    public static String prompt(Context cx,
                                Scriptable thisObj,
                                Object[] args,
                                Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        switch (len) {
        case 0:
            return "";

        case 1:
            String message =
                (String)NativeJavaObject.coerceType(String.class, args[0]);
            return window.prompt(message);

        default:
            message =
                (String)NativeJavaObject.coerceType(String.class, args[0]);
            String defVal =
                (String)NativeJavaObject.coerceType(String.class, args[1]);
            return window.prompt(message, defVal);
        }
    }

    /**
     * To wrap a function in an handler.
     */
    protected static class FunctionWrapper implements Runnable {

        /**
         * The current interpreter.
         */
        protected RhinoInterpreter interpreter;

        /**
         * The function wrapper.
         */
        protected Function function;

        /**
         * The arguments.
         */
        protected Object[] arguments;

        /**
         * Creates a function wrapper.
         */
        public FunctionWrapper(RhinoInterpreter ri,
                               Function f,
                               Object[] args) {
            interpreter = ri;
            function = f;
            arguments = args;
        }

        /**
         * Calls the function.
         */
        public void run() {
            try {
                interpreter.callHandler(function, arguments);
            } catch (JavaScriptException e) {
                throw new WrappedException(e);
            }
        }
    }

    /**
     * To wrap a function passed to getURL().
     */
    protected static class GetURLFunctionWrapper
        implements Window.GetURLHandler {

        /**
         * The current interpreter.
         */
        protected RhinoInterpreter interpreter;

        /**
         * The function wrapper.
         */
        protected Function function;

        /**
         * The WindowWrapper.
         */
        protected WindowWrapper windowWrapper;

        /**
         * Creates a wrapper.
         */
        public GetURLFunctionWrapper(RhinoInterpreter ri, Function fct,
                                     WindowWrapper ww) {
            interpreter = ri;
            function = fct;
            windowWrapper = ww;
        }

        /**
         * Called before 'getURL()' returns.
         * @param success Whether the data was successfully retreived.
         * @param mime The data MIME type.
         * @param content The data.
         */
        public void getURLDone(final boolean success,
                               final String mime,
                               final String content) {
            try {
                interpreter.callHandler(function,
                    new RhinoInterpreter.ArgumentsBuilder() {
                        public Object[] buildArguments() {
                            try {
                                Object[] arguments = new Object[1];
                                ScriptableObject so =
                                    (ScriptableObject)interpreter.evaluate
                                    (new StringReader("new Object()"));
                                so.put("success", so,
                                       (success) ?
                                       Boolean.TRUE : Boolean.FALSE);
                                if (mime != null) {
                                    so.put("contentType", so,
                                           Context.toObject(mime,
                                                            windowWrapper));
                                }
                                if (content != null) {
                                    so.put("content", so,
                                           Context.toObject(content,
                                                            windowWrapper));
                                }
                                arguments[0] = so;
                                return arguments;
                            } catch (IOException e) {
                                throw new WrappedException(e);
                            } catch (InterpreterException e) {
                                throw new WrappedException(e);
                            }
                        }
                    });
            } catch (JavaScriptException e) {
                throw new WrappedException(e);
            }
        }
    }

    /**
     * To wrap an object passed to getURL().
     */
    private static class GetURLObjectWrapper
        implements Window.GetURLHandler {

        /**
         * The current interpreter.
         */
        private RhinoInterpreter interpreter;

        /**
         * The object wrapper.
         */
        private ScriptableObject object;

        /**
         * The WindowWrapper.
         */
        private WindowWrapper windowWrapper;

        private Object[] array = new Object[1];
        private static final String COMPLETE = "operationComplete";

        /**
         * Creates a wrapper.
         */
        public GetURLObjectWrapper(RhinoInterpreter ri,
                                   ScriptableObject obj,
                                   WindowWrapper ww) {
            interpreter = ri;
            object = obj;
            windowWrapper = ww;
        }

        /**
         * Called before 'getURL()' returns.
         * @param success Whether the data was successfully retreived.
         * @param mime The data MIME type.
         * @param content The data.
         */
        public void getURLDone(final boolean success,
                               final String mime,
                               final String content) {
            try {
                interpreter.callMethod(object, COMPLETE,
                                       new RhinoInterpreter.ArgumentsBuilder() {
                                           public Object[] buildArguments() {
                                               Object[] arguments = new Object[1];
                                               ScriptableObject so =
                                                   new NativeObject();
                                               so.put("success", so,
                                                      (success) ?
                                                      Boolean.TRUE : Boolean.FALSE);
                                               if (mime != null) {
                                                   so.put("contentType", so,
                                                          Context.toObject(mime,
                                                                           windowWrapper));
                                               }
                                               if (content != null) {
                                                   so.put("content", so,
                                                          Context.toObject(content,
                                                                           windowWrapper));
                                               }
                                               arguments[0] = so;
                                               return arguments;
                                           }
                                       });
            } catch (JavaScriptException e) {
                Context.exit();
                throw new WrappedException(e);
            }
        }
    }
}
