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
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Font;
import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.font.GlyphVector;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.PathIterator;

import java.io.StringReader;
import java.io.IOException;

import java.net.MalformedURLException;

import java.text.AttributedString;

import java.util.HashMap;
import java.util.Map;

import org.apache.batik.bridge.BridgeMutationEvent;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.filter.Filter;
import org.apache.batik.gvt.filter.Clip;
import org.apache.batik.gvt.filter.Mask;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.util.awt.font.TextPathLayout;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGDocument;

/**
 * A factory for the &lt;textPath&gt; SVG element.
 *
 * @author <a href="mailto:dean@w3.org">Dean Jackson</a>
 * @version $Id$
 */
public class SVGTextPathElementBridge implements GraphicsNodeBridge, SVGConstants {
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

        //System.out.println("creating Text Path node");

        SVGElement svgElement = (SVGElement) element;
        CSSStyleDeclaration cssDecl
            = ctx.getViewCSS().getComputedStyle(element, null);
        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(ctx,
                                              cssDecl);

        ShapeNode node = ctx.getGVTFactory().createShapeNode();

        // Initialize the style properties
        ShapePainter painter
            = CSSUtilities.convertStrokeAndFill(svgElement, node,
                                                ctx, cssDecl, uctx);
        node.setShapePainter(painter);

        // Transform
        AffineTransform at = AWTTransformProducer.createAffineTransform
            (new StringReader(element.getAttributeNS(null, ATTR_TRANSFORM)),
             ctx.getParserFactory());
        node.setTransform(at);

        // Set node composite
        CSSPrimitiveValue opacityVal =
            (CSSPrimitiveValue)cssDecl.getPropertyCSSValue(ATTR_OPACITY);
        Composite composite =
            CSSUtilities.convertOpacityToComposite(opacityVal);
        node.setComposite(composite);

        // Set node filter
        Filter filter = CSSUtilities.convertFilter(element, node, ctx);
        node.setFilter(filter);

        // Set the node mask
        Mask mask = CSSUtilities.convertMask(element, node, ctx);
        node.setMask(mask);

        // Set the node clip
        Clip clip = CSSUtilities.convertClipPath(element, node, ctx);
        node.setClip(clip);

        // <!> TODO only when binding is enabled
        //BridgeEventSupport.addDOMListener(ctx, element);
        //ctx.bind(element, node);

        // Location
        String s = element.getAttributeNS(null, ATTR_START_OFFSET);
        // is this really a HORIZONTAL LENGTH?
        float startOffset = UnitProcessor.svgToUserSpace(s,
                                                         (SVGElement)element,
                                                         UnitProcessor.HORIZONTAL_LENGTH,
                                                         uctx);

        //System.out.println("--- startOffset = " + startOffset);

        // TextLength
        s = element.getAttributeNS(null, ATTR_TEXT_LENGTH);
        // is this really a HORIZONTAL LENGTH?
        boolean lengthSpecified = true;
        float textLength = UnitProcessor.svgToUserSpace(s,
                                                        (SVGElement)element,
                                                        UnitProcessor.HORIZONTAL_LENGTH,
                                                        uctx);

        // FIXME : do this a better way
        if (textLength == 0f) {
            lengthSpecified = false;
        }


        //System.out.println("--- textLength = " + textLength);

        s = element.getAttributeNS(null, ATTR_METHOD);
        int method = TextPathLayout.ADJUST_SPACING;
        if (s.equals(VALUE_ALIGN)) {
            method = TextPathLayout.ADJUST_SPACING;
        } else if (s.equals(VALUE_STRETCH)) {
            method = TextPathLayout.ADJUST_GLYPHS;
        }

        //System.out.println("--- method = " + method);


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

        Map attrs = new HashMap();

        // Font size
        float fs = CSSUtilities.convertFontSize((SVGElement)element,
                                                ctx,
                                                cssDecl,
                                                uctx);
        fs *= 0.92; // Font size correction

        attrs.put(TextAttribute.SIZE, new Float(fs));

        // Text-anchor
        CSSPrimitiveValue v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (TEXT_ANCHOR_PROPERTY);
        s = v.getStringValue();
        int align;
        switch (s.charAt(0)) {
        case 's':
            align = TextPathLayout.ALIGN_START;
            break;
        case 'm':
            align = TextPathLayout.ALIGN_MIDDLE;
            break;
        default:
            align = TextPathLayout.ALIGN_END;
        }

        //System.out.println("--- align = " + align);

        // Font family
        CSSValueList ff = (CSSValueList)cssDecl.getPropertyCSSValue
            (FONT_FAMILY_PROPERTY);
        s = null;
        for (int i = 0; s == null && i < ff.getLength(); i++) {
            v = (CSSPrimitiveValue)ff.item(i);
            s = (String)fonts.get(v.getStringValue());
        }
        s = (s == null) ? "SansSerif" : s;
        attrs.put(TextAttribute.FAMILY, s);

