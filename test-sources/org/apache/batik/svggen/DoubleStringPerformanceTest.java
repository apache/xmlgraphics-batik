/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import org.apache.batik.test.PerformanceTest;

import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;

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
            for (int j=0; j<testValues.length; j++) {
                maxLength = Math.max((gc.doubleString(testValues[j])).length(), maxLength);
            }
        }
    }
}
