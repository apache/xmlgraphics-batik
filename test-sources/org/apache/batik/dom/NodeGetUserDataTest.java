/*

   Copyright 2003  The Apache Software Foundation 

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
package org.apache.batik.dom;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.w3c.dom.*;

/**
 * Tests Node.setUserData and Node.getUserData.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class NodeGetUserDataTest extends DOM3Test {
    static class UserHandler implements UserDataHandler {
        int count = 0;
        public void handle(short op,
                           String key,
                           Object data,
                           Node src,
                           Node dest) {
            count++;
        }
        public int getCount() {
            return count;
        }
    }
    public boolean runImplBase() throws Exception {
        UserHandler udh = new UserHandler();
        Document doc = newDoc();
        org.apache.batik.dom.dom3.Element e = (org.apache.batik.dom.dom3.Element) doc.createElementNS(null, "test");
        e.setUserData("key", "val", udh);
        ((org.apache.batik.dom.dom3.Document) doc).renameNode(e, null, "abc");
        return udh.getCount() == 1
                && ((String) e.getUserData("key")).equals("val");
    }
}
