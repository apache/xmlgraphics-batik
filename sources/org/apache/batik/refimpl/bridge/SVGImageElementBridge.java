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
import java.awt.geom.Rectangle2D;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.Viewport;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.gvt.RasterImageNode;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.gvt.filter.Mask;

import org.apache.batik.parser.AWTTransformProducer;

import org.apache.batik.refimpl.gvt.filter.ConcreteClipRable;
import org.apache.batik.refimpl.gvt.filter.RasterRable;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SVGUtilities;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * A factory for the &lt;Image&gt; SVG element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class SVGImageElementBridge implements GraphicsNodeBridge,
                                              SVGConstants {

    public final static String PROTOCOL_DATA = "data:";

    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element element){
        SVGElement svgElement = (SVGElement) element;

        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);

        String uriStr = XLinkSupport.getXLinkHref(svgElement);
        // nothing referenced.
        if (uriStr == null) {
            return null;
        }
        // bad URL type
        if (uriStr.indexOf('#') != -1)
            throw new IllegalArgumentException
                ("<image> element can not reference elements within a URL: " +
                 uriStr);

        GraphicsNode node = null;
        try {
            if (uriStr.startsWith(PROTOCOL_DATA)) {
                // load the image as a base 64 encoded image
                node = createBase64ImageNode(ctx, svgElement, uriStr);
            } else {
                SVGDocument svgDoc = (SVGDocument)element.getOwnerDocument();
                URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
                URL url = new URL(baseURL, uriStr);
                // try to load an SVG document
                DocumentLoader loader = ctx.getDocumentLoader();
                URIResolver resolver = new URIResolver(svgDoc, loader);
                try {
                    Node n = resolver.getNode(url.toString());
                    if (n.getNodeType() == n.DOCUMENT_NODE) {
                        SVGDocument imgDocument = (SVGDocument)n;
                        node=createSVGImageNode(ctx, svgElement, imgDocument);
                    }
                } catch (Exception ex) { }
                if (node == null) {
                    // try to load the image as a raster image (JPG or PNG)
                    node = createRasterImageNode(ctx, svgElement, url);
                }
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Malformed URL: "+uriStr);
        }
        if (node == null) {
            throw new Error("Unreconized image format URL:"+uriStr);
        }
        ImageNode imgNode = ctx.getGVTFactory().createImageNode();
        imgNode.setImage(node);
        // bind it as soon as it's available...
        ctx.bind(element, imgNode);

        // Set node composite
        CSSPrimitiveValue val
            = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue(ATTR_OPACITY);
        Composite composite = CSSUtilities.convertOpacityToComposite(val);
        imgNode.setComposite(composite);

        // Set node filter
        Filter filter = CSSUtilities.convertFilter(element, imgNode, ctx);
        imgNode.setFilter(filter);

        // Set the node mask
        Mask   mask   = CSSUtilities.convertMask(element, imgNode, ctx);
        imgNode.setMask(mask);

        // Set the node clip
        Clip clip = CSSUtilities.convertClipPath(element, imgNode, ctx);
        imgNode.setClip(clip);

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, element);

        return imgNode;
    }

    public void buildGraphicsNode(GraphicsNode node, BridgeContext ctx,
                                  Element element) {

    }

    protected GraphicsNode createBase64ImageNode(BridgeContext ctx,
                                                 SVGElement svgElement,
                                                 String uriStr) {
        RasterImageNode node = ctx.getGVTFactory().createRasterImageNode();
        // create the image
        Rectangle2D bounds = getImageBounds(ctx, svgElement);
        node.setImage(RasterRable.create(uriStr, bounds));
        node.setImageBounds(bounds);
        // initialize the transform
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(svgElement.getAttributeNS(null, ATTR_TRANSFORM)),
             ctx.getParserFactory());
        node.setTransform(at);
        return node;
    }

    protected GraphicsNode createRasterImageNode(BridgeContext ctx,
                                                 SVGElement svgElement,
                                                 URL url) {
        RasterImageNode node = ctx.getGVTFactory().createRasterImageNode();
        // create the image
        Rectangle2D bounds = getImageBounds(ctx, svgElement);
        node.setImage(RasterRable.create(url, bounds));
        node.setImageBounds(bounds);
        // initialize the transform
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(svgElement.getAttributeNS(null, ATTR_TRANSFORM)),
             ctx.getParserFactory());
        node.setTransform(at);
        return node;
    }

    protected GraphicsNode createSVGImageNode(BridgeContext ctx,
                                              SVGElement element,
                                              SVGDocument imgDocument) {

        Viewport oldViewport = ctx.getCurrentViewport();
        ViewCSS oldViewCSS = ctx.getViewCSS();

        ctx.setCurrentViewport(null);
        ctx.setViewCSS((ViewCSS)((SVGOMDocument)imgDocument).getDefaultView());
        SVGSVGElement svgElement = imgDocument.getRootElement();

        CompositeGraphicsNode result =
            ctx.getGVTFactory().createCompositeGraphicsNode();

        GraphicsNode node = ctx.getGVTBuilder().build(ctx, svgElement);
        result.getChildren().add(node);

        // resolve x, y, width, height and preserveAspectRatio on image
        Rectangle2D bounds = getImageBounds(ctx, element);
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        AffineTransform at;
        at = SVGUtilities.getPreserveAspectRatioTransform
            (element, w, h, ctx.getParserFactory());
        at.preConcatenate(AffineTransform.getTranslateInstance(x, y));
        result.setTransform(at);
        try {
            at = at.createInverse(); // clip in user space

            GraphicsNodeRableFactory gnrFactory
                = ctx.getGraphicsNodeRableFactory();

            Filter filter = gnrFactory.createGraphicsNodeRable(node);

            Shape clip = at.createTransformedShape
                (new Rectangle2D.Float(x, y, w, h));
            result.setClip(new ConcreteClipRable(filter, clip));

        } catch (java.awt.geom.NoninvertibleTransformException ex) {}

        // restore viewport and current CSS view
        ctx.setCurrentViewport(oldViewport);
        ctx.setViewCSS(oldViewCSS);
        return result;
    }

    protected Rectangle2D getImageBounds(BridgeContext ctx,
                                         SVGElement svgElement) {

        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(svgElement, null);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        String s;
        s = svgElement.getAttributeNS(null, ATTR_X);
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

        return new Rectangle2D.Float(x, y, w, h);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }
}
