/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.batik.dom.svg.SVGOMCSSImportedElementRoot;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGOMUseElement;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;use> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGUseElementBridge extends AbstractGraphicsNodeBridge {

    /**
     * Constructs a new bridge for the &lt;use> element.
     */
    public SVGUseElementBridge() {}

    /**
     * Returns 'use'.
     */
    public String getLocalName() {
        return SVG_USE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance(){
        return new SVGUseElementBridge();
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

        // get the referenced element
        String uri = XLinkSupport.getXLinkHref(e);
        if (uri.length() == 0)
            throw new BridgeException(e, ERR_URI_MALFORMED, 
                                      new Object[] {uri});

        Element refElement = ctx.getReferencedElement(e, uri);
        SVGOMDocument document
            = (SVGOMDocument)e.getOwnerDocument();
        SVGOMDocument refDocument
            = (SVGOMDocument)refElement.getOwnerDocument();
        boolean isLocal = (refDocument == document);
        // import or clone the referenced element in current document
        Element localRefElement = (isLocal)
            ? (Element)refElement.cloneNode(true)
            : (Element)document.importNode(refElement, true);

        if (SVG_SYMBOL_TAG.equals(localRefElement.getLocalName())) {
            // The referenced 'symbol' and its contents are deep-cloned into
            // the generated tree, with the exception that the 'symbol'  is
            // replaced by an 'svg'.
            Element svgElement
                = document.createElementNS(SVG_NAMESPACE_URI, SVG_SVG_TAG);
            // move the attributes from <symbol> to the <svg> element
            NamedNodeMap attrs = localRefElement.getAttributes();
            int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                Attr attr = (Attr)attrs.item(i);
                svgElement.setAttributeNS(attr.getNamespaceURI(),
                                          attr.getName(),
                                          attr.getValue());
            }
            // move the children from <symbol> to the <svg> element
            for (Node n = localRefElement.getFirstChild();
                 n != null;
                 n = localRefElement.getFirstChild()) {
                svgElement.appendChild(n);
            }
            localRefElement = svgElement;
        }

        if (SVG_SVG_TAG.equals(localRefElement.getLocalName())) {
            // The referenced 'svg' and its contents are deep-cloned into the
            // generated tree. If attributes width and/or height are provided
            // on the 'use' element, then these values will override the
            // corresponding attributes on the 'svg' in the generated tree.
            String wStr = e.getAttributeNS(null, SVG_WIDTH_ATTRIBUTE);
            if (wStr.length() != 0) {
                localRefElement.setAttributeNS
                    (null, SVG_WIDTH_ATTRIBUTE, wStr);
            }
            String hStr = e.getAttributeNS(null, SVG_HEIGHT_ATTRIBUTE);
            if (hStr.length() != 0) {
                localRefElement.setAttributeNS
                    (null, SVG_HEIGHT_ATTRIBUTE, hStr);
            }
        }

        // attach the referenced element to the current document
        SVGOMCSSImportedElementRoot root;
        root = new SVGOMCSSImportedElementRoot(document, e);
        root.appendChild(localRefElement);

        SVGOMUseElement ue = (SVGOMUseElement)e;
        ue.setCSSImportedElementRoot(root);

        Element g = localRefElement;

        // compute URIs and style sheets for the used element
        CSSUtilities.computeStyleAndURIs(refElement, localRefElement, uri);

        GVTBuilder builder = ctx.getGVTBuilder();
        GraphicsNode refNode = builder.build(ctx, g);

        ///////////////////////////////////////////////////////////////////////

        CompositeGraphicsNode gn = new CompositeGraphicsNode();
        gn.getChildren().add(refNode);

        gn.setTransform(computeTransform(e, ctx));

        // set an affine transform to take into account the (x, y)
        // coordinates of the <use> element

        // 'visibility'
        gn.setVisible(CSSUtilities.convertVisibility(e));

        // 'enable-background'
        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            gn.setBackgroundEnable(r);
        }
        return gn;
    }

    /**
     * Computes the AffineTransform for the node
     */
    protected AffineTransform computeTransform(Element e, BridgeContext ctx) {
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);

        // 'x' attribute - default is 0
        float x = 0;
        String s = e.getAttributeNS(null, SVG_X_ATTRIBUTE);
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_X_ATTRIBUTE, uctx);
        }

        // 'y' attribute - default is 0
        float y = 0;
        s = e.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_Y_ATTRIBUTE, uctx);
        }

        // set an affine transform to take into account the (x, y)
        // coordinates of the <use> element
        s = e.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);

        // 'transform'
        if (s.length() != 0) {
            at.preConcatenate
                (SVGUtilities.convertTransform(e, SVG_TRANSFORM_ATTRIBUTE, s));
        }

        return at;
     }

    /**
     * Creates the GraphicsNode depending on the GraphicsNodeBridge
     * implementation.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return null; // nothing to do, createGraphicsNode is fully overriden
    }

    /**
     * Returns false as the &lt;use> element is a not container.
     */
    public boolean isComposite() {
        return false;
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

        super.buildGraphicsNode(ctx, e, node);

        EventTarget target = (EventTarget)e;

        EventListener l = new CursorMouseOverListener(ctx.getUserAgent());
        target.addEventListener(SVG_EVENT_MOUSEOVER, l, false);
        ctx.storeEventListener(target, SVG_EVENT_MOUSEOVER, l, false);

    }

    /**
     * To handle a mouseover on an anchor and set the cursor.
     */
    public class CursorMouseOverListener implements EventListener {

        protected UserAgent userAgent;

        public CursorMouseOverListener(UserAgent ua) {
            userAgent = ua;
        }

        public void handleEvent(Event evt) {
            //
            // Only modify the cursor if the current target's (i.e., the <use>) cursor 
            // property is *not* 'auto'.
            //
            Element currentTarget = (Element)evt.getCurrentTarget();

            if (!CSSUtilities.isAutoCursor(currentTarget)) {
                Cursor cursor = CSSUtilities.convertCursor(currentTarget, ctx);
                if (cursor != null) {
                    userAgent.setSVGCursor(cursor);
                }
            }
        }
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_X_ATTRIBUTE) ||
            attrName.equals(SVG_Y_ATTRIBUTE) ||
            attrName.equals(SVG_TRANSFORM_ATTRIBUTE)) {
            String s = evt.getNewValue();
            node.setTransform(computeTransform(e, ctx));
            handleGeometryChanged();
        }
    }
}
