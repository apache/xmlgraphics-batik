/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import java.io.StringReader;

import java.text.AttributedString;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGElement;

/**
 * A factory for the &lt;text&gt; SVG element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGTextElementBridge implements GraphicsNodeBridge, SVGConstants {

    public GraphicsNode createGraphicsNode(BridgeContext ctx,
                                           Element element){
        TextNode result = ctx.getGVTFactory().createTextNode();
        result.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        // Transform
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(element.getAttributeNS(null, ATTR_TRANSFORM)),
             ctx.getParserFactory());
        result.setTransform(at);

        // Location
        String s = element.getAttributeNS(null, ATTR_X);
        float x = UnitProcessor.svgToUserSpace(s,
                                               (SVGElement)element,
                                               UnitProcessor.HORIZONTAL_LENGTH,
                                               uctx);
        s = element.getAttributeNS(null, ATTR_Y);
        float y = UnitProcessor.svgToUserSpace(s,
                                               (SVGElement)element,
                                               UnitProcessor.VERTICAL_LENGTH,
                                               uctx);
        
        result.setLocation(new Point2D.Float(x, y));

        // !!! TODO better text handling
        element.normalize();
        String text = element.getFirstChild().getNodeValue();
        text = XMLSupport.defaultXMLSpace(text);
        text = (text.length() == 0) ? " " : text;
        AttributedString as;
        as = new AttributedString(text);

        // Font size
        float fs = CSSUtilities.convertFontSize((SVGElement)element,
                                                ctx,
                                                cssDecl,
                                                uctx);

        as.addAttribute(TextAttribute.SIZE, new Float(fs));

        // Text-anchor
        CSSPrimitiveValue v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (TEXT_ANCHOR_PROPERTY);
        s = v.getStringValue();
        TextNode.Anchor a;
        switch (s.charAt(0)) {
        case 's':
            a = TextNode.Anchor.START;
            break;
        case 'm':
            a = TextNode.Anchor.MIDDLE;
            break;
        default:
            a = TextNode.Anchor.END;
        }
        result.setAnchor(a);

        Paint p = CSSUtilities.convertFillToPaint(cssDecl);
        if (p != null) {
            as.addAttribute(TextAttribute.FOREGROUND, p);
        }

        as.addAttribute(TextAttribute.FAMILY, "Arial");

        result.setAttributedCharacterIterator(as.getIterator());
        return result;
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }
}
