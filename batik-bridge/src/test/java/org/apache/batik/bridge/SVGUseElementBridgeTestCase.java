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

import java.awt.geom.Rectangle2D;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SVGUseElementBridgeTestCase implements ErrorConstants {

    private static final String CIRCULAR_SVG = "/circular.svg";
    private static final String REPEATED_USE_SVG = "/repeatedUse.svg";
    private static final String EXTERNAL_USE_SVG = "/externalUse.svg";

    @Test
    public void testCircularUseReferenceIsDetected() {
        assertCircularReferenceDetected(false);
    }

    @Test
    public void testCircularUseReferenceIsDetectedInDynamicContext() {
        assertCircularReferenceDetected(true);
    }

    @Test
    public void testRepeatedUseOfSameElementIsNotCircular() throws Exception {
        GraphicsNode root = build(REPEATED_USE_SVG, false);
        assertEquals("Both 'use' elements must render the referenced rect",
                new Rectangle2D.Double(0, 0, 30, 10), root.getGeometryBounds());
    }

    @Test
    public void testSameFragmentInExternalDocumentIsNotCircular() throws Exception {
        GraphicsNode root = build(EXTERNAL_USE_SVG, false);
        assertEquals("The rect from the external document must be rendered",
                new Rectangle2D.Double(0, 0, 10, 10), root.getGeometryBounds());
    }

    private static void assertCircularReferenceDetected(boolean dynamic) {
        BridgeException e = assertThrows(
                "Circular references must be detected to avoid possible denial of service attacks",
                BridgeException.class, () -> build(CIRCULAR_SVG, dynamic));
        assertEquals("Must use the correct xlink reference", ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES, e.getCode());
    }

    private static GraphicsNode build(String resource, boolean dynamic) throws Exception {
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        String uri = SVGUseElementBridgeTestCase.class.getResource(resource).toString();
        Document doc = f.createDocument(uri);

        BridgeContext ctx = new BridgeContext(new UserAgentAdapter());
        ctx.setDynamic(dynamic);
        return new GVTBuilder().build(ctx, doc);
    }
}
