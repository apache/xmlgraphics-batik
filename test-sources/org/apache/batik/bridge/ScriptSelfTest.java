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

import org.apache.batik.test.*;
import org.apache.batik.util.ApplicationSecurityEnforcer;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.test.svg.SelfContainedSVGOnLoadTest;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.security.ProtectionDomain;
import java.security.Permissions;

import java.io.FilePermission;

/**
 * Helper class to simplify writing the unitTesting.xml file for 
 * the bridge.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */

public class ScriptSelfTest extends SelfContainedSVGOnLoadTest {
    String scripts = "text/ecmascript, application/java-archive";
    boolean secure = true;
    String scriptOrigin = "any";
    boolean restricted = false;
    String fileName;

    TestUserAgent userAgent = new TestUserAgent();

    public void setId(String id){
        super.setId(id);

        if (id != null) {
            int i = id.indexOf("(");
            if (i != -1) {
                id = id.substring(0, i);
            }
            fileName = "test-resources/org/apache/batik/bridge/" + id + ".svg";
            svgURL = resolveURL(fileName);
        }
    }

    public void setSecure(boolean secure){
        this.secure = secure;
    }

    public boolean getSecure(){
        return secure;
    }

    public String getScriptOrigin() {
        return scriptOrigin;
    }

    public void setScriptOrigin(String scriptOrigin) {
        this.scriptOrigin = scriptOrigin;
    }

    public boolean getRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public void setScripts(String scripts){
        this.scripts = scripts;
    }

    public String getScripts(){
        return scripts;
    }

    public TestReport runImpl() throws Exception {
        ApplicationSecurityEnforcer ase
            = new ApplicationSecurityEnforcer(this.getClass(),
                                              "org/apache/batik/apps/svgbrowser/resources/svgbrowser.policy");

        if (secure) {
            ase.enforceSecurity(true);
        }

        try {
            if (!restricted) {
                return superRunImpl();
            } else {
                // Emulate calling from restricted code. We create a 
                // calling context with only the permission to read
                // the file.
                FilePermission permission 
                    = new FilePermission(fileName, "read");
                Permissions permissions = new Permissions();
                permissions.add(permission);
                ProtectionDomain domain = new ProtectionDomain(null, permissions);
                AccessControlContext ctx = new AccessControlContext
                    (new ProtectionDomain[] {domain});

                try {
                    return (TestReport)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                            public Object run() throws Exception {
                                return superRunImpl();
                            }
                        }, ctx);
                } catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            }
        } finally {
            ase.enforceSecurity(false);
        }
    }

    protected TestReport superRunImpl() throws Exception {
        try {
            return super.runImpl();
        } catch (ExceptionInInitializerError e) {
            e.printStackTrace();
            throw e;
        } catch (NoClassDefFoundError e) {
            // e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    protected UserAgent buildUserAgent(){
        return userAgent;
    }
    
    class TestUserAgent extends UserAgentAdapter {
        public ScriptSecurity getScriptSecurity(String scriptType,
                                                ParsedURL scriptPURL,
                                                ParsedURL docPURL){
            ScriptSecurity scriptSecurity = null;
            if (scripts.indexOf(scriptType) == -1){
                scriptSecurity = new NoLoadScriptSecurity(scriptType);
            } else {
                if ("any".equals(scriptOrigin)) {
                     scriptSecurity = new RelaxedScriptSecurity
                        (scriptType, scriptPURL, docPURL);
                } else if ("document".equals(scriptOrigin)) {
                    scriptSecurity = new DefaultScriptSecurity
                        (scriptType, scriptPURL, docPURL);
                } else if ("embeded".equals(scriptOrigin)) {
                    scriptSecurity = new EmbededScriptSecurity
                        (scriptType, scriptPURL, docPURL);
                } else if ("none".equals(scriptOrigin)) {
                    scriptSecurity = new NoLoadScriptSecurity(scriptType);
                } else {
                    throw new Error("Wrong scriptOrigin : " + scriptOrigin);
                }
            }

            System.err.println(">>>>>>>>>>>>> using script security: " + scriptSecurity + 
                               " for " + scriptPURL + " referenced from " + docPURL);
            return scriptSecurity;
        }
    }

}
