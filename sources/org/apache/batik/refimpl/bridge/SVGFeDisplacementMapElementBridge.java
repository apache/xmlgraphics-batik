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
import java.util.Vector;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.ARGBChannel;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.DisplacementMapRable;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteDisplacementMapRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.refimpl.gvt.filter.FilterSourceRegion;
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
public class SVGFeDisplacementMapElementBridge implements FilterBridge,
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
                         FilterRegion filterRegion,
                         Map filterMap){
        //
        // Extract standard deviation
        //
        String scaleStr
            = filterElement.getAttributeNS(null,
                                           ATTR_SCALE);
        double scale = Float.parseFloat(scaleStr);

        String xChannelSelectorStr
            = filterElement.getAttributeNS(null,
                                           ATTR_X_CHANNEL_SELECTOR);

        ARGBChannel xChannelSelector 
            = computeChannelSelector(xChannelSelectorStr);
                
        String yChannelSelectorStr
            = filterElement.getAttributeNS(null,
                                           ATTR_Y_CHANNEL_SELECTOR);

        ARGBChannel yChannelSelector
            = computeChannelSelector(yChannelSelectorStr);

        //
        // Build filter
        //
        Filter filter = null;

        // Get source 1
        String inAttr = filterElement.getAttributeNS(null, ATTR_IN);
        Filter in1 = CSSUtilities.getFilterSource(filteredNode, 
                                                  inAttr, 
                                                  bridgeContext, 
                                                  filteredElement,
                                                  in, filterMap);

        Filter in2 = null;

        // Get source 2
        String in2Attr = filterElement.getAttributeNS(null, ATTR_IN2);
        in2 = CSSUtilities.getFilterSource(filteredNode, 
                                          in2Attr, 
                                          bridgeContext, 
                                          filteredElement,
                                          in, filterMap);

        FilterRegion defaultRegion = new FilterSourceRegion(in);

        // Get unit. Comes from parent node.
        Node parentNode = filterElement.getParentNode();
        String units = VALUE_USER_SPACE_ON_USE;
        if((parentNode != null)
           &&
           (parentNode.getNodeType() == parentNode.ELEMENT_NODE)){
            units = ((Element)parentNode).getAttributeNS
                (null, ATTR_PRIMITIVE_UNITS);
        }

        // Compute primitive region
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle
            (filterElement,
             null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext,
                                              cssDecl);

        final FilterRegion dispArea
            = SVGUtilities.convertFilterPrimitiveRegion
            (filterElement,
             filteredElement,
             defaultRegion,
             units,
             filteredNode,
             uctx);

        PadRable pad 
            = new ConcretePadRable
                (in,
                 new Rectangle2D.Double(0, 0, 0, 0),
                 PadMode.ZERO_PAD){
            public Rectangle2D getBounds2D(){
                setPadRect(dispArea.getRegion());
                return super.getBounds2D();
            }

            public java.awt.image.RenderedImage createRendering(java.awt.image.renderable.RenderContext rc){
                setPadRect(dispArea.getRegion());
                return super.createRendering(rc);
            }
        };

        // Build filter
        Vector sources = new Vector();
        sources.addElement(pad);
        sources.addElement(in2);
        filter 
            = new ConcreteDisplacementMapRable(sources, scale,
                                               xChannelSelector, 
                                               yChannelSelector);

        // Get result attribute if any
        String result
            = filterElement.getAttributeNS(null,
                                           ATTR_RESULT);
        if((result != null) && (result.trim().length() > 0)){
            // The filter will be added to the filter map. Before
            // we do that, append the filter region crop
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

    private ARGBChannel computeChannelSelector
        (String channelSelectorStr){
        ARGBChannel channelSelector = ARGBChannel.R;

        if(channelSelectorStr.length() == 1){
            char xChar = channelSelectorStr.charAt(0);
            switch(xChar){
            case 'R':
                break;
            case 'G':
                channelSelector = ARGBChannel.G;
                break;
            case 'B':
                channelSelector = ARGBChannel.B;
                break;
            case 'A':
                channelSelector = ARGBChannel.A;
                break;
            default:
                throw new IllegalArgumentException("Illegal Channel: " + xChar);
            }
        }
        else if(channelSelectorStr.length() != 0){
            throw new IllegalArgumentException();
        }

        return channelSelector;
    }
}
