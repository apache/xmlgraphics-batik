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

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.TurbulenceRable;
import org.apache.batik.ext.awt.image.renderable.TurbulenceRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;feTurbulence> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeTurbulenceElementBridge
    extends AbstractSVGFilterPrimitiveElementBridge {

    /**
     * Constructs a new bridge for the &lt;feTurbulence> element.
     */
    public SVGFeTurbulenceElementBridge() {}

    /**
     * Returns 'feTurbulence'.
     */
    public String getLocalName() {
        return SVG_FE_TURBULENCE_TAG;
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

        // default region is the filter chain region
        Rectangle2D defaultRegion = filterRegion;
        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filteredNode,
                                                        defaultRegion,
                                                        filterRegion,
                                                        ctx);

        // 'baseFrequency' attribute - default is [0, 0]
        float [] baseFrequency
            = convertBaseFrenquency(filterElement);

        // 'numOctaves' attribute - default is 1
        int numOctaves
            = convertInteger(filterElement, SVG_NUM_OCTAVES_ATTRIBUTE, 1);

        // 'seed' attribute - default is 0
        int seed
            = convertInteger(filterElement, SVG_SEED_ATTRIBUTE, 0);

        // 'stitchTiles' attribute - default is 'noStitch'
        boolean stitchTiles
            = convertStitchTiles(filterElement);

        // 'fractalNoise' attribute - default is 'turbulence'
        boolean isFractalNoise
            = convertType(filterElement);

        // create the filter primitive
        TurbulenceRable turbulenceRable
            = new TurbulenceRable8Bit(primitiveRegion);

        turbulenceRable.setBaseFrequencyX(baseFrequency[0]);
        turbulenceRable.setBaseFrequencyY(baseFrequency[1]);
        turbulenceRable.setNumOctaves(numOctaves);
        turbulenceRable.setSeed(seed);
        turbulenceRable.setStitched(stitchTiles);
        turbulenceRable.setFractalNoise(isFractalNoise);

        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(turbulenceRable, filterElement);

        // update the filter Map
        updateFilterMap(filterElement, turbulenceRable, filterMap);

        return turbulenceRable;
    }

    /**
     * Converts the 'baseFrequency' attribute of the specified
     * feTurbulence element.
     *
     * @param e the feTurbulence element
     */
    protected static float [] convertBaseFrenquency(Element e) {
        String s = e.getAttributeNS(null, SVG_BASE_FREQUENCY_ATTRIBUTE);
        if (s.length() == 0) {
            return new float[] {0.001f, 0.001f};
        }
        float[] v = new float[2];
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            v[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            if (tokens.hasMoreTokens()) {
                v[1] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            } else {
                v[1] = v[0];
            }
            if (tokens.hasMoreTokens()) {
                throw new BridgeException
                    (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {SVG_BASE_FREQUENCY_ATTRIBUTE, s});
            }
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_BASE_FREQUENCY_ATTRIBUTE, s});
        }
        if (v[0] < 0 || v[1] < 0) {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                 new Object[] {SVG_BASE_FREQUENCY_ATTRIBUTE, s});
        }
        return v;
    }

    /**
     * Converts the 'stitchTiles' attribute of the specified
     * feTurbulence element.
     *
     * @param e the feTurbulence element
     * @return true if stitchTiles attribute is 'stitch', false otherwise
     */
    protected static boolean convertStitchTiles(Element e) {
        String s = e.getAttributeNS(null, SVG_STITCH_TILES_ATTRIBUTE);
        if (s.length() == 0) {
            return false;
        }
        if (SVG_STITCH_VALUE.equals(s)) {
            return true;
        }
        if (SVG_NO_STITCH_VALUE.equals(s)) {
            return false;
        }
        throw new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                  new Object[] {SVG_STITCH_TILES_ATTRIBUTE, s});
    }

    /**
     * Converts the 'type' attribute of the specified feTurbulence element.
     *
     * @param e the feTurbulence element
     * @return true if type attribute value is 'fractalNoise', false otherwise
     */
    protected static boolean convertType(Element e) {
        String s = e.getAttributeNS(null, SVG_TYPE_ATTRIBUTE);
        if (s.length() == 0) {
            return false;
        }
        if (SVG_FRACTAL_NOISE_VALUE.equals(s)) {
            return true;
        }
        if (SVG_TURBULENCE_VALUE.equals(s)) {
            return false;
        }
        throw new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                  new Object[] {SVG_TYPE_ATTRIBUTE, s});
    }
}
