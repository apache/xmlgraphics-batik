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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.SecurityController;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

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
    public GeneratedClassLoader createClassLoader
        (final ClassLoader parentLoader, Object securityDomain) {

        if (securityDomain instanceof RhinoClassLoader) {
            return (RhinoClassLoader)securityDomain;
        }
		
        // FIXX: This should be supported by intersecting perms.
        // Calling var script = Script(source); script(); is not supported
        throw new SecurityException("Script() objects are not supported");
    }

    /**
     * Get dynamic security domain that allows an action only if it is allowed
     * by the current Java stack and <i>securityDomain</i>. If
     * <i>securityDomain</i> is null, return domain representing permissions
     * allowed by the current stack.
     */
    public Object getDynamicSecurityDomain(Object securityDomain) {

        ClassLoader loader = (RhinoClassLoader)securityDomain;
        // Already have a rhino loader in place no need to
        // do anything (normally you would want to union the
        // the current stack with the loader's context but
        // in our case no one has lower privledges than a
        // rhino class loader).
        if (loader != null) 
            return loader;

        return AccessController.getContext();
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
        
        AccessControlContext acc;
        if (securityDomain instanceof AccessControlContext)
            acc = (AccessControlContext)securityDomain;
        else {
            RhinoClassLoader loader = (RhinoClassLoader)securityDomain;
            acc = loader.rhinoAccessControlContext;
        }

        try {
            // acc = new AccessController(acc, acc.getDomainCombiner());
            return AccessController.doPrivileged
                (new PrivilegedExceptionAction() {
                        public Object run() throws JavaScriptException {
                            return script.exec(cx, scope);
                        }
                    }, acc );
        } catch (Exception e) {
            throw new JavaScriptException(e);
        }

    }
}
