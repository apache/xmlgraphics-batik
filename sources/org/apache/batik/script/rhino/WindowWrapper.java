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

package org.apache.batik.script.rhino;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.batik.script.Window;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.PropertyException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
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
        Window window = ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        long to = ((Long)Context.toType(args[1], Long.TYPE)).longValue();
        if (args[0] instanceof Function) {
            RhinoInterpreter interp =
                (RhinoInterpreter)window.getInterpreter();
            FunctionWrapper fw;
            fw = new FunctionWrapper(interp, (Function)args[0],
                                     EMPTY_ARGUMENTS);
            return Context.toObject(window.setInterval(fw, to), thisObj);
        }
        String script =
	  (String)Context.toType(args[0], String.class);
        return Context.toObject(window.setInterval(script, to), thisObj);
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
        Window window = ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        long to = ((Long)Context.toType(args[1], Long.TYPE)).longValue();
        if (args[0] instanceof Function) {
            RhinoInterpreter interp =
                (RhinoInterpreter)window.getInterpreter();
            FunctionWrapper fw;
            fw = new FunctionWrapper(interp, (Function)args[0],
                                     EMPTY_ARGUMENTS);
            return Context.toObject(window.setTimeout(fw, to), thisObj);
        }
        String script =
            (String)Context.toType(args[0], String.class);
        return Context.toObject(window.setTimeout(script, to), thisObj);
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
        Window window = ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        if (len >= 1) {
            window.clearInterval(Context.toType(args[0], Object.class));
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
        Window window = ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        if (len >= 1) {
            window.clearTimeout(Context.toType(args[0], Object.class));
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
        final Window window = 
            ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }

        AccessControlContext acc =
            ((RhinoInterpreter)window.getInterpreter()).getAccessControlContext();

        Object ret = AccessController.doPrivileged( new PrivilegedAction() {
                public Object run() {
                    return window.parseXML
                        ((String)Context.toType(args[0], String.class),
                         (Document)Context.toType(args[1], Document.class));
                }
            }, acc);
        return Context.toObject(ret, thisObj);
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
        final Window window = ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        final ScriptableObject go = ((RhinoInterpreter.ExtendedContext)cx).getGlobalObject();
        if (len < 2) {
            throw Context.reportRuntimeError("invalid argument count");
        }
        RhinoInterpreter interp =
            (RhinoInterpreter)window.getInterpreter();
        final String uri = (String)Context.toType(args[0], String.class);
        Window.GetURLHandler urlHandler = null;
        if (args[1] instanceof Function) {
            urlHandler = new GetURLFunctionWrapper
                (interp, (Function)args[1], go);
        } else {
            urlHandler = new GetURLObjectWrapper
                (interp, (NativeObject)args[1], go);
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
                             (String)Context.toType(args[2], String.class));
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
        Window window = ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        if (len >= 1) {
            String message =
                (String)Context.toType(args[0], String.class);
            window.alert(message);
        }
    }

    /**
     * Wraps the 'confirm' method of the Window interface.
     */
    public static Object confirm(Context cx,
                                  Scriptable thisObj,
                                  Object[] args,
                                  Function funObj)
        throws JavaScriptException {
        int len = args.length;
        Window window = ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        if (len >= 1) {
            String message =
                (String)Context.toType(args[0], String.class);
            if (window.confirm(message))
                return Context.toObject(Boolean.TRUE, thisObj);
            else
                return Context.toObject(Boolean.FALSE, thisObj);
        }
        return Context.toObject(Boolean.FALSE, thisObj);
    }

    /**
     * Wraps the 'prompt' method of the Window interface.
     */
    public static Object prompt(Context cx,
                                Scriptable thisObj,
                                Object[] args,
                                Function funObj)
        throws JavaScriptException {
        int len = args.length;
        Window window = ((RhinoInterpreter.ExtendedContext)cx).getWindow();
        switch (len) {
        case 0:
            return Context.toObject("", thisObj);

        case 1:
            String message =
                (String)Context.toType(args[0], String.class);
            return Context.toObject(window.prompt(message), thisObj);

        default:
            message =
                (String)Context.toType(args[0], String.class);
            String defVal =
                (String)Context.toType(args[1], String.class);
            return Context.toObject(window.prompt(message, defVal), thisObj);
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
         * The Scope for callback
         */
        protected ScriptableObject scope;

        /**
         * Creates a wrapper.
         */
        public GetURLFunctionWrapper(RhinoInterpreter ri, Function fct,
                                     ScriptableObject sc) {
            interpreter = ri;
            function = fct;
            scope = sc;
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
                interpreter.callHandler
                    (function, 
                     new GetURLDoneArgBuilder(success, mime, content, scope));
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
         * The Scope for the callback.
         */
        private ScriptableObject scope;

        private static final String COMPLETE = "operationComplete";

        /**
         * Creates a wrapper.
         */
        public GetURLObjectWrapper(RhinoInterpreter ri,
                                   ScriptableObject obj,
                                   ScriptableObject sc) {
            interpreter = ri;
            object = obj;
            scope = sc;
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
                interpreter.callMethod
                    (object, COMPLETE,
                     new GetURLDoneArgBuilder(success, mime, content, scope));
            } catch (JavaScriptException e) {
                Context.exit();
                throw new WrappedException(e);
            }
        }
    }

    static class GetURLDoneArgBuilder 
        implements RhinoInterpreter.ArgumentsBuilder {
        boolean success;
        String mime, content;
        ScriptableObject scope;
        public GetURLDoneArgBuilder(boolean success, 
                                    String mime, String content,
                                    ScriptableObject scope) {
            this.success = success;
            this.mime    = mime;
            this.content = content;
            this.scope   = scope;
        }

        public Object[] buildArguments() {
            ScriptableObject so = new NativeObject();
            so.put("success", so,
                   (success) ? Boolean.TRUE : Boolean.FALSE);
            if (mime != null) {
                so.put("contentType", so,
                       Context.toObject(mime, scope));
            }
            if (content != null) {
                so.put("content", so,
                       Context.toObject(content, scope));
            }
            return new Object [] { so };
        }
    }
    
}
