/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.test.*;
import org.apache.batik.util.ApplicationSecurityEnforcer;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.test.svg.SelfContainedSVGOnLoadTest;

/**
 * Helper class to simplify writing the unitTesting.xml file for 
 * the bridge.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */

public class ScriptSelfTest extends SelfContainedSVGOnLoadTest {
    boolean secure = true;
    boolean constrain = true;
    String scripts = "text/ecmascript, application/java-archive";
    TestUserAgent userAgent = new TestUserAgent();

    public void setId(String id){
        super.setId(id);
        svgURL = resolveURL("test-resources/org/apache/batik/bridge/" + id + ".svg");
    }

    public void setSecure(Boolean secure){
        this.secure = secure.booleanValue();
    }

    public Boolean getSecure(){
        return new Boolean(this.secure);
    }

    public void setConstrain(Boolean constrain){
        this.constrain = constrain.booleanValue();
    }

    public Boolean getConstrain(){
        return new Boolean(this.constrain);
    }

    public void setScripts(String scripts){
        this.scripts = scripts;
    }

    public String getScripts(){
        return scripts;
    }

    public TestReport runImpl() throws Exception{
        ApplicationSecurityEnforcer ase
            = new ApplicationSecurityEnforcer(this.getClass(),
                                              "org/apache/batik/apps/svgbrowser/resources/svgbrowser.policy",
                                              "dummy.jar");

        if (secure) {
            ase.enforceSecurity(true);
        }

        try {
            return super.runImpl();
        } finally {
            ase.enforceSecurity(false);
        }
    }

    protected UserAgent buildUserAgent(){
        return userAgent;
    }
    
    class TestUserAgent extends UserAgentAdapter {
        public ScriptSecurity getScriptSecurity(String scriptType,
                                                ParsedURL scriptPURL,
                                                ParsedURL docPURL){
            if (scripts.indexOf(scriptType) == -1){
                return new NoLoadScriptSecurity(scriptType);
            } else {
                if (constrain){
                    return new DefaultScriptSecurity
                        (scriptType, scriptPURL, docPURL);
                } else {
                    return new RelaxedScriptSecurity
                        (scriptType, scriptPURL, docPURL);
                }
            }
        }
    }

}
