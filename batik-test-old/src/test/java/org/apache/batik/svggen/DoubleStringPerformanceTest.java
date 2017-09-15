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
package org.apache.batik.svggen;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.test.PerformanceTest;

/**
 * This test checks that there is no performance degradation in the 
 * doubleString utility method.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DoubleStringPerformanceTest extends PerformanceTest {
    static double[] testValues = { 0, 
                                   0.00000000001,
                                   0.2e-14,
                                   0.45,
                                   123412341234e14,
                                   987654321e-12,
                                   234143,
                                   2.3333444000044e56,
                                   45.3456 };
    public void runOp() { 
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = impl.createDocument(svgNS, "svg", null);
        final SVGGeneratorContext gc = new SVGGeneratorContext(doc);

        int maxLength = 0;
        for (int i=0; i<1000; i++) {
            for (double testValue : testValues) {
                maxLength = Math.max((gc.doubleString(testValue)).length(), maxLength);
            }
        }
    }
}
