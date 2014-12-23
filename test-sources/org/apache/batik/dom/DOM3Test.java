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

import org.apache.batik.test.AbstractTest;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMDocument;

import org.w3c.dom.Document;

/**
 *
 * @version $Id$
 */
public class DOM3Test extends AbstractTest {
    static String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";
    static String EX_NAMESPACE_URI = "http://www.example.org/";
    static String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";
    static String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    static String XML_EVENTS_NAMESPACE_URI = "http://www.w3.org/2001/xml-events";

    protected Document newDoc() {
        return new GenericDocument(null, GenericDOMImplementation.getDOMImplementation());
    }

    protected Document newSVGDoc() {
        Document doc = new SVGOMDocument(null, SVGDOMImplementation.getDOMImplementation());
        doc.appendChild(doc.createElementNS(SVG_NAMESPACE_URI, "svg"));
        return doc;
    }
}
