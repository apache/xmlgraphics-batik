/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaMethod;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * A class that wraps an <code>EventTarget</code> instance to expose
 * it in the Rhino engine. Then calling <code>addEventListener</code>
 * with a Rhino function as parameter should redirect the call to
 * <code>addEventListener</code> with a Java function object calling
 * the Rhino function.
 * This class also allows to pass an ECMAScript (Rhino) object as
 * a parameter instead of a function provided the fact that this object
 * has a <code>handleEvent</code> method.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
class EventTargetWrapper extends NativeJavaObject {

    /**
     * The Java function object calling the Rhino function.
     */
    static class FunctionEventListener implements EventListener {
        private Function function;
        private RhinoInterpreter interpreter;
        FunctionEventListener(Function f, RhinoInterpreter i) {
            function = f;
            interpreter = i;
        }
        public void handleEvent(Event evt) {
            try {
                interpreter.callHandler(function, evt);
            } catch (JavaScriptException e) {
                // the only simple solution is to forward it as a
                // RuntimeException to be catched by event dispatching
                // in BridgeEventSupport.java
                // another solution will to give UserAgent to interpreters
                throw new WrappedException(e);
            }
        }
    }

    class HandleEventListener implements EventListener {
        private final static String HANDLE_EVENT = "handleEvent";

        private Scriptable scriptable;
        private Object[] array = new Object[1];
        private RhinoInterpreter interpreter;

        HandleEventListener(Scriptable s, RhinoInterpreter interpreter) {
            scriptable = s;
            this.interpreter = interpreter;
        }
        public void handleEvent(Event evt) {
            try {
                array[0] = evt;
                interpreter.enterContext();
                ScriptableObject.callMethod(scriptable, HANDLE_EVENT, array);
            } catch (JavaScriptException e) {
                // the only simple solution is to forward it as a
                // RuntimeException to be catched by event dispatching
                // in BridgeEventSupport.java
                // another solution will to give UserAgent to interpreters
                throw new WrappedException(e);
            } finally {
                Context.exit();
            }
        }
    }

    class RhinoNativeJavaAddMethod extends NativeJavaMethod {
        Map listenerMap;
        RhinoNativeJavaAddMethod(Method method, String name,
                              Map listenerMap) {
            super(method, name);
            this.listenerMap = listenerMap;
        }

        public Object call(Context ctx, Scriptable scope,
                           Scriptable thisObj, Object[] args)
            throws JavaScriptException {
            NativeJavaObject  njo = (NativeJavaObject)thisObj;
            if (args[1] instanceof Function) {
                
                EventListener evtListener = new FunctionEventListener
                    ((Function)args[1],
                     ((RhinoInterpreter.ExtendedContext)ctx).getInterpreter());
                listenerMap.put(args[1], evtListener);
                // we need to marshall args
                Class[] paramTypes = { String.class, Function.class,
                                       Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.toType(args[i], paramTypes[i]);

                
                ((EventTarget)njo.unwrap()).addEventListener
                    ((String)args[0], evtListener,
                     ((Boolean)args[2]).booleanValue());
                return Undefined.instance;
            } 
            if (args[1] instanceof NativeObject) {
                EventListener evtListener =
                    new HandleEventListener((Scriptable)args[1], 
                                             ((RhinoInterpreter.ExtendedContext)ctx).getInterpreter());
                listenerMap.put(args[1], evtListener);
                // we need to marshall args
                Class[] paramTypes = { String.class, Scriptable.class,
                                       Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.toType(args[i], paramTypes[i]);

                ((EventTarget)njo.unwrap()).addEventListener
                    ((String)args[0], evtListener,
                     ((Boolean)args[2]).booleanValue());
                return Undefined.instance;
            }

            return super.call(ctx, scope, thisObj, args);
        }
    }

    static class RhinoNativeJavaRemoveMethod extends NativeJavaMethod {
        Map listenerMap; 
        RhinoNativeJavaRemoveMethod(Method method, String name,
                                    Map listenerMap) {
            super(method, name);
            this.listenerMap = listenerMap;
        }
        public Object call(Context ctx, Scriptable scope,
                           Scriptable thisObj, Object[] args)
            throws JavaScriptException {
            NativeJavaObject  njo = (NativeJavaObject)thisObj;
            if (args[1] instanceof Function) {
                EventListener el;
                el = (EventListener)listenerMap.remove(args[1]);
                if (el == null) 
                    return Undefined.instance;

                // we need to marshall args
                Class[] paramTypes = { String.class, Function.class,
                                       Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.toType(args[i], paramTypes[i]);

                ((EventTarget)njo.unwrap()).removeEventListener
                    ((String)args[0], el, ((Boolean)args[2]).booleanValue());
                return Undefined.instance;
            }

            if (args[1] instanceof NativeObject) {
                EventListener el;
                el = (EventListener)listenerMap.remove(args[1]);
                if (el == null) 
                    return Undefined.instance;

                // we need to marshall args
                Class[] paramTypes = { String.class, Scriptable.class,
                                       Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.toType(args[i], paramTypes[i]);

                ((EventTarget)njo.unwrap()).removeEventListener
                    ((String)args[0], el, ((Boolean)args[2]).booleanValue());
                return Undefined.instance;
            }
            return super.call(ctx, scope, thisObj, args);
        }
    }

    private NativeJavaMethod methodadd;
    private NativeJavaMethod methodremove;

    private final static String ADD_NAME    = "addEventListener";
    private final static String REMOVE_NAME = "removeEventListener";
    private final static Class[] ARGS_TYPE = { String.class,
                                               EventListener.class,
                                               Boolean.TYPE };
    private final static String NAME = "name";

    EventTargetWrapper(Scriptable scope, EventTarget object) {
        super(scope, object, (Class)null);
        try {
            HashMap listenerMap = new HashMap(2);;
            methodadd = new RhinoNativeJavaAddMethod
                (object.getClass().getMethod(ADD_NAME,ARGS_TYPE), 
                 ADD_NAME, listenerMap);
            methodremove = new RhinoNativeJavaRemoveMethod
                (object.getClass().getMethod(REMOVE_NAME,ARGS_TYPE), 
                 REMOVE_NAME, listenerMap);
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
        if (name.equals(ADD_NAME))
            return methodadd;
        if (name.equals(REMOVE_NAME))
            return methodremove;
        return super.get(name, start);
    }
}
