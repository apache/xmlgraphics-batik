/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * A factory for the &lt;svg&gt; SVG element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGSVGElementBridge implements GraphicsNodeBridge, SVGConstants {

    public GraphicsNode createGraphicsNode(BridgeContext ctx,
                                           Element element){
        SVGElement svgElement = (SVGElement) element;
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              cssDecl);
        CanvasGraphicsNode node
            = ctx.getGVTFactory().createCanvasGraphicsNode();
        node.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                              RenderingHints.VALUE_ANTIALIAS_ON);

        if (svgElement.getOwnerSVGElement() != null) {
            String s = svgElement.getAttributeNS(null, ATTR_X);
            float x = UnitProcessor.svgToUserSpace(s,
                                                   svgElement,
                                               UnitProcessor.HORIZONTAL_LENGTH,
                                                   uctx);
            s = svgElement.getAttributeNS(null, ATTR_Y);
            float y = UnitProcessor.svgToUserSpace(s,
                                                   svgElement,
                                               UnitProcessor.VERTICAL_LENGTH,
                                                   uctx);
            s = svgElement.getAttributeNS(null, ATTR_WIDTH);
            float w = UnitProcessor.svgToUserSpace(s,
                                                   svgElement,
                                               UnitProcessor.HORIZONTAL_LENGTH,
                                                   uctx);
            s = svgElement.getAttributeNS(null, ATTR_HEIGHT);
            float h = UnitProcessor.svgToUserSpace(s,
                                                   svgElement,
                                               UnitProcessor.VERTICAL_LENGTH,
                                                   uctx);
            AffineTransform at;
            at = SVGUtilities.getPreserveAspectRatioTransform
                (svgElement, w, h, ctx.getParserFactory());
            at.translate(x, y);
            node.setTransform(at);

            node.setClippingArea(new Rectangle2D.Float(x, y, w, h));
        }

        return node;
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return true;
    }
}
