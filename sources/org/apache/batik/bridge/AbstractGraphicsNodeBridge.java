/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.SVGCSSEngine;

import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGOMElement;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.events.MutationEvent;

/**
 * The base bridge class for SVG graphics node. By default, the namespace URI is
 * the SVG namespace. Override the <tt>getNamespaceURI</tt> if you want to add
 * custom <tt>GraphicsNode</tt> with a custom namespace.
 *
 * <p>This class handles various attributes that are defined on most
 * of the SVG graphic elements as described in the SVG
 * specification.</p>
 *
 * <ul>
 * <li>clip-path</li>
 * <li>filter</li>
 * <li>mask</li>
 * <li>opacity</li>
 * <li>transform</li>
 * <li>visibility</li>
 * </ul>
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractGraphicsNodeBridge extends AbstractSVGBridge
    implements SVGContext, 
               BridgeUpdateHandler, 
               GraphicsNodeBridge, 
               ErrorConstants {

    /**
     * The element that has been handled by this bridge.
     */
    protected Element e;

    /**
     * The graphics node constructed by this bridge.
     */
    protected GraphicsNode node;

    /**
     * The bridge context to use for dynamic updates.
     */
    protected BridgeContext ctx;

    /**
     * Constructs a new abstract bridge.
     */
    protected AbstractGraphicsNodeBridge() {}

    /**
     * Creates a <tt>GraphicsNode</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }

        GraphicsNode node = instantiateGraphicsNode();
        // 'transform'
        String s = e.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            node.setTransform
                (SVGUtilities.convertTransform(e, SVG_TRANSFORM_ATTRIBUTE, s));
        }
        // 'visibility'
        node.setVisible(CSSUtilities.convertVisibility(e));
        return node;
    }

    /**
     * Creates the GraphicsNode depending on the GraphicsNodeBridge
     * implementation.
     */
    protected abstract GraphicsNode instantiateGraphicsNode();

    /**
     * Builds using the specified BridgeContext and element, the
     * specified graphics node.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @param node the graphics node to build
     */
    public void buildGraphicsNode(BridgeContext ctx,
                                  Element e,
                                  GraphicsNode node) {
        // 'opacity'
        node.setComposite(CSSUtilities.convertOpacity(e));
        // 'filter'
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        // 'mask'
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        // 'clip-path'
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
        // 'pointer-events'
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));

        if (ctx.isDynamic()) {
            initializeDynamicSupport(ctx, e, node);
        }
        // Handle children elements such as <title>
        SVGUtilities.bridgeChildren(ctx, e);
    }

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
        ctx.bind(e, node);
        ((SVGOMElement)e).setSVGContext(this);
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_TRANSFORM_ATTRIBUTE)) {
            String s = evt.getNewValue();
            AffineTransform at = GraphicsNode.IDENTITY;
            if (s.length() != 0) {
                at = SVGUtilities.convertTransform
                    (e, SVG_TRANSFORM_ATTRIBUTE, s);
            }
            node.setTransform(at);
            handleGeometryChanged();
        }
    }

    /**
     * Invoked when the geometry of an graphical element has changed.
     */
    protected  void handleGeometryChanged() {
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        // never called. The global listener on the document will
        // invoke this method on the parent element.
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeRemoved' is fired.
     */
    public void handleDOMNodeRemovedEvent(MutationEvent evt) {
        CompositeGraphicsNode gn = node.getParent();
        gn.remove(node);
        disposeTree(e);
    }

    /**
     * Invoked when an MutationEvent of type 'DOMCharacterDataModified' 
     * is fired.
     */
    public void handleDOMCharacterDataModified(MutationEvent evt) {
    }

    /**
     * Disposes this BridgeUpdateHandler and releases all resources.
     */
    public void dispose() {
        SVGOMElement elt = (SVGOMElement)e;
        elt.setSVGContext(null);
        ctx.unbind(e);
    }


    /**
     * Disposes all resources related to the specified node and its subtree
     */
    static void disposeTree(Node node) {
        if (node instanceof SVGOMElement) {
            SVGOMElement elt = (SVGOMElement)node;
            BridgeUpdateHandler h = (BridgeUpdateHandler)elt.getSVGContext();
            if (h != null)
                h.dispose();
        }
        for (Node n = node.getFirstChild(); n!=null; n = n.getNextSibling()) {
            disposeTree(n);
        }
    }

    /**
     * Invoked when an CSSEngineEvent is fired.
     */
    public void handleCSSEngineEvent(CSSEngineEvent evt) {
        try {
            int [] properties = evt.getProperties();
            for (int i=0; i < properties.length; ++i) {
                handleCSSPropertyChanged(properties[i]);
            }
        } catch (Exception ex) {
            ctx.getUserAgent().displayError(ex);
        }
    }

    /**
     * Invoked for each CSS property that has changed.
     */
    protected void handleCSSPropertyChanged(int property) {
        switch(property) {
        case SVGCSSEngine.VISIBILITY_INDEX:
            node.setVisible(CSSUtilities.convertVisibility(e));
            break;
        case SVGCSSEngine.OPACITY_INDEX:
            node.setComposite(CSSUtilities.convertOpacity(e));
            break;
        case SVGCSSEngine.FILTER_INDEX:
            node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
            break;
        case SVGCSSEngine.MASK_INDEX:
            node.setMask(CSSUtilities.convertMask(e, node, ctx));
            break;
        case SVGCSSEngine.CLIP_PATH_INDEX:
            node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
            break;
        case SVGCSSEngine.POINTER_EVENTS_INDEX:
            node.setPointerEventType(CSSUtilities.convertPointerEvents(e));
            break;
        }
    }

    // SVGContext implementation ///////////////////////////////////////////

    /**
     * Return the pixel to millimeters factor.
     */
    public float getPixelToMM() {
        return ctx.getUserAgent().getPixelToMM();
    }

    /**
     * Returns the tight bounding box in current user space (i.e.,
     * after application of the transform attribute, if any) on the
     * geometry of all contained graphics elements, exclusive of
     * stroke-width and filter effects).
     */
    public Rectangle2D getBBox() {
        return node.getPrimitiveBounds();
    }

    /**
     * Returns the transformation matrix from current user units
     * (i.e., after application of the transform attribute, if any) to
     * the viewport coordinate system for the nearestViewportElement.
     */
    public AffineTransform getCTM() {
        GraphicsNode gn = node;
        AffineTransform ctm = new AffineTransform();
        Element elt = e;
        while (elt != null) {
            AffineTransform at = gn.getTransform();
            if (at != null) {
                ctm.preConcatenate(at);
            }
            elt = SVGCSSEngine.getParentCSSStylableElement(elt);
            gn = gn.getParent();
            if (elt instanceof SVGFitToViewBox) {
                break;
            }
        }
        return ctm;
    }

    /**
     * Returns the global transformation matrix from the current
     * element to the root.
     */
    public AffineTransform getGlobalTransform() {
        return node.getGlobalTransform();
    }

    /**
     * Returns the width of the viewport which directly contains the
     * given element.
     */
    public float getViewportWidth() {
        return ctx.getBlockWidth(e);
    }

    /**
     * Returns the height of the viewport which directly contains the
     * given element.
     */
    public float getViewportHeight() {
        return ctx.getBlockHeight(e);
    }

    /**
     * Returns the font-size on the associated element.
     */
    public float getFontSize() {
        return CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.FONT_SIZE_INDEX).getFloatValue();
    }
}
