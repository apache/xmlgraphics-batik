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
package org.apache.batik.swing.svg;

import org.apache.xmlgraphics.util.UnitConv;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SVGUserAgentAdapterTestCase {

    @Test
    public void testMediumFontResolution_72() {
        checkGetMediumFontSize(72f, 9f);
    }

    @Test
    public void testMediumFontResolution_96() {
        checkGetMediumFontSize(96f, 12f);
    }

    @Test
    public void testPixelMM_72() { checkGetPixelUnitToMillimeter(72f); }

    @Test
    public void testPixelMM_96() {
        checkGetPixelUnitToMillimeter(96f);
    }

    private void checkGetMediumFontSize(float sourceRes, float expectedSize) {
        SVGUserAgentAdapter adapter = new SVGUserAgentAdapter();
        adapter.setSourceResolution(sourceRes);

        // Size must be calculated based on the dpi settings
        assertEquals(expectedSize, adapter.getMediumFontSize(), 0.01);
    }

    private void checkGetPixelUnitToMillimeter(float sourceRes) {
        SVGUserAgentAdapter adapter = new SVGUserAgentAdapter();
        adapter.setSourceResolution(sourceRes);

        // Pixel unit to mm must be calculated using the resolution set in the conf
        // instead of assuming what the resolution is
        assertEquals(UnitConv.IN2MM / sourceRes, adapter.getPixelUnitToMillimeter(), 0.01);
    }
}
