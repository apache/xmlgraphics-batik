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

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;

import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * Bridge class for the &lt;svg> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGSVGElementBridge extends SVGGElementBridge {

    /**
     * Constructs a new bridge for the &lt;svg> element.
     */
    public SVGSVGElementBridge() {}

    /**
     * Returns 'svg'.
     */
    public String getLocalName() {
        return SVG_SVG_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance(){
        return new SVGSVGElementBridge();
    }

    /**
     * Creates a <tt>CompositeGraphicsNode</tt>.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new CanvasGraphicsNode();
    }

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

        CanvasGraphicsNode cgn;
        cgn = (CanvasGraphicsNode)instantiateGraphicsNode();

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);
        String s;

        // In some cases we converted document fragments which didn't
        // have a parent SVG element, this check makes sure only the
        // real root of the SVG Document tries to do negotiation with
        // the UA.
        SVGDocument doc = (SVGDocument)e.getOwnerDocument();
        boolean isOutermost = (doc.getRootElement() == e);
        float x = 0;
        float y = 0;
        // x and y have no meaning on the outermost 'svg' element
        if (!isOutermost) {
            // 'x' attribute - default is 0
            s = e.getAttributeNS(null, SVG_X_ATTRIBUTE);
            if (s.length() != 0) {
                x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                    (s, SVG_X_ATTRIBUTE, uctx);
            }
            // 'y' attribute - default is 0
            s = e.getAttributeNS(null, SVG_Y_ATTRIBUTE);
            if (s.length() != 0) {
                y = UnitProcessor.svgVerticalCoordinateToUserSpace
                    (s, SVG_Y_ATTRIBUTE, uctx);
            }
        }

        // 'width' attribute - default is 100%
        s = e.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
        if (s.length() == 0) {
            s = SVG_SVG_WIDTH_DEFAULT_VALUE;
        }
        float w = UnitProcessor.svgHorizontalLengthToUserSpace
            (s, SVG_WIDTH_ATTRIBUTE, uctx);

        // 'height' attribute - default is 100%
        s = e.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
        if (s.length() == 0) {
            s = SVG_SVG_HEIGHT_DEFAULT_VALUE;
        }
        float h = UnitProcessor.svgVerticalLengthToUserSpace
            (s, SVG_HEIGHT_ATTRIBUTE, uctx);

        // 'visibility'
        cgn.setVisible(CSSUtilities.convertVisibility(e));

        // 'viewBox' and "preserveAspectRatio' attributes
        AffineTransform viewingTransform =
            ViewBox.getPreserveAspectRatioTransform(e, w, h);

        float actualWidth = w;
        float actualHeight = h;
        try {
            AffineTransform vtInv = viewingTransform.createInverse();
            actualWidth = (float) (w*vtInv.getScaleX());
            actualHeight = (float) (h*vtInv.getScaleY());
        } catch (NoninvertibleTransformException ex) {}

        AffineTransform positionTransform =
            AffineTransform.getTranslateInstance(x, y);
        // 'overflow' and 'clip'
        // The outermost preserveAspectRatio matrix is set by the user
        // agent, so we don't need to set the transform for outermost svg
        Shape clip = null;
        if (!isOutermost) {
            // X & Y are ignored on outermost SVG.
            cgn.setPositionTransform(positionTransform);
        } else if (doc == ctx.getDocument()) {
            // <!> FIXME: hack to compute the original document's size
            ctx.setDocumentSize(new Dimension((int)w, (int)h));
        }
        // Set the viewing transform, this is often updated when the
        // component prepares for rendering.
        cgn.setViewingTransform(viewingTransform);

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
                AffineTransform at = new AffineTransform(positionTransform);
                at.concatenate(viewingTransform);
                at = at.createInverse(); // clip in user space
                clip = at.createTransformedShape(clip);
                Filter filter = cgn.getGraphicsNodeRable(true);
                cgn.setClip(new ClipRable8Bit(filter, clip));
            } catch (NoninvertibleTransformException ex) {}
        }

        // 'enable-background'
        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            cgn.setBackgroundEnable(r);
        }

        ctx.openViewport
            (e, new SVGSVGElementViewport((SVGSVGElement)e,
                                          actualWidth,
                                          actualHeight));
        return cgn;
    }

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
        // 'pointer-events'
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));

        initializeDynamicSupport(ctx, e, node);

        // Handle children elements such as <title>
        //SVGUtilities.bridgeChildren(ctx, e);
        //super.buildGraphicsNode(ctx, e, node);
        ctx.closeViewport(e);
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Disposes this BridgeUpdateHandler and releases all resources.
     */
    public void dispose() {
        ctx.removeViewport(e);
        super.dispose();
    }

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        // Don't call 'super' because there is no 'transform' attribute on <svg>
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_X_ATTRIBUTE) ||
            attrName.equals(SVG_Y_ATTRIBUTE) ||
            attrName.equals(SVG_WIDTH_ATTRIBUTE) ||
            attrName.equals(SVG_HEIGHT_ATTRIBUTE) ||
            attrName.equals(SVG_VIEW_BOX_ATTRIBUTE) ||
            attrName.equals(SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE)) {
            
            CompositeGraphicsNode gn = node.getParent();
            gn.remove(node);
            disposeTree(e);

            handleElementAdded(gn, e.getParentNode(), e);
        }
    }

    /**
     * A viewport defined an &lt;svg> element.
     */
    public static class SVGSVGElementViewport implements Viewport {

        private SVGSVGElement e;
        private float width;
        private float height;

        /**
         * Constructs a new viewport with the specified <tt>SVGSVGElement</tt>.
         * @param e the SVGSVGElement that defines this viewport
         * @param w the width of the viewport
         * @param h the height of the viewport
         */
        public SVGSVGElementViewport(SVGSVGElement e, float w, float h) {
            this.e = e;
            this.width = w;
            this.height = h;
        }

        /**
         * Returns the width of this viewport.
         */
        public float getWidth(){
            return width;
        }

        /**
         * Returns the height of this viewport.
         */
        public float getHeight(){
            return height;
        }
    }
}
