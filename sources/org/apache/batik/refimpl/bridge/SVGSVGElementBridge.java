/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGViewport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;

import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.refimpl.gvt.filter.ConcreteClipRable;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;
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
        SVGSVGElement svgElement = (SVGSVGElement) element;
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              cssDecl);
        CanvasGraphicsNode node
            = ctx.getGVTFactory().createCanvasGraphicsNode();
        float x = 0;
        float y = 0;
        float w;
        float h;
        String s;
        if (svgElement.getOwnerSVGElement() != null) {
            s = svgElement.getAttributeNS(null, ATTR_X);
            x = UnitProcessor.svgToUserSpace(s,
                                             svgElement,
                                             UnitProcessor.HORIZONTAL_LENGTH,
                                             uctx);
            s = svgElement.getAttributeNS(null, ATTR_Y);
            y = UnitProcessor.svgToUserSpace(s,
                                             svgElement,
                                             UnitProcessor.VERTICAL_LENGTH,
                                             uctx);
        }
        s = svgElement.getAttributeNS(null, ATTR_WIDTH);
        w = UnitProcessor.svgToUserSpace(s,
                                         svgElement,
                                         UnitProcessor.HORIZONTAL_LENGTH,
                                         uctx);
        s = svgElement.getAttributeNS(null, ATTR_HEIGHT);
        h = UnitProcessor.svgToUserSpace(s,
                                         svgElement,
                                         UnitProcessor.VERTICAL_LENGTH,
                                         uctx);
        AffineTransform at;
        at = SVGUtilities.getPreserveAspectRatioTransform
            (svgElement, w, h, ctx.getParserFactory());
        at.preConcatenate(AffineTransform.getTranslateInstance(x, y));
        if (svgElement.getOwnerSVGElement() != null) {
            // <!> as it is already done in the JSVGCanvas, we don't have to
            // do it for the top most svg element
            node.setTransform(at);
        }
        try {
            at = at.createInverse(); // clip in user space

            GraphicsNodeRableFactory gnrFactory
                = ctx.getGraphicsNodeRableFactory();

            Filter filter = gnrFactory.createGraphicsNodeRable(node);

            Shape clip = at.createTransformedShape
                (new Rectangle2D.Float(x, y, w, h));

            node.setClip(new ConcreteClipRable(filter, clip));

        } catch (java.awt.geom.NoninvertibleTransformException ex) {}

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, element);
        ctx.bind(element, node);

        ctx.setCurrentViewport(new SVGViewport(svgElement, uctx));
        return node;
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return true;
    }
}
