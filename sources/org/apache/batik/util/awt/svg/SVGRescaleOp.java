/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.image.*;
import java.awt.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a RescaleOp object into
 * an SVG filter descriptor. The SVG filter corresponding
 * to a RescaleOp is an feComponentTransfer, with a type
 * set to 'linear', the slopes equal to the RescapeOp
 * scaleFactors and the intercept equal to the RescapeOp
 * offsets.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.util.awt.svg.SVGBufferedImageOp
 */
public class SVGRescaleOp extends AbstractSVGFilterConverter{
    public static final String ERROR_SCALE_FACTORS_AND_OFFSETS_MISMATCH = "RescapeOp offsets and scaleFactor array lenght do not match";
    public static final String ERROR_ILLEGAL_BUFFERED_IMAGE_RESCALE_OP = "BufferedImage RescaleOp should have 1, 3 or 4 scale factors";

    /**
     * @param domFactory used to build Elements
     */
    public SVGRescaleOp(Document domFactory){
        super(domFactory);
    }

    /**
     * Converts a Java 2D API BufferedImageOp into
     * a set of attribute/value pairs and related definitions
     *
     * @param op BufferedImageOp filter to be converted
     * @param filterRect Rectangle, in device space, that defines the area
     *        to which filtering applies. May be null, meaning that the
     *        area is undefined.
     * @return descriptor of the attributes required to represent
     *         the input filter
     * @see org.apache.batik.util.awt.svg.SVGFilterDescriptor
     */
    public SVGFilterDescriptor toSVG(BufferedImageOp filter,
                                     Rectangle filterRect){
        if(filter instanceof RescaleOp)
            return toSVG((RescaleOp)filter);
        else
            return null;
    }

