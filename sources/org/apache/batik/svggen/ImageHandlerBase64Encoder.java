/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.svggen;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.ext.awt.image.codec.ImageEncoder;
import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;
import org.apache.batik.util.Base64EncoderStream;
import org.w3c.dom.Element;

/**
 * This implementation of ImageHandler encodes the input image as
 * a PNG image first, then encodes the PNG image using Base64
 * encoding and uses the result to encoder the image url using
 * the data protocol.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 * @see             org.apache.batik.svggen.ImageHandler
 */
public class ImageHandlerBase64Encoder extends DefaultImageHandler {
    /**
     * Build an <code>ImageHandlerBase64Encoder</code> instance.
     */
    public ImageHandlerBase64Encoder() {
        super();
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleHREF(Image image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        if (image == null)
            throw new SVGGraphics2DRuntimeException(ERR_IMAGE_NULL);

        int width = image.getWidth(null);
        int height = image.getHeight(null);

        if (width==0 || height==0) {
            handleEmptyImage(imageElement);
        } else {
            if (image instanceof RenderedImage) {
                handleHREF((RenderedImage)image, imageElement,
                           generatorContext);
            } else {
                BufferedImage buf =
                    new BufferedImage(width, height,
                                      BufferedImage.TYPE_INT_ARGB);

                Graphics2D g = buf.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                handleHREF((RenderedImage)buf, imageElement,
                           generatorContext);
            }
        }
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleHREF(RenderableImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        if (image == null){
            throw new SVGGraphics2DRuntimeException(ERR_IMAGE_NULL);
        }

        RenderedImage r = image.createDefaultRendering();
        if (r == null) {
            handleEmptyImage(imageElement);
        } else {
            handleHREF(r, imageElement, generatorContext);
        }
    }

    protected void handleEmptyImage(Element imageElement) {
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, DATA_PROTOCOL_PNG_PREFIX);
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE, "0");
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE, "0");
    }

    /**
     * This version of handleHREF encodes the input image into a
     * PNG image whose bytes are then encoded with Base64. The
     * resulting encoded data is used to set the url on the
     * input imageElement, using the data: protocol.
     */
    public void handleHREF(RenderedImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {

        //
        // Setup Base64Encoder stream to byte array.
        //
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Base64EncoderStream b64Encoder = new Base64EncoderStream(os);
        try {
            //
            // Now, encode the input image to the base 64 stream.
            //
            encodeImage(image, b64Encoder);

            // Close the b64 encoder stream (terminates the b64 streams).
            b64Encoder.close();
        } catch (IOException e) {
            // Should not happen because we are doing in-memory processing
            throw new SVGGraphics2DIOException(ERR_UNEXPECTED, e);
        }

        //
        // Finally, write out url
        //
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF,
                                    DATA_PROTOCOL_PNG_PREFIX +
                                    os.toString());

    }

    public void encodeImage(RenderedImage buf, OutputStream os)
        throws SVGGraphics2DIOException {
        try{
            ImageEncoder encoder = new PNGImageEncoder(os, null);
            encoder.encode(buf);
        } catch(IOException e) {
            // We are doing in-memory processing. This should not happen.
            throw new SVGGraphics2DIOException(ERR_UNEXPECTED);
        }
    }

    /**
     * This method creates a BufferedImage with an alpha channel, as this is
     * supported by Base64.
     */
    public BufferedImage buildBufferedImage(Dimension size) {
        return new BufferedImage(size.width, size.height,
                                 BufferedImage.TYPE_INT_ARGB);
    }
}
