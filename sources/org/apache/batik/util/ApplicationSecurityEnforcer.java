/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import java.net.URL;

import java.security.Policy;

/**
 * This is a helper class which helps applications enforce secure
 * script execution.
 * <br />
 * It is used by the Squiggle browser as well as the rasterizer.
 * <br />
 * This class can install a <tt>SecurityManager</tt> for an application
 * and resolves whether the application runs in a development
 * environment or from a jar file (in other words, it resolves code-base
 * issues for the application).
 * <br />
 *
 * @author <a mailto="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ApplicationSecurityEnforcer {
    /**
     * Message for the SecurityException thrown when there is already
     * a SecurityManager installed at the time Squiggle tries
     * to install its own security settings.
     */
    public static final String EXCEPTION_ALIEN_SECURITY_MANAGER
        = "ApplicationSecurityEnforcer.message.security.exception.alien.security.manager";

    /**
     * Message for the NullPointerException thrown when no policy
     * file can be found.
     */
    public static final String EXCEPTION_NO_POLICY_FILE
        = "ApplicationSecurityEnforcer.message.null.pointer.exception.no.policy.file";

    /**
     * System property for specifying an additional policy file.
     */
    public static final String PROPERTY_JAVA_SECURITY_POLICY 
        = "java.security.policy";

    /**
     * Files in a jar file have a URL with the jar protocol
     */
    public static final String JAR_PROTOCOL
        = "jar:";

    /**
     * Used in jar file urls to separate the jar file name 
     * from the referenced file
     */
    public static final String JAR_URL_FILE_SEPARATOR
        = "!/";

    /**
     * System property for App's development base directory
     */
    public static final String PROPERTY_APP_DEV_BASE
        = "app.dev.base";

    /**
     * System property for App's jars base directory
     */
    public static final String PROPERTY_APP_JAR_BASE
        = "app.jar.base";

    /**
     * Directory where classes are expanded in the development
     * version
     */
    public static final String APP_MAIN_CLASS_DIR
        = "classes/";

    /**
     * The application's main entry point
     */
    protected Class appMainClass;

    /**
     * The application's security policy
     */
    protected String securityPolicy;

    /**
     * The jar file into which the application is packaged
     */
    protected String appJarFile;

    /**
     * The resource name for the application's main class
     */
    protected String appMainClassRelativeURL;

    /**
     * Keeps track of the last SecurityManager installed
     */
    protected BatikSecurityManager lastSecurityManagerInstalled;

    /**
     * @param appClass class of the applications's main entry point
     * @param securityPolicy resource for the security policy which 
     *        should be enforced for the application. 
     * @param appJarFile the Jar file into which the application is
     *        packaged.
     */
    public ApplicationSecurityEnforcer(Class appMainClass,
                                       String securityPolicy,
                                       String appJarFile){
        this.appMainClass = appMainClass;
        this.securityPolicy = securityPolicy;
        this.appJarFile = appJarFile;
        this.appMainClassRelativeURL = 
            appMainClass.getName().replace('.', '/')
            +
            ".class";
            
    }

    /**
     * Enforces security by installing a <tt>SecurityManager</tt>.
     * This will throw a <tt>SecurityException</tt> if installing
     * a <tt>SecurityManager</tt> requires overriding an existing
     * <tt>SecurityManager</tt>. In other words, this method will 
     * not install a new <tt>SecurityManager</tt> if there is 
     * already one it did not install in place.
     */
    public void enforceSecurity(boolean enforce){
        SecurityManager sm = System.getSecurityManager();
        if (sm != null && sm != lastSecurityManagerInstalled) {
            // Throw a Security exception: we do not want to override
            // an 'alien' SecurityManager with either null or 
            // a new SecurityManager.
            throw new SecurityException
                (Messages.getString(EXCEPTION_ALIEN_SECURITY_MANAGER));
        }
        
        if (enforce) {
            // We want to install a SecurityManager.
            if (sm == null) {
                installSecurityManager();
            }
        } else {
            if (sm != null) {
                System.setSecurityManager(null);
                lastSecurityManagerInstalled = null;
            }
        }
    }

    /**
     * Installs a SecurityManager on behalf of the application
     */
    public void installSecurityManager(){
        Policy policy = Policy.getPolicy();
        BatikSecurityManager securityManager = new BatikSecurityManager();

        // Specify app's security policy in the
        // system property. 
        ClassLoader cl = appMainClass.getClassLoader();
        URL url = cl.getResource(securityPolicy);

        if (url == null) {
            throw new NullPointerException
                (Messages.formatMessage(EXCEPTION_NO_POLICY_FILE,
                                        new Object[]{securityPolicy}));
        }

        System.setProperty(PROPERTY_JAVA_SECURITY_POLICY,
                           url.toString());

        // 
        // The following detects whether the application is running in the
        // development environment, in which case it will set the 
        // app.dev.base property or if it is running in the binary
        // distribution, in which case it will set the app.jar.base
        // property. These properties are expanded in the security 
        // policy files.
        // Property expansion is used to provide portability of the 
        // policy files between various code bases (e.g., file base,
        // server base, etc..).
        //
        url = cl.getResource(appMainClassRelativeURL);
        if (url == null){
            // Something is really wrong: we would be running a class
            // which can't be found....
            throw new Error(appMainClassRelativeURL);
        }
        
        String expandedMainClassName = url.toString();
        if (expandedMainClassName.startsWith(JAR_PROTOCOL) ) {
            setJarBase(expandedMainClassName);
        } else {
            setDevBase(expandedMainClassName);
        }
        
        // Install new security manager
        System.setSecurityManager(securityManager);
        lastSecurityManagerInstalled = securityManager;

        // Forces re-loading of the security policy
        policy.refresh();
    }

    private void setJarBase(String expandedMainClassName){
        expandedMainClassName = expandedMainClassName.substring(JAR_PROTOCOL.length());

        int codeBaseEnd = 
            expandedMainClassName.indexOf(appJarFile +
                                          JAR_URL_FILE_SEPARATOR +
                                          appMainClassRelativeURL);

        if (codeBaseEnd == -1){
            // Something is seriously wrong. This should *never* happen
            // as the APP_SECURITY_POLICY_URL is such that it will be
            // a substring of its corresponding URL value
            throw new Error();
        }

        String appCodeBase = expandedMainClassName.substring(0, codeBaseEnd);
        System.setProperty(PROPERTY_APP_JAR_BASE, appCodeBase);
    }

    /**
     * Position the app.dev.base property for expansion in 
     * the policy file used when App is running in its 
     * development version
     */
    private void setDevBase(String expandedMainClassName){
        int codeBaseEnd = 
            expandedMainClassName.indexOf(APP_MAIN_CLASS_DIR + 
                                          appMainClassRelativeURL);

        if (codeBaseEnd == -1){
            // Something is seriously wrong. This should *never* happen
            // as the APP_SECURITY_POLICY_URL is such that it will be
            // a substring of its corresponding URL value
            throw new Error();
        }

        String appCodeBase = expandedMainClassName.substring(0, codeBaseEnd);
        System.setProperty(PROPERTY_APP_DEV_BASE, appCodeBase);
    }


}

