/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.FilterPrimitiveBridge;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.MissingAttributeException;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.Light;
import org.apache.batik.gvt.filter.DiffuseLightingRable;
import org.apache.batik.gvt.filter.DistantLight;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.PadMode;
import org.apache.batik.gvt.filter.PadRable;

import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.gvt.filter.ConcreteDiffuseLightingRable;
import org.apache.batik.gvt.filter.ConcretePadRable;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * This class bridges an SVG <tt>feDiffuseLighting</tt> element with
 * a concrete <tt>Filter</tt> filter implementation
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFeDiffuseLightingElementBridge
        implements FilterPrimitiveBridge, SVGConstants {

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
        Filter filter = null;

        GraphicsNodeRenderContext rc = 
                         bridgeContext.getGraphicsNodeRenderContext();

        // First, extract source
        String inAttr = filterElement.getAttributeNS(null, SVG_IN_ATTRIBUTE);
        in = CSSUtilities.getFilterSource(filteredNode,
                                          inAttr,
                                          bridgeContext,
                                          filteredElement,
                                          in,
                                          filterMap);

        // Exit if no 'in' found
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

        if (in == sourceGraphics){
            defaultRegion = filterRegion;
        }

        CSSStyleDeclaration cssDecl
            = bridgeContext.getViewCSS().getComputedStyle(filterElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D primitiveRegion
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filterRegion,
                                                        filteredNode,
                                                        rc,
                                                        uctx);


        // Extract the light color
        Color color = CSSUtilities.convertLightingColor(cssDecl);

        // Extract the Light from the child node.
        NodeList children = filterElement.getChildNodes();
        int nChildren = children.getLength();
        if(nChildren < 1){
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feDiffuseLighting.child.missing",
                                       null));
        }

        Node lightNode = children.item(0);
        if((lightNode == null) || 
           !(lightNode.getNodeType() == Node.ELEMENT_NODE)){
            throw new IllegalAttributeValueException(
                Messages.formatMessage("feDiffuseLighting.child.missing",
                                       null));
        }

        Light light = SVGLightElementBridge.createLight((Element)lightNode, color);

        // Extract diffuse lighting constant
        String kdStr =
            filterElement.getAttributeNS(null, SVG_DIFFUSE_CONSTANT_ATTRIBUTE);

        double kd = 
            SVGUtilities.convertSVGNumber(SVG_DIFFUSE_CONSTANT_ATTRIBUTE, kdStr);
        
        // Extract surface scale
        String surfaceScaleStr =
            filterElement.getAttributeNS(null, SVG_SURFACE_SCALE_ATTRIBUTE);

        double surfaceScale = 
            SVGUtilities.convertSVGNumber(SVG_SURFACE_SCALE_ATTRIBUTE, 
                                          surfaceScaleStr);

        filter = new ConcreteDiffuseLightingRable(in,
                                                  primitiveRegion,
                                                  light,
                                                  kd,
                                                  surfaceScale);

        // Get result attribute and update map
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
        if ((result != null) && (result.trim().length() > 0)) {
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
