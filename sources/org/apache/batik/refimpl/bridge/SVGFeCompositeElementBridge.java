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
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.refimpl.gvt.filter.ConcreteCompositeRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.refimpl.gvt.filter.FilterSourceRegion;
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
public class SVGFeCompositeElementBridge implements FilterBridge,
                                                            SVGConstants{

    public static CompositeRule getRule(Element filterElement) {

        String oper = filterElement.getAttributeNS(null, ATTR_OPERATOR);
        if (oper == null) 
            return CompositeRule.OVER;

        switch (oper.charAt(0)) {
        case 'a': case 'A':            
            switch (oper.charAt(1)) {
            case 't': case 'T':
                // 'atop'
                if (((oper.charAt(2) != 'o') && (oper.charAt(2) != 'O')) ||
                    ((oper.charAt(3) != 'p') && (oper.charAt(3) != 'P')))
                    return null;

                return CompositeRule.ATOP;

            case 'r': case 'R': {
                  // 'arithmetic
                if (((oper.charAt(2) != 'i') && (oper.charAt(2) != 'I')) ||
                    ((oper.charAt(3) != 't') && (oper.charAt(3) != 'T')) ||
                    ((oper.charAt(4) != 'h') && (oper.charAt(4) != 'H')) ||
                    ((oper.charAt(5) != 'm') && (oper.charAt(5) != 'M')) ||
                    ((oper.charAt(6) != 'e') && (oper.charAt(6) != 'E')) ||
                    ((oper.charAt(7) != 't') && (oper.charAt(7) != 'T')) ||
                    ((oper.charAt(8) != 'i') && (oper.charAt(8) != 'I')) ||
                    ((oper.charAt(9) != 'c') && (oper.charAt(9) != 'C')))
                    return null; // error 

                float k1=0, k2=0, k3=0, k4=0;

                String kAttr;
                kAttr = filterElement.getAttributeNS(null, ATTR_K1);
                if (kAttr != null) {
                    try {
                        k1 = SVGUtilities.convertSVGNumber(kAttr);
                    }  catch (NumberFormatException e) { }
                }
                    

                kAttr = filterElement.getAttributeNS(null, ATTR_K2);
                if (kAttr != null) {
                    try {
                        k2 = SVGUtilities.convertSVGNumber(kAttr);
                    }  catch (NumberFormatException e) { }
                }
                    

                kAttr = filterElement.getAttributeNS(null, ATTR_K3);
                if (kAttr != null) {
                    try {
                        k3 = SVGUtilities.convertSVGNumber(kAttr);
                    }  catch (NumberFormatException e) { }
                }
                    

                kAttr = filterElement.getAttributeNS(null, ATTR_K4);
                if (kAttr != null) {
                    try {
                        k4 = SVGUtilities.convertSVGNumber(kAttr);
                    }  catch (NumberFormatException e) { }
                }

                return CompositeRule.ARITHMETIC(k1, k2, k3, k4);
            }
            default:
                return null; // error
            }

        case 'i': case 'I':
              // in
            if ((oper.charAt(1) != 'n') && (oper.charAt(1) != 'N'))
                  return null; // error
            return CompositeRule.IN;

        case 'o': case 'O':
            switch (oper.charAt(1)) {
            case 'v': case 'V':
                  // 'over'
                if (((oper.charAt(2) != 'e') && (oper.charAt(2) != 'E')) ||
                    ((oper.charAt(3) != 'r') && (oper.charAt(3) != 'R')))
                    return null;
                return CompositeRule.OVER;

            case 'u': case'U':
                  // 'out'
                if ((oper.charAt(2) != 't') && (oper.charAt(2) != 'T'))
                    return null;
                return CompositeRule.OUT;

            default:
                return null;
            }

        case 'x': case 'X':
              // 'xor'
                if (((oper.charAt(1) != 'o') && (oper.charAt(1) != 'O')) ||
                    ((oper.charAt(2) != 'r') && (oper.charAt(2) != 'R')))
                      return null;
                return CompositeRule.XOR;

        default:
            return null;
        }
    }

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

        // Extract Composite operation
        CompositeRule rule = getRule(filterElement);
        if (rule == null) {
            String oper = filterElement.getAttributeNS(null, ATTR_OPERATOR);
            throw new IllegalArgumentException
                ("Unknown composite operator: " + oper);
        }
                                           
        // Extract sources
        String in1Attr = filterElement.getAttributeNS(null, ATTR_IN);
        Filter in1;
        in1 = CSSUtilities.getFilterSource(filteredNode, in1Attr, 
                                           bridgeContext, filteredElement,
                                           in, filterMap);

        String in2Attr = filterElement.getAttributeNS(null, ATTR_IN2);
        Filter in2;
        in2 = CSSUtilities.getFilterSource(filteredNode, in2Attr, 
                                           bridgeContext, filteredElement,
                                           in, filterMap);

        if ((in1 == null) || (in2 == null))
            return null;

        FilterRegion defaultRegion = new FilterSourceRegion
            (new Filter[] { in1, in2 });
        
          // Get unit. Comes from parent node.
        Node parentNode = filterElement.getParentNode();
        String units = VALUE_USER_SPACE_ON_USE;
        if((parentNode != null)
           && (parentNode.getNodeType() == parentNode.ELEMENT_NODE)) {
            units = ((Element)parentNode).
                getAttributeNS(null, ATTR_PRIMITIVE_UNITS);
        }
        
          //
          // Now, extraact filter region
          //
        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement,
                                                          null);
        
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext,
                                              cssDecl);
        
        final FilterRegion compositeArea
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        units,
                                                        filteredNode,
                                                        uctx);
        
          // Now, do the composite.
        Filter filter = null;
        Vector srcs = new Vector(2);
        srcs.add(in1);
        srcs.add(in2);
        filter = new ConcreteCompositeRable(srcs, rule);
        
        filter = new ConcretePadRable(filter,
                                      compositeArea.getRegion(),
                                      PadMode.ZERO_PAD) {
                public Rectangle2D getBounds2D(){
                    setPadRect(compositeArea.getRegion());
                    return super.getBounds2D();
                }
                
                public java.awt.image.RenderedImage createRendering
                    (java.awt.image.renderable.RenderContext rc){
                    setPadRect(compositeArea.getRegion());
                    return super.createRendering(rc);
                }
            };
        
        
          // Get result attribute and update map
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
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

}
