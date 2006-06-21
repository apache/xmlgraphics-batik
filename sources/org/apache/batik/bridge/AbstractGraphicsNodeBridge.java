/*

   Copyright 2001-2005  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;

import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.dom.svg.AnimatedLiveAttributeValue;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.ext.awt.geom.SegmentList;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGFitToViewBox;
import org.w3c.dom.svg.SVGLength;

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
public abstract class AbstractGraphicsNodeBridge extends AnimatableSVGBridge
    implements SVGContext, 
               BridgeUpdateHandler, 
               GraphicsNodeBridge, 
               ErrorConstants {
    
    /**
     * The graphics node constructed by this bridge.
     */
    protected GraphicsNode node;

    /**
     * Whether the document is an SVG 1.2 document.
     */
    protected boolean isSVG12;

    /**
     * The unit context for length conversions.
     */
    protected UnitProcessor.Context unitContext;
    
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

        associateSVGContext(ctx, e, node);

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

        initializeDynamicSupport(ctx, e, node);
    }

    /**
     * Returns true if the graphics node has to be displayed, false
     * otherwise.
     */
    public boolean getDisplay(Element e) {
        return CSSUtilities.convertDisplay(e);
    }

    /**
     * Associates the {@link SVGContext} with the element.  This method should
     * be called even for static documents, since some bridges will need to
     * access animated attribute values even during the first build.
     */
    protected void associateSVGContext(BridgeContext ctx,
                                       Element e,
                                       GraphicsNode node) {
        this.e = e;
        this.node = node;
        this.ctx = ctx;
        this.unitContext = UnitProcessor.createContext(ctx, e);
        this.isSVG12 = ctx.isSVG12();
        ((SVGOMElement)e).setSVGContext(this);
    }

    /**
     * This method is invoked during the build phase if the document
     * is dynamic. The responsibility of this method is to ensure that
     * any dynamic modifications of the element this bridge is
     * dedicated to, happen on its associated GVT product.
     */
    protected void initializeDynamicSupport(BridgeContext ctx,
                                            Element e,
                                            GraphicsNode node) {
        if (ctx.isInteractive()) {
            // Bind the nodes for interactive and dynamic.
            ctx.bind(e, node);
        }
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
     * Invoked when the geometry of a graphical element has changed.
     */
    protected void handleGeometryChanged() {
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
        if (isSVG12) {
            if (!SVG_USE_TAG.equals(e.getLocalName())) {
                // ShapeChange events get fired only for basic shapes and paths.
                fireShapeChangeEvent();
            }
            fireBBoxChangeEvent();
        }
    }

    /**
     * Fires a ShapeChange event on the element this bridge is managing.
     */
    protected void fireShapeChangeEvent() {
        DocumentEvent d = (DocumentEvent) e.getOwnerDocument();
        AbstractEvent evt = (AbstractEvent) d.createEvent("SVGEvents");
        evt.initEventNS(SVG_NAMESPACE_URI,
                        "shapechange",
                        true,
                        false);
        try {
            ((EventTarget) e).dispatchEvent(evt);
        } catch (RuntimeException ex) {
            ctx.getUserAgent().displayError(ex);
        }
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        if ( evt.getTarget() instanceof Element ){
            // Handle "generic" bridges.
            Element e2 = (Element)evt.getTarget();
            Bridge b = ctx.getBridge(e2);
            if (b instanceof GenericBridge) {
                ((GenericBridge) b).handleElement(ctx, e2);
            }
        }
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
     * Disposes all resources related to the specified node and its subtree.
     */
    protected void disposeTree(Node node) {
        disposeTree(node, true);
    }

    /**
     * Disposes all resources related to the specified node and its subtree,
     * and optionally removes the nodes' {@link SVGContext}.
     */
    protected void disposeTree(Node node, boolean removeContext) {
        if (node instanceof SVGOMElement) {
            SVGOMElement elt = (SVGOMElement)node;
            BridgeUpdateHandler h = (BridgeUpdateHandler)elt.getSVGContext();
            if (h != null) {
                if (removeContext) {
                    elt.setSVGContext(null);
                }
                h.dispose();
            }
        }
        for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
            disposeTree(n, removeContext);
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
        case SVGCSSEngine.DISPLAY_INDEX:
            if (!getDisplay(e)) {
                // Remove the subtree.
                CompositeGraphicsNode parent = node.getParent();
                parent.remove(node);
                disposeTree(e, false);
            }
            break;
        }
    }

    /**
     * Invoked when the animated value of an animatable attribute has changed.
     */
    public void handleAnimatedAttributeChanged
            (AnimatedLiveAttributeValue alav) {
    }

    /**
     * Checks if the bounding box of the node has changed, and if so,
     * fires a bboxchange event on the element.
     */
    protected void checkBBoxChange() {
        if (e != null) {
            /*Rectangle2D oldBBox = bbox;
            Rectangle2D newBBox = getBBox();
            if (oldBBox != newBBox && newBBox != null) {
                if (oldBBox == null ||
                        oldBBox.getX() != bbox.getX()
                        || oldBBox.getY() != bbox.getY()
                        || oldBBox.getWidth() != bbox.getWidth()
                        || oldBBox.getHeight() != bbox.getHeight()) {*/
                    fireBBoxChangeEvent();
                /*}
            }*/
        }
    }

    /**
     * Fires an svg:bboxchange event on the element.
     */
    protected void fireBBoxChangeEvent() {
        DocumentEvent d = (DocumentEvent) e.getOwnerDocument();
        AbstractEvent evt = (AbstractEvent) d.createEvent("SVGEvents");
        evt.initEventNS(SVG_NAMESPACE_URI,
                        "RenderedBBoxChange",
                        true,
                        false);
        try {
            ((EventTarget) e).dispatchEvent(evt);
        } catch (RuntimeException ex) {
            ctx.getUserAgent().displayError(ex);
        }
    }

    // SVGContext implementation ///////////////////////////////////////////

    /**
     * Returns the size of a px CSS unit in millimeters.
     */
    public float getPixelUnitToMillimeter() {
        return ctx.getUserAgent().getPixelUnitToMillimeter();
    }

    /**
     * Returns the size of a px CSS unit in millimeters.
     * This will be removed after next release.
     * @see #getPixelUnitToMillimeter()
     */
    public float getPixelToMM() {
        return getPixelUnitToMillimeter();
    }

    protected SoftReference bboxShape = null;
    protected Rectangle2D bbox = null;

    /**
     * Returns the tight bounding box in current user space (i.e.,
     * after application of the transform attribute, if any) on the
     * geometry of all contained graphics elements, exclusive of
     * stroke-width and filter effects).
     */
    public Rectangle2D getBBox() {
        if (node == null) {
            return null;
        }
        Shape s = node.getOutline();
        
        if ((bboxShape != null) && (s == bboxShape.get())) return bbox;
        bboxShape = new SoftReference(s); // don't keep this live.
        bbox = null;
        if (s == null) return bbox;

        // SegmentList.getBounds2D gives tight BBox.
        SegmentList sl = new SegmentList(s);
        bbox = sl.getBounds2D();
        return bbox;
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
            if (elt instanceof SVGFitToViewBox) {
                AffineTransform at;
                if (gn instanceof CanvasGraphicsNode) {
                    at = ((CanvasGraphicsNode)gn).getViewingTransform();
                } else {
                    at = gn.getTransform();
                }
                if (at != null) {
                    ctm.preConcatenate(at);
                }
                break;
            }

            AffineTransform at = gn.getTransform();
            if (at != null)
                ctm.preConcatenate(at);

            elt = SVGCSSEngine.getParentCSSStylableElement(elt);
            gn = gn.getParent();
        }
        return ctm;
    }

    /**
     * Returns the display transform.
     */
    public AffineTransform getScreenTransform() {
        return ctx.getUserAgent().getTransform();
    }

    /**
     * Sets the display transform.
     */
    public void setScreenTransform(AffineTransform at) {
        ctx.getUserAgent().setTransform(at);
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

    /**
     * Converts the given SVG length into user units.
     * @param v the SVG length value
     * @param type the SVG length units (one of the
     *             {@link SVGLength}.SVG_LENGTH_* constants)
     * @param pcInterp how to interpretet percentage values (one of the
     *             {@link SVGContext}.PERCENTAGE_* constants) 
     * @return the SVG value in user units
     */
    public float svgToUserSpace(float v, int type, int pcInterp) {
        if (pcInterp == PERCENTAGE_FONT_SIZE
                && type == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
            // XXX
            return 0f;
        } else {
            return UnitProcessor.svgToUserSpace(v, (short) type,
                                                (short) (3 - pcInterp),
                                                unitContext);
        }
    }
}
