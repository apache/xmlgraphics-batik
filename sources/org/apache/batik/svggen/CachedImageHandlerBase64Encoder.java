/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.*;

import org.apache.batik.util.Base64EncoderStream;
import org.apache.batik.ext.awt.image.codec.ImageEncoder;
import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;


/**
 * This subclass of {@link ImageHandlerBase64Encoder} implements
 * functionality specific to the cached version of the image
 * encoder.
 *
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 */
public class CachedImageHandlerBase64Encoder extends DefaultCachedImageHandler {
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


    public String getRefPrefix(){
        return "";
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

        if(hRatio != 1 || vRatio != 1) {
            af = AffineTransform.getScaleInstance(hRatio, vRatio);
        }
        imageElement.setAttributeNS(null,
                                    SVG_X_ATTRIBUTE,
                                    AbstractSVGConverter.doubleString(x));
        imageElement.setAttributeNS(null,
                                    SVG_Y_ATTRIBUTE,
                                    AbstractSVGConverter.doubleString(y));
            
        return af;
    }

    /**
     * Uses PNG encoding.
     */
    public void encodeImage(BufferedImage buf, OutputStream os)
            throws IOException {
        Base64EncoderStream b64Encoder = new Base64EncoderStream(os);
        ImageEncoder encoder = new PNGImageEncoder(b64Encoder, null);
        encoder.encode(buf);
        b64Encoder.close();
    }

    public int getBufferedImageType(){
        return BufferedImage.TYPE_INT_ARGB;
    }
}

