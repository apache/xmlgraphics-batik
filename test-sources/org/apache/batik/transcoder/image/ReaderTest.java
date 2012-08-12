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
package org.apache.batik.transcoder.image;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.batik.transcoder.TranscoderInput;

/**
 * Test the ImageTranscoder input with a Reader.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ReaderTest extends AbstractImageTranscoderTest {

    /** The URI of the input image. */
    protected String inputURI;

    /** The URI of the reference image. */
    protected String refImageURI;

    /**
     * Constructs a new <code>ReaderTest</code>.
     *
     * @param inputURI the URI of the input image
     * @param refImageURI the URI of the reference image
     */
    public ReaderTest(String inputURI, String refImageURI) {
        this.inputURI = inputURI;
        this.refImageURI = refImageURI;
    }

    /**
     * Creates the <code>TranscoderInput</code>.
     */
    protected TranscoderInput createTranscoderInput() {
        try {
            URL url = resolveURL(inputURI);
            Reader reader = new InputStreamReader(url.openStream());
            TranscoderInput input = new TranscoderInput(reader);
            input.setURI(url.toString()); // Needed for external resources
            return input;
        } catch (IOException ex) {
            throw new IllegalArgumentException(inputURI);
        }
    }

    /**
     * Returns the reference image for this test.
     */
    protected byte [] getReferenceImageData() {
        return createBufferedImageData(resolveURL(refImageURI));
    }
}
