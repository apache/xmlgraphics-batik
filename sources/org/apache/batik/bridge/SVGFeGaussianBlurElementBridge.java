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

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterPrimitiveBridge;
import org.apache.batik.bridge.IllegalAttributeValueException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GaussianBlurRable;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;

import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.gvt.filter.ConcreteGaussianBlurRable;
import org.apache.batik.gvt.filter.ConcretePadRable;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>filter</tt> element with a concrete
 * <tt>Filter</tt>
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeGaussianBlurElementBridge implements FilterPrimitiveBridge,
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

        // Extract standard deviation
        String stdDeviation =
            filterElement.getAttributeNS(null, ATTR_STD_DEVIATION);

        Float deviationPair[] = SVGUtilities.buildFloatPair(stdDeviation);

        // parse the stdDeviationX
        float stdDeviationX = 0; // default is 0
        if (deviationPair[0] != null) {
            stdDeviationX = deviationPair[0].floatValue();
        }
        if (stdDeviationX == 0) {
            // <!> FIXME :  the result is a fully transparent image
            // A value of zero disables the effect of the filter primitive
            return null;
        } else if (stdDeviationX < 0) {
            // A negative value is an error
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feGaussianBlur.stdDeviationX.invalid",
                                       null));
        }

        // parse the stdDeviationY
        float stdDeviationY = stdDeviationX; // default is the stdDeviationX
        if (deviationPair[1] != null) {
            stdDeviationY = deviationPair[1].floatValue();
        }
        if (stdDeviationY == 0) {
            // <!> FIXME :  the result is a fully transparent image
            // A value of zero disables the effect of the filter primitive
            return null;
        } else if (stdDeviationY < 0) {
            // A negative value is an error
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feGaussianBlur.stdDeviationY.invalid",
                                       null));
        }

        // Get source
        String inAttr = filterElement.getAttributeNS(null, SVG_IN_ATTRIBUTE);
        in = CSSUtilities.getFilterSource(filteredNode,
                                          inAttr,
                                          bridgeContext,
                                          filteredElement,
                                          in,
                                          filterMap);

        if (in == null) {
            return null;
        }

        //
        // The default region is the input source's region unless the
        // source is SourceGraphics, in which case the default region
        // is the filter chain's region
        //
        Filter sourceGraphics = (Filter)filterMap.get(VALUE_SOURCE_GRAPHIC);

        Rectangle2D defaultRegion = in.getBounds2D();
        if (in == sourceGraphics) {
            defaultRegion = filterRegion;
        }

        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D blurArea
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filterRegion,
                                                        filteredNode,
                                                        rc,
                                                        uctx);

        PadRable pad = new ConcretePadRable(in, blurArea, PadMode.ZERO_PAD);

        // Build filter
        Filter filter = null;
        filter = new ConcreteGaussianBlurRable(pad,
                                               stdDeviationX,
                                               stdDeviationY);

        // Get result attribute if any
        String result = filterElement.getAttributeNS(null,ATTR_RESULT);
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
