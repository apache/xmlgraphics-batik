/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.ext.awt.image.renderable.AffineRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterChainRable;
import org.apache.batik.ext.awt.image.renderable.PadMode;
import org.apache.batik.ext.awt.image.renderable.PadRable;

import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;

import org.apache.batik.util.SVGConstants;
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
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGFeOffsetElementBridge implements FilterPrimitiveBridge,
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

        // parse the dx attribute
        String dxAttr = filterElement.getAttributeNS(null, SVG_DX_ATTRIBUTE);
        float dx = 0; // default is 0
        if (dxAttr.length() != 0) {
            dx = SVGUtilities.convertSVGNumber(SVG_DX_ATTRIBUTE, dxAttr);
        }

        // parse the dy attribute
        String dyAttr = filterElement.getAttributeNS(null, SVG_DY_ATTRIBUTE);
        float dy = 0; // default is 0
        if (dyAttr.length() != 0) {
            dy = SVGUtilities.convertSVGNumber(SVG_DY_ATTRIBUTE, dyAttr);
        }

        AffineTransform offsetTransform =
            AffineTransform.getTranslateInstance(dx, dy);

        // Get source
        String inAttr = filterElement.getAttributeNS(null, SVG_IN_ATTRIBUTE);
        in = CSSUtilities.getFilterSource(filteredNode,
                                          inAttr,
                                          bridgeContext,
                                          filteredElement,
                                          in,
                                          filterMap);

        // feOffset is a point operation. Therefore, to take the
        // filter primitive region into account, only a pad operation
        // on the input is required.

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
            = CSSUtilities.getComputedStyle(filterElement);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        Rectangle2D offsetArea
            = SVGUtilities.convertFilterPrimitiveRegion(filterElement,
                                                        filteredElement,
                                                        defaultRegion,
                                                        filterRegion,
                                                        filteredNode,
                                                        rc,
                                                        uctx,
                                                        loader);

        PadRable pad = new PadRable8Bit(in,
                                            offsetArea,
                                            PadMode.ZERO_PAD);

        // Create the AffineRable that maps the input filter node
        AffineRable offset = new AffineRable8Bit(pad, offsetTransform);

        // Get result attribute if any
        String result = filterElement.getAttributeNS(null, ATTR_RESULT);
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
