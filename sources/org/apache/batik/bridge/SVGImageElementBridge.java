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
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;

import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.Value;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.svg.XMLBaseSupport;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.color.ICCColorSpaceExt;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.gvt.RasterImageNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.util.ParsedURL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;image> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGImageElementBridge extends AbstractGraphicsNodeBridge {

    /**
     * Constructs a new bridge for the &lt;image> element.
     */
    public SVGImageElementBridge() {}

    /**
     * Returns 'image'.
     */
    public String getLocalName() {
        return SVG_IMAGE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGImageElementBridge();
    }

    /**
     * Creates a graphics node using the specified BridgeContext and for the
     * specified element.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        ImageNode imageNode = (ImageNode)super.createGraphicsNode(ctx, e);
        if (imageNode == null) {
            return null;
        }

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
        // try to load the image as an svg document
        SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();

        // try to load an SVG document
        DocumentLoader loader = ctx.getDocumentLoader();
        URIResolver resolver = new URIResolver(svgDoc, loader);
        try {
            Node n = resolver.getNode(uriStr, e);
            if (n.getNodeType() == n.DOCUMENT_NODE) {
                SVGDocument imgDocument = (SVGDocument)n;
                node = createSVGImageNode(ctx, e, imgDocument);
            }
        } catch (BridgeException ex) {
            throw ex;
        } catch (Exception ex) {
            /* Nothing to do */
        }

        if (node == null) {
            String baseURI = XMLBaseSupport.getCascadedXMLBase(e);
            ParsedURL purl;
            if (baseURI == null)
                purl = new ParsedURL(uriStr);
            else 
                purl = new ParsedURL(baseURI, uriStr);

            // try to load the image as a raster image (JPG or PNG)
            node = createRasterImageNode(ctx, e, purl);
        }

        if (node == null) {
            throw new BridgeException(e, ERR_URI_IMAGE_INVALID,
                                      new Object[] {uriStr});
        }

        // 'image-rendering' and 'color-rendering'
        RenderingHints hints = CSSUtilities.convertImageRendering(e, null);
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            node.setRenderingHints(hints);
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

    // dynamic support

    /**
     * This method is invoked during the build phase if the document
     * is dynamic. The responsability of this method is to ensure that
     * any dynamic modifications of the element this bridge is
     * dedicated to, happen on its associated GVT product.
     */
    protected void initializeDynamicSupport(BridgeContext ctx,
                                            Element e,
                                            GraphicsNode node) {
        this.e = e;
        this.node = node;
        this.ctx = ctx;
        // HACK due to the way images are represented in GVT
        ImageNode imgNode = (ImageNode)node;
        if (imgNode.getImage() instanceof RasterImageNode) {
            // register the RasterImageNode instead
            ctx.bind(e, imgNode.getImage());
        } else {
            ctx.bind(e, node);
        }
        ((SVGOMElement)e).setSVGContext(this);
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {

	    String attrName = evt.getAttrName();
        if (attrName.equals(SVG_X_ATTRIBUTE) ||
            attrName.equals(SVG_Y_ATTRIBUTE) ||
            attrName.equals(SVG_WIDTH_ATTRIBUTE) ||
            attrName.equals(SVG_HEIGHT_ATTRIBUTE) ||
	    attrName.equals(SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE)){

            //retrieve the new bounds of the image tag
	    Rectangle2D	bounds = getImageBounds(ctx, e);
	    GraphicsNode imageNode = ((ImageNode)node).getImage();
	    float [] vb = null;

	    if (((ImageNode)node).getImage() instanceof RasterImageNode) {
                //Raster image
		Rectangle2D imgBounds = 
                    ((RasterImageNode)imageNode).getImageBounds();
		// create the implicit viewBox for the raster
		// image. The viewBox for a raster image is the size
		// of the image
		vb = new float[4];
		vb[0] = 0; // x
		vb[1] = 0; // y
		vb[2] = (float)imgBounds.getWidth(); // width
		vb[3] = (float)imgBounds.getHeight(); // height
	    } else {
                // svg image need the viewbox of the embedded
		String uriStr = XLinkSupport.getXLinkHref(e);
                if ( uriStr == null || uriStr.length() == 0 ){
                    throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                              new Object[] {"xlink:href"});
                }
		// try to load the image as an svg document
		SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
		// try to load an SVG document
		DocumentLoader loader = ctx.getDocumentLoader();
		URIResolver resolver = new URIResolver(svgDoc, loader);
		SVGDocument imgDocument = null;
		try {
		    Node n = resolver.getNode(uriStr, e);
		    if (n.getNodeType() == n.DOCUMENT_NODE) {
			imgDocument = (SVGDocument)n;
		    }
		} catch (BridgeException ex) {
		    throw ex;
		} catch (Exception ex) {
		    /* Nothing to do */
		}
		if (imgDocument != null) {
		    Element svgElement = imgDocument.getRootElement();
		    String viewBox =
			svgElement.getAttributeNS(null, SVG_VIEW_BOX_ATTRIBUTE);
		    vb = ViewBox.parseViewBoxAttribute(e, viewBox);
		} else {
		    imageNode = null;
		}
	    }
	    if (imageNode != null) {
		// handles the 'preserveAspectRatio', 'overflow' and
		// 'clip' and sets the appropriate AffineTransform to
		// the image node
		initializeViewport(ctx, e, imageNode, vb, bounds);
	    }
	} else {
	    super.handleDOMAttrModifiedEvent(evt);
	}
    }
    
    // convenient methods //////////////////////////////////////////////////

    /**
     * Returns a GraphicsNode that represents an raster image in JPEG or PNG
     * format.
     *
     * @param ctx the bridge context
     * @param e the image element
     * @param uriStr the uri of the image
     */
    protected static GraphicsNode createRasterImageNode(BridgeContext ctx,
                                                        Element       e,
                                                        ParsedURL     purl) {

        RasterImageNode node = new RasterImageNode();

        ImageTagRegistry reg = ImageTagRegistry.getRegistry();
        Filter           img = reg.readURL(purl, extractColorSpace(e, ctx));
        Object           obj = img.getProperty
            (SVGBrokenLinkProvider.SVG_BROKEN_LINK_DOCUMENT_PROPERTY);
        if ((obj != null) && (obj instanceof SVGDocument)) {
            // Ok so we are dealing with a broken link.
            SVGOMDocument doc = (SVGOMDocument)obj;
            ctx.initializeDocument(doc);
            return createSVGImageNode(ctx, e, doc);
        }
        node.setImage(img);
        Rectangle2D imgBounds = img.getBounds2D();
        node.setImageBounds(imgBounds);
        Rectangle2D bounds = getImageBounds(ctx, e);

        // create the implicit viewBox for the raster image. The viewBox for a
        // raster image is the size of the image
        float [] vb = new float[4];
        vb[0] = 0; // x
        vb[1] = 0; // y
        vb[2] = (float)imgBounds.getWidth(); // width
        vb[3] = (float)imgBounds.getHeight(); // height

        // handles the 'preserveAspectRatio', 'overflow' and 'clip' and sets the
        // appropriate AffineTransform to the image node
        initializeViewport(ctx, e, node, vb, bounds);

        return node;
    }

    /**
     * Returns a GraphicsNode that represents a svg document as an image.
     *
     * @param ctx the bridge context
     * @param e the image element
     * @param imgDocument the SVG document that represents the image
     */
    protected static GraphicsNode createSVGImageNode(BridgeContext ctx,
                                                     Element e,
                                                     SVGDocument imgDocument) {

        CompositeGraphicsNode result = new CompositeGraphicsNode();

        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            result.setBackgroundEnable(r);
        }

        SVGSVGElement svgElement = imgDocument.getRootElement();
        GraphicsNode node = ctx.getGVTBuilder().build(ctx, svgElement);
        // HACK: remove the clip set by the SVGSVGElement as the overflow
        // and clip properties must be ignored. The clip will be set later
        // using the overflow and clip of the <image> element.
        node.setClip(null);
        result.getChildren().add(node);

        // create the implicit viewBox for the SVG image. The viewBox for a
        // SVG image is the viewBox of the outermost SVG element of the SVG file
        String viewBox =
            svgElement.getAttributeNS(null, SVG_VIEW_BOX_ATTRIBUTE);
        float [] vb = ViewBox.parseViewBoxAttribute(e, viewBox);

        // handles the 'preserveAspectRatio', 'overflow' and 'clip' and sets the
        // appropriate AffineTransform to the image node
        Rectangle2D bounds = getImageBounds(ctx, e);
        initializeViewport(ctx, e, result, vb, bounds);

        // add a listener on the outermost svg element of the SVG image.
        // if an event occured inside the SVG image document, send it
        // to the <image> element (inside the original document).
        if (ctx.isDynamic()) {
            EventListener listener = new ForwardEventListener(svgElement, e);
            EventTarget target = (EventTarget)svgElement;

            target.addEventListener(SVG_EVENT_CLICK, listener, false);
            ctx.storeEventListener(target, SVG_EVENT_CLICK, listener, false);

            target.addEventListener(SVG_EVENT_MOUSEOVER, listener, false);
            ctx.storeEventListener(target, SVG_EVENT_MOUSEOVER, listener,false);

            target.addEventListener(SVG_EVENT_MOUSEOUT, listener, false);
            ctx.storeEventListener(target, SVG_EVENT_MOUSEOUT, listener, false);
        }

        return result;
    }

    /**
     * A simple DOM listener to forward events from the SVG image document to
     * the original document.
     */
    protected static class ForwardEventListener implements EventListener {

        /**
         * The root element of the SVG image.
         */
        protected Element svgElement;

        /**
         * The image element.
         */
        protected Element imgElement;

        /**
         * Constructs a new <tt>ForwardEventListener</tt>
         */
        public ForwardEventListener(Element svgElement, Element imgElement) {
            this.svgElement = svgElement;
            this.imgElement = imgElement;
        }

        public void handleEvent(Event e) {
            MouseEvent evt = (MouseEvent) e;
            MouseEvent newMouseEvent = (MouseEvent)
                // DOM Level 2 6.5 cast from Document to DocumentEvent is ok
                ((DocumentEvent)imgElement.getOwnerDocument()).createEvent("MouseEvents");

            newMouseEvent.initMouseEvent(evt.getType(),
                                         evt.getBubbles(),
                                         evt.getCancelable(),
                                         evt.getView(),
                                         evt.getDetail(),
                                         evt.getScreenX(),
                                         evt.getScreenY(),
                                         evt.getClientX(),
                                         evt.getClientY(),
                                         evt.getCtrlKey(),
                                         evt.getAltKey(),
                                         evt.getShiftKey(),
                                         evt.getMetaKey(),
                                         evt.getButton(),
                                         (EventTarget)imgElement);
            ((EventTarget)imgElement).dispatchEvent(newMouseEvent);
        }
    }

    /**
     * Initializes according to the specified element, the specified graphics
     * node with the specified bounds. This method takes into account the
     * 'viewBox', 'preserveAspectRatio', and 'clip' properties. According to
     * those properties, a AffineTransform and a clip is set.
     *
     * @param ctx the bridge context
     * @param e the image element that defines the properties
     * @param node the graphics node
     * @param vb the implicit viewBox definition
     * @param bounds the bounds of the image element
     */
    protected static void initializeViewport(BridgeContext ctx,
                                             Element e,
                                             GraphicsNode node,
                                             float [] vb,
                                             Rectangle2D bounds) {

        float x = (float)bounds.getX();
        float y = (float)bounds.getY();
        float w = (float)bounds.getWidth();
        float h = (float)bounds.getHeight();

        AffineTransform at
            = ViewBox.getPreserveAspectRatioTransform(e, vb, w, h);
        at.preConcatenate(AffineTransform.getTranslateInstance(x, y));
        node.setTransform(at);

        // 'overflow' and 'clip'
        Shape clip = null;
        if (CSSUtilities.convertOverflow(e)) { // overflow:hidden
            float [] offsets = CSSUtilities.convertClip(e);
            if (offsets == null) { // clip:auto
                clip = new Rectangle2D.Float(x, y, w, h);
            } else { // clip:rect(<x> <y> <w> <h>)
                // offsets[0] = top
                // offsets[1] = right
                // offsets[2] = bottom
                // offsets[3] = left
                clip = new Rectangle2D.Float(x+offsets[3],
                                             y+offsets[0],
                                             w-offsets[1]-offsets[3],
                                             h-offsets[2]-offsets[0]);
            }
        }

        if (clip != null) {
            try {
                at = at.createInverse(); // clip in user space
                Filter filter = node.getGraphicsNodeRable(true);
                clip = at.createTransformedShape(clip);
                node.setClip(new ClipRable8Bit(filter, clip));
            } catch (java.awt.geom.NoninvertibleTransformException ex) {}
        }
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

        String colorProfileProperty = CSSUtilities.getComputedStyle
            (element, SVGCSSEngine.COLOR_PROFILE_INDEX).getStringValue();

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

    static {
        ImageTagRegistry.setBrokenLinkProvider(new SVGBrokenLinkProvider());
    }
}
