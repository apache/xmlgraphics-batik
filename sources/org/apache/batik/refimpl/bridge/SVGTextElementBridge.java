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
import java.awt.Stroke;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import java.io.StringReader;

import java.text.AttributedString;

import java.util.HashMap;
import java.util.Map;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Element;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.svg.SVGElement;

/**
 * A factory for the &lt;text&gt; SVG element.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGTextElementBridge implements GraphicsNodeBridge, SVGConstants {
    protected final static Map fonts = new HashMap(11);
    static {
        fonts.put("serif",           "Serif");
        fonts.put("Times",           "Serif");
        fonts.put("Times New Roman", "Serif");
        fonts.put("Garamond",        "Serif");
        fonts.put("sans-serif",      "SansSerif");
        fonts.put("Arial",           "SansSerif");
        fonts.put("Helvetica",       "SansSerif");
        fonts.put("Verdana",         "SansSerif");
        fonts.put("cursive",         "Dialog");
        fonts.put("fantasy",         "Symbol");
        fonts.put("monospace",       "Monospaced");
        fonts.put("monospaced",      "Monospaced");
        fonts.put("Courier",         "Monospaced");
        fonts.put("Courier New",     "Monospaced");
    }

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
        String sp = XMLSupport.getXMLSpace(element);
        if (sp.equals("preserve")) {
            text = XMLSupport.preserveXMLSpace(text);
        } else {
            text = XMLSupport.defaultXMLSpace(text);
        }
        text = (text.length() == 0) ? " " : text;
        AttributedString as;
        as = new AttributedString(text);

        // Font size
        float fs = CSSUtilities.convertFontSize((SVGElement)element,
                                                ctx,
                                                cssDecl,
                                                uctx);
        fs *= 0.92; // Font size correction

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

        // Font family
        CSSValueList ff = (CSSValueList)cssDecl.getPropertyCSSValue
            (FONT_FAMILY_PROPERTY);
        s = null;
        for (int i = 0; s == null && i < ff.getLength(); i++) {
            v = (CSSPrimitiveValue)ff.item(i);
            s = (String)fonts.get(v.getStringValue());
        }
        s = (s == null) ? "SansSerif" : s;
        as.addAttribute(TextAttribute.FAMILY, s);

        // Font weight
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (FONT_WEIGHT_PROPERTY);
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            if (v.getStringValue().charAt(0) == 'n') {
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_REGULAR);
            } else {
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_BOLD);
            }
        } else {
            switch ((int)v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER)) {
            case 100:
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_EXTRA_LIGHT);
                break;
            case 200:
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_LIGHT);
                break;
            case 300:
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_DEMILIGHT);
                break;
            case 400:
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_REGULAR);
                break;
            case 500:
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_SEMIBOLD);
                break;
            case 600:
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_DEMIBOLD);
                break;
            case 700:
                as.addAttribute(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_BOLD);
                break;
            case 800:
                as.addAttribute(TextAttribute.WEIGHT,
                                //TextAttribute.WEIGHT_EXTRABOLD);
                                TextAttribute.WEIGHT_BOLD);
                break;
            case 900:
                as.addAttribute(TextAttribute.WEIGHT,
                                //TextAttribute.WEIGHT_ULTRABOLD);
                                TextAttribute.WEIGHT_BOLD);
            }
        }

        // Font style
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (FONT_STYLE_PROPERTY);
        s = v.getStringValue();
        switch (s.charAt(0)) {
        case 'n':
            as.addAttribute(TextAttribute.POSTURE,
                            TextAttribute.POSTURE_REGULAR);
            break;
        case 'o':
        case 'i':
            as.addAttribute(TextAttribute.POSTURE,
                            TextAttribute.POSTURE_OBLIQUE);
        }

        // Font stretch
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (FONT_STRETCH_PROPERTY);
        s = v.getStringValue();
        switch (s.charAt(0)) {
        case 'u':
            if (s.charAt(6) == 'c') {
                as.addAttribute(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_CONDENSED);
            } else {
                as.addAttribute(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_EXTENDED);
            }
            break;
        case 'e':
            if (s.charAt(6) == 'c') {
                as.addAttribute(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_CONDENSED);
            } else {
                if (s.length() == 8) {
                    as.addAttribute(TextAttribute.WIDTH,
                                    TextAttribute.WIDTH_SEMI_EXTENDED);
                } else {
                    as.addAttribute(TextAttribute.WIDTH,
                                    TextAttribute.WIDTH_EXTENDED);
                }
            }
            break;
        case 's':
            if (s.charAt(6) == 'c') {
                as.addAttribute(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_SEMI_CONDENSED);
            } else {
                as.addAttribute(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_SEMI_EXTENDED);
            }
            break;
        default:
            as.addAttribute(TextAttribute.WIDTH,
                            TextAttribute.WIDTH_REGULAR);
        }

        // Text decoration
        CSSValue cssVal = cssDecl.getPropertyCSSValue
            (TEXT_DECORATION_PROPERTY);
        short t = cssVal.getCssValueType();
        if (t == CSSValue.CSS_VALUE_LIST) {
            CSSValueList lst = (CSSValueList)cssVal;
            for (int i = 0; i < lst.getLength(); i++) {
                v = (CSSPrimitiveValue)lst.item(i);
                s = v.getStringValue();
                switch (s.charAt(0)) {
                case 'u':
                    as.addAttribute(TextAttribute.UNDERLINE,
                                    TextAttribute.UNDERLINE_ON);
                    break;
                case 'o':
                    // overline
                    break;
                case 'l':
                    as.addAttribute(TextAttribute.STRIKETHROUGH,
                                    TextAttribute.STRIKETHROUGH_ON);
                }
            }
        }


        // Fill
        Paint p = CSSUtilities.convertFillToPaint(cssDecl);
        if (p != null) {
            as.addAttribute(TextAttribute.FOREGROUND, p);
        }

        // Stroke Paint
        p = CSSUtilities.convertStrokeToPaint(cssDecl);
        if (p != null) {
            as.addAttribute
                (GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT, p);
        }

        // Stroke
        Stroke stroke
            = CSSUtilities.convertStrokeToBasicStroke((SVGElement)element,
                                                      ctx,
                                                      cssDecl,
                                                      uctx);

        if(stroke != null){
            as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.STROKE,
                            stroke);
        }

        result.setAttributedCharacterIterator(as.getIterator());

        Filter filter = CSSUtilities.convertFilter(element, result, ctx);
        result.setFilter(filter);

        Mask mask = CSSUtilities.convertMask(element, result, ctx);
        result.setMask(mask);

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, element);
        ctx.bind(element, result);

        return result;
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }
}
