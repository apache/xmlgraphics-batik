/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Bridge class for the &lt;feMerge> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeMergeElementBridge
    extends AbstractSVGFilterPrimitiveElementBridge {

    /**
     * Constructs a new bridge for the &lt;feMerge> element.
     */
    public SVGFeMergeElementBridge() {}

    /**
     * Returns 'feMerge'.
     */
    public String getLocalName() {
        return SVG_FE_MERGE_TAG;
    }

    /**
     * Creates a <tt>Filter</tt> primitive according to the specified
     * parameters.
     *
     * @param ctx the bridge context to use
     * @param filterElement the element that defines a filter
     * @param filteredElement the element that references the filter
     * @param filteredNode the graphics node to filter
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

        List srcs = extractFeMergeNode(filterElement,
                                       filteredElement,
                                       filteredNode,
                                       inputFilter,
                                       filterMap,
                                       ctx);

        if (srcs == null) {
            return null; // <!> FIXME: no subelement found, result unspecified
        }

        if (srcs.size() == 0) {
            return null; // <!> FIXME: no subelement found, result unspecified
        }

        // the default region is the input sources regions union
        Iterator iter = srcs.iterator();
        Rectangle2D defaultRegion = 
            (Rectangle2D)((Filter)iter.next()).getBounds2D().clone();

        while (iter.hasNext()) {
            defaultRegion.add(((Filter)iter.next()).getBounds2D());
        }

        // get filter primitive chain region
        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filteredNode,
                                                        defaultRegion,
                                                        filterRegion,
                                                        ctx);

        Filter filter = new CompositeRable8Bit(srcs, CompositeRule.OVER, true);

        // handle the 'color-interpolation-filters' property
        handleColorInterpolationFilters(filter, filterElement);

        filter = new PadRable8Bit(filter, primitiveRegion, PadMode.ZERO_PAD);;

        // update the filter Map
        updateFilterMap(filterElement, filter, filterMap);

        return filter;
    }


    /**
     * Returns a list of Filter objects that represents the feMergeNode of
     * the specified feMerge filter element.
     *
     * @param filterElement the feMerge filter element
     * @param filteredElement the filtered element
     * @param filteredNode the filtered graphics node
     * @param inputFilter the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
     * @param filterMap the filter map that contains named filter primitives
     * @param ctx the bridge context
     */
    protected static List extractFeMergeNode(Element filterElement,
                                             Element filteredElement,
                                             GraphicsNode filteredNode,
                                             Filter inputFilter,
                                             Map filterMap,
                                             BridgeContext ctx) {

        List srcs = null;
        for (Node n = filterElement.getFirstChild();
             n != null;
             n = n.getNextSibling()) {

            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element e = (Element)n;
            Bridge bridge = ctx.getBridge(e);
            if (bridge == null ||
                !(bridge instanceof SVGFeMergeNodeElementBridge)) {
                continue;
            }
            Filter filter =  ((SVGFeMergeNodeElementBridge)bridge).createFilter
                (ctx,
                 e,
                 filteredElement,
                 filteredNode,
                 inputFilter,
                 filterMap);
            if (filter != null) {
                if (srcs == null) {
                    srcs = new LinkedList();
                }
                srcs.add(filter);
            }
        }
        return srcs;
    }

    /**
     * Bridge class for the &lt;feMergeNode> element.
     */
    public static class SVGFeMergeNodeElementBridge extends AbstractSVGBridge {

        /**
         * Constructs a new bridge for the &lt;feMergeNode> element.
         */
        public SVGFeMergeNodeElementBridge() {}

        /**
         * Returns 'feMergeNode'.
         */
        public String getLocalName() {
            return SVG_FE_MERGE_NODE_TAG;
        }

        /**
         * Creates a <tt>Filter</tt> according to the specified parameters.
         *
         * @param ctx the bridge context to use
         * @param filterElement the element that defines a filter
         * @param filteredElement the element that references the filter
         * @param filteredNode the graphics node to filter
         * @param inputFilter the <tt>Filter</tt> that represents the current
         *        filter input if the filter chain.
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
                                   Map filterMap) {
            return getIn(filterElement,
                         filteredElement,
                         filteredNode,
                         inputFilter,
                         filterMap,
                         ctx);
        }
    }
}
