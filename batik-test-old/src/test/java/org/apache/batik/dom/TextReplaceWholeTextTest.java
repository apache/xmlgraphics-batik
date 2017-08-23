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
package org.apache.batik.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

/**
 * Tests Text.replaceWholeText.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class TextReplaceWholeTextTest extends DOM3Test {
    public boolean runImplBasic() throws Exception {
        Document doc = newSVGDoc();
        Text n1 = doc.createTextNode("abc");
        Text n2 = doc.createTextNode("def");
        Text n3 = doc.createCDATASection("ghi");
        doc.getDocumentElement().appendChild(n1);
        doc.getDocumentElement().appendChild(n2);
        doc.getDocumentElement().appendChild(n3);
        ((AbstractText) n2).replaceWholeText("xyz");

        return doc.getDocumentElement().getFirstChild().getNodeValue().equals("xyz")
                && doc.getDocumentElement().getFirstChild().getNextSibling() == null;
    }
}
