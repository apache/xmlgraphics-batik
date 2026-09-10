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

import java.io.File;
import java.io.InputStream;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SVGUseElementBridgeTestCase implements ErrorConstants {

    private static final String CIRCULAR_SVG = "src/test/resources/circular.svg";

    @Test
    public void testCircularUseReferenceIsDetected() throws Exception {
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        InputStream is = SVGUseElementBridgeTestCase.class.getResourceAsStream(CIRCULAR_SVG);
        Document doc = f.createDocument(CIRCULAR_SVG, is);

        GVTBuilder builder = new GVTBuilder();
        BridgeContext ctx = new BridgeContext(new UserAgentAdapter());

        BridgeException e = assertThrows("BridgeException reporting a circular dependency", BridgeException.class,
                () -> {
                    GraphicsNode gn = builder.build(ctx, doc);
                    gn.getBounds();
                });

        assertEquals("Must use the correct xlink reference", ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES, e.getCode());
    }
}
