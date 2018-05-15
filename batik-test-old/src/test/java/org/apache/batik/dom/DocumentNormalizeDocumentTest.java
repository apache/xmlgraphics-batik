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

import org.apache.batik.test.TestReport;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests Document.normalizeDocument.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class DocumentNormalizeDocumentTest extends DOM3Test {
    static class Handler implements DOMErrorHandler {
        int cd = 0;
        int wf = 0;
        int wfn = 0;
        public boolean handleError(DOMError e) {
            if (e.getType().equals("cdata-sections-splitted")) {
                cd++;
            } else if (e.getType().equals("wf-invalid-character")) {
                wf++;
            } else if (e.getType().equals("wf-invalid-character-in-node-name")) {
                wfn++;
            }
            return true;
        }
        public int get(String s) {
            if (s.equals("cdata-sections-splitted")) {
                return cd;
            } else if (s.equals("wf-invalid-character")) {
                return wf;
            } else if (s.equals("wf-invalid-character-in-node-name")) {
                return wfn;
            } else {
                return 0;
            }
        }
    }

    public TestReport runImpl() throws Exception {
        Handler h = new Handler();
        TestReport report = null;

        // cdata-sections == false
        Document doc = newSVGDoc();
        DOMConfiguration conf = doc.getDomConfig();
        conf.setParameter("cdata-sections", Boolean.FALSE);
        Element e = doc.getDocumentElement();
        e.appendChild(doc.createTextNode("abc"));
        e.appendChild(doc.createCDATASection("def"));
        e.appendChild(doc.createTextNode("ghi"));
        doc.normalizeDocument();
        if (!(e.getFirstChild().getNodeType() == Node.TEXT_NODE
                && e.getFirstChild().getNodeValue().equals("abcdefghi")
                && e.getFirstChild() == e.getLastChild())) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "cdata-sections == false");
        }

        // comments == false
        doc = newSVGDoc();
        conf = doc.getDomConfig();
        conf.setParameter("comments", Boolean.FALSE);
        e = doc.getDocumentElement();
        e.appendChild(doc.createTextNode("abc"));
        e.appendChild(doc.createComment("def"));
        e.appendChild(doc.createTextNode("ghi"));
        doc.normalizeDocument();
        if (!(e.getFirstChild().getNodeType() == Node.TEXT_NODE
                && e.getFirstChild().getNodeValue().equals("abcghi")
                && e.getFirstChild() == e.getLastChild())) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "comments == false");
        }

        // element-content-whitespace == false
        doc = newSVGDoc();
        conf = doc.getDomConfig();
        conf.setParameter("element-content-whitespace", Boolean.FALSE);
        e = doc.getDocumentElement();
        e.appendChild(doc.createTextNode("    "));
        e.appendChild(doc.createElementNS(SVG_NAMESPACE_URI, "g"));
        e.appendChild(doc.createTextNode("    "));
        doc.normalizeDocument();
        if (!(e.getFirstChild().getNodeType() == Node.ELEMENT_NODE
                && e.getFirstChild().getNodeName().equals("g")
                && e.getFirstChild() == e.getLastChild())) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "element-content-whitespace == false");
        }

        // split-cdata-sections == true
        doc = newSVGDoc();
        conf = doc.getDomConfig();
        conf.setParameter("split-cdata-sections", Boolean.TRUE);
        conf.setParameter("error-handler", h);
        e = doc.getDocumentElement();
        e.appendChild(doc.createCDATASection("before ]]> after"));
        doc.normalizeDocument();
        if (!(e.getFirstChild().getNodeType() == Node.CDATA_SECTION_NODE
                && e.getFirstChild().getNodeValue().equals("before ]]")
                && e.getFirstChild().getNextSibling().getNodeType() == Node.CDATA_SECTION_NODE
                && e.getFirstChild().getNextSibling().getNodeValue().equals("> after")
                && e.getFirstChild().getNextSibling() == e.getLastChild()
                && h.get("cdata-sections-splitted") == 1)) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "split-cdata-sections == true");
        }

        // well-formed
        doc = newSVGDoc();
        doc.setStrictErrorChecking(false);
        conf = doc.getDomConfig();
        conf.setParameter("error-handler", h);
        e = doc.getDocumentElement();
        e.appendChild(doc.createComment("before -- after"));
        e.appendChild(doc.createComment("ends in a dash -"));
        e.setAttribute("*", "blah");
        e.appendChild(doc.createProcessingInstruction("abc", "def?>"));
        doc.normalizeDocument();
        if (!(h.get("wf-invalid-character-in-node-name") == 1
                && h.get("wf-invalid-character") == 3)) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "well-formed == true");
        }

        // namespaces
        doc = newDoc();
        e = doc.createElementNS(null, "root");
        doc.appendChild(e);
        Element e2 = doc.createElementNS(null, "parent");
        e.appendChild(e2);
        e2.setAttributeNS(XMLNS_NAMESPACE_URI, "xmlns:ns", "http://www.example.org/ns1");
        e2.setAttributeNS(XMLNS_NAMESPACE_URI, "xmlns:bar", "http://www.example.org/ns2");
        Element e3 = doc.createElementNS("http://www.example.org/ns1", "ns:child1");
        e2.appendChild(e3);
        e3.setAttributeNS(XMLNS_NAMESPACE_URI, "xmlns:ns", "http://www.example.org/ns2");
        e3 = doc.createElementNS("http://www.example.org/ns2", "ns:child2");
        e2.appendChild(e3);
        doc.normalizeDocument();
        Attr a = e3.getAttributeNodeNS(XMLNS_NAMESPACE_URI, "ns");
        if (!(a != null
                    && a.getNodeName().equals("xmlns:ns")
                    && a.getNodeValue().equals("http://www.example.org/ns2"))) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "namespaces == true, test 1");
        }

        doc = newDoc();
        e = doc.createElementNS(null, "root");
        doc.appendChild(e);
        e2 = doc.createElementNS("http://www.example.org/ns1", "ns:child1");
        e.appendChild(e2);
        e2.setAttributeNS(XMLNS_NAMESPACE_URI, "xmlns:ns", "http://www.example.org/ns1");
        e3 = doc.createElementNS("http://www.example.org/ns1", "ns:child2");
        e2.appendChild(e3);
        e2 = (Element) ((AbstractDocument) doc).renameNode(e2, "http://www.example.org/ns2", "ns:child1");
        doc.normalizeDocument();
        a = e2.getAttributeNodeNS(XMLNS_NAMESPACE_URI, "ns");
        Attr a2 = e3.getAttributeNodeNS(XMLNS_NAMESPACE_URI, "ns");
        if (!(a != null
                    && a.getNodeName().equals("xmlns:ns")
                    && a.getNodeValue().equals("http://www.example.org/ns2")
                    && a2 != null
                    && a2.getNodeName().equals("xmlns:ns")
                    && a2.getNodeValue().equals("http://www.example.org/ns1"))) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "namespaces == true, test 2");
        }

        doc = newDoc();
        e = doc.createElementNS(null, "root");
        doc.appendChild(e);
        e2 = doc.createElementNS("http://www.example.org/ns1", "child1");
        e.appendChild(e2);
        e2.setAttributeNS("http://www.example.org/ns2", "blah", "hi");
        doc.normalizeDocument();
        a = e2.getAttributeNodeNS(XMLNS_NAMESPACE_URI, "xmlns");
        a2 = e2.getAttributeNodeNS(XMLNS_NAMESPACE_URI, "NS1");
        if (!(a != null
                    && a.getNodeValue().equals("http://www.example.org/ns1")
                    && a2 != null
                    && a2.getNodeValue().equals("http://www.example.org/ns2"))) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "namespaces == true, test 3");
        }

        // namespace-declarations == false
        doc = newDoc();
        e = doc.createElementNS(null, "ex:root");
        e.setAttributeNS(XMLNS_NAMESPACE_URI, "xmlns:ex", "http://www.example.org/ns1");
        conf = doc.getDomConfig();
        conf.setParameter("namespace-declarations", Boolean.FALSE);
        doc.appendChild(e);
        doc.normalizeDocument();
        if (!(e.getAttributeNodeNS(XMLNS_NAMESPACE_URI, "ex") == null)) {
            if (report == null) {
                report = reportError("Document.normalizeDocument test failed");
            }
            report.addDescriptionEntry("DOMConfiguration parameter", "namespace-declarations == false");
        }

        if (report == null) {
            return reportSuccess();
        }
        return report;
    }
}
