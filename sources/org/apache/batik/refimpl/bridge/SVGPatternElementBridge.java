/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Paint;
import java.awt.geom.AffineTransform;

import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.FilterRegion;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;

import org.apache.batik.refimpl.gvt.ConcretePatternPaint;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * This class bridges an SVG <tt>pattern</tt> element with
 * a <tt>PatternPaint</tt>
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGPatternElementBridge implements PaintBridge, SVGConstants {

    /**
     * Creates a <tt>Paint</tt> used to draw the outline of a
     * <tt>Shape</tt> of a <tt>ShapeNode</tt>.
     * @param ctx the context to use
     * @param paintedElement the Element with 'stroke' and
     * 'stroke-opacity' attributes.
     * @param paintElement teh Element which contains the paint's definition
     */
    public Paint createStrokePaint(BridgeContext ctx,
                                   GraphicsNode paintedNode,
                                   Element paintedElement,
                                   Element paintElement){
        return createPaint(ctx, paintedNode, paintedElement, paintElement);
    }

    /**
     * Creates a <tt>Paint</tt> used to fill a <tt>Shape</tt> of a
     * <tt>ShapeNode</tt>.
     * @param ctx the context to use
     * @param paintedElement the Element with 'fill' and
     * 'fill-opacity' attributes.
     * @param paintElement teh Element which contains the paint's definition
     */
    public Paint createFillPaint(BridgeContext ctx,
                                 GraphicsNode paintedNode,
                                 Element paintedElement,
                                 Element paintElement){
        return createPaint(ctx, paintedNode, paintedElement, paintElement);
    }

    protected Paint createPaint(BridgeContext ctx,
                                GraphicsNode paintedNode,
                                Element paintedElement,
                                Element paintElement) {
        //
        // Get unit processor to compute gradient control points
        //
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(paintElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        //
        // Get pattern patternUnits
        //
        FilterRegion patternRegion 
            = SVGUtilities.convertPatternRegion(paintElement,
                                         paintedElement,
                                         paintedNode,
                                         uctx);

        if(patternRegion == null) {
            throw new Error();
        } else {
            System.out.println("Pattern region : " + patternRegion);
        }

        // String patternUnits = paintElement.getAttributeNS(null, ATTR_PATTERN_UNITS);

        //
        // Build pattern content
        //
        GVTBuilder builder = ctx.getGVTBuilder();

        CompositeGraphicsNode patternNodeContent
            = ctx.getGVTFactory().createCompositeGraphicsNode();

        // get the transform on the patternElement and put it to the graphicsNode
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(paintElement.getAttributeNS(null, ATTR_PATTERN_TRANSFORM)),
             ctx.getParserFactory());
        at.preConcatenate(paintedNode.getGlobalTransform());
        patternNodeContent.setTransform(at);
        System.out.println("patternTransform  : " + at);
        System.out.println("shearX            : " + at.getShearX());

        // build the GVT tree that represents the pattern
        for(Node child=paintElement.getFirstChild();
            child != null;
            child = child.getNextSibling()){
            if(child.getNodeType() == child.ELEMENT_NODE){
                Element e = (Element)child;
                GraphicsNode node
                    = builder.build(ctx, e) ;
                if(node != null){
                    patternNodeContent.getChildren().add(node);
                }
            }
        }
        
        if (patternNodeContent == null) {
            System.out.println("Null pattern GN");
            System.out.println("patternEl: " + paintElement);
            return null;
        }

        //
        // Now, build a Paint from the pattern content
        //
        Paint paint = new ConcretePatternPaint(patternNodeContent, patternRegion);

        return paint;

    }

}
