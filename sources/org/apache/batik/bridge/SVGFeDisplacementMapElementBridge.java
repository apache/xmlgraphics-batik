/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;

import java.util.Map;
import java.util.Vector;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.ext.awt.image.renderable.ARGBChannel;
import org.apache.batik.ext.awt.image.renderable.DisplacementMapRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadMode;
import org.apache.batik.ext.awt.image.renderable.PadRable;

import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.ext.awt.image.renderable.DisplacementMapRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>filter</tt> element with a concrete
 * <tt>Filter</tt>
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com">Sheng Pei</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeDisplacementMapElementBridge implements FilterPrimitiveBridge,
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

        DocumentLoader loader = bridgeContext.getDocumentLoader();
        //
        // Extract standard deviation
        //
        String scaleStr = filterElement.getAttributeNS(null, ATTR_SCALE);
        double scale = 0; // default is 0
        if (scaleStr.length() != 0) {
            scale = SVGUtilities.convertSVGNumber(ATTR_SCALE, scaleStr);
        }

        String xChannelSelectorStr
            = filterElement.getAttributeNS(null, SVG_X_CHANNEL_SELECTOR_ATTRIBUTE);

        ARGBChannel xChannelSelector =
            computeChannelSelector(xChannelSelectorStr);

        String yChannelSelectorStr
            = filterElement.getAttributeNS(null, SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE);

        ARGBChannel yChannelSelector
            = computeChannelSelector(yChannelSelectorStr);

        //
        // Build filter
        //
        Filter filter = null;

        // Get source 1
        String inAttr = filterElement.getAttributeNS(null, SVG_IN_ATTRIBUTE);
        Filter in1 = CSSUtilities.getFilterSource(filteredNode,
                                                  inAttr,
                                                  bridgeContext,
                                                  filteredElement,
                                                  in,
                                                  filterMap);

        Filter in2 = null;

        // Get source 2
        String in2Attr = filterElement.getAttributeNS(null, SVG_IN2_ATTRIBUTE);
        if (in2Attr.length() == 0) {
            throw new MissingAttributeException(
                Messages.formatMessage("feDisplacementMap.in2.required", null));
        }
        in2 = CSSUtilities.getFilterSource(filteredNode,
                                           in2Attr,
                                           bridgeContext,
                                           filteredElement,
                                           in,
                                           filterMap);

        //
        // The default region is the union of the input sources bounds
        // unless in in is SourceGraphic, in which case the default is
        // the filter chain's region
        //
        Filter sourceGraphics = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);

        Rectangle2D defaultRegion = in1.getBounds2D();
        defaultRegion.add(in2.getBounds2D());

        if(in1 == sourceGraphics){
            defaultRegion = filterRegion;
        }

        CSSStyleDeclaration cssDecl
            = CSSUtilities.getComputedStyle(filterElement);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D dispArea
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filterRegion,
                                                        filteredNode,
                                                        rc,
                                                        uctx,
                                                        loader);

        PadRable pad = new PadRable8Bit(in, dispArea, PadMode.ZERO_PAD);

        // Build filter
        Vector sources = new Vector();
        sources.addElement(pad);
        sources.addElement(in2);
        filter = new DisplacementMapRable8Bit(sources,
                                                  scale,
                                                  xChannelSelector,
                                                  yChannelSelector);

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

    private static ARGBChannel computeChannelSelector(String value) {
        ARGBChannel channelSelector;
        if (value.length() == 0) {
            channelSelector = ARGBChannel.A; // default value
        } else if (SVG_A_VALUE.equals(value)) {
            channelSelector = ARGBChannel.A;
        } else if (SVG_R_VALUE.equals(value)) {
            channelSelector = ARGBChannel.R;
        } else if (SVG_G_VALUE.equals(value)) {
            channelSelector = ARGBChannel.G;
        } else if (SVG_B_VALUE.equals(value)) {
            channelSelector = ARGBChannel.B;
        } else {
            throw new IllegalAttributeValueException(
                Messages.formatMessage(
                    "feDisplacementMap.channelSelector.invalid",
                    new Object[] { value }));

        }
        return channelSelector;
    }
}
