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

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.PrivilegedAction;

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
     * Helper, returns the URL array from the parent loader
     */
    static URL[] getURL(ClassLoader parent) {
        if (parent instanceof RhinoClassLoader) {
            URL documentURL = ((RhinoClassLoader)parent).documentURL;
            if (documentURL != null) {
                return new URL[] {documentURL};
            } else {
                return new URL[] {};
            }
        } else {
            return new URL[] {};
        } 
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

