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

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.GaussianBlurRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;feGaussianBlur> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeGaussianBlurElementBridge
    extends AbstractSVGFilterPrimitiveElementBridge {


    /**
     * Constructs a new bridge for the &lt;feGaussianBlur> element.
     */
    public SVGFeGaussianBlurElementBridge() {}

    /**
     * Returns 'feGaussianBlur'.
     */
    public String getLocalName() {
        return SVG_FE_GAUSSIAN_BLUR_TAG;
    }

    /**
     * Creates a <tt>Filter</tt> primitive according to the specified
     * parameters.
     *
     * @param ctx the bridge context to use
     * @param filterElement the element that defines a filter
     * @param filteredElement the element that references the filter
     * @param filteredNode the graphics node to filter
     *
     * @param inputFilter the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
     * @param filterRegion the filter area defined for the filter chain
     *        the new node will be part of.
     * @param filterMap a map where the mediator can map a name to the
     *        <tt>Filter</tt> it creates. Other <tt>FilterBridge</tt>s
     *        can then access a filter node from the filterMap if they
     *        know its name.
     */
    public Filter createFilter(BridgeContext ctx,
                               Element filterElement,
                               Element filteredElement,
                               GraphicsNode filteredNode,
                               Filter inputFilter,
                               Rectangle2D filterRegion,
                               Map filterMap) {

        // 'stdDeviation' attribute - default is [0, 0]
        float [] stdDeviationXY = convertStdDeviation(filterElement);
        if (stdDeviationXY[0] < 0 || stdDeviationXY[1] < 0) {
            throw new BridgeException(filterElement,
                                      ERR_ATTRIBUTE_VALUE_MALFORMED,
                                      new Object[] {SVG_STD_DEVIATION_ATTRIBUTE,
                                                    "" + stdDeviationXY[0] + 
                                                    stdDeviationXY[1]});
        }

        // 'in' attribute
        Filter in = getIn(filterElement,
                          filteredElement,
                          filteredNode,
                          inputFilter,
                          filterMap,
                          ctx);
        if (in == null) {
            return null; // disable the filter
        }

        // Default region is the size of in (if in is SourceGraphic or
        // SourceAlpha it will already include a pad/crop to the
        // proper filter region size).
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filteredNode,
                                                        defaultRegion,
                                                        filterRegion,
                                                        ctx);

        // Take the filter primitive region into account, we need to
        // pad/crop the input and output.
        PadRable pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);

        // build filter
        Filter blur = new GaussianBlurRable8Bit
            (pad, stdDeviationXY[0], stdDeviationXY[1]);

        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(blur, filterElement);

        PadRable filter
            = new PadRable8Bit(blur, primitiveRegion, PadMode.ZERO_PAD);

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);

        return filter;
    }

    /**
     * Returns the standard deviation of the specified feGaussianBlur
     * filter primitive element.
     *
     * @param filterElement the feGaussianBlur filter primitive element
     */
    protected static float [] convertStdDeviation(Element filterElement) {
        String s
            = filterElement.getAttributeNS(null, SVG_STD_DEVIATION_ATTRIBUTE);
        if (s.length() == 0) {
            return new float[] {0, 0};
        }
        float [] stdDevs = new float[2];
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            stdDevs[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            if (tokens.hasMoreTokens()) {
                stdDevs[1] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            } else {
                stdDevs[1] = stdDevs[0];
            }
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_STD_DEVIATION_ATTRIBUTE, s, ex});
        }
        if (tokens.hasMoreTokens()) {
            throw new BridgeException
                (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_STD_DEVIATION_ATTRIBUTE, s});
        }
        return stdDevs;
    }
}
