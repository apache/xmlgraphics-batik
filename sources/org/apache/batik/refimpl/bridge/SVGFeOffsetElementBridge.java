/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterBridge;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.AffineRable;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.FilterChainRable;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;
import org.apache.batik.refimpl.gvt.filter.ConcreteAffineRable;
import org.apache.batik.refimpl.gvt.filter.ConcretePadRable;
import org.apache.batik.refimpl.gvt.filter.FilterSourceRegion;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.views.DocumentView;

/**
 * This class bridges an SVG <tt>filter</tt> element with a concrete
 * <tt>Filter</tt>.
 *
 * @author <a href="mailto:dean@w3.org">Dean Jackson</a>
 * @version $Id$
 */
public class SVGFeOffsetElementBridge implements FilterBridge, SVGConstants {
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

        System.out.println("In Offset Bridge");

        // Extract dx attribute
        String dxAttr = filterElement.getAttributeNS(null,
                                                     ATTR_DX);
        float dx = DEFAULT_VALUE_DX;
        try {
            dx = SVGUtilities.convertSVGNumber(dxAttr);
        } catch (NumberFormatException e) {
            // use default
        }

        // Extract dy attribute
        String dyAttr = filterElement.getAttributeNS(null,
                                                     ATTR_DY);
        float dy = DEFAULT_VALUE_DY;
        try {
            dy = SVGUtilities.convertSVGNumber(dyAttr);
        } catch (NumberFormatException e) {
            // use default
        }

        AffineTransform offsetTransform =
            AffineTransform.getTranslateInstance(dx, dy);

        // Get source
        String inAttr = filterElement.getAttributeNS(null, ATTR_IN);
        in = CSSUtilities.getFilterSource(filteredNode, inAttr, bridgeContext, 
                                          filteredElement,
                                          in, filterMap);

        System.out.println("In: " + in);
        // feOffset is a point operation. Therefore, to take the
        // filter primitive region into account, only a pad operation
        // on the input is required.

        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement,
                                                          null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext,
                                              cssDecl);

        // Get default filter region. Source for feOffset
        FilterRegion defaultRegion = new FilterSourceRegion(in);

        // Get unit. Comes from parent node.
        Node parentNode = filterElement.getParentNode();
        String units = VALUE_USER_SPACE_ON_USE;
        if((parentNode != null)
           &&
           (parentNode.getNodeType() == parentNode.ELEMENT_NODE)){
            units = ((Element)parentNode).getAttributeNS(null, ATTR_PRIMITIVE_UNITS);
        }

        final FilterRegion offsetArea
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        units,
                                                        filteredNode,
                                                        uctx);

        PadRable pad = new ConcretePadRable(in,
                                            offsetArea.getRegion(),
                                            PadMode.ZERO_PAD){
                public Rectangle2D getBounds2D(){
                    setPadRect(offsetArea.getRegion());
                    return super.getBounds2D();
                }

                public java.awt.image.RenderedImage createRendering(java.awt.image.renderable.RenderContext rc){
                    setPadRect(offsetArea.getRegion());
                    return super.createRendering(rc);
                }
            };

        // Create the AffineRable that maps the input filter node
        AffineRable offset = new ConcreteAffineRable(pad, offsetTransform);

        // Get result attribute if any
        String result = filterElement.getAttributeNS(null,
                                                     ATTR_RESULT);
        if((result != null) && (result.trim().length() > 0)){
            filterMap.put(result, offset);
        }

        return offset;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}
