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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.util.awt.geom.AffineTransformSource;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GVTFactory;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;

import org.apache.batik.refimpl.gvt.ConcretePatternPaint;

import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.refimpl.gvt.filter.ConcreteClipRable;

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
        // Build pattern content
        //
        GVTBuilder builder = ctx.getGVTBuilder();

        CompositeGraphicsNode patternContentNode
            = ctx.getGVTFactory().createCompositeGraphicsNode();

        // build the GVT tree that represents the pattern
        for(Node child=paintElement.getFirstChild();
            child != null;
            child = child.getNextSibling()){
            if(child.getNodeType() == child.ELEMENT_NODE){
                Element e = (Element)child;
                GraphicsNode node
                    = builder.build(ctx, e) ;
                if(node != null){
                    patternContentNode.getChildren().add(node);
                }
            }
        }
        
        if (patternContentNode == null) {
            // System.out.println("Null pattern GN");
            // System.out.println("patternEl: " + paintElement);
            return null;
        }

        //
        // Get the patternTransfrom
        //
        AffineTransform patternTransform = AWTTransformProducer.createAffineTransform
            (new StringReader(paintElement.getAttributeNS(null, ATTR_PATTERN_TRANSFORM)),
             ctx.getParserFactory());

        //
        // Get unit processor to compute the pattern region
        //
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(paintedElement, null);

        // <!> QUESTION : SHOULD THE UNIT PROCESSOR COME FROM THE paintedElement
        //     OR FROM THE paintElement. Same question for the overflow property.
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        //
        // Get the overflow property on the pattern element
        //
        CSSPrimitiveValue vbOverflow 
            = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue(OVERFLOW_PROPERTY);
        
        String overFlowValue = vbOverflow.getStringValue();
        if(overFlowValue.length() == 0){
            overFlowValue = HIDDEN;
        }

        // System.out.println("Overflow value : " + overFlowValue);

        boolean overflow = true;
        if(HIDDEN.equals(overFlowValue)){
            overflow = false;
        }

        //
        // Get pattern region
        //
        Rectangle2D patternRegion 
            = SVGUtilities.convertPatternRegion(paintElement,
                                                paintedElement,
                                                paintedNode,
                                                uctx);

        //
        // Get the transform that will initialize the 
        // viewport for the pattern's viewBox
        //
        boolean hasViewBox = false;

        // viewBox -> patterRegion (viewport)
        AffineTransform preserveAspectRatioTransform
            = null;

        String viewBoxAttr 
            = paintElement.getAttributeNS(null,
                                          ATTR_VIEW_BOX);

        Rectangle2D viewBox = null;
        if(viewBoxAttr.length() > 0){
            preserveAspectRatioTransform
                = SVGUtilities.getPreserveAspectRatioTransform
                ((SVGElement)paintElement,
                 (float)patternRegion.getWidth(),
                 (float)patternRegion.getHeight(),
                 ctx.getParserFactory());

            float vb[] = SVGUtilities.parseViewBoxAttribute(viewBoxAttr);
            viewBox = new Rectangle2D.Float(vb[0], vb[1],
                                            vb[2], vb[3]);
            hasViewBox = true;
        }

        //
        // Compute transform on pattern content. This is only necessary if there
        // is no viewBox
        //
        AffineTransform patternContentTransform = null;

        if(!hasViewBox){
            String patternContentUnits 
                = paintElement.getAttributeNS(null, ATTR_PATTERN_CONTENT_UNITS);
            if(VALUE_OBJECT_BOUNDING_BOX.equals(patternContentUnits)){
                Rectangle2D bounds = paintedNode.getGeometryBounds();
                patternContentTransform = new AffineTransform();
                patternContentTransform.translate(bounds.getX(),
                                                  bounds.getY());
                patternContentTransform.scale(bounds.getWidth(),
                                              bounds.getHeight());
            }
        }

        //
        // When there is a viewbox, need two node:
        //  + one for the viewBox and implements the clipping (sometimes not, depending
        //    on overflow)
        //  + one for the viewBox to patternRegion transform
        //
        GVTFactory gvtFactory = ctx.getGVTFactory();
        AffineTransform nodeTransform = null;
        if(hasViewBox){
            nodeTransform = preserveAspectRatioTransform;
            if(!overflow){
                // Need to do clipping
                CompositeGraphicsNode newPatternContentNode 
                    = gvtFactory.createCompositeGraphicsNode(); // new CompositeGraphicsNode();

                newPatternContentNode.getChildren().add(patternContentNode);

                GraphicsNodeRableFactory gnrFactory
                    = ctx.getGraphicsNodeRableFactory();

                Filter filter = gnrFactory.createGraphicsNodeRable
                    (newPatternContentNode);

                newPatternContentNode.setClip
                    (new ConcreteClipRable(filter, viewBox));

                patternContentNode = newPatternContentNode;
            }
        } else{
            //
            // There may be an additional boundingBoxSpace to 
            // user space transform needed.
            //
            nodeTransform = patternContentTransform;
        }

        //
        // Now, build a Paint from the pattern content
        //
        Paint paint = new ConcretePatternPaint(patternContentNode, 
                                               nodeTransform,
                                               patternRegion, 
                                               overflow,
                                               patternTransform);

        return paint;

    }
}
