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
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.StringTokenizer;

import org.apache.batik.css.CSSOMReadOnlyStyleDeclaration;
import org.apache.batik.css.AbstractViewCSS;
import org.apache.batik.dom.svg.SVGOMDocument;
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
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;text> element.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class SVGTextElementBridge extends AbstractSVGBridge
    implements GraphicsNodeBridge, ErrorConstants {

    /**
     * The element that has been handled by this bridge.
     */
    protected Element e;

    /**
     * The graphics node constructed by this bridge.
     */
    protected GraphicsNode node;

    /**
     * The bridge context to use for dynamic updates.
     */
    protected BridgeContext ctx;

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
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }

        TextNode node = new TextNode();
        // specify the text painter to use if one has been provided in the
        // bridge context
        if (ctx.getTextPainter() != null) {
            node.setTextPainter(ctx.getTextPainter());
        }

        // 'transform'
        String s = e.getAttributeNS(null, SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0) {
            node.setTransform
                (SVGUtilities.convertTransform(e, SVG_TRANSFORM_ATTRIBUTE, s));
        }
        // 'visibility'
        node.setVisible(CSSUtilities.convertVisibility(e));

        // 'text-rendering' and 'color-rendering'
        Map textHints = CSSUtilities.convertTextRendering(e);
        Map colorHints = CSSUtilities.convertColorRendering(e);
        if (textHints != null || colorHints != null) {
            RenderingHints hints;
            if (textHints == null) {
                hints = new RenderingHints(colorHints);
            } else if (colorHints == null) {
                hints = new RenderingHints(textHints);
            } else {
                hints = new RenderingHints(textHints);
                hints.putAll(colorHints);
            }
            node.setRenderingHints(hints);
        }

        node.setLocation(getLocation(ctx, e));

        return node;
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
            StringTokenizer st = new StringTokenizer(s);
            String startX = st.nextToken();
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (startX, SVG_X_ATTRIBUTE, uctx);
        }

        // 'y' attribute - default is 0
        s = e.getAttributeNS(null, SVG_Y_ATTRIBUTE);

        float y = 0;
        if (s.length() != 0) {
            StringTokenizer st = new StringTokenizer(s);
            String startY = st.nextToken();
            y = UnitProcessor.svgVerticalCoordinateToUserSpace
                (startY, SVG_Y_ATTRIBUTE, uctx);
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
        AttributedString as = buildAttributedString(ctx, e, node);
        addGlyphPositionAttributes(as, e, ctx);
        ((TextNode)node).setAttributedCharacterIterator(as.getIterator());

        // now add the painting attributes, cannot do it before this because
        // some of the Paint objects need to know the bounds of the text
        // and this isn't know until the text node aci is set
        TextDecoration textDecoration = 
            getTextDecoration(e, (TextNode)node, new TextDecoration(), ctx);
        addPaintAttributes(as, e, (TextNode)node, textDecoration, ctx);
        ((TextNode)node).setAttributedCharacterIterator(as.getIterator());

        // 'filter'
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        // 'mask'
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        // 'clip-path'
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
        // 'pointer-events'
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));

        // bind the specified element and its associated graphics node if needed
        if (ctx.isDynamic()) {
            ((EventTarget)e).addEventListener("DOMAttrModified", 
                                              new DOMAttrModifiedEventListener(),
                                              false);
            this.e = e;
            this.node = node;
            this.ctx = ctx;
            ctx.bind(e, node);
            BridgeEventSupport.addDOMListener(ctx, e);
        }

        // Handle children elements such as <title>
        SVGUtilities.bridgeChildren(ctx, e);
    }

    /**
     * Returns false as text is not a container.
     */
    public boolean isComposite() {
        return false;
    }

    // dynamic support

    /**
     * Handles DOMAttrModified events.
     *
     * @param evt the DOM mutation event
     */
    protected void handleDOMAttrModifiedEvent(MutationEvent evt) {
        String attrName = evt.getAttrName();
        if (attrName.equals(SVG_TRANSFORM_ATTRIBUTE)) {
            BridgeUpdateEvent be = new BridgeUpdateEvent();
            fireBridgeUpdateStarting(be);
            
            String s = evt.getNewValue();
            AffineTransform at = GraphicsNode.IDENTITY;
            if (s.length() != 0) {
                at = SVGUtilities.convertTransform
                    (e, SVG_TRANSFORM_ATTRIBUTE, s);
            }
            node.setTransform(at);
            
            fireBridgeUpdateCompleted(be);
        } else {
            System.out.println("Unsupported attribute modification: "+attrName+
                               " on "+e.getLocalName());
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
            if (evt.getTarget() != e) {
                return;
            }
            handleDOMAttrModifiedEvent((MutationEvent)evt);
        }
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
     * @param textNode the textNode that will be used to paint the text
     */
    protected AttributedString buildAttributedString(BridgeContext ctx,
                                                     Element element,
                                                     GraphicsNode node) {

        AttributedString result = null;
        List l = buildAttributedStrings
            (ctx, element, node, true, null, new LinkedList());

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

        // First pass: build the string buffer.
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            AttributedString s = (AttributedString)it.next();
            AttributedCharacterIterator aci = s.getIterator();
            // Build the StringBuffer
            char c = aci.first();
            for (; c != CharacterIterator.DONE; c = aci.next()) {
                sb.append(c);
            }
        }
        result = new AttributedString(sb.toString());

        // Second pass: decorate the attributed string.
        int i=0;
        it = l.iterator();
        while (it.hasNext()) {
            AttributedString s = (AttributedString)it.next();
            AttributedCharacterIterator aci = s.getIterator();
            Iterator attrIter = aci.getAllAttributeKeys().iterator();
            while (attrIter.hasNext()) { // for each attribute key...
                AttributedCharacterIterator.Attribute key
                    = (AttributedCharacterIterator.Attribute) attrIter.next();
                int begin;
                int end;
                aci.first();
                do {
                    begin = aci.getRunStart(key);
                    end = aci.getRunLimit(key);
                    aci.setIndex(begin);
                    Object value = aci.getAttribute(key);
                    //System.out.println("Adding attribute "+key+": "+value+" from "+(i+begin)+"->"+(i+end));
                    result.addAttribute(key, value, i+begin, i+end);
                    aci.setIndex(end);
                } while (end < aci.getEndIndex()); // more runs in aci
            }
            i += aci.getEndIndex();
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
                                          boolean top,
                                          TextPath textPath,
                                          LinkedList result) {

        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent())) {
            return result;
        }

        // !!! return two lists
        Map m = getAttributeMap(ctx, element, node, textPath);
        String s = XMLSupport.getXMLSpace(element);
        boolean preserve = s.equals(SVG_PRESERVE_VALUE);
        boolean first = true;
        boolean last;
        boolean stripFirst = !preserve;
        boolean stripLast = !preserve;
        Element nodeElement = element;
        AttributedString as = null;

        if (!result.isEmpty()) {
            as = (AttributedString) result.getLast();
        }

        for (Node n = element.getFirstChild();
             n != null;
             n = n.getNextSibling()) {

            last = n.getNextSibling() == null;

            int lastChar = (as != null) ?
                (as.getIterator().last()) : CharacterIterator.DONE;
            stripFirst = !preserve && first &&
                (top ||
                 (lastChar == ' ') ||
                 (lastChar == CharacterIterator.DONE));

            switch (n.getNodeType()) {
            case Node.ELEMENT_NODE:

                nodeElement = (Element)n;

                if (n.getLocalName().equals(SVG_TSPAN_TAG) ||
                    n.getLocalName().equals(SVG_ALT_GLYPH_TAG) ||
                    n.getLocalName().equals(SVG_A_TAG)) {

                    buildAttributedStrings(ctx,
                                           nodeElement,
                                           node,
                                           false,
                                           textPath,
                                           result);
                } else if (n.getLocalName().equals(SVG_TEXT_PATH_TAG)) {

                    SVGTextPathElementBridge textPathBridge
                        = (SVGTextPathElementBridge)ctx.getBridge(nodeElement);
                    TextPath newTextPath
                        = textPathBridge.createTextPath(ctx, nodeElement);
                    if (newTextPath != null) {
                        buildAttributedStrings(ctx,
                                           nodeElement,
                                           node,
                                           false,
                                           newTextPath,
                                           result);
                    }

                } else if (n.getLocalName().equals(SVG_TREF_TAG)) {

                    String uriStr = XLinkSupport.getXLinkHref((Element)n);
                    Element ref = ctx.getReferencedElement((Element)n, uriStr);
                    s = getElementContent(ref);
                    Map map = getAttributeMap(ctx, nodeElement, node, textPath);
                    int[] indexMap = new int[s.length()];
                    as = createAttributedString(s, map, indexMap, preserve,
                                                stripFirst, last && top);
                    if (as != null) {
                        stripLast = !preserve &&
                            (as.getIterator().first() == ' ');
                        if (stripLast) {
                            AttributedString las
                                = (AttributedString) result.removeLast();
                            if (las != null) {
                                AttributedCharacterIterator iter
                                    = las.getIterator();
                                int endIndex = iter.getEndIndex()-1;
                                if (iter.setIndex(endIndex) == ' ') {
                                    las = new AttributedString
                                        (las.getIterator (null, iter.getBeginIndex(), endIndex));
                                }
                                result.add(las);
                            }
                        }
                        result.add(as);
                    }
                }
                break;
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                s = n.getNodeValue();
                int[] indexMap = new int[s.length()];
                as = createAttributedString
                    (s, m, indexMap, preserve, stripFirst, last && top);
                if (as != null) {
                    stripLast =
                        !preserve && (as.getIterator().first() == ' ');
                    if (stripLast && !result.isEmpty()) {
                        AttributedString las =
                            (AttributedString) result.removeLast();
                        if (las != null) {
                            AttributedCharacterIterator iter
                                = las.getIterator();
                            int endIndex = iter.getEndIndex()-1;
                            if (iter.setIndex(endIndex) == ' ') {
                                las = new AttributedString
                                    (las.getIterator(null,
                                        iter.getBeginIndex(), endIndex));
                            }
                            result.add(las);
                        }
                    }
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
                                                      int[] indexMap,
                                                      boolean preserve,
                                                      boolean stripfirst,
                                                      boolean striplast) {
        AttributedString as = null;
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
                indexMap[i] = i;
            }
        } else {
            boolean space = false;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                case 10:
                case 13:
                    break; // should break, newlines are not whitespace
                case ' ':
                case '\t':
                    if (!space) {
                        sb.append(' ');
                        indexMap[sb.length()-1] = i;
                        space = true;
                    }
                    break;
                default:
                    sb.append(c);
                    indexMap[sb.length()-1] = i;
                    space = false;
                }
            }
            if (stripfirst) {
                while (sb.length() > 0) {
                    if (sb.charAt(0) == ' ') {
                        sb.deleteCharAt(0);
                        System.arraycopy(indexMap, 1, indexMap, 0, sb.length());
                    } else {
                        break;
                    }
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
            for (int i=sb.length(); i<indexMap.length; ++i) {
                indexMap[i] = -1;
            }
        }
        if (sb.length() > 0) {
            as = new AttributedString(sb.toString(), m);
        } else if (stripfirst && striplast) {
            as = new AttributedString(" ", m);
        }
        return as;
    }

    /**
     * Returns true if node1 is an ancestor of node2
     */
    private boolean nodeAncestorOf(Node node1, Node node2) {
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
            GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
            if (delimeter == element || nodeAncestorOf(element, delimeter)) {
                firstChar = i;
                break;
            }
        }
        int lastChar = aci.getEndIndex()-1;
        for (int i = aci.getEndIndex()-1; i >= 0; i--) {
            aci.setIndex(i);
            Element delimeter = (Element)aci.getAttribute(
                GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
            if (delimeter == element || nodeAncestorOf(element, delimeter)) {
                lastChar = i;
                break;
            }
        }

        // process the x attribute
        if (xAtt.length() != 0) {
            float x[] = TextUtilities.svgHorizontalCoordinateArrayToUserSpace
                        (element, SVG_X_ATTRIBUTE, xAtt, ctx);

            for (int i = 0; i < x.length; i++) {
                if (firstChar+i <= lastChar) {
                    as.addAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.X,
                         new Float(x[i]), firstChar+i, firstChar+i+1);
                }
            }
        }

       // process the y attribute
        if (yAtt.length() != 0) {
            float y[] = TextUtilities.svgVerticalCoordinateArrayToUserSpace
                (element, SVG_Y_ATTRIBUTE, yAtt, ctx);

            for (int i = 0; i < y.length; i++) {
                if (firstChar+i <= lastChar) {
                    as.addAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.Y,
                         new Float(y[i]), firstChar+i, firstChar+i+1);
                }
            }
        }

        // process dx attribute
        if (dxAtt.length() != 0) {
            float dx[] = TextUtilities.svgHorizontalCoordinateArrayToUserSpace
                (element, SVG_DX_ATTRIBUTE, dxAtt, ctx);

            for (int i = 0; i < dx.length; i++) {
                if (firstChar+i <= lastChar) {
                    as.addAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.DX,
                         new Float(dx[i]), firstChar+i, firstChar+i+1);
                }
            }
        }

        // process dy attribute
        if (dyAtt.length() != 0) {
            float dy[] = TextUtilities.svgVerticalCoordinateArrayToUserSpace
                (element, SVG_DY_ATTRIBUTE, dyAtt, ctx);

            for (int i = 0; i < dy.length; i++) {
                if (firstChar+i <= lastChar) {
                    as.addAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.DY,
                         new Float(dy[i]), firstChar+i, firstChar+i+1);
                }
            }
        }

        // process rotate attribute
        if (rotateAtt.length() != 0) {
            float rotate[] = TextUtilities.svgRotateArrayToFloats
                (element, SVG_ROTATE_ATTRIBUTE, rotateAtt, ctx);

            if (rotate.length == 1) {  // not a list
                // each char will have the same rotate value
                as.addAttribute
                    (GVTAttributedCharacterIterator.TextAttribute.ROTATION,
                    new Float(rotate[0]), firstChar, lastChar+1);

            } else {  // its a list
                // set each rotate value from the list
                for (int i = 0; i < rotate.length; i++) {
                    if (firstChar+i <= lastChar) {
                        as.addAttribute
                            (GVTAttributedCharacterIterator.TextAttribute.ROTATION,
                            new Float(rotate[i]), firstChar+i, firstChar+i+1);
                    }
                }
            }
        }

        // do the same for each child element
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
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
            GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
            if (delimeter == element || nodeAncestorOf(element, delimeter)) {
                firstChar = i;
                break;
            }
        }
        int lastChar = aci.getEndIndex()-1;
        for (int i = aci.getEndIndex()-1; i >= 0; i--) {
            aci.setIndex(i);
            Element delimeter = (Element)aci.getAttribute(
                GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
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
        Stroke stroke = PaintServer.convertStroke(element, ctx);
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
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)child;
                TextDecoration childTextDecoration
                    = getTextDecoration(childElement, node, textDecoration, ctx);
                addPaintAttributes(as, childElement, node, childTextDecoration, ctx);
            }
        }
    }


    /**
     * Returns the map to pass to the current characters.
     */
    protected Map getAttributeMap(BridgeContext ctx,
                                  Element element,
                                  GraphicsNode node,
                                  TextPath textPath) {

        CSSOMReadOnlyStyleDeclaration cssDecl
            = CSSUtilities.getComputedStyle(element);
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);

        Map result = new HashMap();
        CSSPrimitiveValue v;
        String s;
        float f;
        short t;
        boolean verticalText = false;

        result.put(GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER, element);

        if (element.getTagName().equals(SVG_ALT_GLYPH_TAG)) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER,
                       new SVGAltGlyphHandler(ctx, element));
        }

        if (textPath != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.TEXTPATH, textPath);
        }

        // Text-anchor
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_TEXT_ANCHOR_PROPERTY);
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
        result.put(GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE, a);

        // Font size, in user space units.
        float fs = TextUtilities.convertFontSize(element, ctx, cssDecl, uctx);
        result.put(TextAttribute.SIZE, new Float(fs));

        // Font weight
        // TODO: improve support for relative values
        // (e.g. "lighter", "bolder")
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_FONT_WEIGHT_PROPERTY);
        String fontWeightString;
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            fontWeightString = v.getStringValue();
            //System.out.println("CSS Font Weight "+v.getStringValue());
            if (v.getStringValue().charAt(0) == 'n') {
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_REGULAR);
            } else if (v.getStringValue().charAt(0) == 'l') {
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_LIGHT);
            } else {
                result.put(TextAttribute.WEIGHT,
                           TextAttribute.WEIGHT_BOLD);
            }
        } else {
            //System.out.println("CSS Font Weight "+v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER));
            fontWeightString = "" + v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
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
                           //TextAttribute.WEIGHT_DEMIBOLD);
                           TextAttribute.WEIGHT_BOLD);
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
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_FONT_STYLE_PROPERTY);
        String fontStyleString = v.getStringValue();
        switch (fontStyleString.charAt(0)) {
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
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_FONT_STRETCH_PROPERTY);
        String fontStretchString = v.getStringValue();
        switch (fontStretchString.charAt(0)) {
        case 'u':
            if (fontStretchString.charAt(6) == 'c') {
                result.put(TextAttribute.WIDTH,
                           TextAttribute.WIDTH_CONDENSED);
            } else {
                result.put(TextAttribute.WIDTH,
                           TextAttribute.WIDTH_EXTENDED);
            }
            break;
        case 'e':
            if (fontStretchString.charAt(6) == 'c') {
                result.put(TextAttribute.WIDTH,
                           TextAttribute.WIDTH_CONDENSED);
            } else {
                if (fontStretchString.length() == 8) {
                    result.put(TextAttribute.WIDTH,
                               TextAttribute.WIDTH_SEMI_EXTENDED);
                } else {
                    result.put(TextAttribute.WIDTH,
                               TextAttribute.WIDTH_EXTENDED);
                }
            }
            break;
        case 's':
            if (fontStretchString.charAt(6) == 'c') {
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


        // Font family
        CSSValueList ff = (CSSValueList)cssDecl.getPropertyCSSValueInternal
            (CSS_FONT_FAMILY_PROPERTY);

        //  make a list of GVTFontFamily objects
        Vector fontFamilyList = new Vector();
        for (int i = 0; i < ff.getLength(); i++) {
            v = (CSSPrimitiveValue)ff.item(i);
            String fontFamilyName = v.getStringValue();
            GVTFontFamily fontFamily
                = SVGFontUtilities.getFontFamily(element, ctx, fontFamilyName,
                   fontWeightString, fontStyleString);
            fontFamilyList.add(fontFamily);
        }
        result.put(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT_FAMILIES,
                   fontFamilyList);

        // Text baseline adjustment.
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_BASELINE_SHIFT_PROPERTY);
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            s = v.getStringValue();
            //System.out.println("Baseline-shift: "+s);
            switch (s.charAt(2)) {
            case 'p': //suPerscript
                result.put(GVTAttributedCharacterIterator.
                           TextAttribute.BASELINE_SHIFT,
                           TextAttribute.SUPERSCRIPT_SUPER);
                break;
            case 'b': //suBscript
                result.put(GVTAttributedCharacterIterator.
                           TextAttribute.BASELINE_SHIFT,
                           TextAttribute.SUPERSCRIPT_SUB);
                break;
            case 's': //baSeline
                break;
            }
        } else if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
            f = v.getFloatValue(v.getPrimitiveType());
            result.put(GVTAttributedCharacterIterator.TextAttribute.BASELINE_SHIFT,
                       new Float(f*fs/100f));
        } else {

            f = UnitProcessor.cssOtherLengthToUserSpace
                (v, CSS_BASELINE_SHIFT_PROPERTY, uctx);
            result.put(GVTAttributedCharacterIterator.TextAttribute.BASELINE_SHIFT, new Float(f));
        }

        // Unicode-bidi mode
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_UNICODE_BIDI_PROPERTY);
        s = v.getStringValue();
        if (s.charAt(0) == 'n') {
            result.put(TextAttribute.BIDI_EMBEDDING,
                       new Integer(0));
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

            v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
                (CSS_DIRECTION_PROPERTY);
            String rs = v.getStringValue();
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

        boolean horizontal = true;

        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_WRITING_MODE_PROPERTY);
        s = v.getStringValue();
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
            horizontal = false;
            break;
        }

        // glyph-orientation-vertical

        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_GLYPH_ORIENTATION_VERTICAL_PROPERTY);

        // why is it that getStringValue() throws an exception?
        s = v.getCssText();
        switch (s.charAt(0)) {
        case 'a':
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION,
                       GVTAttributedCharacterIterator.
                       TextAttribute.ORIENTATION_AUTO);
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case '.':
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION,
                       GVTAttributedCharacterIterator.
                       TextAttribute.ORIENTATION_ANGLE);
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.VERTICAL_ORIENTATION_ANGLE,
                       new Float(s));
            break;
        }


        // Font stretch
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_FONT_STRETCH_PROPERTY);
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

        // text spacing properties...

        // Letter Spacing
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_LETTER_SPACING_PROPERTY);
        t = v.getPrimitiveType();
        if (t != CSSPrimitiveValue.CSS_IDENT) {
            if (horizontal) {
                f = UnitProcessor.cssHorizontalCoordinateToUserSpace
                    (v, CSS_LETTER_SPACING_PROPERTY, uctx);
            } else {
                f = UnitProcessor.cssVerticalCoordinateToUserSpace
                    (v, CSS_LETTER_SPACING_PROPERTY, uctx);
            }
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.LETTER_SPACING,
                       new Float(f));
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.CUSTOM_SPACING,
                       Boolean.TRUE);
        }

        // Word spacing
        v = (CSSPrimitiveValue)cssDecl.getPropertyCSSValueInternal
            (CSS_WORD_SPACING_PROPERTY);
        t = v.getPrimitiveType();
        if (t != CSSPrimitiveValue.CSS_IDENT) {
            if (horizontal) {
                f = UnitProcessor.cssHorizontalCoordinateToUserSpace
                    (v, CSS_WORD_SPACING_PROPERTY, uctx);
            } else {
                f = UnitProcessor.cssVerticalCoordinateToUserSpace
                    (v, CSS_WORD_SPACING_PROPERTY, uctx);
            }
            result.put(GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING,
                       new Float(f));
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.CUSTOM_SPACING,
                       Boolean.TRUE);
        }

        // Kerning
        s = element.getAttributeNS(null, SVG_KERNING_ATTRIBUTE);
        if (s.length() != 0) {
            if (horizontal) {
                f = UnitProcessor.svgHorizontalLengthToUserSpace
                    (s, SVG_KERNING_ATTRIBUTE, uctx);
            } else {
                f = UnitProcessor.svgVerticalLengthToUserSpace
                    (s, SVG_KERNING_ATTRIBUTE, uctx);
            }
            result.put(GVTAttributedCharacterIterator.TextAttribute.KERNING,
                       new Float(f));
            result.put(GVTAttributedCharacterIterator.
                       TextAttribute.CUSTOM_SPACING,
                       Boolean.TRUE);
        }

        // textLength
        s = element.getAttributeNS(null, SVG_TEXT_LENGTH_ATTRIBUTE);
        if (s.length() != 0) {
            if (horizontal) {
                f = UnitProcessor.svgHorizontalLengthToUserSpace
                    (s, SVG_TEXT_LENGTH_ATTRIBUTE, uctx);
            } else {
                f = UnitProcessor.svgVerticalLengthToUserSpace
                    (s, SVG_TEXT_LENGTH_ATTRIBUTE, uctx);
            }
            result.put(GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH,
                       new Float(f));

            // lengthAdjust
            s = element.getAttributeNS(null, SVG_LENGTH_ADJUST_ATTRIBUTE);

            if (s.length() < 10) {
                result.put(GVTAttributedCharacterIterator.
                           TextAttribute.LENGTH_ADJUST,
                           GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING);
                result.put(GVTAttributedCharacterIterator.
                           TextAttribute.CUSTOM_SPACING,
                           Boolean.TRUE);
            } else {
                result.put(GVTAttributedCharacterIterator.
                           TextAttribute.LENGTH_ADJUST,
                           GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL);
            }
        }

        return result;
    }


    /**
     * Constructs a TextDecoration object for the specified element. This will
     * contain all of the decoration properties to be used when drawing the
     * text.
     */
    private TextDecoration getTextDecoration(Element element, GraphicsNode node,
                       TextDecoration parentTextDecoration, BridgeContext ctx) {

        TextDecoration textDecoration = new TextDecoration(parentTextDecoration);

        AbstractViewCSS viewCss = CSSUtilities.getViewCSS(element);
        CSSOMReadOnlyStyleDeclaration styleDecl = viewCss.getCascadedStyle(element, null);

        // determine if text-decoration was explicity set on this element
        CSSValue cssVal = styleDecl.getLocalPropertyCSSValue(CSS_TEXT_DECORATION_PROPERTY);
        if (cssVal == null) {
            // not explicitly set so return the copy of the parent's decoration
            return textDecoration;
        }

        short t = cssVal.getCssValueType();

        if (t == CSSValue.CSS_VALUE_LIST) {

            // first check to see if its a valid list,
            // ie. if it contains none then that is the only element
            CSSValueList lst = (CSSValueList)cssVal;

            Paint paint = PaintServer.convertFillPaint(element, node, ctx);
            Paint strokePaint = PaintServer.convertStrokePaint(element, node, ctx);
            Stroke stroke = PaintServer.convertStroke(element, ctx);

            for (int i = 0; i < lst.getLength(); i++) {
                CSSPrimitiveValue v = (CSSPrimitiveValue)lst.item(i);
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
        } else if (t == CSSValue.CSS_PRIMITIVE_VALUE) {
            // must be explicitly set to "none"
            return new TextDecoration();
        }
        return textDecoration;
    }


    protected static class TextDecoration {

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


