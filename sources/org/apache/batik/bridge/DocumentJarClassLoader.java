/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.io.File;
import java.io.FilePermission;

import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;

import java.security.AccessController;
import java.security.Permission;
import java.security.Policy;
import java.security.SecureClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;

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
