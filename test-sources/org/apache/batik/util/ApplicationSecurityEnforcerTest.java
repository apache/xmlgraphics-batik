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
                                               APP_SECURITY_POLICY);
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
                                                "dont.exist.policy");

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
