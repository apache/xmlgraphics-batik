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

import org.apache.batik.dom.dom3.Node;
import org.w3c.dom.*;

/**
 * Tests Node.compareDocumentPosition.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class NodeCompareDocumentPositionTest extends DOM3Test {
    public boolean runImplBase() throws Exception {
        Document doc = newSVGDoc();
        org.apache.batik.dom.dom3.Element e = (org.apache.batik.dom.dom3.Element) doc.createElementNS(null, "test");
        doc.getDocumentElement().appendChild(e);
        org.apache.batik.dom.dom3.Element e2 = (org.apache.batik.dom.dom3.Element) doc.createElementNS(null, "two");
        e.appendChild(e2);
        org.apache.batik.dom.dom3.Element e3 = (org.apache.batik.dom.dom3.Element) doc.createElementNS(null, "three");
        e.appendChild(e3);
        org.apache.batik.dom.dom3.Element e4 = (org.apache.batik.dom.dom3.Element) doc.createElementNS(null, "four");
        doc.getDocumentElement().appendChild(e4);

        return e.compareDocumentPosition(e2) == (Node.DOCUMENT_POSITION_CONTAINS | Node.DOCUMENT_POSITION_PRECEDING)
                && e2.compareDocumentPosition(e) == (Node.DOCUMENT_POSITION_CONTAINED_BY | Node.DOCUMENT_POSITION_FOLLOWING)
                && e.compareDocumentPosition(e) == 0
                && e2.compareDocumentPosition(e3) == Node.DOCUMENT_POSITION_PRECEDING
                && e3.compareDocumentPosition(e2) == Node.DOCUMENT_POSITION_FOLLOWING
                && e3.compareDocumentPosition(e4) == Node.DOCUMENT_POSITION_PRECEDING
                && e4.compareDocumentPosition(e3) == Node.DOCUMENT_POSITION_FOLLOWING;
    }
}
