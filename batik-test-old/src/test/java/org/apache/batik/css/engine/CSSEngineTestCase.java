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
package org.apache.batik.css.engine;

import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CSSEngineTestCase {
    @Test
    public void testParseErrorWithNoDataUri() throws Exception {
        String svgContent = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 1337 528\" width=\"1337\" height=\"528\">\n" +
                "  <text dx=\"0\" dy=\"0\" font-family=\"Allianz_Neo\" font-size=\"25pt\" font-weight=\"bold\" " +
                "transform=\"translate(622.771987 374.659548)\" stroke-width=\"0\">\n" +
                "    <tspan y=\"0\" font-weight=\"bold\" stroke-width=\"0\">H5005912</tspan>\n" +
                "  </text>\n" +
                "  <style>@font-face {font-family: 'Allianz_Neo';font-style: Allianz_Neo;font-weight: bold;src: " +
                "url(data:font/ttf;charset=utf-8;base64,xx) format('truetype');}</style>\n" +
                "</svg>";
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgContent.getBytes()));
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);
        final String[] ex = {""};
        transcoder.setErrorHandler(new ErrorHandler() {
            public void error(TranscoderException e) {
                ex[0] = e.getMessage();
            }
            public void fatalError(TranscoderException ex) {
            }
            public void warning(TranscoderException ex) {
            }
        });
        try {
            transcoder.transcode(input, output);
        } catch (RuntimeException e) {
            //ignore
        }
        Assert.assertEquals(ex[0], "null\n" +
                "Enclosed Exception:\n" +
                "<unknown>:\n" +
                "The following stylesheet represents an invalid \n" +
                "CSS document.\n" +
                "@font-face {font-family: 'Allianz_Neo';font-style: Allianz_Neo;font-weight: " +
                "bold;src: url(data:font/ttf;charset=utf-8;base64,xx) format('truetype');}\n" +
                "Original message:\n" +
                "The \"Allianz_Neo\" identifier is not a valid value for the \"font-style\" property. ");
    }
}
