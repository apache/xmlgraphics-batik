/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrappedException;

import org.apache.batik.script.JavaFunction;

/**
 * This class is used to call a JavaFunction.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class RhinoFunction extends NativeFunction {
    
    /**
     * The JavaFunction to call.
     */
    protected JavaFunction javaFunction;

    /**
     * The parameter types.
     */
    protected Class[] paramTypes;

    /**
     * The global object.
     */
    protected ScriptableObject globalObject;

    /**
     * Creates a new RhinoFunction.
     */
    public RhinoFunction(JavaFunction jf,
                         ScriptableObject glob) {
        javaFunction = jf;
        paramTypes = jf.getParameterTypes();
        globalObject = glob;
    }

    /**
     * Called by the interpreter.
     */
    public Object call(Context cx,
                       Scriptable scope,
                       Scriptable thisObj,
                       Object[] args) throws JavaScriptException {
        Object[] jargs = new Object[args.length];
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = NativeJavaObject.coerceType(paramTypes[i],
                                                  args[i]);
        }
        Object result = javaFunction.call(args);
        if (result == null) {
            return Undefined.instance;
        } else {
            return Context.toObject(result, globalObject);
        }
    }


}
