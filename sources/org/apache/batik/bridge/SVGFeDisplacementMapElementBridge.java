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

import org.apache.batik.ext.awt.image.ARGBChannel;
import org.apache.batik.ext.awt.image.PadMode;

import org.apache.batik.ext.awt.image.renderable.DisplacementMapRable8Bit;
import org.apache.batik.ext.awt.image.renderable.DisplacementMapRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;feDisplacementMap> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeDisplacementMapElementBridge
    extends AbstractSVGFilterPrimitiveElementBridge {


    /**
     * Constructs a new bridge for the &lt;feDisplacementMap> element.
     */
    public SVGFeDisplacementMapElementBridge() {}

    /**
     * Returns 'feDisplacementMap'.
     */
    public String getLocalName() {
        return SVG_FE_DISPLACEMENT_MAP_TAG;
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

        // 'scale' attribute - default is 0
        float scale = convertNumber(filterElement, SVG_SCALE_ATTRIBUTE, 0);

        // 'xChannelSelector' attribute - default is 'A'
        ARGBChannel xChannelSelector = convertChannelSelector
            (filterElement, SVG_X_CHANNEL_SELECTOR_ATTRIBUTE, ARGBChannel.A);

        // 'yChannelSelector' attribute - default is 'A'
        ARGBChannel yChannelSelector = convertChannelSelector
            (filterElement, SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE, ARGBChannel.A);

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

        PadRable pad
            = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);

        // build the displacement map filter
        List srcs = new ArrayList(2);
        srcs.add(pad);
        srcs.add(in2);
        Filter displacementMap = new DisplacementMapRable8Bit
            (srcs, scale, xChannelSelector, yChannelSelector);

        PadRable filter = new PadRable8Bit
            (displacementMap, primitiveRegion, PadMode.ZERO_PAD);

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);

        return filter;
    }

    /**
     * Returns the channel for the specified feDisplacementMap filter
     * primitive attribute, considering the specified attribute name.
     *
     * @param filterElement the feDisplacementMap filter primitive element
     * @param attrName the name of the channel attribute
     */
    protected static
        ARGBChannel convertChannelSelector(Element filterElement,
                                           String attrName,
                                           ARGBChannel defaultChannel) {

        String s = filterElement.getAttributeNS(null, attrName);
        if (s.length() == 0) {
            return defaultChannel;
        }
        if (SVG_A_VALUE.equals(s)) {
            return ARGBChannel.A;
        }
        if (SVG_R_VALUE.equals(s)) {
            return ARGBChannel.R;
        }
        if (SVG_G_VALUE.equals(s)) {
            return ARGBChannel.G;
        }
        if (SVG_B_VALUE.equals(s)) {
            return ARGBChannel.B;
        }
        throw new BridgeException(filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                  new Object[] {attrName, s});
    }
}
