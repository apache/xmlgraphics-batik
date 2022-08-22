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
package org.apache.batik.bridge;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMImageElement;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGImageElementBridgeTestCase {
    @Test
    public void testNoLoadExternalResourceSecurity() {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = impl.createDocument(svgNS, "svg", null);
        SVGOMImageElement imageElement = new SVGOMImageElement("", (AbstractDocument) doc);
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink","href", "http://localhost/x");
        UserAgentAdapter userAgentAdapter = new UserAgentAdapter() {
            public ExternalResourceSecurity getExternalResourceSecurity(ParsedURL resourceURL, ParsedURL docURL) {
                return new NoLoadExternalResourceSecurity();
            }
        };
        SVGImageElementBridge imageElementBridge = new SVGImageElementBridge() {
            protected GraphicsNode createImageGraphicsNode(BridgeContext ctx, Element e, ParsedURL purl) {
                return null;
            }
        };
        String msg = "";
        try {
            imageElementBridge.buildImageGraphicsNode(new BridgeContext(userAgentAdapter), imageElement);
        } catch (BridgeException e) {
            msg = e.getMessage();
        }
        Assert.assertEquals(msg, "The security settings do not allow any external resources to be referenced from the document");
    }
}
