/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;

import java.util.Map;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a TexturePaint object into an
 * SVG pattern element
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGTexturePaint extends AbstractSVGConverter{
    public static final String ERROR_IMAGE_HANDLER_NULL =
        "imageHandler should not be null";

    /**
     * Used to populate image Element attributes
     */
    private ImageHandler imageHandler;

    /**
     * @param domFactory used to build Elements
     * @param imageHandler used to populate image Elements
     */
    public SVGTexturePaint(Document domFactory,
                           ImageHandler imageHandler){
        super(domFactory);

        if(imageHandler == null)
            throw new IllegalArgumentException(ERROR_IMAGE_HANDLER_NULL);

        this.imageHandler = imageHandler;
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
    public SVGDescriptor toSVG(GraphicContext gc){
        return toSVG((TexturePaint)gc.getPaint());
    }

    /**
     * @param texture the TexturePaint to be converted
     * @return a descriptor whose paint value references
     *         a pattern. The definition of the
     *         pattern in put in the patternDefsMap
     */
    public SVGPaintDescriptor toSVG(TexturePaint texture){
        // Reuse definition if pattern has already been converted
        SVGPaintDescriptor patternDesc = (SVGPaintDescriptor)descMap.get(texture);

        if(patternDesc == null){
            Rectangle2D anchorRect = texture.getAnchorRect();
            Element patternDef = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                            SVG_PATTERN_TAG);
            patternDef.setAttributeNS(null, ATTR_PATTERN_UNITS,
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
            Element imageElement = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_IMAGE_TAG);

            //
            // Rescale the image to fit the anchor rectangle
            //
            if(textureImage.getWidth() > 0 &&
               textureImage.getHeight() > 0){

                // Rescale only if necessary
                if(textureImage.getWidth() != anchorRect.getWidth() ||
                   textureImage.getHeight() != anchorRect.getHeight()){

                    // Rescale only if anchor area is not a point or a line
                    if(anchorRect.getWidth() > 0 &&
                       anchorRect.getHeight() > 0){
                        double scaleX = anchorRect.getWidth()/textureImage.getWidth();
                        double scaleY = anchorRect.getHeight()/textureImage.getHeight();
                        BufferedImage newImage
                            = new BufferedImage((int)(scaleX*textureImage.getWidth()),
                                                (int)(scaleY*textureImage.getHeight()),
                                                BufferedImage.TYPE_INT_ARGB);

                        Graphics2D g = newImage.createGraphics();
                        g.scale(scaleX, scaleY);
                        g.drawImage(textureImage, 0, 0, null);
                        g.dispose();

                        textureImage = newImage;
                    }
                }
            }

            imageHandler.handleImage((RenderedImage)textureImage, imageElement);
            patternDef.appendChild(imageElement);

            patternDef.setAttributeNS(null, ATTR_ID,
                                    SVGIDGenerator.generateID(ID_PREFIX_PATTERN));

            StringBuffer patternAttrBuf = new StringBuffer(URL_PREFIX);
            patternAttrBuf.append(SIGN_POUND);
            patternAttrBuf.append(patternDef.getAttributeNS(null, ATTR_ID));
            patternAttrBuf.append(URL_SUFFIX);

            patternDesc = new SVGPaintDescriptor(patternAttrBuf.toString(),
                                                 VALUE_OPAQUE,
                                                 patternDef);

            descMap.put(texture, patternDesc);
            defSet.add(patternDef);
        }

        return patternDesc;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{
        Document domFactory = TestUtil.getDocumentPrototype();

        BufferedImage buf = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        TexturePaint paint = new TexturePaint(buf, new Rectangle(0, 0, 200, 200));

        SVGTexturePaint converter = new SVGTexturePaint(domFactory, new DefaultImageHandler());
        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);

        SVGPaintDescriptor patternDesc = converter.toSVG(paint);
        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element patternDef = (Element)iter.next();
            defs.appendChild(patternDef);
        }

        Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
        rect.setAttributeNS(null, SVG_FILL_ATTRIBUTE, patternDesc.getPaintValue());
        rect.setAttributeNS(null, SVG_FILL_OPACITY_ATTRIBUTE, patternDesc.getOpacityValue());

        group.appendChild(defs);
        group.appendChild(rect);

        TestUtil.trace(group, System.out);
    }
}
