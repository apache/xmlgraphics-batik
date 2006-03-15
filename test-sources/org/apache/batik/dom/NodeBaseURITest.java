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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests Node.baseURI.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class NodeBaseURITest extends DOM3Test {
    public boolean runImplBase() throws Exception {
        Document doc = newSVGDoc();
        ((AbstractDocument) doc).setDocumentURI("http://example.com/blah");
        Element e = doc.createElementNS(SVG_NAMESPACE_URI, "g");
        doc.getDocumentElement().appendChild(e);
        e.setAttributeNS(XML_NAMESPACE_URI, "xml:base", "http://example.org/base");
        Element e2 = doc.createElementNS(SVG_NAMESPACE_URI, "g");
        e.appendChild(e2);
        e2.setAttributeNS(XML_NAMESPACE_URI, "xml:base", "/somewhere");
        return "http://example.com/blah".equals(((AbstractNode) doc).getBaseURI())
                && "http://example.org/base".equals(((AbstractNode) e).getBaseURI())
                && "http://example.org/somewhere".equals(((AbstractNode) e2).getBaseURI());
    }
}
