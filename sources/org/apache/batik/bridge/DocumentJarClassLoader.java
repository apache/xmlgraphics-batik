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

package org.apache.batik.bridge;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.util.Enumeration;

/**
 * This <tt>ClassLoader</tt> implementation only grants permission to
 * connect back to the server from where the document referencing the
 * jar file was loaded. 
 * 
 * A <tt>URLClassLoader</tt> extension is needed in case the user
 * allows linked jar files to come from a different origin than
 * the document referencing them.
 *
 * @author <a mailto="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DocumentJarClassLoader extends URLClassLoader {
    /**
     * CodeSource for the Document which referenced the Jar file
     * @see #getPermissions
     */
    protected CodeSource documentCodeSource = null;

    /**
     * Constructor
     */
    public DocumentJarClassLoader(URL jarURL,
                                  URL documentURL){
        super(new URL[]{jarURL});

        if (documentURL != null) {
            documentCodeSource = new CodeSource(documentURL, null);
        }
    }

    /**
     * Returns the permissions for the given codesource object.
     * The implementation of this method first gets the permissions
     * granted by the policy, and then adds additional permissions
     * based on the URL of the codesource.
     * <p>
     * Then, if the documentURL passed at construction time is
     * not null, the permissions granted to that URL are added.
     *
     * As a result, the jar file code will only be able to 
     * connect to the server which served the document.
     *
     * @param codesource the codesource
     * @return the permissions granted to the codesource
     */
    protected PermissionCollection getPermissions(CodeSource codesource)
    {
        // First, get the permissions which may be granted 
        // through the policy file(s)
	Policy p = Policy.getPolicy();

	PermissionCollection pc = null;
	if (p != null) {
	    pc = p.getPermissions(codesource);
	}

        // Now, add permissions if the documentCodeSource is not null
        if (documentCodeSource != null){
            PermissionCollection urlPC 
                = super.getPermissions(documentCodeSource);

            if (pc != null) {
                Enumeration items = urlPC.elements();
                while (items.hasMoreElements()) {
                    pc.add((Permission)(items.nextElement()));
                }
            } else {
                pc = urlPC;
            }
        }

	return pc;
    }
}
