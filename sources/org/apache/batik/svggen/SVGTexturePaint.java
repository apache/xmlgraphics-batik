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

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a TexturePaint object into an
 * SVG pattern element
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGTexturePaint extends AbstractSVGConverter {
    /**
     * @param generatorContext used to build Elements
     */
    public SVGTexturePaint(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc) {
        return toSVG((TexturePaint)gc.getPaint());
    }

    /**
     * @param texture the TexturePaint to be converted
     * @return a descriptor whose paint value references
     *         a pattern. The definition of the
     *         pattern in put in the patternDefsMap
     */
    public SVGPaintDescriptor toSVG(TexturePaint texture) {
        // Reuse definition if pattern has already been converted
        SVGPaintDescriptor patternDesc = (SVGPaintDescriptor)descMap.get(texture);
        Document domFactory = generatorContext.domFactory;

        if (patternDesc == null) {
            Rectangle2D anchorRect = texture.getAnchorRect();
            Element patternDef = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                            SVG_PATTERN_TAG);
            patternDef.setAttributeNS(null, SVG_PATTERN_UNITS_ATTRIBUTE,
                                      SVG_USER_SPACE_ON_USE_VALUE);

            //
            // First, set the pattern anchor
            //
            patternDef.setAttributeNS(null, SVG_X_ATTRIBUTE,
                                    doubleString(anchorRect.getX()));
            patternDef.setAttributeNS(null, SVG_Y_ATTRIBUTE,
                                    doubleString(anchorRect.getY()));
            patternDef.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE,
                                    doubleString(anchorRect.getWidth()));
            patternDef.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE,
                                    doubleString(anchorRect.getHeight()));

            //
            // Now, add an image element for the image.
            //
            BufferedImage textureImage = (BufferedImage)texture.getImage();
            Element imageElement =
                domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_IMAGE_TAG);

            //
            // Rescale the image to fit the anchor rectangle
            //
            if (textureImage.getWidth() > 0 &&
                textureImage.getHeight() > 0){

                // Rescale only if necessary
                if(textureImage.getWidth() != anchorRect.getWidth() ||
                   textureImage.getHeight() != anchorRect.getHeight()){

                    // Rescale only if anchor area is not a point or a line
                    if(anchorRect.getWidth() > 0 &&
                       anchorRect.getHeight() > 0){
                        double scaleX =
                            anchorRect.getWidth()/textureImage.getWidth();
                        double scaleY =
                            anchorRect.getHeight()/textureImage.getHeight();
                        BufferedImage newImage
                            = new BufferedImage((int)(scaleX*
                                                      textureImage.getWidth()),
                                                (int)(scaleY*
                                                      textureImage.getHeight()),
                                                BufferedImage.TYPE_INT_ARGB);

                        Graphics2D g = newImage.createGraphics();
                        g.scale(scaleX, scaleY);
                        g.drawImage(textureImage, 0, 0, null);
                        g.dispose();

                        textureImage = newImage;
                    }
                }
            }

            // generatorContext.imageHandler.
            // handleImage((RenderedImage)textureImage, imageElement,
            // generatorContext);

            Element patternContent 
                = generatorContext.genericImageHandler.createElement
                (generatorContext);

            generatorContext.genericImageHandler.handleImage
                ((RenderedImage)textureImage,
                 patternContent,
                 0, 
                 0,
                 textureImage.getWidth(),
                 textureImage.getHeight(),
                 generatorContext);

            patternDef.appendChild(patternContent);

            patternDef.setAttributeNS(null, ATTR_ID,
                                      generatorContext.idGenerator.
                                      generateID(ID_PREFIX_PATTERN));

            StringBuffer patternAttrBuf = new StringBuffer(URL_PREFIX);
            patternAttrBuf.append(SIGN_POUND);
            patternAttrBuf.append(patternDef.getAttributeNS(null, ATTR_ID));
            patternAttrBuf.append(URL_SUFFIX);

            patternDesc = new SVGPaintDescriptor(patternAttrBuf.toString(),
                                                 SVG_OPAQUE_VALUE,
                                                 patternDef);

            descMap.put(texture, patternDesc);
            defSet.add(patternDef);
        }

        return patternDesc;
    }
}
