/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.MorphologyRable;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteMorphologyRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>filter</tt> element with a concrete
 * <tt>Filter</tt>
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com">Sheng Pei</a>
 * @version $Id$
 */
public class SVGFeMorphologyElementBridge implements FilterBridge,
                                                       SVGConstants {
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
                         Rectangle2D filterRegion,
                         Map filterMap){
        //
        // Extract standard deviation
        //
        String radius
            = filterElement.getAttributeNS(null,
                                           ATTR_RADIUS);

        Float radiusPair[]
            = SVGUtilities.buildFloatPair(radius);

        String operator
            = filterElement.getAttributeNS(null,
                                           ATTR_OPERATOR);

        boolean doDilation = VALUE_DILATE.equals(operator);

        //
        // Build filter only if radius is not negative
        //
        if((radiusPair[0] == null)
           || (radiusPair[0].floatValue() <= 0))
            return null;

        float radiusX = radiusPair[0].floatValue();
        float radiusY = radiusX;
        if(radiusPair[1] != null){
            radiusY = radiusPair[1].floatValue();
        }

        if(radiusY <= 0)
            return null;

          // Get source
        String inAttr = filterElement.getAttributeNS(null, ATTR_IN);
        in = CSSUtilities.getFilterSource(filteredNode, inAttr, 
                                          bridgeContext, 
                                          filteredElement,
                                          in, filterMap);

        // feMorphology is a point operation. Therefore, to take the
        // filter primitive region into account, only a pad operation
        // on the input is required.
        
        //
        // The default region is the input source's region
        // unless the source is SourceGraphics, in which
        // case the default region is the filter chain's 
        // region
        //
        Filter sourceGraphics 
            = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);
        
        Rectangle2D defaultRegion 
            = in.getBounds2D();
        
        if(in == sourceGraphics){
            defaultRegion = filterRegion;
        }
        
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement,
                                                          null);
        
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext,
                                              cssDecl);
        
        Rectangle2D primitiveRegion 
            = SVGUtilities.convertFilterPrimitiveRegion2
            (filterElement,
             filteredElement,
             defaultRegion,
             filteredNode,
             uctx);
        
        PadRable pad 
            = new ConcretePadRable(in,
                                   primitiveRegion,
                                   PadMode.ZERO_PAD);
        
        // Build filter
        Filter filter = null;
        filter = new ConcreteMorphologyRable(pad, radiusX, radiusY, doDilation);
        
        // Get result attribute if any
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
        if((result != null) && (result.trim().length() > 0)){
            filterMap.put(result, filter);
        }
    
        return filter;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
