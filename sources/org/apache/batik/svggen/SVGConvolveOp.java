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

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that converts a ConvolveOp object into
 * an SVG filter descriptor.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see                org.apache.batik.svggen.SVGBufferedImageOp
 */
public class SVGConvolveOp extends AbstractSVGFilterConverter {
    /**
     * @param generatorContext used to build Elements
     */
    public SVGConvolveOp(SVGGeneratorContext generatorContext) {
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
        SVGFilterDescriptor filterDesc =
            (SVGFilterDescriptor)descMap.get(convolveOp);
        Document domFactory = generatorContext.domFactory;

        if (filterDesc == null) {
            //
            // First time filter is converted: create its corresponding
            // SVG filter
            //
            Kernel kernel = convolveOp.getKernel();
            Element filterDef =
                domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_FILTER_TAG);
            Element feConvolveMatrixDef =
                domFactory.createElementNS(SVG_NAMESPACE_URI,
                                           SVG_FE_CONVOLVE_MATRIX_TAG);

            // Convert the kernel size
            feConvolveMatrixDef.setAttributeNS(null, SVG_ORDER_ATTRIBUTE,
                                             kernel.getWidth() + SPACE +
                                             kernel.getHeight());

            // Convert the kernel values
            StringBuffer kernelMatrixBuf = new StringBuffer();
            float data[] = kernel.getKernelData(null);
            for(int i=0; i<data.length; i++){
                kernelMatrixBuf.append(doubleString(data[i]));
                kernelMatrixBuf.append(SPACE);
            }

            feConvolveMatrixDef.
                setAttributeNS(null, SVG_KERNEL_MATRIX_ATTRIBUTE,
                               kernelMatrixBuf.toString().trim());

            filterDef.appendChild(feConvolveMatrixDef);

            filterDef.setAttributeNS(null, ATTR_ID,
                                     generatorContext.idGenerator.
                                     generateID(ID_PREFIX_FE_CONVOLVE_MATRIX));

            // Convert the edge mode
            if(convolveOp.getEdgeCondition() == ConvolveOp.EDGE_NO_OP)
                feConvolveMatrixDef.setAttributeNS(null, SVG_EDGE_MODE_ATTRIBUTE,
                                                 SVG_DUPLICATE_VALUE);
            else
                feConvolveMatrixDef.setAttributeNS(null, SVG_EDGE_MODE_ATTRIBUTE,
                                                 SVG_NONE_VALUE);

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
            descMap.put(convolveOp, filterDesc);
        }

        return filterDesc;
    }
}
