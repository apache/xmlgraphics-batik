/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGFitToViewBox;

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
     * This method is invoked during the build phase if the document
     * is dynamic. The responsability of this method is to ensure that
     * any dynamic modifications of the element this bridge is
     * dedicated to, happen on its associated GVT product.
     */
    protected void initializeDynamicSupport(BridgeContext ctx,
                                            Element e,
                                            GraphicsNode node) {
        if (!ctx.isInteractive())
            return;

        // Bind the nodes for interactive and dynamic
        ctx.bind(e, node);

        if (ctx.isDynamic()) {
            // only set context for dynamic documents not interactive.
            this.e = e;
            this.node = node;
            this.ctx = ctx;
            ((SVGOMElement)e).setSVGContext(this);
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
        case SVGCSSEngine.DISPLAY_INDEX:
            if (!getDisplay(e)) {
                // Remove the subtree.
                CompositeGraphicsNode parent = node.getParent();
                int idx = parent.indexOf(node);
                parent.remove(node);
                disposeTree(e);
            }
            break;
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
     * Returns the display transform.
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
}
