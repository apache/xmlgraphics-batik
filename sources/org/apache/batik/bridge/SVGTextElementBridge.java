/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Composite;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.StringTokenizer;

import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.ComputedValue;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;

import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPath;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.UnresolvedFontFamily;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;text> element.
 *
 * @author <a href="stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public class SVGTextElementBridge extends AbstractGraphicsNodeBridge {

    protected final static Integer ZERO = new Integer(0);

    /**
     * Constructs a new bridge for the &lt;text> element.
     */
    public SVGTextElementBridge() {}

    /**
     * Returns 'text'.
     */
    public String getLocalName() {
        return SVG_TEXT_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGTextElementBridge();
    }

    /**
     * Creates a <tt>GraphicsNode</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        TextNode node = (TextNode)super.createGraphicsNode(ctx, e);
        if (node == null) {
            return null;
        }
        // specify the text painter to use
        if (ctx.getTextPainter() != null) {
            node.setTextPainter(ctx.getTextPainter());
        }
        // 'text-rendering' and 'color-rendering'
        RenderingHints hints = CSSUtilities.convertTextRendering(e, null);
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            node.setRenderingHints(hints);
        }
        node.setLocation(getLocation(ctx, e));

        return node;
    }

    /**
     * Creates the GraphicsNode depending on the GraphicsNodeBridge
     * implementation.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new TextNode();
    }

    /**
     * Returns the text node location according to the 'x' and 'y'
     * attributes of the specified text element.
     *
     * @param ctx the bridge context to use
     * @param e the text element
     */
    protected Point2D getLocation(BridgeContext ctx, Element e) {
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);

        // 'x' attribute - default is 0
        String s = e.getAttributeNS(null, SVG_X_ATTRIBUTE);
        float x = 0;
        if (s.length() != 0) {
            StringTokenizer st = new StringTokenizer(s, ", ", false);
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (st.nextToken(), SVG_X_ATTRIBUTE, uctx);
        }

        // 'y' attribute - default is 0
        s = e.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        float y = 0;
        if (s.length() != 0) {
            StringTokenizer st = new StringTokenizer(s, ", ", false);
            y = UnitProcessor.svgVerticalCoordinateToUserSpace
                (st.nextToken(), SVG_Y_ATTRIBUTE, uctx);
        }

        return new Point2D.Float(x, y);
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
        e.normalize();

        AttributedString as = buildAttributedString(ctx, e);
        addGlyphPositionAttributes(as, e, ctx);
        ((TextNode)node).setAttributedCharacterIterator(as.getIterator());

        // now add the painting attributes, cannot do it before this because
        // some of the Paint objects need to know the bounds of the text
        // and this isn't know until the text node aci is set
        TextDecoration textDecoration = 
            getTextDecoration(e, (TextNode)node, new TextDecoration(), ctx);
        addPaintAttributes(as, e, (TextNode)node, textDecoration, ctx);
        ((TextNode)node).setAttributedCharacterIterator(as.getIterator());

        super.buildGraphicsNode(ctx, e, node);
    }

    /**
     * Returns false as text is not a container.
     */
    public boolean isComposite() {
        return false;
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMAttrModified' is fired.
     */
    public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        super.handleDOMAttrModifiedEvent(evt);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------

    /**
     * Creates the attributed string which represents the given text
     * element children.
     *
     * @param ctx the bridge context to use
     * @param element the text element
     */
    protected AttributedString buildAttributedString(BridgeContext ctx,
                                                     Element element) {
        
        AttributedStringBuffer asb = new AttributedStringBuffer();
        fillAttributedStringBuffer(ctx, element, true, null, asb);
        return asb.toAttributedString();
    }

    /**
     * Fills the given AttributedStringBuffer.
     */
    protected void fillAttributedStringBuffer(BridgeContext ctx,
                                              Element element,
                                              boolean top,
                                              TextPath textPath,
                                              AttributedStringBuffer asb) {
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent())) {
            return;
        }
        
        String s = XMLSupport.getXMLSpace(element);
        boolean preserve = s.equals(SVG_PRESERVE_VALUE);
        boolean first = true;
        boolean last;
        boolean stripFirst = !preserve;
        boolean stripLast = !preserve;
        Element nodeElement = element;
        Map map = null;

        for (Node n = element.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            
            last = n.getNextSibling() == null;

            int lastChar = asb.getLastChar();
            stripFirst = !preserve && first &&
                (top || lastChar == ' ' || lastChar == -1);

            switch (n.getNodeType()) {
            case Node.ELEMENT_NODE:
                if (n.getNamespaceURI() != SVG_NAMESPACE_URI) {
                    break;
                }
                
                nodeElement = (Element)n;

                String ln = n.getLocalName();

                if (ln.equals(SVG_TSPAN_TAG) ||
                    ln.equals(SVG_ALT_GLYPH_TAG) ||
                    ln.equals(SVG_A_TAG)) {
                    fillAttributedStringBuffer(ctx,
                                               nodeElement,
                                               false,
                                               textPath,
                                               asb);
                } else if (ln.equals(SVG_TEXT_PATH_TAG)) {
                    SVGTextPathElementBridge textPathBridge
                        = (SVGTextPathElementBridge)ctx.getBridge(nodeElement);
                    TextPath newTextPath
                        = textPathBridge.createTextPath(ctx, nodeElement);
                    if (newTextPath != null) {
                        fillAttributedStringBuffer(ctx,
                                                   nodeElement,
                                                   false,
                                                   newTextPath,
                                                   asb);
                    }
                } else if (ln.equals(SVG_TREF_TAG)) {
                    String uriStr = XLinkSupport.getXLinkHref((Element)n);
                    Element ref = ctx.getReferencedElement((Element)n, uriStr);
                    s = TextUtilities.getElementContent(ref);
                    s = normalizeString(s, preserve, stripFirst, last && top);
                    if (s != null) {
                        stripLast = !preserve && s.charAt(0) == ' ';
                        if (stripLast && !asb.isEmpty()) {
                            asb.stripLast();
                        }
                        Map m = getAttributeMap(ctx, nodeElement, textPath);
                        asb.append(s, m);
                    }
                }
                break;
                
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                s = n.getNodeValue();
                s = normalizeString(s, preserve, stripFirst, last && top);
                if (s != null) {
                    stripLast = !preserve && s.charAt(0) == ' ';
                    if (stripLast && !asb.isEmpty()) {
                        asb.stripLast();
                    }
                    if (map == null) {
                        map = getAttributeMap(ctx, element, textPath);
                    }
                    asb.append(s, map);
                }
            }
            first = false;
        }
    }

    /**
     * Normalizes the given string.
     */
    protected String normalizeString(String s,
                                     boolean preserve,
                                     boolean stripfirst,
                                     boolean striplast) {
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
            int idx = 0;
            if (stripfirst) {
                loop: while (idx < s.length()) {
                    switch (s.charAt(idx)) {
                    default:
                        break loop;
                    case 10:
                    case 13:
                    case ' ':
                    case '\t':
                        idx++;
                    }
                }
            }
            for (int i = idx; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                case 10:
                case 13:
                    break;
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
            if (striplast) {
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
        if (sb.length() > 0) {
            return sb.toString();
        } else if (stripfirst && striplast) {
            return " ";
        }
        return null;
    }

    /**
     * This class is used to build an AttributedString.
     */
    protected static class AttributedStringBuffer {

        /**
         * The strings.
         */
        protected List strings;

        /**
         * The attributes.
         */
        protected List attributes;
        
        /**
         * The number of items.
         */
        protected int count;
        
        /**
         * Creates a new empty AttributedStringBuffer.
         */
        public AttributedStringBuffer() {
            strings = new ArrayList();
            attributes = new ArrayList();
        }

        /**
         * Tells whether this AttributedStringBuffer is empty.
         */
        public boolean isEmpty() {
            return count == 0;
        }

        /**
         * Appends a String and its associated attributes.
         */
        public void append(String s, Map m) {
            strings.add(s);
            attributes.add(m);
            count++;
        }

        /**
         * Returns the value of the last char or -1.
         */
        public int getLastChar() {
            if (count == 0) {
                return -1;
            }
            String s = (String)strings.get(count - 1);
            return s.charAt(s.length() - 1);
        }

        /**
         * Strips the last string last character.
         */
        public void stripLast() {
            String s = (String)strings.remove(count - 1);
            if (s.charAt(s.length() - 1) == ' ') {
                if (s.length() == 1) {
                    attributes.remove(count - 1);
                    return;
                }
                strings.add(s.substring(0, s.length() - 1));
            } else {
                strings.add(s);
            }
        }

        /**
         * Builds an attributed string from the content of this
         * buffer.
         */
        public AttributedString toAttributedString() {
            switch (count) {
            case 0:
                return new AttributedString(" ");
            case 1:
                return new AttributedString((String)strings.get(0),
                                            (Map)attributes.get(0));
            }

            StringBuffer sb = new StringBuffer();
            Iterator it = strings.iterator();
            while (it.hasNext()) {
                sb.append((String)it.next());
            }

            AttributedString result = new AttributedString(sb.toString());

            // Set the attributes

            Iterator sit = strings.iterator();
            Iterator ait = attributes.iterator();
            int idx = 0;
            while (sit.hasNext()) {
                String s = (String)sit.next();
                int nidx = idx + s.length();
                Map m = (Map)ait.next();
                Iterator kit = m.keySet().iterator();
                Iterator vit = m.values().iterator();
                while (kit.hasNext()) {
                    Attribute attr = (Attribute)kit.next();
                    Object val = vit.next();
                    result.addAttribute(attr, val, idx, nidx);
                }
                idx = nidx;
            }

            return result;
        }
    }

    /**
     * Returns true if node1 is an ancestor of node2
     */
    protected boolean nodeAncestorOf(Node node1, Node node2) {
        if (node2 == null || node1 == null) {
            return false;
        }
        Node parent = node2.getParentNode();
        while (parent != null && parent != node1) {
            parent = parent.getParentNode();
        }
        return (parent == node1);
    }


    /**
     * Adds glyph position attributes to an AttributedString.
     */
    protected void addGlyphPositionAttributes(AttributedString as,
                                              Element element,
                                              BridgeContext ctx) {

        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent())) {
            return;
        }
        // get all of the glyph position attribute values
        String xAtt = element.getAttributeNS(null, SVG_X_ATTRIBUTE);
        String yAtt = element.getAttributeNS(null, SVG_Y_ATTRIBUTE);
        String dxAtt = element.getAttributeNS(null, SVG_DX_ATTRIBUTE);
        String dyAtt = element.getAttributeNS(null, SVG_DY_ATTRIBUTE);
        String rotateAtt = element.getAttributeNS(null, SVG_ROTATE_ATTRIBUTE);

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        AttributedCharacterIterator aci = as.getIterator();

        // calculate which chars in the string belong to this element
        int firstChar = 0;
        for (int i = 0; i < aci.getEndIndex(); i++) {
            aci.setIndex(i);
            Element delimeter = (Element)aci.getAttribute(
            GVTAttributedCharacterIterator.
            TextAttribute.TEXT_COMPOUND_DELIMITER);
            if (delimeter == element || nodeAncestorOf(element, delimeter)) {
                firstChar = i;
                break;
            }
        }
        int lastChar = aci.getEndIndex()-1;
        for (int i = aci.getEndIndex()-1; i >= 0; i--) {
            aci.setIndex(i);
            Element delimeter = (Element)aci.getAttribute(
                GVTAttributedCharacterIterator.
                TextAttribute.TEXT_COMPOUND_DELIMITER);
            if (delimeter == element || nodeAncestorOf(element, delimeter)) {
                lastChar = i;
                break;
            }
        }

        ArrayList al;
        int len;

        // process the x attribute
        if (xAtt.length() != 0) {
            al = TextUtilities.svgHorizontalCoordinateArrayToUserSpace
                (element, SVG_X_ATTRIBUTE, xAtt, ctx);
            len = al.size();

            for (int i = 0; i < len; i++) {
                if (firstChar + i <= lastChar) {
                    as.addAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.X,
                         al.get(i), firstChar+i, firstChar+i+1);
                }
            }
        }

       // process the y attribute
        if (yAtt.length() != 0) {
            al = TextUtilities.svgVerticalCoordinateArrayToUserSpace
                (element, SVG_Y_ATTRIBUTE, yAtt, ctx);
            len = al.size();

            for (int i = 0; i < len; i++) {
                if (firstChar+i <= lastChar) {
                    as.addAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.Y,
                         al.get(i), firstChar+i, firstChar+i+1);
                }
            }
        }

        // process dx attribute
        if (dxAtt.length() != 0) {
            al = TextUtilities.svgHorizontalCoordinateArrayToUserSpace
                (element, SVG_DX_ATTRIBUTE, dxAtt, ctx);
            len = al.size();

            for (int i = 0; i < len; i++) {
                if (firstChar+i <= lastChar) {
                    as.addAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.DX,
                         al.get(i), firstChar+i, firstChar+i+1);
                }
            }
        }

        // process dy attribute
        if (dyAtt.length() != 0) {
            al = TextUtilities.svgVerticalCoordinateArrayToUserSpace
                (element, SVG_DY_ATTRIBUTE, dyAtt, ctx);
            len = al.size();

            for (int i = 0; i < len; i++) {
                if (firstChar+i <= lastChar) {
                    as.addAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.DY,
                         al.get(i), firstChar+i, firstChar+i+1);
                }
            }
        }

        // process rotate attribute
        if (rotateAtt.length() != 0) {
            al = TextUtilities.svgRotateArrayToFloats
                (element, SVG_ROTATE_ATTRIBUTE, rotateAtt, ctx);
            len = al.size();

            if (len == 1) {  // not a list
                // each char will have the same rotate value
                as.addAttribute
                    (GVTAttributedCharacterIterator.TextAttribute.ROTATION,
                     al.get(0), firstChar, lastChar + 1);

            } else {  // its a list
                // set each rotate value from the list
                for (int i = 0; i < len; i++) {
                    if (firstChar+i <= lastChar) {
                        as.addAttribute
                            (GVTAttributedCharacterIterator.
                             TextAttribute.ROTATION,
                             al.get(i), firstChar+i, firstChar+i+1);
                    }
                }
            }
        }

        // do the same for each child element
        for (Node child = element.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                addGlyphPositionAttributes(as, (Element)child, ctx);
            }
        }
    }

    /**
     * Adds painting attributes to an AttributedString.
     */
    protected void addPaintAttributes(AttributedString as,
                                      Element element,
                                      TextNode node,
                                      TextDecoration textDecoration,
                                      BridgeContext ctx) {


        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent())) {
            return;
        }
        AttributedCharacterIterator aci = as.getIterator();

        // calculate which chars in the string belong to this element
        int firstChar = 0;
        for (int i = 0; i < aci.getEndIndex(); i++) {
            aci.setIndex(i);
            Element delimeter = (Element)aci.getAttribute(
            GVTAttributedCharacterIterator.
            TextAttribute.TEXT_COMPOUND_DELIMITER);
            if (delimeter == element || nodeAncestorOf(element, delimeter)) {
                firstChar = i;
                break;
            }
        }
        int lastChar = aci.getEndIndex()-1;
        for (int i = aci.getEndIndex()-1; i >= 0; i--) {
            aci.setIndex(i);
            Element delimeter = (Element)aci.getAttribute(
                GVTAttributedCharacterIterator.
                TextAttribute.TEXT_COMPOUND_DELIMITER);
            if (delimeter == element || nodeAncestorOf(element, delimeter)) {
                lastChar = i;
                break;
            }
        }

        // Opacity
        Composite composite = CSSUtilities.convertOpacity(element);
        as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.OPACITY,
                        composite, firstChar, lastChar+1);

        // Fill
        Paint p = PaintServer.convertFillPaint(element, node, ctx);
        as.addAttribute(TextAttribute.FOREGROUND, p,
                        firstChar, lastChar+1);

        // Stroke Paint
        Paint sp = PaintServer.convertStrokePaint(element, node, ctx);
        as.addAttribute
            (GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT,
             sp, firstChar, lastChar+1);

        // Stroke
        Stroke stroke = PaintServer.convertStroke(element);
        as.addAttribute
            (GVTAttributedCharacterIterator.TextAttribute.STROKE,
             stroke, firstChar, lastChar+1);

        // Text decoration
        if (textDecoration != null) {
            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.UNDERLINE_PAINT,
                            textDecoration.underlinePaint,
                            firstChar, lastChar+1);

            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.UNDERLINE_STROKE_PAINT,
                            textDecoration.underlineStrokePaint,
                            firstChar, lastChar+1);

            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.UNDERLINE_STROKE,
                            textDecoration.underlineStroke,
                            firstChar, lastChar+1);

            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.OVERLINE_PAINT,
                            textDecoration.overlinePaint,
                            firstChar, lastChar+1);

            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.OVERLINE_STROKE_PAINT,
                            textDecoration.overlineStrokePaint,
                            firstChar, lastChar+1);

            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.OVERLINE_STROKE,
                            textDecoration.overlineStroke,
                            firstChar, lastChar+1);

            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.STRIKETHROUGH_PAINT,
                            textDecoration.strikethroughPaint,
                            firstChar, lastChar+1);

            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.STRIKETHROUGH_STROKE_PAINT,
                            textDecoration.strikethroughStrokePaint,
                            firstChar, lastChar+1);

            as.addAttribute(GVTAttributedCharacterIterator.
                            TextAttribute.STRIKETHROUGH_STROKE,
                            textDecoration.strikethroughStroke,
                            firstChar, lastChar+1);
        }

        // do the same for each child element
        for (Node child = element.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)child;
                TextDecoration td = getTextDecoration(childElement, node,
                                                      textDecoration, ctx);
                addPaintAttributes(as, childElement, node, td, ctx);
            }
        }
    }


    /**
     * Returns the map to pass to the current characters.
     */
    protected Map getAttributeMap(BridgeContext ctx,
                                  Element element,
                                  TextPath textPath) {
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);

        Map result = new HashMap();
        String s;
        float f;
        short t;
        boolean verticalText = false;

        result.put
        (GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER,
         element);

        if (element.getTagName().equals(SVG_ALT_GLYPH_TAG)) {
            result.put
              (GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER,
               new SVGAltGlyphHandler(ctx, element));
        }

        if (textPath != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.TEXTPATH,
                       textPath);
        }

        // Text-anchor
        TextNode.Anchor a = TextUtilities.convertTextAnchor(element);
        result.put(GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE,
                   a);

        // Font size.
        Float fs = TextUtilities.convertFontSize(element);
        result.put(TextAttribute.SIZE, fs);

        // Font weight
        Float fw = TextUtilities.convertFontWeight(element);
        Value v = CSSUtilities.getComputedStyle
            (element, SVGCSSEngine.FONT_WEIGHT_INDEX);
        String fontWeightString = v.getCssText();
        result.put(TextAttribute.WEIGHT, fw);

        // Font style
        String fontStyleString = CSSUtilities.getComputedStyle
            (element, SVGCSSEngine.FONT_STYLE_INDEX).getStringValue();
        result.put(TextAttribute.POSTURE,
                   TextUtilities.convertFontStyle(element));

        // Font stretch
        String fontStretchString = CSSUtilities.getComputedStyle
            (element, SVGCSSEngine.FONT_STRETCH_INDEX).getStringValue();
        result.put(TextAttribute.WIDTH,
                   TextUtilities.convertFontStretch(element));

        // Font family
        Value val = CSSUtilities.getComputedStyle
            (element, SVGCSSEngine.FONT_FAMILY_INDEX);

        //  make a list of GVTFontFamily objects
        List fontFamilyList = new Vector();
        int len = val.getLength();
        for (int i = 0; i < len; i++) {
            Value it = val.item(i);
            String fontFamilyName = it.getStringValue();
            GVTFontFamily fontFamily
                = SVGFontUtilities.getFontFamily(element, ctx, fontFamilyName,
                   fontWeightString, fontStyleString);
            fontFamilyList.add(fontFamily);
        }
        result.put
            (GVTAttributedCharacterIterator.TextAttribute.GVT_FONT_FAMILIES,
             fontFamilyList);

        // Text baseline adjustment.
        Object bs = TextUtilities.convertBaselineShift(element);
        if (bs != null) {
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.BASELINE_SHIFT, bs);
        }

        // Unicode-bidi mode
        val =  CSSUtilities.getComputedStyle
            (element, SVGCSSEngine.UNICODE_BIDI_INDEX);
        s = val.getStringValue();
        if (s.charAt(0) == 'n') {
            result.put(TextAttribute.BIDI_EMBEDDING, ZERO);
        } else {

            // Text direction
            // XXX: this needs to coordinate with the unicode-bidi
            // property, so that when an explicit reversal
            // occurs, the BIDI_EMBEDDING level is
            // appropriately incremented or decremented.
            // Note that direction is implicitly handled by unicode
            // BiDi algorithm in most cases, this property
            // is only needed when one wants to override the
            // normal writing direction for a string/substring.

            val = CSSUtilities.getComputedStyle
                (element, SVGCSSEngine.DIRECTION_INDEX);
            String rs = val.getStringValue();
            switch (rs.charAt(0)) {
            case 'l':
                result.put(TextAttribute.RUN_DIRECTION,
                           TextAttribute.RUN_DIRECTION_LTR);

                switch (s.charAt(0)) {
                case 'b': // bidi-override
                    result.put(TextAttribute.BIDI_EMBEDDING,
                               new Integer(-2));
                    break;
                case 'e': // embed
                    result.put(TextAttribute.BIDI_EMBEDDING,
                               new Integer(2));
                    break;
                }

                break;
            case 'r':
                result.put(TextAttribute.RUN_DIRECTION,
                           TextAttribute.RUN_DIRECTION_RTL);
                switch (s.charAt(0)) {
                case 'b': // bidi-override
                    result.put(TextAttribute.BIDI_EMBEDDING,
                               new Integer(-1));
                    break;
                case 'e': // embed
                    result.put(TextAttribute.BIDI_EMBEDDING,
                               new Integer(1));
                    break;
                }
                break;
            }
        }

        // Writing mode

        val = CSSUtilities.getComputedStyle
            (element, SVGCSSEngine.WRITING_MODE_INDEX);
        s = val.getStringValue();
        switch (s.charAt(0)) {
        case 'l':
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.WRITING_MODE,
                       GVTAttributedCharacterIterator.
                       TextAttribute.WRITING_MODE_LTR);
            break;
        case 'r':
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.WRITING_MODE,
                       GVTAttributedCharacterIterator.
                       TextAttribute.WRITING_MODE_RTL);
            break;
        case 't':
                result.put(GVTAttributedCharacterIterator.
                       TextAttribute.WRITING_MODE,
                       GVTAttributedCharacterIterator.
                       TextAttribute.WRITING_MODE_TTB);
            break;
        }

        // glyph-orientation-vertical

        val = CSSUtilities.getComputedStyle
            (element, SVGCSSEngine.GLYPH_ORIENTATION_VERTICAL_INDEX);
        switch (val.getPrimitiveType()) {
        case CSSPrimitiveValue.CSS_IDENT: // auto
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION,
                       GVTAttributedCharacterIterator.
                       TextAttribute.ORIENTATION_AUTO);
            break;
        case CSSPrimitiveValue.CSS_DEG:
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION,
                       GVTAttributedCharacterIterator.
                       TextAttribute.ORIENTATION_ANGLE);
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION_ANGLE,
                       new Float(val.getFloatValue()));
            break;
        case CSSPrimitiveValue.CSS_RAD:
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION,
                       GVTAttributedCharacterIterator.
                       TextAttribute.ORIENTATION_ANGLE);
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION_ANGLE,
                       new Float(val.getFloatValue() * 180 / Math.PI));
            break;
        case CSSPrimitiveValue.CSS_GRAD:
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION,
                       GVTAttributedCharacterIterator.
                       TextAttribute.ORIENTATION_ANGLE);
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION_ANGLE,
                       new Float(val.getFloatValue() * 9 / 5));
            break;
        default:
            // Cannot happen
            throw new InternalError();
        }

        // text spacing properties...

        // Letter Spacing
        Float sp = TextUtilities.convertLetterSpacing(element);
        if (sp != null) {
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.LETTER_SPACING,
                       sp);
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.CUSTOM_SPACING,
                       Boolean.TRUE);
        }

        // Word spacing
        sp = TextUtilities.convertWordSpacing(element);
        if (sp != null) {
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.WORD_SPACING,
                       sp);
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.CUSTOM_SPACING,
                       Boolean.TRUE);
        }

        // Kerning
        sp = TextUtilities.convertKerning(element);
        if (sp != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.KERNING,
                       sp);
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.CUSTOM_SPACING,
                       Boolean.TRUE);
        }

        // textLength
        s = element.getAttributeNS(null, SVG_TEXT_LENGTH_ATTRIBUTE);
        if (s.length() != 0) {
            f = UnitProcessor.svgOtherLengthToUserSpace
                (s, SVG_TEXT_LENGTH_ATTRIBUTE, uctx);
            result.put(GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH,
                       new Float(f));

            // lengthAdjust
            s = element.getAttributeNS(null, SVG_LENGTH_ADJUST_ATTRIBUTE);

            if (s.length() < 10) {
                result.put(GVTAttributedCharacterIterator.
                           TextAttribute.LENGTH_ADJUST,
                           GVTAttributedCharacterIterator.
                           TextAttribute.ADJUST_SPACING);
                result.put(GVTAttributedCharacterIterator.
                           TextAttribute.CUSTOM_SPACING,
                           Boolean.TRUE);
            } else {
                result.put(GVTAttributedCharacterIterator.
                           TextAttribute.LENGTH_ADJUST,
                           GVTAttributedCharacterIterator.
                           TextAttribute.ADJUST_ALL);
            }
        }

        return result;
    }


    /**
     * Constructs a TextDecoration object for the specified element. This will
     * contain all of the decoration properties to be used when drawing the
     * text.
     */
    protected TextDecoration getTextDecoration(Element element,
                                               GraphicsNode node,
                                               TextDecoration parent,
                                               BridgeContext ctx) {
        int pidx = SVGCSSEngine.TEXT_DECORATION_INDEX;
        Value val = CSSUtilities.getComputedStyle(element, pidx);
        
        // Was text-decoration explicity set on this element?
        StyleMap sm = ((CSSStylableElement)element).getComputedStyleMap(null);
        if (sm.isNullCascaded(pidx)) {
            // If not, keep the same decorations.
            return parent;
        }

        TextDecoration textDecoration = new TextDecoration(parent);

        short t = val.getCssValueType();

        switch (val.getCssValueType()) {
        case CSSValue.CSS_VALUE_LIST:
            ListValue lst = (ListValue)val;

            Paint paint = PaintServer.convertFillPaint(element, node, ctx);
            Paint strokePaint = PaintServer.convertStrokePaint(element,
                                                               node, ctx);
            Stroke stroke = PaintServer.convertStroke(element);

            int len = lst.getLength();
            for (int i = 0; i < len; i++) {
                Value v = lst.item(i);
                String s = v.getStringValue();
                switch (s.charAt(0)) {
                case 'u':
                    if (paint != null) {
                       textDecoration.underlinePaint = paint;
                    }
                    if (strokePaint != null) {
                        textDecoration.underlineStrokePaint = strokePaint;
                    }
                    if (stroke != null) {
                        textDecoration.underlineStroke = stroke;
                    }
                    break;
                case 'o':
                    if (paint != null) {
                       textDecoration.overlinePaint = paint;
                    }
                    if (strokePaint != null) {
                        textDecoration.overlineStrokePaint = strokePaint;
                    }
                    if (stroke != null) {
                        textDecoration.overlineStroke = stroke;
                    }
                    break;
                case 'l':
                    if (paint != null) {
                       textDecoration.strikethroughPaint = paint;
                    }
                    if (strokePaint != null) {
                        textDecoration.strikethroughStrokePaint = strokePaint;
                    }
                    if (stroke != null) {
                        textDecoration.strikethroughStroke = stroke;
                    }
                    break;
                }
            }
            return textDecoration;
        default: // None
            return TextDecoration.EMPTY_TEXT_DECORATION;
        }
    }

    /**
     * To store the text decorations of a text element.
     */
    protected static class TextDecoration {

        static final TextDecoration EMPTY_TEXT_DECORATION =
            new TextDecoration();

        Paint underlinePaint;
        Paint underlineStrokePaint;
        Stroke underlineStroke;
        Paint overlinePaint;
        Paint overlineStrokePaint;
        Stroke overlineStroke;
        Paint strikethroughPaint;
        Paint strikethroughStrokePaint;
        Stroke strikethroughStroke;

        TextDecoration() {
            underlinePaint = null;
            underlineStrokePaint = null;
            underlineStroke = null;
            overlinePaint = null;
            overlineStrokePaint = null;
            overlineStroke = null;
            strikethroughPaint = null;
            strikethroughStrokePaint = null;
            strikethroughStroke = null;
        }

        TextDecoration(TextDecoration td) {
            underlinePaint = td.underlinePaint;
            underlineStrokePaint = td.underlineStrokePaint;
            underlineStroke = td.underlineStroke;
            overlinePaint = td.overlinePaint;
            overlineStrokePaint = td.overlineStrokePaint;
            overlineStroke = td.overlineStroke;
            strikethroughPaint = td.strikethroughPaint;
            strikethroughStrokePaint = td.strikethroughStrokePaint;
            strikethroughStroke = td.strikethroughStroke;
        }
    }
}
