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
import org.apache.batik.gvt.filter.CompositeRable;
import org.apache.batik.gvt.filter.CompositeRule;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.refimpl.gvt.filter.ConcreteCompositeRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>feMerge</tt> element with
 * a concrete <tt>Filter</tt> filter implementation
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class SVGFeMergeElementBridge implements FilterBridge,
                                                SVGConstants{

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

        // Extract sources, they are defined in the filterElement's
        // children.
        NodeList children = filterElement.getChildNodes();
        int nChildren = children.getLength();
        Filter [] srcs = new Filter[nChildren];

        int count = 0;
        for (int i=0; i<nChildren; i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element elt = (Element)child;
            if (!elt.getNodeName().equals(TAG_FE_MERGE_NODE))
                continue;

            String inAttr = elt.getAttributeNS(null, ATTR_IN);
            Filter tmp;
            tmp = CSSUtilities.getFilterSource(filteredNode, inAttr, 
                                               bridgeContext, 
                                               elt, in, filterMap);
            if (tmp == null) continue;
            srcs[count++] = in = tmp;
        }

        if (count == 0)
            return null;

        if (count != nChildren) {
            Filter [] tmp = new Filter[count];
            System.arraycopy(srcs, 0, tmp, 0, count);
            srcs=tmp;
        }

        //
        // The default region is the input sources regions union
        //
        Rectangle2D defaultRegion = srcs[0].getBounds2D();
        for(int i=1; i<srcs.length; i++){
            defaultRegion.add(srcs[i].getBounds2D());
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
        
        // Now, do the Merge.
        Vector srcsVec = new Vector(count);
        for (int i=0; i<count; i++)
            srcsVec.add(srcs[i]);

        Filter filter = null;
        filter = new ConcreteCompositeRable(srcsVec, CompositeRule.OVER);
        
        filter = new ConcretePadRable(filter,
                                      primitiveRegion,
                                      PadMode.ZERO_PAD);;
        
        
        // Get result attribute and update map
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
