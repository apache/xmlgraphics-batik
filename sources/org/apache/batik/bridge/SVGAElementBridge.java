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

import java.awt.Cursor;

import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.UIEvent;
import org.w3c.dom.svg.SVGAElement;

/**
 * Bridge class for the &lt;a> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGAElementBridge extends SVGGElementBridge {

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

        if (ctx.isInteractive()) {
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
            if (AbstractEvent.getEventPreventDefault(evt))
                return;
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
            if (AbstractEvent.getEventPreventDefault(evt))
                return;
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
            if (AbstractEvent.getEventPreventDefault(evt))
                return;
            // No need to set the cursor on out events: this is taken care of
            // by the BridgeContext
            
            // Hide the href in the userAgent
            SVGAElement elt = (SVGAElement)evt.getCurrentTarget();
            if (elt != null) {
                userAgent.displayMessage("");
            }
        }
    }
}
