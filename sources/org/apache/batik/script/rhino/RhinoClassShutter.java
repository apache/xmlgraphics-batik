/*

   Copyright 2004  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

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
