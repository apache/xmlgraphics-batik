/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.util.Map;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.TileRable;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

import org.apache.batik.refimpl.gvt.filter.ConcreteTileRable;
import org.apache.batik.refimpl.gvt.filter.FilterSourceRegion;

/**
 * This class bridges an SVG <tt>feTile</tt> filter element 
 * with <tt>ConcreteTileRable</tt>.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFeTileElementBridge implements FilterBridge, SVGConstants {
    /**
     * Returns the <tt>Filter</tt> that implements the filter 
     * operation modeled by the input DOM element
     *
     * @param filteredNode the node to which the filter will be attached.
     * @param bridgeContext the context to use.
     * @param filterElement DOM element that represents a filter abstraction
     * @param in the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
     * @param filterRegion the filter area defined for the filter chained 
     *        the new node will be part of.
     * @param filterMap a map where the mediator can map a name to the 
     *        <tt>Filter</tt> it creates. Other <tt>FilterBridge</tt>s
     *        can then access a filter node from the filterMap if they
     *        know its name.
     */
    public Filter create(GraphicsNode filteredNode,
                         BridgeContext bridgeContext,
                         Element filterElement,
                         Element filteredElement,
                         Filter in,
                         FilterRegion filterRegion,
                         Map filterMap){
        //
        // Tile region is defined by the filter region
        //
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement, 
                                                          null);
        
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext,
                                              cssDecl);
        
        //
        // Get the tiled region. For feTile, the default for the 
        // filter primitive subregion is the parent filter region.
        //
        // Get unit. Comes from parent node.
        Node parentNode = filterElement.getParentNode();
        String units = VALUE_USER_SPACE_ON_USE;
        if((parentNode != null)
           &&
           (parentNode.getNodeType() == parentNode.ELEMENT_NODE)){
            units = ((Element)parentNode).getAttributeNS(null, ATTR_PRIMITIVE_UNITS);
            if(units.length() == 0){
                units = VALUE_USER_SPACE_ON_USE;
            }
        }


        final FilterRegion tiledRegion 
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        filterRegion,
                                                        units,
                                                        filteredNode,
                                                        uctx);
        //
        // Get the tile source
        //
        String inAttr 
            = filterElement.getAttributeNS(null, ATTR_IN);
        in = CSSUtilities.getFilterSource(filteredNode, 
                                          inAttr, 
                                          bridgeContext, 
                                          filteredElement,
                                          in, filterMap);

        //
        // For feTile, the source defines the tile size
        //
        TileRable tileRable = null;

        if(in != null){
            tileRable 
                = new ConcreteTileRable(in, 
                                        tiledRegion, 
                                        new FilterSourceRegion(in), 
                                        false);
        }

        return tileRable;
    }

   /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}

