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

import java.security.SecureClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;

/**
 * This class loader implementation will work whether or not the
 * documentURL is null.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class RhinoClassLoader extends URLClassLoader {
    /**
     * URL for the document referencing the script.
     */
    protected URL documentURL;

    /**
     * CodeSource for classes defined by this loader
     */
    protected CodeSource codeSource;
    
    /**
     * Constructor.
     * @param documentURL the URL from which to load classes and resources 
     */
    public RhinoClassLoader(URL documentURL){
        super(documentURL != null ? new URL[]{documentURL} : new URL[]{});
        // super(new URL[]{});
        this.documentURL = documentURL;
        if (documentURL != null){
            codeSource = new CodeSource(documentURL, null);
        }
    }
    
    /**
     * Define and load a Java class
     */
    public Class defineClass(String name, 
                             byte[] data){
        return super.defineClass(name, data, 0, data.length, codeSource);
    }

    /**
     * Returns the permissions for the given CodeSource object. 
     */
    protected PermissionCollection getPermissions(CodeSource codesource) {
        return super.getPermissions(codesource);
    }
}

