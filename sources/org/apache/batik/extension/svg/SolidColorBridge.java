/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import java.awt.Color;
import java.awt.Paint;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.PaintServer;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.ICCColor;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.CSSConstants;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGColor;

/**
 * Bridge class for a regular polygon element.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas Deweese</a>
 */
public class SolidColorBridge 
    extends AbstractSVGBridge
    implements PaintBridge, BatikExtConstants, CSSConstants, ErrorConstants {

    /**
     * Constructs a new bridge for the &lt;rect> element.
     */
    public SolidColorBridge() { /* nothing */ }

    /**
     * Returns the SVG namespace URI.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns 'rect'.
     */
    public String getLocalName() {
        return BATIK_EXT_SOLID_COLOR_TAG;
    }

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

        opacity = extractOpacity(paintElement, opacity, ctx);

        return extractColor(paintElement, opacity, ctx);
    }

    protected static float extractOpacity(Element paintElement, 
                                          float opacity,
                                          BridgeContext ctx) {
        Map refs = new HashMap();
        CSSEngine eng = CSSUtilities.getCSSEngine(paintElement);
        int pidx = eng.getPropertyIndex(BATIK_EXT_SOLID_OPACITY_PROPERTY);

        for (;;) {
            Value opacityVal =
                CSSUtilities.getComputedStyle(paintElement, pidx);
        
            // Was solid-opacity explicity set on this element?
            StyleMap sm =
                ((CSSStylableElement)paintElement).getComputedStyleMap(null);
            if (!sm.isNullCascaded(pidx)) {
                // It was explicit...
                float attr = PaintServer.convertOpacity(opacityVal);
                return (opacity * attr);
            }

            String uri = XLinkSupport.getXLinkHref(paintElement);
            if (uri.length() == 0) {
                return opacity; // no xlink:href found, exit
            }

            SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
            URL url;
            try {
                url = new URL(doc.getURLObject(), uri);
            } catch (MalformedURLException ex) {
                throw new BridgeException(paintElement,
                                          ERR_URI_MALFORMED,
                                          new Object[] {uri});
            
            }
            // check if there is circular dependencies
            if (refs.containsKey(url)) {
                throw new BridgeException(paintElement,
                                          ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES,
                                          new Object[] {uri});
            }
            refs.put(url, url);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }

    protected static Color extractColor(Element paintElement, 
                                        float opacity,
                                        BridgeContext ctx) {
        Map refs = new HashMap();
        CSSEngine eng = CSSUtilities.getCSSEngine(paintElement);
        int pidx = eng.getPropertyIndex(BATIK_EXT_SOLID_COLOR_PROPERTY);

        for (;;) {
            Value colorDef =
                CSSUtilities.getComputedStyle(paintElement, pidx);
        
            // Was solid-color explicity set on this element?
            StyleMap sm =
                ((CSSStylableElement)paintElement).getComputedStyleMap(null);
            if (!sm.isNullCascaded(pidx)) {
                // It was explicit...
                if (colorDef.getCssValueType() ==
                    CSSValue.CSS_PRIMITIVE_VALUE) {
                    return PaintServer.convertColor(colorDef, opacity);
                } else {
                    return PaintServer.convertRGBICCColor
                        (paintElement, colorDef.item(0),
                         (ICCColor)colorDef.item(1),
                         opacity, ctx);
                }
            }


            String uri = XLinkSupport.getXLinkHref(paintElement);
            if (uri.length() == 0) {
                // no xlink:href found, exit    
                return new Color(0, 0, 0, opacity); 
            }

            SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
            URL url;
            try {
                url = new URL(doc.getURLObject(), uri);
            } catch (MalformedURLException ex) {
                throw new BridgeException(paintElement,
                                          ERR_URI_MALFORMED,
                                          new Object[] {uri});
            
            }
            // check if there is circular dependencies
            if (refs.containsKey(url)) {
                throw new BridgeException
                    (paintElement,
                     ERR_XLINK_HREF_CIRCULAR_DEPENDENCIES,
                     new Object[] {uri});
            }
            refs.put(url, url);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }
}
