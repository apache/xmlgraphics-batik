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
package org.apache.batik.util;

import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class Base64TestCases {

    @Test
    public void testB64_1() throws Exception {
        performTest("B64.1", "ENCODE", "zeroByte", "zeroByte.64");
    }

    @Test
    public void testB64_2() throws Exception {
        performTest("B64.2", "DECODE", "zeroByte.64", "zeroByte");
    }

    @Test
    public void testB64_3() throws Exception {
        performTest("B64.3", "ROUND", "zeroByte", null);
    }

    @Test
    public void testB64_4() throws Exception {
        performTest("B64.4", "ENCODE", "oneByte", "oneByte.64");
    }

    @Test
    public void testB64_5() throws Exception {
        performTest("B64.5", "DECODE", "oneByte.64", "oneByte");
    }

    @Test
    public void testB64_6() throws Exception {
        performTest("B64.6", "ROUND", "oneByte", null);
    }

    @Test
    public void testB64_7() throws Exception {
        performTest("B64.4", "ENCODE", "twoByte", "twoByte.64");
    }

    @Test
    public void testB64_8() throws Exception {
        performTest("B64.4", "DECODE", "twoByte.64", "twoByte");
    }

    @Test
    public void testB64_9() throws Exception {
        performTest("B64.9", "ROUND", "twoByte", null);
    }

    @Test
    public void testB64_10() throws Exception {
        performTest("B64.10", "ENCODE", "threeByte", "threeByte.64");
    }

    @Test
    public void testB64_11() throws Exception {
        performTest("B64.11", "DECODE", "threeByte.64", "threeByte");
    }

    @Test
    public void testB64_12() throws Exception {
        performTest("B64.12", "ROUND", "threeByte", null);
    }

    @Test
    public void testB64_13() throws Exception {
        performTest("B64.13", "ENCODE", "fourByte", "fourByte.64");
    }

    @Test
    public void testB64_14() throws Exception {
        performTest("B64.14", "DECODE", "fourByte.64", "fourByte");
    }

    @Test
    public void testB64_15() throws Exception {
        performTest("B64.15", "ROUND", "fourByte", null);
    }

    @Test
    public void testB64_16() throws Exception {
        performTest("B64.16", "ENCODE", "tenByte", "tenByte.64");
    }

    @Test
    public void testB64_17() throws Exception {
        performTest("B64.17", "DECODE", "tenByte.64", "tenByte");
    }

    @Test
    public void testB64_18() throws Exception {
        performTest("B64.18", "ROUND", "tenByte", null);
    }

    @Test
    public void testB64_19() throws Exception {
        performTest("B64.19", "ENCODE", "small", "small.64");
    }

    @Test
    public void testB64_20() throws Exception {
        performTest("B64.20", "DECODE", "small.64", "small");
    }

    @Test
    public void testB64_21() throws Exception {
        performTest("B64.21", "ROUND", "small", null);
    }

    @Test
    public void testB64_22() throws Exception {
        performTest("B64.22", "ENCODE", "medium", "medium.64");
    }

    @Test
    public void testB64_23() throws Exception {
        performTest("B64.23", "DECODE", "medium.64", "medium");
    }

    @Test
    public void testB64_24() throws Exception {
        performTest("B64.24", "DECODE", "medium.pc.64", "medium");
    }

    @Test
    public void testB64_25() throws Exception {
        performTest("B64.25", "ROUND", "medium", null);
    }

    @Test
    public void testB64_26() throws Exception {
        performTest("B64.26", "ROUND", "large", null);
    }

    private void performTest(String id, String action, String in, String ref) {
        performTestCont(id, action, in != null ? getResource(in) : null, ref != null ? getResource(ref) : null);
    }

    private void performTestCont(String id, String action, URL in, URL ref) {
        Base64Test t = new Base64Test(action, in, ref);
        t.setId(id);
        assertTrue(t.run().hasPassed());
    }

    private URL getResource(String name) {
        return getClass().getResource(name);
    }

}
