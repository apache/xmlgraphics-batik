/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.CompositeRule;

import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import org.apache.batik.ext.awt.image.renderable.CompositeRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;feBlend> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeBlendElementBridge
    extends AbstractSVGFilterPrimitiveElementBridge {


    /**
     * Constructs a new bridge for the &lt;feBlend> element.
     */
    public SVGFeBlendElementBridge() {}

    /**
     * Returns 'feBlend'.
     */
    public String getLocalName() {
        return SVG_FE_BLEND_TAG;
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


        // 'mode' attribute - default is 'normal'
        CompositeRule rule = convertMode(filterElement);

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

        // 'in2' attribute - required
        Filter in2 = getIn2(filterElement,
                            filteredElement,
                            filteredNode,
                            inputFilter,
                            filterMap,
                            ctx);
        if (in2 == null) {
            return null; // disable the filter
        }

        // The default region is the union of the input sources
        // regions unless 'in' is 'SourceGraphic' in which case the
        // default region is the filterChain's region
        Filter sourceGraphics = (Filter)filterMap.get(SVG_SOURCE_GRAPHIC_VALUE);
        Rectangle2D defaultRegion;
        if (in == sourceGraphics) {
            defaultRegion = filterRegion;
        } else {
            defaultRegion = in.getBounds2D();
            defaultRegion.add(in2.getBounds2D());
        }

        // get filter primitive chain region
        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filteredNode,
                                                        defaultRegion,
                                                        filterRegion,
                                                        ctx);

        // build the blend filter
        List srcs = new ArrayList(2);
        srcs.add(in2);
        srcs.add(in);

        Filter filter = new CompositeRable8Bit(srcs, rule, true);
        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(filter, filterElement);

        filter = new PadRable8Bit(filter, primitiveRegion, PadMode.ZERO_PAD);

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);


        return filter;
    }

    /**
     * Converts the 'mode' of the specified feBlend filter primitive.
     *
     * @param filterElement the filter feBlend element
     */
    protected static CompositeRule convertMode(Element filterElement) {
        String rule = filterElement.getAttributeNS(null, SVG_MODE_ATTRIBUTE);
        if (rule.length() == 0) {
            return CompositeRule.OVER;
        }
        if (SVG_NORMAL_VALUE.equals(rule)) {
            return CompositeRule.OVER;
        }
        if (SVG_MULTIPLY_VALUE.equals(rule)) {
            return CompositeRule.MULTIPLY;
        }
        if (SVG_SCREEN_VALUE.equals(rule)) {
            return CompositeRule.SCREEN;
        }
        if (SVG_DARKEN_VALUE.equals(rule)) {
            return CompositeRule.DARKEN;
        }
        if (SVG_LIGHTEN_VALUE.equals(rule)) {
            return CompositeRule.LIGHTEN;
        }
        throw new BridgeException(filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                  new Object[] {SVG_MODE_ATTRIBUTE, rule});
    }
}
