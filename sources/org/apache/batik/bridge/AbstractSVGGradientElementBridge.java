/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for vending gradients.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractSVGGradientElementBridge extends AbstractSVGBridge
    implements PaintBridge, ErrorConstants {

    protected BridgeContext ctx;

    protected Element paintElement;

    protected Element paintedElement;

    protected GraphicsNode paintedNode;

    protected float opacity;

    protected Paint paint;

    /**
     * Constructs a new AbstractSVGGradientElementBridge.
     */
    protected AbstractSVGGradientElementBridge() {}

    /**
     * Creates a <tt>Paint</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param paintElement the element that defines a Paint
     * @param paintedElement the element referencing the paint
     * @param paintedNode the graphics node on which the Paint will be applied
     * @param opacity the opacity of the Paint to create
     */
    public Paint createPaint(BridgeContext ctx,
                             Element paintElement,
                             Element paintedElement,
                             GraphicsNode paintedNode,
                             float opacity) {

        String s;

        // stop elements
        List stops = extractStop(paintElement, opacity, ctx);
        // if no stops are defined, painting is the same as 'none'
        if (stops == null) {
            return null;
        }
        int stopLength = stops.size();
        // if one stops is defined, painting is the same as a single color
        if (stopLength == 1) {
            return ((Stop)stops.get(0)).color;
        }
        float [] offsets = new float[stopLength];
        Color [] colors = new Color[stopLength];
        Iterator iter = stops.iterator();
        for (int i=0; iter.hasNext(); ++i) {
            Stop stop = (Stop)iter.next();
            offsets[i] = stop.offset;
            colors[i] = stop.color;
        }

        // 'spreadMethod' attribute - default is pad
        MultipleGradientPaint.CycleMethodEnum spreadMethod
            = MultipleGradientPaint.NO_CYCLE;
        s = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_SPREAD_METHOD_ATTRIBUTE, ctx);
        if (s.length() != 0) {
            spreadMethod = convertSpreadMethod(paintElement, s);
        }

        // 'color-interpolation' CSS property
        MultipleGradientPaint.ColorSpaceEnum colorSpace
            = CSSUtilities.convertColorInterpolation(paintElement);

        // 'gradientTransform' attribute - default is an Identity matrix
        AffineTransform transform;
        s = SVGUtilities.getChainableAttributeNS
            (paintElement, null, SVG_GRADIENT_TRANSFORM_ATTRIBUTE, ctx);
        if (s.length() != 0) {
            transform = SVGUtilities.convertTransform
                (paintElement, SVG_GRADIENT_TRANSFORM_ATTRIBUTE, s);
        } else {
            transform = new AffineTransform();
        }

        Paint paint = buildGradient(paintElement,
                                    paintedElement,
                                    paintedNode,
                                    spreadMethod,
                                    colorSpace,
                                    transform,
                                    colors,
                                    offsets,
                                    ctx);

        if (ctx.isDynamic()) {
            if (handler == null) { // quick hack to fix dynamic
                this.handler = ctx.getCurrentBridgeUpdateHandler();
                this.handlerKey = ctx.getCurrentBridgeUpdateHandlerKey();

                this.ctx = ctx;
                this.paintElement = paintElement;
                this.paintedElement = paintedElement;
                this.paintedNode = paintedNode;
                this.opacity = opacity;

                ((EventTarget)paintElement).addEventListener
                    ("DOMAttrModified",
                     new DOMAttrModifiedEventListener(),
                     false);
            }
            this.paint = paint;
        }

        return paint;
    }

    /**
     * Builds a concrete gradient according to the specified parameters.
     *
     * @param paintElement the element that defines a Paint
     * @param paintedElement the element referencing the paint
     * @param paintedNode the graphics node on which the Paint will be applied
     * @param spreadMethod the spread method
     * @param colorSpace the color space (sRGB | LinearRGB)
     * @param transform the gradient transform
     * @param colors the colors of the gradient
     * @param offsets the offsets
     * @param ctx the bridge context to use
     */
    protected abstract
        Paint buildGradient(Element paintElement,
                            Element paintedElement,
                            GraphicsNode paintedNode,
                            MultipleGradientPaint.CycleMethodEnum spreadMethod,
                            MultipleGradientPaint.ColorSpaceEnum colorSpace,
                            AffineTransform transform,
                            Color [] colors,
                            float [] offsets,
                            BridgeContext ctx);
    // dynamic support

    /**
     * Handles DOMAttrModified events.
     *
     * @param evt the DOM mutation event
     */
    protected void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_GRADIENT_TRANSFORM_ATTRIBUTE) ||
            attrName.equals(SVG_OFFSET_ATTRIBUTE) || // <stop> attribute
            attrName.equals(SVG_SPREAD_METHOD_ATTRIBUTE)) {

            BridgeUpdateEvent be = new BridgeUpdateEvent(this);
            be.setOldValue(paint);
            fireBridgeUpdateStarting(be);
            this.paint = createPaint
                (ctx, paintElement, paintedElement, paintedNode, opacity);
            be.setNewValue(paint);
            fireBridgeUpdateCompleted(be);
        } else {
            System.out.println("Unsupported attribute modification: "+attrName+
                               " on "+paintElement.getLocalName());
        }
    }

    /**
     * The listener class for 'DOMAttrModified' event.
     */
    protected class DOMAttrModifiedEventListener implements EventListener {

        /**
         * Handles 'DOMAttrModfied' events and deleguates to the
         * 'handleDOMAttrModifiedEvent' method any changes to the
         * GraphicsNode if any.
         *
         * @param evt the DOM event
         */
        public void handleEvent(Event evt) {
            Element e = (Element)evt.getTarget();
            // <!> FIXME: need to check if e is a stop
            // check if an attribute has changed on the gradient or on a stop
            if (e != paintElement && e.getParentNode() != paintElement) {
                return;
            }
            handleDOMAttrModifiedEvent((MutationEvent)evt);
        }
    }

    // convenient methods

    /**
     * Converts the spreadMethod attribute.
     *
     * @param paintElement the paint Element with a spreadMethod
     * @param s the spread method
     */
    protected static MultipleGradientPaint.CycleMethodEnum convertSpreadMethod
        (Element paintElement, String s) {
        if (SVG_REPEAT_VALUE.equals(s)) {
            return MultipleGradientPaint.REPEAT;
        }
        if (SVG_REFLECT_VALUE.equals(s)) {
            return MultipleGradientPaint.REFLECT;
        }
        if (SVG_PAD_VALUE.equals(s)) {
            return MultipleGradientPaint.NO_CYCLE;
        }
        throw new BridgeException
            (paintElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
             new Object[] {SVG_SPREAD_METHOD_ATTRIBUTE, s});
    }

    /**
     * Returns the stops elements of the specified gradient
     * element. Stops can be children of the gradients or defined on
     * one of its 'ancestor' (linked with the xlink:href attribute).
     *
     * @param paintElement the gradient element
     * @param opacity the opacity
     * @param ctx the bridge context to use
     */
    protected static List extractStop(Element paintElement,
                                      float opacity,
                                      BridgeContext ctx) {

        List refs = new LinkedList();
        for (;;) {
            List stops = extractLocalStop(paintElement, opacity, ctx);
            if (stops != null) {
                return stops; // stop elements found, exit
            }
            String uri = XLinkSupport.getXLinkHref(paintElement);
            if (uri.length() == 0) {
                return null; // no xlink:href found, exit
            }
            // check if there is circular dependencies
            SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
            URL url;
            try {
                url = new URL(doc.getURLObject(), uri);
            } catch (MalformedURLException ex) {
                throw new BridgeException(paintElement,
                                          ERR_URI_MALFORMED,
                                          new Object[] {uri});

            }
            if (contains(refs, url)) {
                throw new BridgeException(paintElement,
                                          ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES,
                                          new Object[] {uri});
            }
            refs.add(url);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }

    /**
     * Returns a list of <tt>Stop</tt> elements, children of the
     * specified paintElement can have or null if any.
     *
     * @param gradientElement the paint element
     * @param opacity the opacity
     * @param ctx the bridge context
     */
    protected static List extractLocalStop(Element gradientElement,
                                           float opacity,
                                           BridgeContext ctx) {
        LinkedList stops = null;
        Stop previous = null;
        for (Node n = gradientElement.getFirstChild();
             n != null;
             n = n.getNextSibling()) {

            if ((n.getNodeType() != Node.ELEMENT_NODE)) {
                continue;
            }

            Element e = (Element)n;
            Bridge bridge = ctx.getBridge(e);
            if (bridge == null || !(bridge instanceof SVGStopElementBridge)) {
                continue;
            }
            Stop stop = ((SVGStopElementBridge)bridge).createStop
                (ctx, gradientElement, e, opacity);
            if (stops == null) {
                stops = new LinkedList();
            }
            if (previous != null) {
                if (stop.offset < previous.offset) {
                    stop.offset = previous.offset;
                }
            }
            stops.add(stop);
            previous = stop;
        }
        return stops;
    }

    /**
     * Returns true if the specified list of URLs contains the specified url.
     *
     * @param urls the list of URLs
     * @param key the url to search for
     */
    private static boolean contains(List urls, URL key) {
        Iterator iter = urls.iterator();
        while (iter.hasNext()) {
            URL url = (URL)iter.next();
            if (url.sameFile(key) && url.getRef().equals(key.getRef())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This class represents a gradient &lt;stop> element.
     */
    public static class Stop {

        /** The stop color. */
        public Color color;
        /** The stop offset. */
        public float offset;

        /**
         * Constructs a new stop definition.
         *
         * @param color the stop color
         * @param offset the stop offset
         */
        public Stop(Color color, float offset) {
            this.color = color;
            this.offset = offset;
        }
    }

    /**
     * Bridge class for the gradient &lt;stop> element.
     */
    public static class SVGStopElementBridge extends AbstractSVGBridge
        implements Bridge {

        /**
         * Returns 'stop'.
         */
        public String getLocalName() {
            return SVG_STOP_TAG;
        }

        /**
         * Creates a <tt>Stop</tt> according to the specified parameters.
         *
         * @param ctx the bridge context to use
         * @param gradientElement the gradient element
         * @param stopElement the stop element
         * @param opacity an additional opacity of the stop color
         */
        public Stop createStop(BridgeContext ctx,
                               Element gradientElement,
                               Element stopElement,
                               float opacity) {

            String s = stopElement.getAttributeNS(null, SVG_OFFSET_ATTRIBUTE);
            if (s.length() == 0) {
                throw new BridgeException(stopElement, ERR_ATTRIBUTE_MISSING,
                                          new Object[] {SVG_OFFSET_ATTRIBUTE});
            }
            float offset;
            try {
                offset = SVGUtilities.convertRatio(s);
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (stopElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {SVG_OFFSET_ATTRIBUTE, s, ex});
            }
            Color color
                = CSSUtilities.convertStopColor(stopElement, opacity, ctx);

            return new Stop(color, offset);
        }
    }
}
