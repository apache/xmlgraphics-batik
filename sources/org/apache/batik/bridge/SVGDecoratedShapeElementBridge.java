/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.gvt.DecoratedShapeNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.Marker;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.ext.awt.image.renderable.Clip;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.parser.ParseException;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * A factory for the SVG elements that represents a shape.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class SVGDecoratedShapeElementBridge 
    extends SVGShapeElementBridge {
    
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element element){
        SVGElement svgElement = (SVGElement) element;
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              cssDecl);

        DecoratedShapeNode node = new DecoratedShapeNode();

        // Initialize the transform
        AffineTransform at =
            SVGUtilities.convertAffineTransform(element,
                                                ATTR_TRANSFORM);
        node.setTransform(at);
        // Initialize the shape of the ShapeNode
        buildShape(ctx, svgElement, node, cssDecl, uctx);

        return node;
    }

    public void buildGraphicsNode(GraphicsNode gn, 
                                  BridgeContext ctx,
                                  Element element) {
        DecoratedShapeNode node = (DecoratedShapeNode)gn;

        SVGElement svgElement = (SVGElement) element;
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              cssDecl);

        //
        // Extract the marker properties
        //
        

        // Extract start, middle and end markers
        Marker startMarker 
            = CSSUtilities.convertMarker(svgElement,
                                         CSS_MARKER_START_PROPERTY,
                                         ctx, cssDecl, uctx);
        node.setStartMarker(startMarker);
        
        Marker endMarker 
            = CSSUtilities.convertMarker(svgElement,
                                         CSS_MARKER_END_PROPERTY,
                                         ctx, cssDecl, uctx);
        node.setEndMarker(endMarker);
        
        Marker middleMarker 
            = CSSUtilities.convertMarker(svgElement,
                                         CSS_MARKER_MID_PROPERTY,
                                         ctx, cssDecl, uctx);
        
        node.setMiddleMarker(middleMarker);


        super.buildGraphicsNode(gn, ctx, element);
    }
}
