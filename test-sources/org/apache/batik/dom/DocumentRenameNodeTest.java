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
import org.w3c.dom.svg.*;

/**
 * Tests Document.renameNode.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class DocumentRenameNodeTest extends DOM3Test {
    public boolean runImplBase() throws Exception {
        Document doc = newSVGDoc();
        Element e = doc.getDocumentElement();
        Element e2 = doc.createElementNS(SVG_NAMESPACE_URI, "g");
        boolean pass = e2 instanceof SVGGElement;
        e2 = (Element) ((org.apache.batik.dom.dom3.Document) doc).renameNode(e2, SVG_NAMESPACE_URI, "svg");
        pass = pass && e2 instanceof SVGSVGElement;
        Attr a = doc.createAttributeNS(null, "test");
        a = (Attr) ((org.apache.batik.dom.dom3.Document) doc).renameNode(a, EX_NAMESPACE_URI, "test2");
        pass = pass && a.getNamespaceURI().equals(EX_NAMESPACE_URI)
            && a.getLocalName().equals("test2");
        return pass;
    }
}
