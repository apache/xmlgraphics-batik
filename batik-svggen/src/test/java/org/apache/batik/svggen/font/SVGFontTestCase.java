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
package org.apache.batik.svggen.font;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SVGFontTestCase {
    @Test
    public void testWriteFontAsSVGFragment() throws Exception {
        Font font = new Font();
        font.read("samples/tests/resources/ttf/glb12.ttf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SVGFont.writeFontAsSVGFragment(new PrintStream(bos), font, "abc&", 0,0, false, false);
        Assert.assertEquals("<font id=\"abc&amp;\" horiz-adv-x=\"1024\" ><font-face\n" +
                "    font-family=\"Gladiator\"\n" +
                "    units-per-em=\"2048\"\n" +
                "    panose-1=\"2 0 8 3 0 0 0 0 0 0\"\n" +
                "    ascent=\"1567\"\n" +
                "    descent=\"-481\"\n" +
                "    alphabetic=\"0\" />\n" +
                "<missing-glyph horiz-adv-x=\"886\" d=\"M68 0V1365H750V0H68ZM136 68H682V1297H136V68Z\" />\n" +
                "</font>\n", bos.toString().replace("\r", ""));
    }
}
