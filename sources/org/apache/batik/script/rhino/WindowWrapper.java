/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;
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
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class WindowWrapper extends ScriptableObject {

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
    public WindowWrapper() {
    }

    /**
     * The ecmascript constructor for the Window class.
     */
    public static Object jsConstructor(Context cx, Object[] args, 
                                       Function ctorObj, boolean inNewExpr) {
        WindowWrapper result = new WindowWrapper();
        result.window = (Window)((Wrapper)args[0]).unwrap();
        return result;
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
    public static Object jsFunction_setInterval(Context cx,
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
    public static Object jsFunction_setTimeout(Context cx,
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
    public static void jsFunction_clearInterval(Context cx,
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
    public static void jsFunction_clearTimeout(Context cx,
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
    public static Object jsFunction_parseXML(Context cx,
                                             Scriptable thisObj,
                                             Object[] args,
                                             Function funObj)
        throws JavaScriptException {
        int len = args.length;
        WindowWrapper ww = (WindowWrapper)thisObj;
        Window window = ww.window;
        if (len >= 2) {
            return window.parseXML
              ((String)NativeJavaObject.coerceType(String.class, args[0]),
               (Document)NativeJavaObject.coerceType(Document.class, args[1]));
        }
        return null;
    }

    /**
     * Wraps the 'alert' method of the Window interface.
     */
    public static void jsFunction_alert(Context cx,
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
    public static boolean jsFunction_confirm(Context cx,
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
    public static String jsFunction_prompt(Context cx,
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
}
