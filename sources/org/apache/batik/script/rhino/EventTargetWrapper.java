/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeJavaMethod;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;

/**
 * A class that wrap an <code>EventTarget</code> instance to expose
 * it in the Rhino engine. Then calling <code>addEventListener</code>
 * with a Rhino function as parameter should redirect the call to
 * <code>addEventListener</code> with a Java function object calling
 * the Rhino function.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
class EventTargetWrapper extends NativeJavaObject {

    /**
     * The Java function object calling the Rhino function.
     */
    class RhinoEventListener implements EventListener {
        private Function function;

        RhinoEventListener(Function f) {
            function = f;
        }
        public void handleEvent(Event evt) {
            try {
                interpreter.callHandler(function, evt);
            } catch (JavaScriptException e) {
                // the only simple solution is to forward it as a
                // RuntimeException to be catch by event dispatching
                // in BridgetEventSupport.java
                // another solution will to give UserAgent to interpreters
                throw new WrappedException(e);
            }
        }
    }

    class RhinoNativeJavaMethod extends NativeJavaMethod {
        RhinoNativeJavaMethod(Method method, String name) {
            super(method, name);
        }
        public Object call(Context ctx, Scriptable scope,
                           Scriptable thisObj, Object[] args)
            throws JavaScriptException {
            if (args[1] instanceof Function) {
                if (this.get(NAME, this).equals(ADD_NAME)) {
                    EventListener evtListener =
                        new RhinoEventListener((Function)args[1]);
                    if (listenerMap == null)
                        listenerMap = new HashMap(2);
                    listenerMap.put(args[1], evtListener);
                    // we need to marshall args
                    Class[] paramTypes = { String.class, Function.class,
                                           Boolean.TYPE };
                    for (int i = 0; i < args.length; i++)
                        args[i] = NativeJavaObject.coerceType(paramTypes[i],
                                                              args[i]);
                    ((EventTarget)unwrap()).
                        addEventListener((String)args[0],
                                         evtListener,
                                         ((Boolean)args[2]).booleanValue());
                    return Undefined.instance;
                } else {
                    if (listenerMap != null) {
                        // we need to marshall args
                        Class[] paramTypes = { String.class, Function.class,
                                               Boolean.TYPE };
                        for (int i = 0; i < args.length; i++)
                            args[i] =
                                NativeJavaObject.coerceType(paramTypes[i],
                                                            args[i]);
                        ((EventTarget)unwrap()).
                            removeEventListener((String)args[0],
                                                (EventListener)listenerMap.
                                                remove(args[1]),
                                                ((Boolean)args[2]).
                                                booleanValue());
                    }
                    return Undefined.instance;
                }
            }
            return super.call(ctx, scope, thisObj, args);
        }
    }

    private RhinoInterpreter interpreter;
    private NativeJavaMethod methodadd;
    private NativeJavaMethod methodremove;
    private HashMap listenerMap;

    private final static String ADD_NAME = "addEventListener";
    private final static String REMOVE_NAME = "removeEventListener";
    private final static Class[] ARGS_TYPE = { String.class,
                                               EventListener.class,
                                               Boolean.TYPE };
    private final static String NAME = "name";

    EventTargetWrapper(Scriptable scope, EventTarget object,
                       RhinoInterpreter interp) {
        super(scope, object, (Class)null);
        interpreter = interp;
        try {
            methodadd =
                new RhinoNativeJavaMethod(object.getClass().
                                          getMethod(ADD_NAME,
                                                    ARGS_TYPE),
                                          ADD_NAME);
            methodremove =
                new RhinoNativeJavaMethod(object.getClass().
                                          getMethod(REMOVE_NAME,
                                                    ARGS_TYPE),
                                          REMOVE_NAME);
        } catch (NoSuchMethodException e) {
            // should not happened
            // we are sure the method are there as we
            // have an EventTarget in parameter
        }
    }

    /**
     * Overriden Rhino method.
     */
    public Object get(String name, Scriptable start) {
        Object method = null;
        if (name.equals(ADD_NAME))
            method = methodadd;
        else
            if (name.equals(REMOVE_NAME))
                method = methodremove;
            else
                method = super.get(name, start);
        return method;
    }
}
