/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.PatternPaint;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable8Bit;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.xml.sax.SAXException;

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
        return createPaint(ctx, paintedNode, paintedElement, paintElement, CSS_STROKE_OPACITY_PROPERTY);
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
        return createPaint(ctx, paintedNode, paintedElement, paintElement, CSS_FILL_OPACITY_PROPERTY);
    }

    protected static List extractPatternChildren(Element patternElement,
                                                 BridgeContext ctx) {
        GVTBuilder builder = ctx.getGVTBuilder();
        Element e = patternElement;
        List refs = new LinkedList();
        List children = new ArrayList();
        DocumentLoader loader = ctx.getDocumentLoader();
        for (;;) {
            for(Node node=e.getFirstChild();
                    node != null;
                    node = node.getNextSibling()) {
                // check if the node is a valid Element
                if (node.getNodeType() != node.ELEMENT_NODE) {
                    continue;
                }
                Element child = (Element)node;
                GraphicsNode patternNode = builder.build(ctx, child) ;
                // check if a GVT node has been created
                if (patternNode == null) {
                    continue; // skip element as <pattern> can contain <defs>...
                }
                children.add(patternNode);
            }
            if (children.size() > 0) {
                return children; // exit if children found
            }
            String uriStr = XLinkSupport.getXLinkHref(e);
            if (uriStr.length() == 0) {
                return children; // exit if no more xlink:href
            }
            SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
            URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
            try {
                URL url = new URL(baseURL, uriStr);
                Iterator iter = refs.iterator();
                while (iter.hasNext()) {
                    URL urlTmp = (URL)iter.next();
                    if (urlTmp.sameFile(url) &&
                            urlTmp.getRef().equals(url.getRef())) {
                        throw new IllegalAttributeValueException(
                            "circular reference on "+e);
                    }
                }
                URIResolver resolver = new URIResolver(svgDoc, loader);
                e = resolver.getElement(url.toString());
                refs.add(url);
            } catch(MalformedURLException ex) {
                throw new IllegalAttributeValueException("bad url on "+uriStr);
            } catch(IOException ex) {
                throw new IllegalAttributeValueException("I/O error on "+uriStr);
            }
        }
    }

    protected Paint createPaint(BridgeContext ctx,
                                GraphicsNode paintedNode,
                                Element paintedElement,
                                Element paintElement,
                                String paintOpacityProperty) {

        GraphicsNodeRenderContext rc =
                         ctx.getGraphicsNodeRenderContext();
        DocumentLoader loader = ctx.getDocumentLoader();

        // parse the patternContentUnits attribute
        String patternContentUnits
            = SVGUtilities.getChainableAttributeNS(paintElement,
                                                   null,
                                                   ATTR_PATTERN_CONTENT_UNITS,
                                                   loader);

        if(patternContentUnits.length() == 0){
            patternContentUnits = SVG_USER_SPACE_ON_USE_VALUE;
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

        List children = extractPatternChildren(paintElement, ctx);
        if (children.size() == 0) {
            return null; // no pattern defined
        }
        // Build pattern content
        CompositeGraphicsNode patternContentNode = new CompositeGraphicsNode();
        Iterator iter = children.iterator();
        while (iter.hasNext()) {
            patternContentNode.getChildren().add((GraphicsNode)iter.next());
        }

        // Get the patternTransfrom
        AffineTransform patternTransform;
        String transformStr =
            SVGUtilities.getChainableAttributeNS(paintElement,
                                                 null,
                                                 ATTR_PATTERN_TRANSFORM,
                                                 loader);
        if (transformStr.length() > 0) {
            patternTransform = SVGUtilities.convertAffineTransform(transformStr);
        } else {
            patternTransform = new AffineTransform();
        }

        CSSStyleDeclaration cssDecl
            = CSSUtilities.getComputedStyle(paintElement);

        // Get the overflow property on the pattern element
        boolean overflowIsHidden = CSSUtilities.convertOverflow(paintElement);


        // Get pattern region. This is from the paintedElement, as
        // percentages are from the referencing element.
        CSSStyleDeclaration cssDeclPainted
            = CSSUtilities.getComputedStyle(paintedElement);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        Rectangle2D patternRegion
            = SVGUtilities.convertPatternRegion(paintElement,
                                                paintedElement,
                                                paintedNode,
                                                rc,
                                                uctx,
                                                loader);
        // Get the transform that will initialize the viewport for the
        // pattern's viewBox
        boolean hasViewBox = false;
        // viewBox -> patterRegion (viewport)
        AffineTransform preserveAspectRatioTransform = null;
        String viewBoxAttr = SVGUtilities.getChainableAttributeNS(paintElement,
                                                                  null,
                                                                  ATTR_VIEW_BOX,
                                                                  loader);
        Rectangle2D viewBox = null;
        if (viewBoxAttr.length() > 0) {
            preserveAspectRatioTransform
                = SVGUtilities.getPreserveAspectRatioTransform
                ((SVGElement)paintElement,
                 (float)patternRegion.getWidth(),
                 (float)patternRegion.getHeight());

            float vb[] = SVGUtilities.parseViewBoxAttribute(viewBoxAttr);
            viewBox = new Rectangle2D.Float(vb[0], vb[1], vb[2], vb[3]);
            hasViewBox = true;
        }

        // Compute transform on pattern content. This is only necessary if there
        // is no viewBox
        AffineTransform patternContentTransform = null;
        if (!hasViewBox) {
            if(SVG_OBJECT_BOUNDING_BOX_VALUE.equals(patternContentUnits)){
                Rectangle2D bounds = paintedNode.getGeometryBounds(rc);
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
        AffineTransform nodeTransform = null;
        if(hasViewBox){
            nodeTransform = preserveAspectRatioTransform;
            if(overflowIsHidden){
                // Need to do clipping
                CompositeGraphicsNode newPatternContentNode
                    = new CompositeGraphicsNode();

                newPatternContentNode.getChildren().add(patternContentNode);

                GraphicsNodeRableFactory gnrFactory
                    = ctx.getGraphicsNodeRableFactory();

                Filter filter = gnrFactory.createGraphicsNodeRable
                    (newPatternContentNode, rc);

                newPatternContentNode.setClip
                    (new ClipRable8Bit(filter, viewBox));

                patternContentNode = newPatternContentNode;
            }
        } else {
            // May be an additional boundingBoxSpace to user space
            // transform is needed.
            nodeTransform = patternContentTransform;
        }

        // Account for the opacity of the paint operation by adding a
        // scale on the content.
        CSSStyleDeclaration paintedCssDecl
            = CSSUtilities.getComputedStyle(paintedElement);
        CSSPrimitiveValue v =
            (CSSPrimitiveValue)paintedCssDecl.getPropertyCSSValue(paintOpacityProperty);
        float opacity = CSSUtilities.convertOpacity(v);
        if(opacity != 1){
            float[][] matrix = {{1, 0, 0, 0, 0},
                                {0, 1, 0, 0, 0},
                                {0, 0, 1, 0, 0},
                                {0, 0, 0, opacity, 0} };
            ColorMatrixRable filter = ColorMatrixRable8Bit.buildMatrix(matrix);
            CompositeGraphicsNode newPatternContentNode
                = new CompositeGraphicsNode();

            newPatternContentNode.getChildren().add(patternContentNode);

            GraphicsNodeRableFactory gnrFactory
                = ctx.getGraphicsNodeRableFactory();

            Filter contentRable = gnrFactory.createGraphicsNodeRable(patternContentNode, rc);
            filter.setSource(contentRable);
            patternContentNode.setFilter(filter);
            patternContentNode = newPatternContentNode;
        }

        // Now, build a Paint from the pattern content
        Paint paint = new PatternPaint(patternContentNode,
                                       rc,
                                       nodeTransform,
                                       patternRegion,
                                       !overflowIsHidden,
                                       patternTransform);

        return paint;
    }
}
