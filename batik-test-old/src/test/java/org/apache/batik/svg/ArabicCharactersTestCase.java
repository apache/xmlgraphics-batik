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
package org.apache.batik.svg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.junit.Assert;
import org.junit.Test;

public class ArabicCharactersTestCase  {
    @Test
    public void testDoubleCharRemappings() throws Exception {
        String text = "\u0634\u0627\u0631\u0639 \u0637\u064E\u0648\u0650\u064A \u0623\u0645 \u0627\u0644\u0638\u0651\u0650\u0628\u0627";
        String svgContent = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                "width=\"700\" height=\"500\" viewBox=\"0 0 700 500\">\n" +
                "\t<g font-family=\"dialog\" font-size=\"35\" text-anchor=\"start\">\n" +
                "\t\t<text fill=\"DarkRed\" x=\"5%\" y=\"10%\"\n" +
                "\t\t> Arabic Shadda : " + text + "</text>\n" +
                "\t</g>\n" +
                "</svg>";
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgContent.getBytes()));
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);
        transcoder.transcode(input, output);
        Assert.assertNotNull(ostream.toByteArray());
    }
}
