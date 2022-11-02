/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.script.rhino;

import org.mozilla.javascript.ClassShutter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class shutter that restricts access to Batik internals from script.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class RhinoClassShutter implements ClassShutter {
    public static final List<String> WHITELIST = new ArrayList<>();

    /*
    public RhinoClassShutter() {
        // I suspect that we might want to initialize this
        // from a resource file.
        // test();
    }

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

    /**
     * Returns whether the given class is visible to scripts.
     */
    public boolean visibleToScripts(String fullClassName) {
        for (String v : WHITELIST) {
            if (fullClassName.matches(v)) {
                return true;
            }
        }
        return false;
    }
}
