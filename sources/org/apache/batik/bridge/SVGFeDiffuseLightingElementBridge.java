/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apache.batik.ext.awt.image.Light;
import org.apache.batik.ext.awt.image.renderable.DiffuseLightingRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;

/**
 * Bridge class for the &lt;feDiffuseLighting> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeDiffuseLightingElementBridge
    extends SVGFeAbstractLightingElementBridge {


    /**
     * Constructs a new bridge for the &lt;feDiffuseLighting> element.
     */
    public SVGFeDiffuseLightingElementBridge() {}

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

        // 'surfaceScale' attribute - default is 1
        float surfaceScale
            = convertNumber(filterElement, SVG_SURFACE_SCALE_ATTRIBUTE, 1);

        // 'diffuseConstant' attribute - default is 1
        float diffuseConstant
            = convertNumber(filterElement, SVG_DIFFUSE_CONSTANT_ATTRIBUTE, 1);

        // 'kernelUnitLength' attribute
        // <!> FIXME: Why is it ignored ???

        // extract the light definition from the filterElement's children list
        Light light = extractLight(filterElement, ctx);

        // 'kernelUnitLength' attribute
        double [] kernelUnitLength = convertKernelUnitLength(filterElement);

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

        // The default region is the union of the input sources
        // regions unless 'in' is 'SourceGraphic' in which case the
        // default region is the filterChain's region
        Filter sourceGraphics = (Filter)filterMap.get(SVG_SOURCE_GRAPHIC_VALUE);
        Rectangle2D defaultRegion;
        if (in == sourceGraphics) {
            defaultRegion = filterRegion;
        } else {
            defaultRegion = in.getBounds2D();
        }

        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filteredNode,
                                                        defaultRegion,
                                                        filterRegion,
                                                        ctx);
        Filter filter = new DiffuseLightingRable8Bit(in,
                                                     primitiveRegion,
                                                     light,
                                                     diffuseConstant,
                                                     surfaceScale,
                                                     kernelUnitLength);

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);

        return filter;
    }
}
