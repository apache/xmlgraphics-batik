/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.Rect;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

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
        CSSStyleDeclaration cssDecl = CSSUtilities.getComputedStyle(element);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              cssDecl);
        CanvasGraphicsNode node = new CanvasGraphicsNode();
        // visibility
        node.setVisible(CSSUtilities.convertVisibility(element));
        float x;
        float y;
        float w;
        float h;
        String s;
        if (svgElement.getOwnerSVGElement() != null) {
            // parse the x attribute, (default is 0)
            s = svgElement.getAttributeNS(null, SVG_X_ATTRIBUTE);
            x = 0;
            if (s.length() != 0) {
               x = SVGUtilities.svgToUserSpace(svgElement,
                                               SVG_X_ATTRIBUTE, s,
                                               uctx,
                                               UnitProcessor.HORIZONTAL_LENGTH);
            }

            // parse the y attribute, (default is 0)
            s = svgElement.getAttributeNS(null, SVG_Y_ATTRIBUTE);
            y = 0;
            if (s.length() != 0) {
                y = SVGUtilities.svgToUserSpace(svgElement,
                                                SVG_Y_ATTRIBUTE, s,
                                                uctx,
                                                UnitProcessor.VERTICAL_LENGTH);
            }
        } else {
            x = 0;
            y = 0;
        }

        // parse the width attribute (default is 100%)
        s = svgElement.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        if (s.length() == 0) {
            s = "100%";
        }
        w = SVGUtilities.svgToUserSpace(svgElement,
                                        SVG_WIDTH_ATTRIBUTE, s,
                                        uctx,
                                        UnitProcessor.HORIZONTAL_LENGTH);

        // parse the height attribute (default is 100%)
        s = svgElement.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
        if (s.length() == 0) {
            s = "100%";
        }
        h = SVGUtilities.svgToUserSpace(svgElement,
                                        SVG_HEIGHT_ATTRIBUTE, s,
                                        uctx,
                                        UnitProcessor.VERTICAL_LENGTH);

        // parse the tranform attribute
        AffineTransform at;
        at = SVGUtilities.getPreserveAspectRatioTransform
            (svgElement, w, h);
        at.preConcatenate(AffineTransform.getTranslateInstance(x, y));

        Shape clip = null;
        if (svgElement.getOwnerSVGElement() != null) {
            // <!> as it is already done in the JSVGCanvas, we don't have to
            // do it for the top most svg element
            node.setTransform(at);
            // Get the overflow property on the svg element
            if (CSSUtilities.convertOverflow(svgElement)) { // hidden
                float [] offsets = CSSUtilities.convertClip(svgElement);
                if (offsets == null) { // auto
                    clip = new Rectangle2D.Float(x, y, w, h);
                } else { // clip
                    // offsets[0] = top
                    // offsets[1] = right
                    // offsets[2] = bottom
                    // offsets[3] = left
                    clip = new Rectangle2D.Float(x+offsets[3],
                                                 y+offsets[0],
                                                 w-offsets[1],
                                                 h-offsets[2]);
                }
            }
        } else {
            clip = new Rectangle2D.Float(x, y, w, h);
        }
        if (clip != null) {
            try {
                at = at.createInverse(); // clip in user space
                clip = at.createTransformedShape(clip);
                GraphicsNodeRableFactory gnrFactory
                    = ctx.getGraphicsNodeRableFactory();
                Filter filter =
                    gnrFactory.createGraphicsNodeRable(node,
                                     ctx.getGraphicsNodeRenderContext());
                node.setClip(new ClipRable8Bit(filter, clip));
            } catch (NoninvertibleTransformException ex) {}
        }

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, svgElement);
        ctx.bind(element, node);

        ctx.setViewport(new SVGViewport(svgElement, uctx));
        return node;
    }

    public void buildGraphicsNode(GraphicsNode node,
                                  BridgeContext ctx,
                                  Element element) {

    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return true;
    }
}
