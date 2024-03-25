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
package org.apache.batik.test.svg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

/**
 * Test class for verifying correct handling of SVG gradient stops without an 'offset' attribute.
 * *
 * @version $Id$
 */
public class AbstractSVGGradietElementBridgeOffsetAttributeTest  extends AbstractTest  {

    public static final String ERROR_PNG_RENDERING_NOT_ACCURATE
    = "SVGRenderingAccuracyTest.error.svg.rendering.not.accurate";

    public TestReport runImpl() throws Exception{
    String svgContent = "<svg xmlns='http://www.w3.org/2000/svg' width='100' height='100'>" +
                            "<defs>" +
                            "<linearGradient id='gradient'>" +
                            "<stop stop-color='red'/>" + // No offset attribute
                            "<stop offset='100%' stop-color='blue'/>" +
                            "</linearGradient>" +
                            "</defs>" +
                            "<rect width='100%' height='100%' fill='url(#gradient)' />" +
                            "</svg>";

        // Set up the PNGTranscoder and input/output streams
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgContent.getBytes()));
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);

        // Perform the transcoding process
        transcoder.transcode(input, output);

        // Verify the output is not null, indicating transcoding occurred without exception
        if(ostream.toByteArray() == null){
            error(ERROR_PNG_RENDERING_NOT_ACCURATE);
        }

        System.out.println("Test completed successfully - SVG without explicit stop offset was handled.");
        return reportSuccess();
        }
}
