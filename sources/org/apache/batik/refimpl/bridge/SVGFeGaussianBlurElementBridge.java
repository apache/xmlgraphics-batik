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

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.GaussianBlurRable;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;

import org.apache.batik.refimpl.gvt.filter.ConcreteGaussianBlurRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;

import org.w3c.dom.Element;

/**
 * This class bridges an SVG <tt>filter</tt> element with a concrete
 * <tt>Filter</tt>
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFeGaussianBlurElementBridge implements FilterBridge,
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
                                Filter in,
                                FilterRegion filterRegion,
                                Map filterMap){
        // 
        // Extract deviation
        //
        Float deviationPair[] 
            = SVGUtilities.buildFloatPair(filterElement,
                                          null,
                                          ATTR_STD_DEVIATION);

        //
        // Build filter only if stdDeviationX is greater than 0
        // 
        GaussianBlurRable filter = null;
        if((deviationPair[0] != null)
           || (deviationPair[0].floatValue() > 0)){
            float stdDeviationX = deviationPair[0].floatValue();
            float stdDeviationY = stdDeviationX;
            if(deviationPair[1] != null){
                stdDeviationY = deviationPair[1].floatValue();
            }
            
            if(stdDeviationY > 0){
                // Get source
                /*String inAttr = element.getAttributeNS(null, ATTR_IN);
                if(inAttr != null){
                    int inValue = SVGUtilities.parseInAttribute();
                    switch(inValue){
                    case SVGUtilities.IDENTIFIER:
                        in = (Filter)filterMap.get(inAttr);
                        break;
                    case SVGUtilities.FILL_PAINT:
                        
                    }
                    }*/

                // Build a pad
                final FilterRegion blurArea 
                    = SVGUtilities.buildFilterRegion(filterElement,
                                                     filteredNode);
                
                PadRable pad = new ConcretePadRable(in, 
                                                    blurArea.getRegion(),
                                                    PadMode.ZERO_PAD){
                        public Rectangle2D getBounds2D(){
                            setPadRect(blurArea.getRegion());
                            System.out.println("PadRable: " +
                                               blurArea.getRegion());
                            return super.getBounds2D();
                        }

                        public java.awt.image.RenderedImage createRendering(java.awt.image.renderable.RenderContext rc){
                            setPadRect(blurArea.getRegion());
                            return super.createRendering(rc);
                        }
                    };

                // Build filter
                filter = new ConcreteGaussianBlurRable(pad, stdDeviationX, stdDeviationY);

                                                                       

                // Get result attribute if any
                String result = filterElement.getAttributeNS(null, 
                                                             ATTR_RESULT);
                if((result != null) && (result.trim().length() > 0)){
                    // The filter will be added to the filter map. Before
                    // we do that, append the filter region crop
                    filterMap.put(result, filter);
                }
            }
        }

        return filter;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     *
     * @param bridgeContext the context to use.
     * @param filterElement DOM element that represents the filter abstraction
     * @param filterNode image that implements the filter abstraction and whose
     *        state should be updated to reflect the filterElement's current
     *        state.
     */
    public void update(BridgeContext bridgeContext,
                       Element filterElement,
                       Filter filter,
                       Map filterMap){
    }

}
