/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import java.io.StringReader;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
        fonts.put("sans-serif",      "SansSerif");
        fonts.put("cursive",         "Dialog");
        fonts.put("fantasy",         "Symbol");
        fonts.put("monospace",       "Monospaced");
        fonts.put("monospaced",      "Monospaced");
        fonts.put("Courier",         "Monospaced");

        //
        // Load all fonts. Work around
        //
        GraphicsEnvironment env;
        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        // System.out.println("Initializing fonts .... please wait");
        String fontNames[] = env.getAvailableFontFamilyNames();
        int nFonts = fontNames != null ? fontNames.length : 0;
        // System.out.println("Done initializing " + nFonts + " fonts");
        for(int i=0; i<nFonts; i++){
            fonts.put(fontNames[i], fontNames[i]);
        }
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

        // parse the x attribute, (default is 0)
        String s = element.getAttributeNS(null, ATTR_X);
        float x = 0;
        if (s.length() != 0) {
            x = UnitProcessor.svgToUserSpace(s,
                                             (SVGElement)element,
                                             UnitProcessor.HORIZONTAL_LENGTH,
                                             uctx);
        }

        // parse the y attribute, (default is 0)
        s = element.getAttributeNS(null, ATTR_Y);
        float y = 0;
        if (s.length() != 0) {
            y = UnitProcessor.svgToUserSpace(s,
                                             (SVGElement)element,
                                             UnitProcessor.VERTICAL_LENGTH,
                                             uctx);
        }

        result.setLocation(new Point2D.Float(x, y));

        return result;
    }

    public void buildGraphicsNode(GraphicsNode node, BridgeContext ctx,
                                  Element element) {
        TextNode result = (TextNode)node;

        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        // Text-anchor
        CSSPrimitiveValue v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (TEXT_ANCHOR_PROPERTY);
        String s = v.getStringValue();
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

        element.normalize();

        AttributedString as = buildAttributedString(ctx,
                                                    element,
                                                    result);
        result.setAttributedCharacterIterator(as.getIterator());

        // Set the node filter
        Filter filter = CSSUtilities.convertFilter(element, result, ctx);
        result.setFilter(filter);

        // Set the node mask
        Mask mask = CSSUtilities.convertMask(element, result, ctx);
        result.setMask(mask);

        // Set the node clip
        Clip clip = CSSUtilities.convertClipPath(element, result, ctx);
        result.setClip(clip);

        // <!> TODO only when binding is enabled
        BridgeEventSupport.addDOMListener(ctx, element);
        ctx.bind(element, result);
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }

    /**
     * Creates the attributed string which represent the given text
     * element children.
     */
    protected AttributedString buildAttributedString(BridgeContext ctx,
                                                     Element element,
                                                     GraphicsNode node) {
        AttributedString result = null;
        List l = buildAttributedStrings(ctx,
                                        element,
                                        node,
                                        true);
        // Simple cases
        switch (l.size()) {
        case 0:
            return new AttributedString(" ");
        case 1:
            return (AttributedString)l.get(0);
        }

        //
        // Merge the attributed strings
        //
        List buffers = new LinkedList();
        List maps = new LinkedList();
        Iterator it = l.iterator();
        // Build the StringBuffer list and the attribute map list.
        while (it.hasNext()) {
            AttributedString s = (AttributedString)it.next();
            AttributedCharacterIterator aci = s.getIterator();
            char c = aci.first();
            StringBuffer sb = new StringBuffer();
            Map m = aci.getAttributes();
            for (; c != CharacterIterator.DONE; c = aci.next()) {
                sb.append(c);
            }
            buffers.add(sb);
            maps.add(m);
        }
        // Build the attributed string
        it = buffers.iterator();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            sb.append(it.next());
        }
        result = new AttributedString(sb.toString());

        // Decorate the attributed string
        int i = 0;
        it = buffers.iterator();
        Iterator it2 = maps.iterator();
        while (it.hasNext()) {
            sb = (StringBuffer)it.next();
            result.addAttributes((Map)it2.next(), i, i += sb.length());
        }
        return result;
    }

    /**
     * Creates the attributed strings which represent the given text
     * element children.
     */
    protected List buildAttributedStrings(BridgeContext ctx,
                                          Element element,
                                          GraphicsNode node,
                                          boolean top) {
        // !!! return two lists

        List result = new LinkedList();
        Map m = getAttributeMap(ctx, element, node);
        String s = XMLSupport.getXMLSpace(element);
        boolean preserve = s.equals("preserve");
        boolean first = true;
        boolean last;
        for (Node n = element.getFirstChild();
             n != null;
             n = n.getNextSibling()) {

            last = n.getNextSibling() == null;

            switch (n.getNodeType()) {
            case Node.ELEMENT_NODE:
                if (n.getLocalName().equals("tspan")) {
                    result.addAll(buildAttributedStrings(ctx,
                                                         (Element)n,
                                                         node,
                                                         false));
                } else if (n.getLocalName().equals("tref")) {
                    s = XLinkSupport.getXLinkHref((Element)n);
                    if (s.startsWith("#")) {
                        Document doc = n.getOwnerDocument();
                        Element ref = doc.getElementById(s.substring(1));
                        s = getElementContent(ref);
                        AttributedString as;
                        Map map = getAttributeMap(ctx, (Element)n, node);
                        as = createAttributedString(s, map, preserve, top,
                                                    first, last);
                        if (as != null) {
                            result.add(as);
                        }
                    } else {
                        System.out.println(" !!! <tref> Non local URI");
                    }
                }
                break;
            case Node.TEXT_NODE:
                s = n.getNodeValue();
                AttributedString as = createAttributedString(s, m, preserve,
                                                             top, first, last);
                if (as != null) {
                    result.add(as);
                }
            }
            first = false;
        }
        return result;
    }

    /**
     * Returns the content of the given element.
     */
    protected String getElementContent(Element e) {
        StringBuffer result = new StringBuffer();
        for (Node n = e.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            switch (n.getNodeType()) {
            case Node.ELEMENT_NODE:
                result.append(getElementContent((Element)n));
                break;
            case Node.TEXT_NODE:
                result.append(n.getNodeValue());
            }
        }
        return result.toString();
    }

    /**
     * Creates an attributes string from the content of the given string.
     */
    protected AttributedString createAttributedString(String s,
                                                      Map m,
                                                      boolean preserve,
                                                      boolean top,
                                                      boolean first,
                                                      boolean last) {
        StringBuffer sb = new StringBuffer();
        if (preserve) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                case 10:
                case 13:
                case '\t':
                    sb.append(' ');
                    break;
                default:
                    sb.append(c);
                }
            }
        } else {
            boolean space = false;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                case 10:
                case 13:
                    // I don't think behavior below is correct for tspan...
                    // including "space = false" means that newlines
                    // cause leading whitespace on next line to turn into
                    // a single space!
                    // space = false;
                    break; // should break, newlines are not whitespace
                case ' ':
                case '\t':
                    if (!space) {
                        sb.append(' ');
                        space = true;
                    }
                    break;
                default:
                    sb.append(c);
                    space = false;

                }
            }
            if (top) {
                if (first) {
                    while (sb.length() > 0) {
                        if (sb.charAt(0) == ' ') {
                            sb.deleteCharAt(0);
                        } else {
                            break;
                        }
                    }
                }
                if (last) {
                    int len;
                    while ((len = sb.length()) > 0) {
                        if (sb.charAt(len - 1) == ' ') {
                            sb.deleteCharAt(len - 1);
                        } else {
                            break;
                        }
                    }
                }
             }
        }
        if (sb.length() > 0) {
            return new AttributedString(sb.toString(), m);
        }
        return null;
    }

    /**
     * Returns the map to pass to the current characters.
     */
    protected Map getAttributeMap(BridgeContext ctx,
                                  Element element,
                                  GraphicsNode node) {
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx, cssDecl);

        Map result = new HashMap();
        CSSPrimitiveValue v;

        // Font size
        float fs = CSSUtilities.convertFontSize((SVGElement)element,
                                                ctx,
                                                cssDecl,
                                                uctx);

        fs = fs * ctx.getUserAgent().getPixelToMM() * 72f / 25.4f;

        result.put(TextAttribute.SIZE, new Float(fs));

        // Font family
        CSSValueList ff = (CSSValueList)cssDecl.getPropertyCSSValue
            (FONT_FAMILY_PROPERTY);
        String s = null;
        for (int i = 0; s == null && i < ff.getLength(); i++) {
            v = (CSSPrimitiveValue)ff.item(i);
            s = (String)fonts.get(v.getStringValue());
        }
        s = (s == null) ? "SansSerif" : s;
        result.put(TextAttribute.FAMILY, s);

        // Font weight
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (FONT_WEIGHT_PROPERTY);
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            if (v.getStringValue().charAt(0) == 'n') {
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_REGULAR);
            } else {
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_BOLD);
            }
        } else {
            switch ((int)v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER)) {
            case 100:
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_EXTRA_LIGHT);
                break;
            case 200:
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_LIGHT);
                break;
            case 300:
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_DEMILIGHT);
                break;
            case 400:
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_REGULAR);
                break;
            case 500:
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_SEMIBOLD);
                break;
            case 600:
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_DEMIBOLD);
                break;
            case 700:
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_BOLD);
                break;
            case 800:
                result.put(TextAttribute.WEIGHT,
                           //TextAttribute.WEIGHT_EXTRABOLD);
                           TextAttribute.WEIGHT_BOLD);
                break;
            case 900:
                result.put(TextAttribute.WEIGHT,
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
            result.put(TextAttribute.POSTURE,
                       TextAttribute.POSTURE_REGULAR);
            break;
        case 'o':
        case 'i':
            result.put(TextAttribute.POSTURE,
                       TextAttribute.POSTURE_OBLIQUE);
        }

        // Font stretch
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (FONT_STRETCH_PROPERTY);
        s = v.getStringValue();
        switch (s.charAt(0)) {
        case 'u':
            if (s.charAt(6) == 'c') {
                result.put(TextAttribute.WIDTH,
                           TextAttribute.WIDTH_CONDENSED);
            } else {
                result.put(TextAttribute.WIDTH,
                           TextAttribute.WIDTH_EXTENDED);
            }
            break;
        case 'e':
            if (s.charAt(6) == 'c') {
                result.put(TextAttribute.WIDTH,
                           TextAttribute.WIDTH_CONDENSED);
            } else {
                if (s.length() == 8) {
                    result.put(TextAttribute.WIDTH,
                               TextAttribute.WIDTH_SEMI_EXTENDED);
                } else {
                    result.put(TextAttribute.WIDTH,
                               TextAttribute.WIDTH_EXTENDED);
                }
            }
            break;
        case 's':
            if (s.charAt(6) == 'c') {
                result.put(TextAttribute.WIDTH,
                           TextAttribute.WIDTH_SEMI_CONDENSED);
            } else {
                result.put(TextAttribute.WIDTH,
                           TextAttribute.WIDTH_SEMI_EXTENDED);
            }
            break;
        default:
            result.put(TextAttribute.WIDTH,
                       TextAttribute.WIDTH_REGULAR);
        }

        // Fill
        Paint p = CSSUtilities.convertFillToPaint((SVGElement)element,
                                                  node,
                                                  ctx,
                                                  cssDecl,
                                                  uctx);

        if (p != null) {
            result.put(TextAttribute.FOREGROUND, p);
        }

        // Stroke Paint
        Paint sp = CSSUtilities.convertStrokeToPaint((SVGElement)element,
                                              node,
                                              ctx,
                                              cssDecl,
                                              uctx);

        if (sp != null) {
            result.put
                (GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT, sp);
        }

        // Stroke
        Stroke stroke
            = CSSUtilities.convertStrokeToBasicStroke((SVGElement)element,
                                                      ctx,
                                                      cssDecl,
                                                      uctx);

        if(stroke != null){
            result.put(GVTAttributedCharacterIterator.TextAttribute.STROKE,
                       stroke);
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
                    result.put(
                        GVTAttributedCharacterIterator.TextAttribute.UNDERLINE,
                        GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_ON);
                    if (sp != null) {
                        result.put(GVTAttributedCharacterIterator.
                                   TextAttribute.UNDERLINE_STROKE_PAINT, sp);
                    }
                    if (stroke != null) {
                        result.put(GVTAttributedCharacterIterator.
                                   TextAttribute.UNDERLINE_STROKE, stroke);
                    }
                    if (p != null) {
                        result.put(GVTAttributedCharacterIterator.
                                   TextAttribute.UNDERLINE_PAINT, p);
                    }
                    break;
                case 'o':
                    result.put(GVTAttributedCharacterIterator.TextAttribute.OVERLINE,
                               GVTAttributedCharacterIterator.TextAttribute.OVERLINE_ON);
                    break;
                case 'l':
                    result.put(
                        GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH,
                        GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_ON);
                }
            }
        }

        return result;
    }
}
