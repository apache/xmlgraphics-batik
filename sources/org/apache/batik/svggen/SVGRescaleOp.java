/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

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
 * @see                org.apache.batik.svggen.SVGBufferedImageOp
 */
public class SVGRescaleOp extends AbstractSVGFilterConverter {
    public static final String ERROR_SCALE_FACTORS_AND_OFFSETS_MISMATCH =
        "RescapeOp offsets and scaleFactor array lenght do not match";
    public static final String ERROR_ILLEGAL_BUFFERED_IMAGE_RESCALE_OP =
        "BufferedImage RescaleOp should have 1, 3 or 4 scale factors";

    /**
     * @param generatorContext used to build Elements
     */
    public SVGRescaleOp(SVGGeneratorContext generatorContext) {
        super(generatorContext);
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
     * @see org.apache.batik.svggen.SVGFilterDescriptor
     */
    public SVGFilterDescriptor toSVG(BufferedImageOp filter,
                                     Rectangle filterRect) {
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
    public SVGFilterDescriptor toSVG(RescaleOp rescaleOp) {
        // Reuse definition if rescaleOp has already been converted
        SVGFilterDescriptor filterDesc =
            (SVGFilterDescriptor)descMap.get(rescaleOp);

        Document domFactory = generatorContext.domFactory;

        if (filterDesc == null) {
            //
            // First time filter is converted: create its corresponding
            // SVG filter
            //
            Element filterDef = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                           SVG_FILTER_TAG);
            Element feComponentTransferDef =
                domFactory.createElementNS(SVG_NAMESPACE_URI,
                                           SVG_FE_COMPONENT_TRANSFER_TAG);

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

            Element feFuncR = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                         SVG_FE_FUNC_R_TAG);
            Element feFuncG = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                         SVG_FE_FUNC_G_TAG);
            Element feFuncB = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                         SVG_FE_FUNC_B_TAG);
            Element feFuncA = null;
            String type = SVG_TYPE_LINEAR_VALUE;

            if(offsets.length == 1){
                String slope = doubleString(scaleFactors[0]);
                String intercept = doubleString(offsets[0]);
                feFuncR.setAttributeNS(null, SVG_TYPE_ATTRIBUTE, type);
                feFuncG.setAttributeNS(null, SVG_TYPE_ATTRIBUTE, type);
                feFuncB.setAttributeNS(null, SVG_TYPE_ATTRIBUTE, type);
                feFuncR.setAttributeNS(null, SVG_SLOPE_ATTRIBUTE, slope);
                feFuncG.setAttributeNS(null, SVG_SLOPE_ATTRIBUTE, slope);
                feFuncB.setAttributeNS(null, SVG_SLOPE_ATTRIBUTE, slope);
                feFuncR.setAttributeNS(null, SVG_INTERCEPT_ATTRIBUTE, intercept);
                feFuncG.setAttributeNS(null, SVG_INTERCEPT_ATTRIBUTE, intercept);
                feFuncB.setAttributeNS(null, SVG_INTERCEPT_ATTRIBUTE, intercept);
            }
            else if(offsets.length >= 3){
                feFuncR.setAttributeNS(null, SVG_TYPE_ATTRIBUTE, type);
                feFuncG.setAttributeNS(null, SVG_TYPE_ATTRIBUTE, type);
                feFuncB.setAttributeNS(null, SVG_TYPE_ATTRIBUTE, type);
                feFuncR.setAttributeNS(null, SVG_SLOPE_ATTRIBUTE,
                                       doubleString(scaleFactors[0]));
                feFuncG.setAttributeNS(null, SVG_SLOPE_ATTRIBUTE,
                                       doubleString(scaleFactors[1]));
                feFuncB.setAttributeNS(null, SVG_SLOPE_ATTRIBUTE,
                                       doubleString(scaleFactors[2]));
                feFuncR.setAttributeNS(null, SVG_INTERCEPT_ATTRIBUTE,
                                       doubleString(offsets[0]));
                feFuncG.setAttributeNS(null, SVG_INTERCEPT_ATTRIBUTE,
                                       doubleString(offsets[1]));
                feFuncB.setAttributeNS(null, SVG_INTERCEPT_ATTRIBUTE,
                                       doubleString(offsets[2]));

                if(offsets.length == 4){
                    feFuncA = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                         SVG_FE_FUNC_A_TAG);
                    feFuncA.setAttributeNS(null, SVG_TYPE_ATTRIBUTE, type);
                    feFuncA.setAttributeNS(null, SVG_SLOPE_ATTRIBUTE,
                                         doubleString(scaleFactors[3]));
                    feFuncA.setAttributeNS(null, SVG_INTERCEPT_ATTRIBUTE,
                                         doubleString(offsets[3]));
                }
            }

            feComponentTransferDef.appendChild(feFuncR);
            feComponentTransferDef.appendChild(feFuncG);
            feComponentTransferDef.appendChild(feFuncB);
            if(feFuncA != null)
                feComponentTransferDef.appendChild(feFuncA);

            filterDef.appendChild(feComponentTransferDef);

            filterDef.
                setAttributeNS(null, ATTR_ID,
                               generatorContext.idGenerator.
                               generateID(ID_PREFIX_FE_COMPONENT_TRANSFER));

            //
            // Create a filter descriptor
            //

            // Process filter attribute
            StringBuffer filterAttrBuf = new StringBuffer(URL_PREFIX);
            filterAttrBuf.append(SIGN_POUND);
            filterAttrBuf.append(filterDef.getAttributeNS(null, ATTR_ID));
            filterAttrBuf.append(URL_SUFFIX);

            filterDesc = new SVGFilterDescriptor(filterAttrBuf.toString(),
                                                 filterDef);

            defSet.add(filterDef);
            descMap.put(rescaleOp, filterDesc);
        }

        return filterDesc;
    }
}
