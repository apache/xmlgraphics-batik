/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.batik.util.BatikSecurityManager;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.SecurityController;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

/**
 * This implementation of the Rhino <tt>SecurityController</tt> interface is
 * meant for use within the context of Batik only. It is a partial
 * implementation of the interface that does what is needed by Batik and
 * no more.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class BatikSecurityController extends SecurityController {

    /**
     * Default constructor
     */
    public GeneratedClassLoader createClassLoader(final ClassLoader parentLoader,
                                                  Object securityDomain) {
        return (RhinoClassLoader)securityDomain;
    }

    /**
     * Get dynamic security domain that allows an action only if it is allowed
     * by the current Java stack and <i>securityDomain</i>. If
     * <i>securityDomain</i> is null, return domain representing permissions
     * allowed by the current stack.
     */
    public Object getDynamicSecurityDomain(Object securityDomain) {
        return securityDomain;
    }

    /**
     * Call {@link Script#exec(Context cx, Scriptable scope)} of
     * <i>script</i> under restricted security domain where an action is
     * allowed only if it is allowed according to the Java stack on the
     * moment of the <i>execWithDomain</i> call and <i>securityDomain</i>.
     * Any call to {@link #getDynamicSecurityDomain(Object)} during
     * execution of {@link Script#exec(Context cx, Scriptable scope)}
     * should return a domain incorporate restrictions imposed by
     * <i>securityDomain</i>.
     */
    public Object execWithDomain(final Context cx, final Scriptable scope,
                                 final Script script, Object securityDomain)
        throws JavaScriptException {
        return script.exec(cx, scope);
    }
}
