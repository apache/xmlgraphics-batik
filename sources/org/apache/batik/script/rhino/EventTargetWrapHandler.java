/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.script.rhino;

import org.mozilla.javascript.WrapHandler;
import org.mozilla.javascript.Scriptable;

import org.w3c.dom.events.EventTarget;

/**
 * This is utility class allowing to pass an ECMAScript function
 * as a parameter of the <code>addEventListener</code> method of
 * <code>EventTarget</code> objects as DOM Level 2 recommendation
 * required.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
class EventTargetWrapHandler implements WrapHandler {
    private RhinoInterpreter interpreter;

    public EventTargetWrapHandler(RhinoInterpreter interp) {
        interpreter = interp;
    }

    public Object wrap(Scriptable scope, Object obj, Class staticType) {
        if (obj instanceof EventTarget) {
            return interpreter.buildEventTargetWrapper((EventTarget)obj);
        }
        return null;
    }
}
