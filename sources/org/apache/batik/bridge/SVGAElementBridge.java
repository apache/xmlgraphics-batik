/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Cursor;

import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAElement;

/**
 * Bridge class for the &lt;a> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGAElementBridge extends AbstractGraphicsNodeBridge {

    /**
     * Constructs a new bridge for the &lt;a> element.
     */
    public SVGAElementBridge() {}

    /**
     * Returns 'a'.
     */
    public String getLocalName() {
        return SVG_A_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGAElementBridge();
    }

    /**
     * Creates a <tt>CompositeGraphicsNode</tt>.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new CompositeGraphicsNode();
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

        EventListener l = new AnchorListener(ctx.getUserAgent());
        target.addEventListener(SVG_EVENT_CLICK, l, false);
        ctx.storeEventListener(target, SVG_EVENT_CLICK, l, false);

        l = new CursorMouseOverListener(ctx.getUserAgent());
        target.addEventListener(SVG_EVENT_MOUSEOVER, l, false);
        ctx.storeEventListener(target, SVG_EVENT_MOUSEOVER, l, false);

        l = new CursorMouseOutListener(ctx.getUserAgent());
        target.addEventListener(SVG_EVENT_MOUSEOUT, l, false);
        ctx.storeEventListener(target, SVG_EVENT_MOUSEOUT, l, false);
    }

    /**
     * Returns true as the &lt;a> element is a container.
     */
    public boolean isComposite() {
        return true;
    }

    /**
     * To handle a click on an anchor.
     */
    public static class AnchorListener implements EventListener {

        protected UserAgent userAgent;

        public AnchorListener(UserAgent ua) {
            userAgent = ua;
        }

        public void handleEvent(Event evt) {
            SVGAElement elt = (SVGAElement)evt.getCurrentTarget();
            Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            userAgent.setSVGCursor(cursor);
            userAgent.openLink(elt);
            evt.stopPropagation();
        }
    }

    /**
     * To handle a mouseover on an anchor and set the cursor.
     */
    public static class CursorMouseOverListener implements EventListener {

        protected UserAgent userAgent;

        public CursorMouseOverListener(UserAgent ua) {
            userAgent = ua;
        }

        public void handleEvent(Event evt) {
            //
            // Only modify the cursor if the target's cursor property is 
            // 'auto'. Note that we do not need to check the value of 
            // anchor element as the target's cursor value is resulting
            // from the CSS cascade which has accounted for inheritance.
            // This means that our behavior is to set the cursor to a 
            // hand cursor for any content on which the cursor property is
            // 'auto' inside an anchor element. If, for example, the 
            // content was:
            // <a cusor="wait">
            //    <g cursor="auto">
            //       <rect />
            //    </g>
            // </a>
            //
            // The cursor on the inside rect will be set to the hand cursor and
            // not the wait cursor
            //
            Element target = (Element)evt.getTarget();
            
            if (CSSUtilities.isAutoCursor(target)) {
                // The target's cursor value is 'auto': use the hand cursor
                userAgent.setSVGCursor(CursorManager.ANCHOR_CURSOR);
            }
            
            // 
            // In all cases, display the href in the userAgent
            //

            SVGAElement elt = (SVGAElement)evt.getCurrentTarget();
            if (elt != null) {
                String href = XLinkSupport.getXLinkHref(elt);
                userAgent.displayMessage(href);
            }
        }
    }

    /**
     * To handle a mouseout on an anchor and set the cursor.
     */
    public static class CursorMouseOutListener implements EventListener {

        protected UserAgent userAgent;

        public CursorMouseOutListener(UserAgent ua) {
            userAgent = ua;
        }

        public void handleEvent(Event evt) {
            // No need to set the cursor on out events: this is taken care of
            // by the BridgeContext
            
            // Hide the href in the userAgent
            SVGAElement elt = (SVGAElement)evt.getCurrentTarget();
            if (elt != null) {
                String href = XLinkSupport.getXLinkHref(elt);
                userAgent.displayMessage("");
            }
        }
    }
}
