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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests Node.textContent.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class NodeTextContentTest extends DOM3Test {
    public boolean runImplBase() throws Exception {
        Document doc = newSVGDoc();
        AbstractElement e = (AbstractElement) doc.getDocumentElement();
        e.appendChild(doc.createTextNode("abc"));
        Element e2 = doc.createElementNS(SVG_NAMESPACE_URI, "text");
        e2.appendChild(doc.createTextNode("def"));
        e.appendChild(e2);
        e.appendChild(doc.createCDATASection("ghi"));
        String s = e.getTextContent();
        e.setTextContent("blah");
        return s.equals("abcdefghi")
                && e.getFirstChild().getNodeType() == Node.TEXT_NODE
                && e.getFirstChild().getNodeValue().equals("blah")
                && e.getLastChild() == e.getFirstChild();
    }
}
