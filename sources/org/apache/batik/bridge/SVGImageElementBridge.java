/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RasterRable;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.gvt.RasterImageNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * Bridge class for the &lt;image> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGImageElementBridge extends AbstractGraphicsNodeBridge {

    public final static String PROTOCOL_DATA = "data:";

    /**
     * Constructs a new bridge for the &lt;image> element.
     */
    public SVGImageElementBridge() {}

    /**
     * Creates a graphics node using the specified BridgeContext and
     * for the specified element.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {

        ImageNode imageNode = (ImageNode)super.createGraphicsNode(ctx, e);

        // 'xlink:href' attribute - required
        String uriStr = XLinkSupport.getXLinkHref(e);
        if (uriStr.length() == 0) {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {"xlink:href"});
        }
        if (uriStr.indexOf('#') != -1) {
            throw new BridgeException(e, ERR_ATTRIBUTE_VALUE_MALFORMED,
                                      new Object[] {"xlink:href", uriStr});
        }
        GraphicsNode node = null;
        try {
            if (uriStr.startsWith(PROTOCOL_DATA)) {
                // load the image as a base 64 encoded image
                node = createBase64ImageNode(ctx, e, uriStr);
            } else {
                // try to load the image as an svg document
                SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
                URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
                URL url = new URL(baseURL, uriStr);
                // try to load an SVG document
                DocumentLoader loader = ctx.getDocumentLoader();
                URIResolver resolver = new URIResolver(svgDoc, loader);
                try {
                    Node n = resolver.getNode(url.toString());
                    if (n.getNodeType() == n.DOCUMENT_NODE) {
                        SVGDocument imgDocument = (SVGDocument)n;
                        node = createSVGImageNode(ctx, e, imgDocument);
                    }
                } catch (BridgeException ex) {
                    throw ex;
                } catch (Exception ex) { /* Nothing to do */ }
                if (node == null) {
                    // try to load the image as a raster image (JPG or PNG)
                    node = createRasterImageNode(ctx, e, url);
                }
            }
        } catch (MalformedURLException ex) {
            throw new BridgeException(e, ERR_URI_MALFORMED,
                                      new Object[] {uriStr});
        } catch (IOException ex) {
            throw new BridgeException(e, ERR_URI_IO,
                                      new Object[] {uriStr});
        }
        if (node == null) {
            throw new BridgeException(e, ERR_URI_IMAGE_INVALID,
                                      new Object[] {uriStr});
        }
        imageNode.setImage(node);
        return imageNode;
    }

    /**
     * Creates an <tt>ImageNode</tt>.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new ImageNode();
    }

    /**
     * Returns false as image is not a container.
     */
    public boolean isComposite() {
        return false;
    }

    /**
     * Returns a GraphicsNode that represents an raster image encoded
     * in the base 64 format.
     *
     * @param ctx the bridge context
     * @param e the image element
     * @param uriStr the uri of the image
     */
    protected static GraphicsNode createBase64ImageNode(BridgeContext ctx,
                                                        Element e,
                                                        String uriStr) {
        RasterImageNode node = new RasterImageNode();
        // create the image
        Rectangle2D bounds = getImageBounds(ctx, e);
        node.setImage
            (RasterRable.create(uriStr, bounds, extractColorSpace(e, ctx)));
        node.setImageBounds(bounds);
        return node;
    }

    /**
     * Returns a GraphicsNode that represents an raster image in JPEG
     * or PNG format.
     *
     * @param ctx the bridge context
     * @param e the image element
     * @param uriStr the uri of the image
     */
    protected static GraphicsNode createRasterImageNode(BridgeContext ctx,
                                                        Element e,
                                                        URL url) {
        RasterImageNode node = new RasterImageNode();
        // create the image
        Rectangle2D bounds = getImageBounds(ctx, e);
        node.setImage
            (RasterRable.create(url, bounds, extractColorSpace(e, ctx)));
        node.setImageBounds(bounds);
        return node;
    }

    /**
     * Returns a GraphicsNode that represents a svg document as an image.
     *
     * @param ctx the bridge context
     * @param element the image element
     * @param imgDocument the SVG document that represents the image
     */
    protected static GraphicsNode createSVGImageNode(BridgeContext ctx,
                                                     Element element,
                                                     SVGDocument imgDocument) {

        // viewport is automatically created by the svg element of the image
        SVGSVGElement svgElement = imgDocument.getRootElement();
        CompositeGraphicsNode result = new CompositeGraphicsNode();
        CSSStyleDeclaration decl = CSSUtilities.getComputedStyle(element);
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);

        Rectangle2D r
            = CSSUtilities.convertEnableBackground(element, uctx);
        if (r != null) {
            result.setBackgroundEnable(r);
        }

        Rectangle2D bounds = getImageBounds(ctx, element);
        svgElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE, 
                                  String.valueOf(bounds.getWidth()));
        svgElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE, 
                                  String.valueOf(bounds.getHeight())); 

        AffineTransform at
            = ViewBox.getPreserveAspectRatioTransform(svgElement, 
                                                      (float)bounds.getWidth(), 
                                                      (float)bounds.getHeight());
        at.preConcatenate(AffineTransform.getTranslateInstance(bounds.getX(), 
                                                               bounds.getY()));
        result.setTransform(at);

        GraphicsNode node = ctx.getGVTBuilder().build(ctx, svgElement);
        result.getChildren().add(node);

        /*
        // resolve x, y, width, height and preserveAspectRatio on image
        
        Rectangle2D bounds = getImageBounds(ctx, element);
        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();
        AffineTransform at
            = ViewBox.getPreserveAspectRatioTransform(element, w, h);
        at.preConcatenate(AffineTransform.getTranslateInstance(x, y));
        result.setTransform(at);
        try {
            at = at.createInverse(); // clip in user space
            Filter filter = new GraphicsNodeRable8Bit
                (node, ctx.getGraphicsNodeRenderContext());
            Shape clip = at.createTransformedShape
                (new Rectangle2D.Float(x, y, w, h));
            result.setClip(new ClipRable8Bit(filter, clip));
        } catch (java.awt.geom.NoninvertibleTransformException ex) {}
        */
        return result;
    }

    /**
     * Analyzes the color-profile property and builds an ICCColorSpaceExt
     * object from it.
     *
     * @param element the element with the color-profile property
     * @param ctx the bridge context
     */
    protected static ICCColorSpaceExt extractColorSpace(Element element,
                                                        BridgeContext ctx) {

        CSSStyleDeclaration decl = CSSUtilities.getComputedStyle(element);
        String colorProfileProperty
            = ((CSSPrimitiveValue)decl.getPropertyCSSValue
               (CSS_COLOR_PROFILE_PROPERTY)).getStringValue();

        // The only cases that need special handling are 'sRGB' and 'name'
        ICCColorSpaceExt colorSpace = null;
        if (CSS_SRGB_VALUE.equalsIgnoreCase(colorProfileProperty)) {

            colorSpace = new ICCColorSpaceExt
                (ICC_Profile.getInstance(ColorSpace.CS_sRGB),
                 ICCColorSpaceExt.AUTO);

        } else if (!CSS_AUTO_VALUE.equalsIgnoreCase(colorProfileProperty)
                   && !"".equalsIgnoreCase(colorProfileProperty)){

            // The value is neither 'sRGB' nor 'auto': it is a profile name.
            SVGColorProfileElementBridge profileBridge =
                (SVGColorProfileElementBridge) ctx.getBridge
                (SVG_NAMESPACE_URI, SVG_COLOR_PROFILE_TAG);
            if (profileBridge != null) {
                colorSpace = profileBridge.createICCColorSpaceExt
                    (ctx, element, colorProfileProperty);

            }
        }
        return colorSpace;
    }

    /**
     * Returns the bounds of the specified image element.
     *
     * @param ctx the bridge context
     * @param element the image element
     */
    protected static
        Rectangle2D getImageBounds(BridgeContext ctx, Element element) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);

        // 'x' attribute - default is 0
        String s = element.getAttributeNS(null, SVG_X_ATTRIBUTE);
        float x = 0;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_X_ATTRIBUTE, uctx);
        }

        // 'y' attribute - default is 0
        s = element.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        float y = 0;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_Y_ATTRIBUTE, uctx);
        }

        // 'width' attribute - required
        s = element.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        float w;
        if (s.length() == 0) {
            throw new BridgeException(element, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_WIDTH_ATTRIBUTE});
        } else {
            w = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, SVG_WIDTH_ATTRIBUTE, uctx);
        }

        // 'height' attribute - required
        s = element.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
        float h;
        if (s.length() == 0) {
            throw new BridgeException(element, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_HEIGHT_ATTRIBUTE});
        } else {
            h = UnitProcessor.svgVerticalLengthToUserSpace
                (s, SVG_HEIGHT_ATTRIBUTE, uctx);
        }

        return new Rectangle2D.Float(x, y, w, h);
    }

}