        // Font weight
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (FONT_WEIGHT_PROPERTY);
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            if (v.getStringValue().charAt(0) == 'n') {
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_REGULAR);
            } else {
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_BOLD);
            }
        } else {
            switch ((int)v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER)) {
            case 100:
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_EXTRA_LIGHT);
                break;
            case 200:
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_LIGHT);
                break;
            case 300:
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_DEMILIGHT);
                break;
            case 400:
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_REGULAR);
                break;
            case 500:
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_SEMIBOLD);
                break;
            case 600:
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_DEMIBOLD);
                break;
            case 700:
                attrs.put(TextAttribute.WEIGHT,
                                TextAttribute.WEIGHT_BOLD);
                break;
            case 800:
                attrs.put(TextAttribute.WEIGHT,
                                //TextAttribute.WEIGHT_EXTRABOLD);
                                TextAttribute.WEIGHT_BOLD);
                break;
            case 900:
                attrs.put(TextAttribute.WEIGHT,
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
            attrs.put(TextAttribute.POSTURE,
                            TextAttribute.POSTURE_REGULAR);
            break;
        case 'o':
        case 'i':
            attrs.put(TextAttribute.POSTURE,
                            TextAttribute.POSTURE_OBLIQUE);
        }

        // Font stretch
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValue
            (FONT_STRETCH_PROPERTY);
        s = v.getStringValue();
        switch (s.charAt(0)) {
        case 'u':
            if (s.charAt(6) == 'c') {
                attrs.put(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_CONDENSED);
            } else {
                attrs.put(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_EXTENDED);
            }
            break;
        case 'e':
            if (s.charAt(6) == 'c') {
                attrs.put(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_CONDENSED);
            } else {
                if (s.length() == 8) {
                    attrs.put(TextAttribute.WIDTH,
                                    TextAttribute.WIDTH_SEMI_EXTENDED);
                } else {
                    attrs.put(TextAttribute.WIDTH,
                                    TextAttribute.WIDTH_EXTENDED);
                }
            }
            break;
        case 's':
            if (s.charAt(6) == 'c') {
                attrs.put(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_SEMI_CONDENSED);
            } else {
                attrs.put(TextAttribute.WIDTH,
                                TextAttribute.WIDTH_SEMI_EXTENDED);
            }
            break;
        default:
            attrs.put(TextAttribute.WIDTH,
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
                    attrs.put(TextAttribute.UNDERLINE,
                                    TextAttribute.UNDERLINE_ON);
                    break;
                case 'o':
                    // overline
                    break;
                case 'l':
                    attrs.put(TextAttribute.STRIKETHROUGH,
                                    TextAttribute.STRIKETHROUGH_ON);
                }
            }
        }


        Font font = new Font(attrs);


        // get the path

        URIResolver ur;
        ur = new URIResolver((SVGDocument)element.getOwnerDocument(),
                             ctx.getDocumentLoader());
        String uriString = XLinkSupport.getXLinkHref(element);
        Shape path = null;

        try {
            Node n = ur.getNode(uriString);
            if (n.getOwnerDocument() == null) {
                throw new Error("Can't use documents");
            }
            Element elt = (Element)n;
            boolean local =
                n.getOwnerDocument() == element.getOwnerDocument();

            Element pathElement = null;
            if (local) {
                pathElement = (Element)elt.cloneNode(true);
            } else {
                pathElement = (Element)element.getOwnerDocument().importNode(elt, true);

            }

            if (pathElement != null) {

                String d = pathElement.getAttributeNS(null, ATTR_D);
                try {

                    // add the transform for the path as well

                    path = AWTPathProducer.createShape(new StringReader(d),
                                                       PathIterator.WIND_NON_ZERO,
                                                       ctx.getParserFactory());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }

            }
        } catch (Exception ex) {
            System.out.println("bad url " + uriString);
            ex.printStackTrace();
        }

        GlyphVector vector = font.createGlyphVector(new FontRenderContext(null, true, true),
                                                    new String(text));

        //System.out.println("num glyphs in vector == " + vector.getNumGlyphs());

        Shape shape = null;

        if (!lengthSpecified) {
            textLength = (float) vector.getVisualBounds().getWidth();
        }

        shape = TextPathLayout.layoutGlyphVector(vector, path, align, startOffset, textLength, method);

        node.setShape(shape);

        return node;
    }

    public void buildGraphicsNode(GraphicsNode node, BridgeContext ctx,
                                  Element elt) {
    }

    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }

    public boolean isContainer() {
        return false;
    }
}
