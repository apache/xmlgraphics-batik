/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.io.StringReader;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * A factory for the &lt;rect> SVG element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public abstract class SVGShapeElementBridge implements GraphicsNodeBridge,
                                                       SVGConstants {

    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element element){
        SVGElement svgElement = (SVGElement) element;
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              cssDecl);
        ShapeNode node = ctx.getGVTFactory().createShapeNode();
        // Initialize the shape of the ShapeNode
        node.setShape(createShape(ctx, svgElement, cssDecl, uctx));
        // Initialize the style properties
        ShapePainter painter
            = CSSUtilities.convertStrokeAndFill(svgElement, ctx, cssDecl, uctx);
        node.setShapePainter(painter);
        // Initialize the transform
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(element.getAttributeNS(null, ATTR_TRANSFORM)),
             ctx.getParserFactory());
        node.setTransform(at);

        // Set node composite
        CSSPrimitiveValue opacityVal = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue(ATTR_OPACITY);
        Composite composite = CSSUtilities.convertOpacityToComposite(opacityVal);
        node.setComposite(composite);

        // Set node filter
        Filter filter = CSSUtilities.convertFilter(element, node, ctx);
        node.setFilter(filter);

        Mask mask = CSSUtilities.convertMask(element, node, ctx);
        node.setMask(mask);

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, element);
        ctx.bind(element, node);

        return node;
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }

    /**
     * Creates the shape depending on the specified context and element.
     */
    protected abstract Shape createShape(BridgeContext ctx,
                                         SVGElement svgElement,
                                         CSSStyleDeclaration decl,
                                         UnitProcessor.Context uctx);
}
