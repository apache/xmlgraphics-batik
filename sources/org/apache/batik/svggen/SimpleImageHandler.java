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

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

import org.w3c.dom.Element;

/**
 * Implements the <tt>GenericImageHandler</tt> interface and only
 * uses &lt;image&gt; elements. This class delegates to the
 * <tt>ImageHandler</tt> interface for handling the xlink:href
 * attribute on the elements it creates.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SimpleImageHandler implements GenericImageHandler, SVGSyntax, ErrorConstants {
    // duplicate the string here to remove dependencies on
    // org.apache.batik.dom.util.XLinkSupport
    static final String XLINK_NAMESPACE_URI =
        "http://www.w3.org/1999/xlink";

    /**
     * <tt>ImageHandler</tt> which handles xlink:href attribute setting
     */
    protected ImageHandler imageHandler;

    /**
     * @param imageHandler ImageHandler handling the xlink:href on the 
     *        &lt;image&gt; elements this GenericImageHandler implementation
     *        creates.
     */
    public SimpleImageHandler(ImageHandler imageHandler){
        if (imageHandler == null){
            throw new IllegalArgumentException();
        }

        this.imageHandler = imageHandler;
    }

    /**
     * This <tt>GenericImageHandler</tt> implementation does not
     * need to interact with the DOMTreeManager.
     */
    public void setDOMTreeManager(DOMTreeManager domTreeManager){
    }

    /**
     * Creates an Element which can refer to an image.
     * Note that no assumptions should be made by the caller about the
     * corresponding SVG tag.
     */
    public Element createElement(SVGGeneratorContext generatorContext) {
        // Create a DOM Element in SVG namespace to refer to an image
        Element imageElement =
            generatorContext.getDOMFactory().createElementNS
            (SVG_NAMESPACE_URI, SVG_IMAGE_TAG);

        return imageElement;
    }

    /**
     * The handler sets the xlink:href tag and returns a transform
     */
    public AffineTransform handleImage(Image image,
                                       Element imageElement,
                                       int x, int y,
                                       int width, int height,
                                       SVGGeneratorContext generatorContext) {

        int imageWidth      = image.getWidth(null);
        int imageHeight     = image.getHeight(null);
        AffineTransform af  = null;

        if(imageWidth == 0 || imageHeight == 0 ||
           width == 0 || height == 0) {

            // Forget about it
            handleEmptyImage(imageElement);

        } else {
            imageHandler.handleImage(image, imageElement, generatorContext);
            setImageAttributes(imageElement, (double) x, (double) y,
                               (double)width, (double)height,
                               generatorContext);
        }
        return null;
    }

    /**
     * The handler sets the xlink:href tag and returns a transform
     */
    public AffineTransform handleImage(RenderedImage image,
                                       Element imageElement,
                                       int x, int y,
                                       int width, int height,
                                       SVGGeneratorContext generatorContext) {

        int imageWidth      = image.getWidth();
        int imageHeight     = image.getHeight();
        AffineTransform af  = null;

        if(imageWidth == 0 || imageHeight == 0 ||
           width == 0 || height == 0) {

            // Forget about it
            handleEmptyImage(imageElement);

        } else {
            imageHandler.handleImage(image, imageElement, generatorContext);
            setImageAttributes(imageElement, (double) x, (double) y,
                               (double)width, (double)height, generatorContext);
        }
        return null;
    }

    /**
     * The handler sets the xlink:href tag and returns a transform
     */
    public AffineTransform handleImage(RenderableImage image,
                                       Element imageElement,
                                       double x, double y,
                                       double width, double height,
                                       SVGGeneratorContext generatorContext) {

        double imageWidth   = image.getWidth();
        double imageHeight  = image.getHeight();
        AffineTransform af  = null;

        if(imageWidth == 0 || imageHeight == 0 ||
           width == 0 || height == 0) {

            // Forget about it
            handleEmptyImage(imageElement);

        } else {
            imageHandler.handleImage(image, imageElement, generatorContext);
            setImageAttributes(imageElement, x, y, width, height, generatorContext);
        }
        return null;
    }

    /**
     * Sets the x/y/width/height attributes on the &lt;image&gt; 
     * element.
     */
    protected void setImageAttributes(Element imageElement,
                                      double x, 
                                      double y,
                                      double width,
                                      double height,
                                      SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null,
                                    SVG_X_ATTRIBUTE,
                                    generatorContext.doubleString(x));
        imageElement.setAttributeNS(null,
                                    SVG_Y_ATTRIBUTE,
                                    generatorContext.doubleString(y));
        imageElement.setAttributeNS(null,
                                    SVG_WIDTH_ATTRIBUTE,
                                    generatorContext.doubleString(width));
        imageElement.setAttributeNS(null,
                                    SVG_HEIGHT_ATTRIBUTE,
                                    generatorContext.doubleString(height));
        imageElement.setAttributeNS(null,
                                    SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE,
                                    SVG_NONE_VALUE);
    }
              
    protected void handleEmptyImage(Element imageElement) {
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, "");
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE, "0");
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE, "0");
    }

}
