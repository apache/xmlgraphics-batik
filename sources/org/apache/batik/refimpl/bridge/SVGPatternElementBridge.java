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
import org.apache.batik.bridge.IllegalAttributeValueException;
import org.apache.batik.bridge.ObjectBoundingBoxViewport;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.Viewport;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GVTFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.parser.ParserFactory;
import org.apache.batik.refimpl.bridge.resources.Messages;
import org.apache.batik.refimpl.gvt.ConcretePatternPaint;
import org.apache.batik.refimpl.gvt.filter.ConcreteClipRable;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

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

        Viewport oldViewport = ctx.getCurrentViewport();

        // parse the patternContentUnits attribute
        String patternContentUnits
            = paintElement.getAttributeNS(null, ATTR_PATTERN_CONTENT_UNITS);

        if(patternContentUnits.length() == 0){
            patternContentUnits = VALUE_USER_SPACE_ON_USE;
        }
        int unitsType;
        try {
            unitsType = SVGUtilities.parseCoordinateSystem(patternContentUnits);
        } catch (IllegalArgumentException ex) {
            throw new IllegalAttributeValueException(
                Messages.formatMessage("pattern.units.invalid",
                                       new Object[] {patternContentUnits,
                                                  ATTR_PATTERN_CONTENT_UNITS}));
        }

        if (unitsType == SVGUtilities.OBJECT_BOUNDING_BOX) {
            ctx.setCurrentViewport(new ObjectBoundingBoxViewport());
        }

        // Build pattern content
        GVTBuilder builder = ctx.getGVTBuilder();

        CompositeGraphicsNode patternContentNode
            = ctx.getGVTFactory().createCompositeGraphicsNode();

        // build the GVT tree that represents the pattern
        boolean hasChildren = false;
        for(Node node=paintElement.getFirstChild();
                 node != null;
                 node = node.getNextSibling()) {

            // check if the node is a valid Element
            if (node.getNodeType() != node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element) node;
            GraphicsNode patternNode = builder.build(ctx, child) ;
            // check if a GVT node has been created
            if (patternNode == null) {
                continue; // skip element as <pattern> can contain <defs>...
                /*
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("pattern.subelement.illegal",
                                           new Object[] {node.getLocalName()}));
                                           */
            }
            hasChildren = true;
            patternContentNode.getChildren().add(patternNode);
        }
        // restore the viewport
        ctx.setCurrentViewport(oldViewport);
        if (!hasChildren) {
            return null; // no pattern defined
        }

        // Get the patternTransfrom
        AffineTransform patternTransform =
            SVGUtilities.convertAffineTransform(paintElement,
                                                ATTR_PATTERN_TRANSFORM,
                                                ctx.getParserFactory());

        // Get the overflow property on the pattern element
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(paintElement, null);

        CSSPrimitiveValue vbOverflow =
            (CSSPrimitiveValue)cssDecl.getPropertyCSSValue(CSS_OVERFLOW_PROPERTY);

        String overFlowValue = vbOverflow.getStringValue();
        if(overFlowValue.length() == 0){
            overFlowValue = CSS_HIDDEN_VALUE;
        }

        boolean overflow = true;
        if(CSS_HIDDEN_VALUE.equals(overFlowValue)){
            overflow = false;
        }

        // Get pattern region. This is from the paintedElement, as
        // percentages are from the referencing element.
        CSSStyleDeclaration cssDeclPainted
            = ctx.getViewCSS().getComputedStyle(paintedElement, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        Rectangle2D patternRegion
            = SVGUtilities.convertPatternRegion(paintElement,
                                                paintedElement,
                                                paintedNode,
                                                uctx);
        // Get the transform that will initialize the viewport for the
        // pattern's viewBox
        boolean hasViewBox = false;
        // viewBox -> patterRegion (viewport)
        AffineTransform preserveAspectRatioTransform = null;
        String viewBoxAttr = paintElement.getAttributeNS(null, ATTR_VIEW_BOX);
        Rectangle2D viewBox = null;
        if (viewBoxAttr.length() > 0) {
            preserveAspectRatioTransform
                = SVGUtilities.getPreserveAspectRatioTransform
                ((SVGElement)paintElement,
                 (float)patternRegion.getWidth(),
                 (float)patternRegion.getHeight(),
                 ctx.getParserFactory());

            float vb[] = SVGUtilities.parseViewBoxAttribute(viewBoxAttr);
            viewBox = new Rectangle2D.Float(vb[0], vb[1], vb[2], vb[3]);
            hasViewBox = true;
        }

        // Compute transform on pattern content. This is only necessary if there
        // is no viewBox
        AffineTransform patternContentTransform = null;
        if (!hasViewBox) {
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
        //  + one for the viewBox and implements the clipping  (sometimes not,
        // depending on overflow)
        //  + one for the viewBox to patternRegion transform
        //
        GVTFactory gvtFactory = ctx.getGVTFactory();
        AffineTransform nodeTransform = null;
        if(hasViewBox){
            nodeTransform = preserveAspectRatioTransform;
            if(!overflow){
                // Need to do clipping
                CompositeGraphicsNode newPatternContentNode
                    = gvtFactory.createCompositeGraphicsNode();

                newPatternContentNode.getChildren().add(patternContentNode);

                GraphicsNodeRableFactory gnrFactory
                    = ctx.getGraphicsNodeRableFactory();

                Filter filter = gnrFactory.createGraphicsNodeRable
                    (newPatternContentNode);

                newPatternContentNode.setClip
                    (new ConcreteClipRable(filter, viewBox));

                patternContentNode = newPatternContentNode;
            }
        } else {
            // May be an additional boundingBoxSpace to user space
            // transform is needed.
            nodeTransform = patternContentTransform;
        }

        // Now, build a Paint from the pattern content
        Paint paint = new ConcretePatternPaint(patternContentNode,
                                               nodeTransform,
                                               patternRegion,
                                               overflow,
                                               patternTransform);

        return paint;
    }
}
