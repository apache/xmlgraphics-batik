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
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;feColorMatrix> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeColorMatrixElementBridge
    extends AbstractSVGFilterPrimitiveElementBridge {

    /**
     * Constructs a new bridge for the &lt;feColorMatrix> element.
     */
    public SVGFeColorMatrixElementBridge() {}

    /**
     * Returns 'feColorMatrix'.
     */
    public String getLocalName() {
        return SVG_FE_COLOR_MATRIX_TAG;
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

        int type = convertType(filterElement);
        ColorMatrixRable colorMatrix;
        switch (type) {
        case ColorMatrixRable.TYPE_HUE_ROTATE:
            float a = convertValuesToHueRotate(filterElement);
            colorMatrix = ColorMatrixRable8Bit.buildHueRotate(a);
            break;
        case ColorMatrixRable.TYPE_LUMINANCE_TO_ALPHA:
            colorMatrix = ColorMatrixRable8Bit.buildLuminanceToAlpha();
            break;
        case ColorMatrixRable.TYPE_MATRIX:
            float [][] matrix = convertValuesToMatrix(filterElement);
            colorMatrix = ColorMatrixRable8Bit.buildMatrix(matrix);
            break;
        case ColorMatrixRable.TYPE_SATURATE:
            float s = convertValuesToSaturate(filterElement);
            colorMatrix = ColorMatrixRable8Bit.buildSaturate(s);
            break;
        default:
            throw new Error(); // can't be reached
        }
        colorMatrix.setSource(in);

        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(colorMatrix, filterElement);

        Filter filter
            = new PadRable8Bit(colorMatrix, primitiveRegion, PadMode.ZERO_PAD);

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);

        return filter;
    }

    /**
     * Converts the 'values' attribute of the specified feColorMatrix
     * filter primitive element for the 'matrix' type.
     *
     * @param filterElement the filter element
     */
    protected static float[][] convertValuesToMatrix(Element filterElement) {
        String s = filterElement.getAttributeNS(null, SVG_VALUES_ATTRIBUTE);
        float [][] matrix = new float[4][5];
        if (s.length() == 0) {
            matrix[0][0] = 1;
            matrix[1][1] = 1;
            matrix[2][2] = 1;
            matrix[3][3] = 1;
            return matrix;
        }
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        int n = 0;
        try {
            while (n < 20 && tokens.hasMoreTokens()) {
                matrix[n/5][n%5]
                    = SVGUtilities.convertSVGNumber(tokens.nextToken());
                n++;
            }
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_VALUES_ATTRIBUTE, s, ex});
        }
        if (n != 20 || tokens.hasMoreTokens()) {
            throw new BridgeException
                (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_VALUES_ATTRIBUTE, s});
        }

        for (int i = 0; i < 4; ++i) {
            matrix[i][4] *= 255;
        }
        return matrix;
    }

    /**
     * Converts the 'values' attribute of the specified feColorMatrix
     * filter primitive element for the 'saturate' type.
     *
     * @param filterElement the filter element
     */
    protected static float convertValuesToSaturate(Element filterElement) {
        String s = filterElement.getAttributeNS(null, SVG_VALUES_ATTRIBUTE);
        int length = s.length();
        if (s.length() == 0) {
            return 1; // default is 1
        } else {
            try {
                return SVGUtilities.convertSVGNumber(s);
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object [] {SVG_VALUES_ATTRIBUTE, s});
            }
        }
    }

    /**
     * Converts the 'values' attribute of the specified feColorMatrix
     * filter primitive element for the 'hueRotate' type.
     *
     * @param filterElement the filter element
     */
    protected static float convertValuesToHueRotate(Element filterElement) {
        String s = filterElement.getAttributeNS(null, SVG_VALUES_ATTRIBUTE);
        int length = s.length();
        if (s.length() == 0) {
            return 0; // default is 0
        } else {
            try {
                return (float)(SVGUtilities.convertSVGNumber(s)*Math.PI)/180f;
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object [] {SVG_VALUES_ATTRIBUTE, s});
            }
        }
    }

    /**
     * Converts the type of the specified color matrix filter primitive.
     *
     * @param filterElement the filter element
     */
    protected static int convertType(Element filterElement) {
        String s = filterElement.getAttributeNS(null, SVG_TYPE_ATTRIBUTE);
        int length = s.length();
        if (s.length() == 0) {
            return ColorMatrixRable.TYPE_MATRIX;
        }
        if (SVG_HUE_ROTATE_VALUE.equals(s)) {
            return ColorMatrixRable.TYPE_HUE_ROTATE;
        }
        if (SVG_LUMINANCE_TO_ALPHA_VALUE.equals(s)) {
            return ColorMatrixRable.TYPE_LUMINANCE_TO_ALPHA;
        }
        if (SVG_MATRIX_VALUE.equals(s)) {
            return ColorMatrixRable.TYPE_MATRIX;
        }
        if (SVG_SATURATE_VALUE.equals(s)) {
            return ColorMatrixRable.TYPE_SATURATE;
        }
        throw new BridgeException(filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                  new Object[] {SVG_TYPE_ATTRIBUTE, s});
    }
}
