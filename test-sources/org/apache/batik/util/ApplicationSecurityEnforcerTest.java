/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util;

import org.apache.batik.test.*;

/**
 * Validates the operation of the security enforcer class.
 *
 * @author <a mailto="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class ApplicationSecurityEnforcerTest extends DefaultTestSuite {
    final static Class APP_MAIN_CLASS = org.apache.batik.apps.svgbrowser.Main.class;
    final static String APP_SECURITY_POLICY = "org/apache/batik/apps/svgbrowser/resources/svgbrowser.policy";
    final static String APP_JAR = "batik-svgbrowser.jar";

    /**
     * In the constructor, append atomic tests
     */
    public ApplicationSecurityEnforcerTest(){
        addTest(new CheckNoSecurityManagerOverride());
        addTest(new CheckSecurityEnforcement());
        addTest(new CheckSecurityRemoval());
        addTest(new CheckNoPolicyFile());
    }

    static ApplicationSecurityEnforcer buildTestTarget(){
        return new ApplicationSecurityEnforcer(APP_MAIN_CLASS,
                                               APP_SECURITY_POLICY,
                                               APP_JAR);
    }

    static class CheckNoSecurityManagerOverride extends AbstractTest {
        public boolean runImplBasic(){
            ApplicationSecurityEnforcer aseA
                = buildTestTarget();

            aseA.enforceSecurity(true);

            ApplicationSecurityEnforcer aseB
                = buildTestTarget();

            boolean passed = false;
            try {
                // This should throw a SecurityException
                aseB.enforceSecurity(true);
            } catch (SecurityException se){
                System.out.println(">>>>>>>>>>>>> got expected SecurityException A");
                try {
                    System.out.println(">>>>>>>>>>>>> got expected SecurityException B");
                    aseB.enforceSecurity(false);
                } catch (SecurityException se2){
                    passed = true;
                }
            } 

            aseA.enforceSecurity(false);
            
            return passed;
        }
    }

    static class CheckSecurityEnforcement extends AbstractTest {
        public boolean runImplBasic() {
            ApplicationSecurityEnforcer ase = buildTestTarget();

            try {
                ase.enforceSecurity(true);
                SecurityManager sm = System.getSecurityManager();
                if (sm == ase.lastSecurityManagerInstalled){
                    return true;
                }
            } finally {
                System.setSecurityManager(null);
            }

            return false;
        }
    }

    static class CheckSecurityRemoval extends AbstractTest {
        public boolean runImplBasic() {
            ApplicationSecurityEnforcer ase = buildTestTarget();

            try {
                ase.enforceSecurity(true);
                ase.enforceSecurity(false);
                SecurityManager sm = System.getSecurityManager();
                if (sm == null && ase.lastSecurityManagerInstalled == null) {
                    return true;
                }
            } finally {
                System.setSecurityManager(null);
            }

            return false;
        }
    }

    static class CheckNoPolicyFile extends AbstractTest {
        public boolean runImplBasic() {
            ApplicationSecurityEnforcer ase = 
                new ApplicationSecurityEnforcer(APP_MAIN_CLASS,
                                                "dont.exist.policy",
                                                APP_JAR);

            try {
                ase.enforceSecurity(true);
            } catch (NullPointerException se) {
                return true;
            } finally {
                ase.enforceSecurity(false);
            }
            return false;
        }
    }

}