    /**
     * @param rescaleOp the RescaleOp to be converted
     * @return a description of the SVG filter corresponding to
     *         rescaleOp. The definition of the feComponentTransfer
     *         filter in put in feComponentTransferDefSet
     */
    public SVGFilterDescriptor toSVG(RescaleOp rescaleOp){
        // Reuse definition if rescaleOp has already been converted
        SVGFilterDescriptor filterDesc = (SVGFilterDescriptor)descMap.get(rescaleOp);

        if(filterDesc == null){
            //
            // First time filter is converted: create its corresponding
            // SVG filter
            //
            Element filterDef = domFactory.createElement(TAG_FILTER);
            Element feComponentTransferDef = domFactory.createElement(TAG_FE_COMPONENT_TRANSFER);

            // Append transfer function for each component, setting
            // the attributes corresponding to the scale and offset.
            // Because we are using a RescaleOp as a BufferedImageOp,
            // the scaleFactors must be either:
            // + 1, in which case the same scale is applied to the
            //   Red, Green and Blue components,
            // + 3, in which case the scale factors apply to the
            //   Red, Green and Blue components
            // + 4, in which case the scale factors apply to the
            //   Red, Green, Blue and Alpha components
            float offsets[] = rescaleOp.getOffsets(null);
            float scaleFactors[] = rescaleOp.getScaleFactors(null);
            if(offsets.length != scaleFactors.length)
                throw new IllegalArgumentException(ERROR_SCALE_FACTORS_AND_OFFSETS_MISMATCH);

            if(offsets.length != 1 &&
               offsets.length != 3 &&
               offsets.length != 4)
                throw new IllegalArgumentException(ERROR_ILLEGAL_BUFFERED_IMAGE_RESCALE_OP);

            Element feFuncR = domFactory.createElement(TAG_FE_FUNC_R);
            Element feFuncG = domFactory.createElement(TAG_FE_FUNC_G);
            Element feFuncB = domFactory.createElement(TAG_FE_FUNC_B);
            Element feFuncA = null;
            String type = VALUE_TYPE_LINEAR;

            if(offsets.length == 1){
                String slope = doubleString(scaleFactors[0]);
                String intercept = doubleString(offsets[0]);
                feFuncR.setAttribute(ATTR_TYPE, type);
                feFuncG.setAttribute(ATTR_TYPE, type);
                feFuncB.setAttribute(ATTR_TYPE, type);
                feFuncR.setAttribute(ATTR_SLOPE, slope);
                feFuncG.setAttribute(ATTR_SLOPE, slope);
                feFuncB.setAttribute(ATTR_SLOPE, slope);
                feFuncR.setAttribute(ATTR_INTERCEPT, intercept);
                feFuncG.setAttribute(ATTR_INTERCEPT, intercept);
                feFuncB.setAttribute(ATTR_INTERCEPT, intercept);
            }
            else if(offsets.length >= 3){
                feFuncR.setAttribute(ATTR_TYPE, type);
                feFuncG.setAttribute(ATTR_TYPE, type);
                feFuncB.setAttribute(ATTR_TYPE, type);
                feFuncR.setAttribute(ATTR_SLOPE, doubleString(scaleFactors[0]));
                feFuncG.setAttribute(ATTR_SLOPE, doubleString(scaleFactors[1]));
                feFuncB.setAttribute(ATTR_SLOPE, doubleString(scaleFactors[2]));
                feFuncR.setAttribute(ATTR_INTERCEPT, doubleString(offsets[0]));
                feFuncG.setAttribute(ATTR_INTERCEPT, doubleString(offsets[1]));
                feFuncB.setAttribute(ATTR_INTERCEPT, doubleString(offsets[2]));

                if(offsets.length == 4){
                    feFuncA = domFactory.createElement(TAG_FE_FUNC_A);
                    feFuncA.setAttribute(ATTR_TYPE, type);
                    feFuncA.setAttribute(ATTR_SLOPE, doubleString(scaleFactors[3]));
                    feFuncA.setAttribute(ATTR_INTERCEPT, doubleString(offsets[3]));
                }
            }

            feComponentTransferDef.appendChild(feFuncR);
            feComponentTransferDef.appendChild(feFuncG);
            feComponentTransferDef.appendChild(feFuncB);
            if(feFuncA != null)
                feComponentTransferDef.appendChild(feFuncA);

            filterDef.appendChild(feComponentTransferDef);
            filterDef.setAttribute(ATTR_ID, SVGIDGenerator.generateID(ID_PREFIX_FE_COMPONENT_TRANSFER));

            //
            // Create a filter descriptor
            //

            // Process filter attribute
            StringBuffer filterAttrBuf = new StringBuffer(URL_PREFIX);
            filterAttrBuf.append(SIGN_POUND);
            filterAttrBuf.append(filterDef.getAttribute(ATTR_ID));
            filterAttrBuf.append(URL_SUFFIX);

            filterDesc = new SVGFilterDescriptor(filterAttrBuf.toString(), filterDef);

            defSet.add(filterDef);
            descMap.put(rescaleOp, filterDesc);
        }

        return filterDesc;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{
        Document domFactory = TestUtil.getDocumentPrototype();

        RescaleOp rescaleOps[] = { new RescaleOp(3, 25, null),
                                   new RescaleOp(new float[]{ 1, 2, 3 }, new float[]{10, 20, 30}, null),
                                   new RescaleOp(new float[]{ 1, 2, 3, 4 }, new float[]{10, 20, 30, 40}, null),
        };

        SVGRescaleOp converter = new SVGRescaleOp(domFactory);

        Element group = domFactory.createElement(TAG_G);
        Element defs = domFactory.createElement(TAG_DEFS);
        Element rectGroupOne = domFactory.createElement(TAG_G);
        Element rectGroupTwo = domFactory.createElement(TAG_G);

        for(int i=0; i<rescaleOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(rescaleOps[i]);
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttribute(ATTR_FILTER, filterDesc.getFilterValue());
            rectGroupOne.appendChild(rect);
        }

        for(int i=0; i<rescaleOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(rescaleOps[i]);
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttribute(ATTR_FILTER, filterDesc.getFilterValue());
            rectGroupTwo.appendChild(rect);
        }

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element feComponentTransferDef = (Element)iter.next();
            defs.appendChild(feComponentTransferDef);
        }

        group.appendChild(defs);
        group.appendChild(rectGroupOne);
        group.appendChild(rectGroupTwo);

        TestUtil.trace(group, System.out);
    }
}
