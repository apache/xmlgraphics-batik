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

import org.mozilla.javascript.SecuritySupport;

/**
 * This implementation of the Rhino <tt>SecuritySupport</tt> interface is
 * meant for use within the context of Batik only. It is a partial 
 * implementation of the interface that does what is needed by Batik and 
 * no more.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class BatikSecuritySupport implements SecuritySupport {
    /**
     * Default constructor
     */
    public BatikSecuritySupport(){
    }

    /**
     * Define and load a Java class
     */
    public Class defineClass(String name,
                             byte[] data,
                             Object securityDomain){
        RhinoClassLoader rcl = (RhinoClassLoader)securityDomain;
        return rcl.defineClass(name, data);
    }

    /**
     * Get the current class Context.
     * This implementation always returns null.
     */
    public Class[] getClassContext(){
        return null;
    }

    /**
     * Return teh security context associated with the 
     * given class.
     * In this implementation, we return the <tt>ClassLoader</tt>
     * which created the input class.
     */
    public Object getSecurityDomain(Class cl){
        return cl.getClassLoader();
    }

    /**
     * Return true if the Java class with the given name should 
     * be exposed to scripts.
     * 
     * In this implementation, this always return true, as 
     * security is enforced by the SecurityManager's policy
     * and the Permissions granted by the URLClassLoader 
     * used to load classes.
     */
    public boolean visibleToScripts(String fullClassName){
        return true;
    }
}
