/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import org.w3c.dom.Element;

import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;

import org.apache.batik.gvt.GraphicsNode;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.PadMode;

/**
 * Bridge class for a histogram normalization element.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas Deweese</a>
 */
public class BatikHistogramNormalizationElementBridge 
    extends AbstractSVGFilterPrimitiveElementBridge  
    implements BatikExtConstants {

    /**
     * Constructs a new bridge for the &lt;histogramNormalization> element.
     */
    public BatikHistogramNormalizationElementBridge() { /* nothing */ }

    /**
     * Returns the SVG namespace URI.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns 'histogramNormalization'.
     */
    public String getLocalName() {
        return BATIK_EXT_HISTOGRAM_NORMALIZATION_TAG;
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

        float trim = 1;
        String s = filterElement.getAttributeNS
            (null, BATIK_EXT_TRIM_ATTRIBUTE);

        if (s.length() != 0) {
            try {
                trim = SVGUtilities.convertSVGNumber(s);
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {BATIK_EXT_TRIM_ATTRIBUTE, s});
            }
        }

        if (trim < 0) trim =0;
        else if (trim > 100) trim=100;

        Filter filter = in;
        filter = new BatikHistogramNormalizationFilter8Bit(filter, trim/100);
        
        filter = new PadRable8Bit(filter, primitiveRegion, PadMode.ZERO_PAD);

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);

        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(filter, filterElement);

        return filter;
    }

    /**
     * Stolen from AbstractSVGFilterPrimitiveElementBridge.
     * Converts on the specified filter primitive element, the specified
     * attribute that represents an integer and with the specified
     * default value.
     *
     * @param filterElement the filter primitive element
     * @param attrName the name of the attribute
     * @param defaultValue the default value of the attribute
     */
    protected static int convertSides(Element filterElement,
                                        String attrName,
                                        int defaultValue) {
        String s = filterElement.getAttributeNS(null, attrName);
        if (s.length() == 0) {
            return defaultValue;
        } else {
            int ret = 0;
            try {
                ret = SVGUtilities.convertSVGInteger(s);
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {attrName, s});
            }

            if (ret <3) 
                throw new BridgeException
                    (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {attrName, s});
            return ret;
        }
    }
}
