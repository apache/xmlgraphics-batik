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
import java.awt.Cursor;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.io.StringReader;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.css.HiddenChildElementSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.views.DocumentView;

/**
 * A factory for the &lt;a&gt; SVG element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGAElementBridge implements GraphicsNodeBridge, SVGConstants {

    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element element){

        CompositeGraphicsNode gn;
        gn = ctx.getGVTFactory().createCompositeGraphicsNode();

        // Initialize the transform
        AffineTransform at =
            SVGUtilities.convertAffineTransform(element,
                                                ATTR_TRANSFORM,
                                                ctx.getParserFactory());

        gn.setTransform(at);

        CSSStyleDeclaration decl;
        decl = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, decl);

        Rectangle2D rect = CSSUtilities.convertEnableBackground((SVGElement)element,
                                                                decl,
                                                                uctx);
        if (rect != null) {
            gn.setBackgroundEnable(rect);
        }

        return gn;
    }

    public void buildGraphicsNode(GraphicsNode gn, 
                                  BridgeContext ctx,
                                  Element element) {
        CSSStyleDeclaration decl;
        decl = ctx.getViewCSS().getComputedStyle(element, null);
        CSSPrimitiveValue val =
            (CSSPrimitiveValue)decl.getPropertyCSSValue(ATTR_OPACITY);
        Composite composite = CSSUtilities.convertOpacityToComposite(val);
        gn.setComposite(composite);

        Filter filter = CSSUtilities.convertFilter(element, gn, ctx);
        gn.setFilter(filter);

        Mask mask = CSSUtilities.convertMask(element, gn, ctx);
        gn.setMask(mask);

        Clip clip = CSSUtilities.convertClipPath(element, gn, ctx);
        gn.setClip(clip);

        EventTarget et = (EventTarget)element;
        et.addEventListener("click",
                            new AnchorListener(ctx.getUserAgent()), false);
        et.addEventListener("mouseover",
                            new CursorMouseOverListener(ctx.getUserAgent()),
                            false);
        et.addEventListener("mouseout",
                            new CursorMouseOutListener(ctx.getUserAgent()),
                            false);

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, element);
        ctx.bind(element, gn);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return true;
    }

    /**
     * To handle a click on an anchor.
     */
    protected static class AnchorListener implements EventListener {

        protected UserAgent userAgent;

        public AnchorListener(UserAgent ua) {
            userAgent = ua;
        }

        public void handleEvent(Event evt) {
            SVGAElement elt = null;
            for (Element e = (Element)evt.getTarget();
                 e != null;
                 e = HiddenChildElementSupport.getParentElement(e)) {
                if (e instanceof SVGAElement) {
                    elt = (SVGAElement)e;
                    break;
                }
            }
            Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            userAgent.setSVGCursor(cursor);
            userAgent.openLink(elt);
        }
    }

    /**
     * To handle a mouseover on an anchor and set the cursor.
     */
    protected static class CursorMouseOverListener implements EventListener {

        protected UserAgent userAgent;

        public CursorMouseOverListener(UserAgent ua) {
            userAgent = ua;
        }

        public void handleEvent(Event evt) {
            SVGAElement elt = null;
            for (Element e = (Element)evt.getTarget();
                 e != null;
                 e = HiddenChildElementSupport.getParentElement(e)) {
                if (e instanceof SVGAElement) {
                    elt = (SVGAElement)e;
                    break;
                }
            }
            Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            userAgent.setSVGCursor(cursor);
            if (elt != null) {
                userAgent.displayMessage(elt.getHref().getBaseVal());
            }
        }
    }

    /**
     * To handle a mouseout on an anchor and set the cursor.
     */
    protected static class CursorMouseOutListener implements EventListener {

        protected UserAgent userAgent;

        public CursorMouseOutListener(UserAgent ua) {
            userAgent = ua;
        }

        public void handleEvent(Event evt) {
            SVGAElement elt = null;
            for (Element e = (Element)evt.getTarget();
                 e != null;
                 e = HiddenChildElementSupport.getParentElement(e)) {
                if (e instanceof SVGAElement) {
                    elt = (SVGAElement)e;
                    break;
                }
            }
            Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            userAgent.setSVGCursor(cursor);
            userAgent.displayMessage("");
        }
    }
}
