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
 * Utility class that converts a ConvolveOp object into
 * an SVG filter descriptor.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.util.awt.svg.SVGBufferedImageOp
 */
public class SVGConvolveOp extends AbstractSVGFilterConverter{
    /**
     * @param domFactory used to build Elements
     */
    public SVGConvolveOp(Document domFactory){
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
        if(filter instanceof ConvolveOp)
            return toSVG((ConvolveOp)filter);
        else
            return null;
    }

    /**
     * @param convolveOp the ConvolveOp to be converted
     * @return a description of the SVG filter corresponding to
     *         convolveOp. The definition of the feConvolveMatrix
     *         filter in put in feConvolveMatrixDefSet
     */
    public SVGFilterDescriptor toSVG(ConvolveOp convolveOp){
        // Reuse definition if convolveOp has already been converted
        SVGFilterDescriptor filterDesc = (SVGFilterDescriptor)descMap.get(convolveOp);

        if(filterDesc == null){
            //
            // First time filter is converted: create its corresponding
            // SVG filter
            //
            Kernel kernel = convolveOp.getKernel();
            Element filterDef = domFactory.createElement(TAG_FILTER);
            Element feConvolveMatrixDef = domFactory.createElement(TAG_FE_CONVOLVE_MATRIX);

            // Convert the kernel size
            feConvolveMatrixDef.setAttribute(ATTR_ORDER, kernel.getWidth() + SPACE +  kernel.getHeight());

            // Convert the kernel values
            StringBuffer kernelMatrixBuf = new StringBuffer();
            float data[] = kernel.getKernelData(null);
            for(int i=0; i<data.length; i++){
                kernelMatrixBuf.append(doubleString(data[i]));
                kernelMatrixBuf.append(SPACE);
            }

            feConvolveMatrixDef.setAttribute(ATTR_KERNEL_MATRIX, kernelMatrixBuf.toString().trim());

            filterDef.appendChild(feConvolveMatrixDef);
            filterDef.setAttribute(ATTR_ID, SVGIDGenerator.generateID(ID_PREFIX_FE_CONVOLVE_MATRIX));

            // Convert the edge mode
            if(convolveOp.getEdgeCondition() == ConvolveOp.EDGE_NO_OP)
                feConvolveMatrixDef.setAttribute(ATTR_EDGE_MODE, VALUE_EDGE_DUPLICATE);
            else
                feConvolveMatrixDef.setAttribute(ATTR_EDGE_MODE, VALUE_EDGE_NONE);

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
            descMap.put(convolveOp, filterDesc);
        }

        return filterDesc;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{
        Document domFactory = TestUtil.getDocumentPrototype();

        Kernel k = new Kernel(5, 3, new float[] { 1, 1, 1, 1, 1,
                                                      2, 2, 2, 2, 2,
                                                      3, 3, 3, 3, 3 });
        ConvolveOp convolveOps[] = { new ConvolveOp(k),
                                     new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null),
                                     new ConvolveOp(k, ConvolveOp.EDGE_ZERO_FILL, null) };


        SVGConvolveOp converter = new SVGConvolveOp(domFactory);

        Element group = domFactory.createElement(TAG_G);
        Element defs = domFactory.createElement(TAG_DEFS);
        Element rectGroupOne = domFactory.createElement(TAG_G);
        Element rectGroupTwo = domFactory.createElement(TAG_G);

        for(int i=0; i<convolveOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(convolveOps[i]);
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttribute(ATTR_FILTER, filterDesc.getFilterValue());
            rectGroupOne.appendChild(rect);
        }

        for(int i=0; i<convolveOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(convolveOps[i]);
            Element rect = domFactory.createElement(TAG_RECT);
            rect.setAttribute(ATTR_FILTER, filterDesc.getFilterValue());
            rectGroupTwo.appendChild(rect);
        }

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element feConvolveMatrixDef = (Element)iter.next();
            defs.appendChild(feConvolveMatrixDef);
        }

        group.appendChild(defs);
        group.appendChild(rectGroupOne);
        group.appendChild(rectGroupTwo);

        TestUtil.trace(group, System.out);
    }
}
