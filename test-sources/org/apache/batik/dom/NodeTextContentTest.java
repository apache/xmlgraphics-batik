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
 * Tests Node.textContent.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class NodeTextContentTest extends DOM3Test {
    public boolean runImplBase() throws Exception {
        Document doc = newSVGDoc();
        org.apache.batik.dom.dom3.Element e = (org.apache.batik.dom.dom3.Element) doc.getDocumentElement();
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
