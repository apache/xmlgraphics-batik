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

import org.mozilla.javascript.ClassShutter;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>deweese</a>
 * @version $Id$
 */
public class RhinoClassShutter implements ClassShutter {
    
    public RhinoClassShutter() {
        // I suspect that we might want to initialize this
        // from a resource file.
        // test();
    }

    /*
    public void test() {
        test("org.mozilla.javascript.Context");
        test("org.mozilla.javascript");
        test("org.apache.batik.dom.SVGOMDocument");
        test("org.apache.batik.script.rhino.RhinoInterpreter");
        test("org.apache.batik.apps.svgbrowser.JSVGViewerFrame");
        test("org.apache.batik.bridge.BridgeContext");
        test("org.apache.batik.bridge.BaseScriptingEnvironment");
        test("org.apache.batik.bridge.ScriptingEnvironment");
    }
    public void test(String cls) {
        System.err.println("Test '" + cls + "': " + 
                           visibleToScripts(cls));
    }
    */

    public boolean visibleToScripts(String fullClassName) {
        // Don't let them mess with script engine's internals.
        if (fullClassName.startsWith("org.mozilla.javascript"))
            return false;

        if (fullClassName.startsWith("org.apache.batik.")) {
            // Just get packge within batik.
            String batikPkg = fullClassName.substring(17);

            // Don't let them mess with Batik script internals.
            if (batikPkg.startsWith("script"))
                return false;

            // Don't let them get global structures.
            if (batikPkg.startsWith("apps"))
                return false;

            // Don't let them get Scripting stuff from bridge.
            if (batikPkg.startsWith("bridge.")) {
                
                if (batikPkg.indexOf(".BaseScriptingEnvironment")!=-1)
                    return false;
                if (batikPkg.indexOf(".ScriptingEnvironment")!=-1)
                    return false;
            }
        }

        return true;
    }
    
};
