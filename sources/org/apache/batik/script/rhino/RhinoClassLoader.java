/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 *---------------------------------------------------------------------------*
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script.rhino;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;

import org.mozilla.javascript.GeneratedClassLoader;

/**
 * This class loader implementation will work whether or not the
 * documentURL is null.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class RhinoClassLoader extends URLClassLoader implements GeneratedClassLoader {
    /**
     * URL for the document referencing the script.
     */
    protected URL documentURL;

    /**
     * CodeSource for classes defined by this loader
     */
    protected CodeSource codeSource;

    /**
     * The AccessControlContext which can be associated with
     * code loaded by this class loader if it was running
     * stand-alone (i.e., not invoked by code with lesser
     * priviledges).
     */
    protected AccessControlContext rhinoAccessControlContext;

    /**
     * Constructor.
     * @param documentURL the URL from which to load classes and resources
     * @param parent the parent class loader for delegation
     */
    public RhinoClassLoader(URL documentURL, ClassLoader parent){
        super(documentURL != null ? new URL[]{documentURL} : new URL[]{},
              parent);
        this.documentURL = documentURL;
        if (documentURL != null){
            codeSource = new CodeSource(documentURL, null);
        }

        //
        // Create the Rhino ProtectionDomain
        // and AccessControlContext
        //
        ProtectionDomain rhinoProtectionDomain
            = new ProtectionDomain(codeSource,
                                   getPermissions(codeSource));

        rhinoAccessControlContext
            = new AccessControlContext(new ProtectionDomain[]{
                rhinoProtectionDomain});
    }

    /**
     * Define and load a Java class
     */
    public Class defineClass(String name,
                             byte[] data) {
        return super.defineClass(name, data, 0, data.length, codeSource);
    }

    /**
     * Links the Java class.
     */
    public void linkClass(Class clazz) {
        super.resolveClass(clazz);
    }

    /**
     * Returns the AccessControlContext which should be associated with
     * RhinoCode.
     */
    public AccessControlContext getAccessControlContext() {
        return rhinoAccessControlContext;
    }

    /**
     * Returns the permissions for the given CodeSource object.
     * Compared to URLClassLoader, this adds a FilePermission so
     * that files under the same root directory as the document
     * can be read.
     */
    protected PermissionCollection getPermissions(CodeSource codesource) {
        PermissionCollection perms = null;

        if (codesource != null) {
            perms = super.getPermissions(codesource);
        }

        if (documentURL != null && perms != null) {
            Permission p = null;
            Permission dirPerm = null;
            try {
                p = documentURL.openConnection().getPermission();
            } catch (IOException e){
                p = null;
            }

            if (p instanceof FilePermission){
                String path = p.getName();
                if (!path.endsWith(File.separator)) {
                    // We are dealing with a file, as we would expect
                    // from a document file URL
                    int dirEnd = path.lastIndexOf(File.separator);
                    if (dirEnd != -1){
                        // Include trailing file separator
                        path = path.substring(0, dirEnd + 1);
                        path += "-";
                        dirPerm = new FilePermission(path, "read");
                        perms.add(dirPerm);
                    }
                }
            }
        }

        return perms;
    }
}

