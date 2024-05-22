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

import org.apache.xmlgraphics.util.UnitConv;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserAgentAdapterTestCase {

    @Test
    public void testEqualResolution_72() {
        checkGetMediumFontSize(72f, 72f, 9f);
    }

    @Test
    public void testEqualResolution_96() {
        checkGetMediumFontSize(96f, 96f, 9f);
    }

    @Test
    public void testDiffResolution_72_96() {
        checkGetMediumFontSize(72f, 96f, 6.74f);
    }

    @Test
    public void testDiffResolution_96_72() {
        checkGetMediumFontSize(96f, 72f, 12f);
    }

    @Test
    public void testPixelMM_72() {
        checkGetPixelUnitToMillimeter(72f, 72f);
    }

    @Test
    public void testPixelMM_96() { checkGetPixelUnitToMillimeter(96f, 96f); }

    @Test
    public void testPixelMM_72_96() { checkGetPixelUnitToMillimeter(72f, 96f); }

    @Test
    public void testPixelMM_96_72() {
        checkGetPixelUnitToMillimeter(96f, 72f);
    }

    private void checkGetMediumFontSize(float sourceRes, float targetRes, float expectedSize) {
        UserAgentAdapter adapter = new UserAgentAdapter();
        adapter.setSourceResolution(sourceRes);
        adapter.setTargetResolution(targetRes);

        // Size must be calculated based on the dpi settings
        assertEquals(expectedSize, adapter.getMediumFontSize(), 0.01);
    }

    private void checkGetPixelUnitToMillimeter(float sourceRes, float targetRes) {
        UserAgentAdapter adapter = new UserAgentAdapter();
        adapter.setSourceResolution(sourceRes);
        adapter.setTargetResolution(targetRes);

        // Pixel unit to mm must be calculated using the resolution set in the conf
        // instead of assuming what the resolution is
        assertEquals(UnitConv.IN2MM / sourceRes, adapter.getPixelUnitToMillimeter(), 0.01);
    }
}
