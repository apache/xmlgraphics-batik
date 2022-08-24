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
package org.apache.batik.ext.awt.image.codec.png;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;

/**
 * ImageWriter implementation that uses Batik's PNG codec to
 * write PNG files.
 *
 * @version $Id$
 */
// NOTE: the "codec" package is deprecated, there entries are kept here for compatibility with older JVM versions
// (uses "sun.image", which is only supported in Sun Java implementations and was retired in JDK 7)
//@ServiceProvider(value = ImageWriter.class, attribute = {
//		"mimeType:String='" + PNGImageWriter.MIMETYPE_IMAGE_PNG + "'" ,
//		"codec:String='" + PNGImageWriter.CODEC + "'" })
public class PNGImageWriter implements ImageWriter {

    public static final String CODEC="Batik";

	public static final String MIMETYPE_IMAGE_PNG = "image/png";

	/**
     * @see ImageWriter#writeImage(java.awt.image.RenderedImage, java.io.OutputStream)
     */
    public void writeImage(RenderedImage image, OutputStream out)
            throws IOException {
        writeImage(image, out, null);
    }

    /**
     * @see ImageWriter#writeImage(java.awt.image.RenderedImage, java.io.OutputStream, org.apache.batik.ext.awt.image.spi.ImageWriterParams)
     */
    public void writeImage(RenderedImage image, OutputStream out,
            ImageWriterParams params) throws IOException {
        PNGImageEncoder encoder = new PNGImageEncoder(out, null);
        encoder.encode(image);
    }

    /**
     * @see ImageWriter#getMIMEType()
     */
    public String getMIMEType() {
        return MIMETYPE_IMAGE_PNG;
    }
}
