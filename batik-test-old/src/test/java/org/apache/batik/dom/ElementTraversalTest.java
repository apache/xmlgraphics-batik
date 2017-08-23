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

import java.io.StringReader;

import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.ElementTraversal;

/**
 * Tests the {@link ElementTraversal} interface.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class ElementTraversalTest extends AbstractTest {

    private String DOC = "<a><b/><c>.<?x?>.</c><d>.<?x?><e/><f/><?x?>.</d><g><h/>.<i/></g></a>";

    public boolean runImplBasic() throws Exception {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXDocumentFactory df = new SAXDocumentFactory(GenericDOMImplementation.getDOMImplementation(), parser);
        Document doc = df.createDocument("http://example.org/", new StringReader(DOC));

        AbstractElement a = (AbstractElement) doc.getDocumentElement();
        AbstractElement b = (AbstractElement) a.getFirstChild();
        AbstractElement c = (AbstractElement) b.getNextSibling();
        AbstractElement d = (AbstractElement) c.getNextSibling();
        AbstractElement g = (AbstractElement) d.getNextSibling();

        // (1) Test firstElementChild with no children
        ensure(1, b.getFirstElementChild() == null);

        // (2) Test firstElementChild with children but no element children
        ensure(2, c.getFirstElementChild() == null);

        // (3) Test firstElementChild with children but element child is not first
        AbstractElement e = (AbstractElement) d.getFirstElementChild();
        ensure(3, e != null && e.getNodeName().equals("e"));

        // (4) Test firstElementChild with children and element child is first
        AbstractElement h = (AbstractElement) g.getFirstElementChild();
        ensure(4, h != null && h.getNodeName().equals("h"));

        // (5) Test lastElementChild with no children
        ensure(5, b.getLastElementChild() == null);

        // (6) Test lastElementChild with children but no element children
        ensure(6, c.getLastElementChild() == null);

        // (7) Test lastElementChild with children but element child is not last
        AbstractElement f = (AbstractElement) d.getLastElementChild();
        ensure(7, f != null && f.getNodeName().equals("f"));

        // (8) Test lastElementChild with children and element child is last
        AbstractElement i = (AbstractElement) g.getLastElementChild();
        ensure(8, i != null && i.getNodeName().equals("i"));

        // (9) Test nextElementSibling with no next sibling
        ensure(9, a.getNextElementSibling() == null);

        // (10) Test nextElementSibling with next siblings but no element next sibling
        ensure(10, f.getNextElementSibling() == null);

        // (11) Test nextElementSibling with next element sibling but not first
        ensure(11, h.getNextElementSibling() == i);

        // (12) Test nextElementSibling with next element sibling which is first
        ensure(12, e.getNextElementSibling() == f);

        // (13) Test previousElementSibling with no previous sibling
        ensure(13, a.getPreviousElementSibling() == null);

        // (14) Test previousElementSibling with previous siblings but no element previous sibling
        ensure(14, e.getPreviousElementSibling() == null);

        // (15) Test previousElementSibling with previous element sibling but not first
        ensure(15, i.getPreviousElementSibling() == h);

        // (16) Test previousElementSibling with previous element sibling which is first
        ensure(16, f.getPreviousElementSibling() == e);

        // (17-20) Test childElementCount for a few cases
        ensure(17, a.getChildElementCount() == 4);
        ensure(18, b.getChildElementCount() == 0);
        ensure(19, c.getChildElementCount() == 0);
        ensure(20, d.getChildElementCount() == 2);

        return true;
    }

    protected void ensure(int subTestNumber, boolean b) {
        if (!b) {
            throw new RuntimeException("Assertion failure in sub-test " + subTestNumber);
        }
    }
}
