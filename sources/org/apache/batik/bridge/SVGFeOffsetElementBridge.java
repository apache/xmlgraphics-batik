/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;feOffset> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeOffsetElementBridge
    extends AbstractSVGFilterPrimitiveElementBridge {

    /**
     * Constructs a new bridge for the &lt;feOffset> element.
     */
    public SVGFeOffsetElementBridge() {}

    /**
     * Returns 'feOffset'.
     */
    public String getLocalName() {
        return SVG_FE_OFFSET_TAG;
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

        float dx = convertNumber(filterElement, SVG_DX_ATTRIBUTE, 0);
        float dy = convertNumber(filterElement, SVG_DY_ATTRIBUTE, 0);
        AffineTransform at = AffineTransform.getTranslateInstance(dx, dy);

        // feOffset is a point operation. Therefore, to take the
        // filter primitive region into account, only a pad operation
        // on the input is required.
        PadRable pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        Filter filter = new AffineRable8Bit(pad, at);
        filter = new PadRable8Bit(filter, primitiveRegion, PadMode.ZERO_PAD);

        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(filter, filterElement);

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);

        return filter;
    }
}
