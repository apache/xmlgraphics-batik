/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.w3c.dom.*;

import org.apache.batik.util.Base64EncoderStream;


/**
 * This subclass of {@link ImageHandlerBase64Encoder} implements
 * functionality specific to the cached version of the image
 * encoder.
 *
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 */
public class CachedImageHandlerBase64Encoder extends ImageHandlerBase64Encoder {

    /**
     * Build a <code>CachedImageHandlerBase64Encoder</code> instance.
     */
    public CachedImageHandlerBase64Encoder() {
        super();
        setImageCacher(new ImageCacher.Embedded());
    }
    
   /**
     * Creates an Element which can refer to an image.
     * Note that no assumptions should be made by the caller about the
     * corresponding SVG tag.
     */
    public Element createElement(SVGGeneratorContext generatorContext) {
        // Create a DOM Element in SVG namespace to refer to an image
        // For this cached version we return <use>
        Element imageElement =
            generatorContext.getDOMFactory().createElementNS(
                                    SVG_NAMESPACE_URI, SVG_USE_TAG);

        return imageElement;
    }

    
    /**
     * This version of handleHREF encodes the input image into a
     * PNG image whose bytes are then encoded with Base64. The
     * resulting encoded data is used to set the url on the
     * input imageElement.
     */
    protected void handleHREF(RenderedImage image, Element imageElement,
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
        // Ask for a href from the image cacher
        //
        String href = imageCacher.lookup(os,
                                         image.getWidth(),
                                         image.getHeight(),
                                         generatorContext);

        //
        // Finally, write out url
        //
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF,
                                    href);

    }

    /**
     * Determines the transformation needed to get the cached image to
     * scale & position properly. Sets x and y attributes on the element
     * accordingly.
     */
    protected AffineTransform handleTransform(Element imageElement,
                                              double x, double y,
                                              double srcWidth,
                                              double srcHeight,
                                              double dstWidth,
                                              double dstHeight) {

        // If scaling is necessary, create a transform, since "width" and "height"
        // have no effect on a <use> element referring to an <image> element.

        AffineTransform af  = null;
        double hRatio = dstWidth / srcWidth;
        double vRatio = dstHeight / srcHeight;
        double xScaled = x / hRatio;
        double yScaled = y / vRatio;

        if(hRatio != 1 || vRatio != 1) {
            af = AffineTransform.getScaleInstance(hRatio, vRatio);
        }
        imageElement.setAttributeNS(null,
                                    SVG_X_ATTRIBUTE,
                                    AbstractSVGConverter.doubleString(xScaled));
        imageElement.setAttributeNS(null,
                                    SVG_Y_ATTRIBUTE,
                                    AbstractSVGConverter.doubleString(yScaled));
            
        return af;
    }
}

