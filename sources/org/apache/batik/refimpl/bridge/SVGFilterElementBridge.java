/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterChainRable;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.refimpl.gvt.filter.ConcreteFilterChainRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>filter</tt> element with a concrete
 * <tt>Filter</tt>.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFilterElementBridge implements FilterBridge, SVGConstants {
    /**
     * Returns the <tt>Filter</tt> that implements the filter
     * operation modeled by the input DOM element
     *
     * @param filteredNode the node to which the filter will be attached.
     * @param bridgeContext the context to use.
     * @param filterElement DOM element that represents a filter abstraction
     * @param filteredElement DOM element that is filtered.
     * @param in the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
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
        // Make the initial source as a RenderableImage
        GraphicsNodeRableFactory gnrFactory
            = bridgeContext.getGraphicsNodeRableFactory();
        GraphicsNodeRable sourceGraphic
            = gnrFactory.createGraphicsNodeRable(filteredNode);

        // Get the filter region and resolution
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement,
                                                          null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext,
                                              cssDecl);

        Rectangle2D filterRegionRect =
            SVGUtilities.convertFilterChainRegion
            (filterElement,
             filteredElement,
             filteredNode,
             uctx);

        filterRegion = new DummyFilterRegion(filterRegionRect);

        // Build a ConcreteFilterChainRable
        FilterChainRable filterChain
            = new ConcreteFilterChainRable(sourceGraphic, filterRegionRect);


        // Get filter resolution. -1 means undefined.
        float filterResolutionX = -1;
        float filterResolutionY = -1;
        StringTokenizer st = new StringTokenizer(filterElement.getAttributeNS(null, ATTR_FILTER_RES));
        if(st.countTokens()>0){
            // Get resolution along the x-axis
            String filterResolutionXStr = st.nextToken();
            try{
                filterResolutionX = Float.parseFloat(filterResolutionXStr);
            }catch(NumberFormatException e){}

            if(st.hasMoreTokens()){
                try{
                    filterResolutionY = Float.parseFloat((String)st.nextElement());
                }catch(NumberFormatException e){}
            }
        }

        // Set resolution in filterChain
        filterChain.setFilterResolutionX((int)filterResolutionX);
        filterChain.setFilterResolutionY((int)filterResolutionY);

        //
        // Now, build filter chain
        //

        // Create a map for filter nodes to advertise themselves as named
        // sources.
        Map filterNodeMap = new Hashtable();

        NodeList childList = filterElement.getChildNodes();
        if(in == null){
            in = sourceGraphic;  // For the filter element, the in parameter is overridden
            filterNodeMap.put(VALUE_SOURCE_GRAPHIC, sourceGraphic);
        }

        if(childList != null){
            int nChildren = childList.getLength();
            for(int i=0; i<nChildren; i++){
                Node child = childList.item(i);
                if(child instanceof Element){
                    FilterBridge bridge
                        = (FilterBridge)bridgeContext.getBridge((Element)child);

                    if(bridge != null){
                        // If we have a bridge, ask it to create a
                        // filter node.
                        Filter filterNode
                            = bridge.create(filteredNode,
                                            bridgeContext,
                                            (Element)child,
                                            filteredElement,
                                            in,
                                            filterRegion,
                                            filterNodeMap);

                        // Update in if we were able to create a
                        // child node.
                        if(filterNode != null){
                            in = filterNode;
                        }
                        else{
                            System.out.println("Filter bridge could not bridge element: " + ((Element)child).getNodeName());
                        }
                    }
                    else{
                        System.out.println("Could not find bridge for " + ((Element)child).getNodeName());
                    }
                }
            }
        }

        // Set the source on the filter node
        if(in != sourceGraphic){
            filterChain.setSource(in);
        }
        else{
            // No child filter node. Disable filter
            filterChain.setSource(null);
        }
        return filterChain;

    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}

class DummyFilterRegion implements FilterRegion{
    Rectangle2D r;

    public Rectangle2D getRegion(){
        return (Rectangle2D)r.clone();
    }

    public DummyFilterRegion(Rectangle2D r){
        this.r = r;
    }
}
