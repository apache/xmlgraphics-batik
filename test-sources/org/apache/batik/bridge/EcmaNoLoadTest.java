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

import org.apache.batik.test.svg.SVGOnLoadExceptionTest;

/**
 * Checks that ECMA Scripts which should not be loaded are not
 * loaded.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class EcmaNoLoadTest extends DefaultTestSuite {
    public EcmaNoLoadTest() {
        String scripts = "application/java-archive";
        String[] scriptSource = {"bridge/ecmaCheckNoLoadAny",
                                 "bridge/ecmaCheckNoLoadSameAsDocument",
                                 "bridge/ecmaCheckNoLoadEmbed",
                                 "bridge/ecmaCheckNoLoadEmbedAttr",
        };
        boolean[] secure = {true, false};
        String[] scriptOrigin = {"ANY", "DOCUMENT", "EMBEDED", "NONE"};

        //
        // If "application/ecmascript" is disallowed, scripts
        // should not be loaded, no matter their origin or the
        // other security settings.
        //
        for (int i=0; i<scriptSource.length; i++) {
            for (int j=0; j<secure.length; j++) {
                for (int k=0; k<scriptOrigin.length; k++) {
                    SVGOnLoadExceptionTest t = buildTest(scripts,
                                                         scriptSource[i],
                                                         scriptOrigin[k],
                                                         secure[j],
                                                         false);
                    addTest(t);
                }
            }
        }

        //
        // If script run in restricted mode, then there should be
        // a security exception, no matter what the other settings are
        // (if we are running code under a security manager, that is,
        // i.e., secure is true).
        scripts = "text/ecmascript";
        for (int i=0; i<scriptSource.length; i++) {
            for (int k=0; k<scriptOrigin.length; k++) {
                SVGOnLoadExceptionTest t = buildTest(scripts,
                                                     scriptSource[i],
                                                     scriptOrigin[k],
                                                     true,
                                                     true);
                addTest(t);
            }
        }

        //
        // If "applicatin/ecmascript" is allowed, but the accepted
        // script origin is lower than the candidate script, then
        // the script should not be loaded (e.g., if scriptOrigin
        // is embeded and trying to load an external script).
        //
        for (int j=0; j<scriptOrigin.length; j++) {
            int max = j;
            if (j == scriptOrigin.length - 1) {
                max = j+1;
            }
            for (int i=0; i<max; i++) {
                for (int k=0; k<secure.length; k++) {
                    SVGOnLoadExceptionTest t= buildTest(scripts, scriptSource[i],
                                                        scriptOrigin[j],
                                                        secure[k],
                                                        false);
                    addTest(t);
                }
            }
        }
    }

    SVGOnLoadExceptionTest buildTest(String scripts, String id, String origin, boolean secure, boolean restricted) {
        SVGOnLoadExceptionTest t = new SVGOnLoadExceptionTest();
        String desc = 
            "(scripts=" + scripts + 
            ")(scriptOrigin=" + origin +
            ")(secure=" + secure +
            ")(restricted=" + restricted + ")";
        
        t.setId(id + desc);
        t.setScriptOrigin(origin);
        t.setSecure(secure);
        t.setScripts(scripts);
        t.setExpectedExceptionClass("java.lang.SecurityException");
        t.setRestricted(restricted);

        return t;
    }
                             
}
