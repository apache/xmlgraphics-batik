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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterPrimitiveBridge;
import org.apache.batik.bridge.IllegalAttributeValueException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.CompositeRable;
import org.apache.batik.gvt.filter.CompositeRule;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.PadMode;

import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.refimpl.gvt.filter.ConcreteCompositeRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>feMerge</tt> element with
 * a concrete <tt>Filter</tt> filter implementation
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeMergeElementBridge implements FilterPrimitiveBridge,
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

        GraphicsNodeRenderContext rc = 
                     bridgeContext.getGraphicsNodeRenderContext();

        // Extract sources, they are defined in the filterElement's children.
        List srcs = new LinkedList();
        for(Node child=filterElement.getFirstChild();
                 child != null;
                 child = child.getNextSibling()) {

            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue; // skip node that is not an Element
            }

            Element elt = (Element)child;
            String namespaceURI = elt.getNamespaceURI();
            if (namespaceURI == null ||
                    !namespaceURI.equals(SVG_NAMESPACE_URI)) {
                continue; // skip element in the wrong namespace
            }
            if (!elt.getLocalName().equals(SVG_FE_MERGE_NODE_TAG)) {
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("feMerge.subelement.invalid",
                                           new Object[] {elt.getLocalName()}));
            }

            String inAttr = elt.getAttributeNS(null, SVG_IN_ATTRIBUTE);
            Filter tmp = CSSUtilities.getFilterSource(filteredNode,
                                                      inAttr,
                                                      bridgeContext,
                                                      elt,
                                                      in,
                                                      filterMap);
            if (tmp == null) {
                continue;
            }
            in = tmp;
            srcs.add(in);
        }

        if (srcs.size() == 0) { // no subelement found
            // <!> FIXME :  the result is unspecified
            return null;
        }

        // The default region is the input sources regions union
        Iterator iter = srcs.iterator();
        Rectangle2D defaultRegion = ((Filter) iter.next()).getBounds2D();
        while (iter.hasNext()) {
            defaultRegion.add(((Filter) iter.next()).getBounds2D());
        }

        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filteredNode,
                                                        rc,
                                                        uctx);

        Filter filter = null;
        filter = new ConcreteCompositeRable(srcs, CompositeRule.OVER);

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
