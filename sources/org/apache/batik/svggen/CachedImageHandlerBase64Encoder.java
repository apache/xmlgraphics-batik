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

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.ext.awt.image.codec.ImageEncoder;
import org.apache.batik.ext.awt.image.codec.PNGImageEncoder;
import org.apache.batik.util.Base64EncoderStream;
import org.w3c.dom.Element;


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
                                              double dstHeight,
                                              SVGGeneratorContext generatorContext) {

        // If scaling is necessary, create a transform, since "width" and "height"
        // have no effect on a <use> element referring to an <image> element.

        AffineTransform af  = new AffineTransform();
        double hRatio = dstWidth / srcWidth;
        double vRatio = dstHeight / srcHeight;

        af.translate(x,y);

        if(hRatio != 1 || vRatio != 1) {
            af.scale(hRatio, vRatio);
        } 

        if (!af.isIdentity()){
            return af;
        } else {
            return null;
        }
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

