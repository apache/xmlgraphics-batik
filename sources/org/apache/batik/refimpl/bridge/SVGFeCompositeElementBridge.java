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
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.MissingAttributeException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.CompositeRable;
import org.apache.batik.gvt.filter.CompositeRule;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.PadMode;

import org.apache.batik.refimpl.bridge.resources.Messages;
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
 * This class bridges an SVG <tt>feComposite</tt> element with
 * a concrete <tt>Filter</tt> filter implementation
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class SVGFeCompositeElementBridge implements FilterBridge, SVGConstants {


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

        // Extract Composite operation
        CompositeRule rule = getRule(filterElement);

        // Extract sources
        String in1Attr = filterElement.getAttributeNS(null, ATTR_IN);
        Filter in1;
        in1 = CSSUtilities.getFilterSource(filteredNode,
                                           in1Attr,
                                           bridgeContext,
                                           filteredElement,
                                           in,
                                           filterMap);

        String in2Attr = filterElement.getAttributeNS(null, ATTR_IN2);
        if (in2Attr.length() == 0) {
            throw new MissingAttributeException(
                Messages.formatMessage("feComposite.in2.required", null));
        }
        Filter in2;
        in2 = CSSUtilities.getFilterSource(filteredNode,
                                           in2Attr,
                                           bridgeContext,
                                           filteredElement,
                                           in,
                                           filterMap);

        if ((in1 == null) || (in2 == null)) {
            return null;
        }

        //
        // The default region is the union of the input sources
        // regions unless 'in' is 'SourceGraphic' in which case the
        // default region is the filterChain's region
        //
        Filter sourceGraphics = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);

        Rectangle2D defaultRegion = in1.getBounds2D();
        defaultRegion.add(in2.getBounds2D());

        if(in1 == sourceGraphics) {
            defaultRegion = filterRegion;
        }

        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D compositeArea
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filteredNode,
                                                        uctx);

        // Now, do the composite.
        Filter filter = null;
        Vector srcs = new Vector(2);
        srcs.add(in2);
        srcs.add(in1);
        filter = new ConcreteCompositeRable(srcs, rule);

        filter = new ConcretePadRable(filter, compositeArea, PadMode.ZERO_PAD);

        // Get result attribute and update map
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
        if((result != null) && (result.trim().length() > 0)){
            filterMap.put(result, filter);
        }

        return filter;
    }

    private static CompositeRule getRule(Element filterElement) {
        String ruleStr = filterElement.getAttributeNS(null, ATTR_OPERATOR);
        CompositeRule rule;

        if (ruleStr.length() == 0) {
            rule = CompositeRule.OVER; // default value

        } else if (VALUE_ATOP.equals(ruleStr)) {
            rule = CompositeRule.ATOP;

        } else if (VALUE_ARITHMETIC.equals(ruleStr)) {
            String kAttr;
            float k1=0, k2=0, k3=0, k4=0;

            kAttr = filterElement.getAttributeNS(null, ATTR_K1);
            if (kAttr.length() != 0) {
                k1 = SVGUtilities.convertSVGNumber(kAttr);
            }

            kAttr = filterElement.getAttributeNS(null, ATTR_K2);
            if (kAttr.length() != 0) {
                k2 = SVGUtilities.convertSVGNumber(kAttr);
            }

            kAttr = filterElement.getAttributeNS(null, ATTR_K3);
            if (kAttr.length() != 0) {
                k3 = SVGUtilities.convertSVGNumber(kAttr);
            }

            kAttr = filterElement.getAttributeNS(null, ATTR_K4);
            if (kAttr.length() != 0) {
                k4 = SVGUtilities.convertSVGNumber(kAttr);
            }
            rule = CompositeRule.ARITHMETIC(k1, k2, k3, k4);

        } else if (VALUE_IN.equals(ruleStr)) {
            rule = CompositeRule.IN;

        } else if (VALUE_OVER.equals(ruleStr)) {
            rule = CompositeRule.OVER;

        } else if (VALUE_OUT.equals(ruleStr)) {
            rule = CompositeRule.OUT;

        } else if (VALUE_XOR.equals(ruleStr)) {
            rule = CompositeRule.XOR;

        } else {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feComposite.operator.invalid",
                                       new Object[] { ruleStr }));
        }
        return rule;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

}
