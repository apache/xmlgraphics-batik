/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.extension.svg;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.TextUtilities;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.SVGAElementBridge;

import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.MarginInfo;

/**
 * Bridge class for the &lt;flowText> element.
 *
 * @author <a href="deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class SVGFlowTextElementBridge extends SVGTextElementBridge 
    implements BatikExtConstants {

    public static final AttributedCharacterIterator.Attribute FLOW_PARAGRAPH
        = GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;

    public static final AttributedCharacterIterator.Attribute 
        FLOW_EMPTY_PARAGRAPH
        = GVTAttributedCharacterIterator.TextAttribute.FLOW_EMPTY_PARAGRAPH;

    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK
        = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;
    
    public static final AttributedCharacterIterator.Attribute FLOW_REGIONS
        = GVTAttributedCharacterIterator.TextAttribute.FLOW_REGIONS;

    /**
     * Constructs a new bridge for the &lt;flowText> element.
     */
    public SVGFlowTextElementBridge() {}

    /**
     * Returns the SVG namespace URI.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns 'flowText'.
     */
    public String getLocalName() {
        return BATIK_EXT_FLOW_TEXT_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGFlowTextElementBridge();
    }

    /**
     * Returns false as text is not a container.
     */
    public boolean isComposite() {
        return false;
    }

    /**
     * Returns the text node location In this case the text node may
     * have serveral effective locations (one for each flow region).
     * So it always returns 0,0.
     *
     * @param ctx the bridge context to use
     * @param e the text element
     */
    protected Point2D getLocation(BridgeContext ctx, Element e) {
        return new Point2D.Float(0,0);
    }

    /**
     * Creates the attributed string which represents the given text
     * element children.
     *
     * @param ctx the bridge context to use
     * @param element the text element
     */
    protected AttributedString buildAttributedString(BridgeContext ctx,
                                                     Element element) {
        List rgns = getRegions(ctx, element);
        AttributedString ret = getFlowDiv(ctx, element);
        ret.addAttribute(FLOW_REGIONS, rgns, 0, 1);
        return ret;
    }

    /**
     * Adds glyph position attributes to an AttributedString.
     */
    protected void addGlyphPositionAttributes(AttributedString as,
                                              Element element,
                                              BridgeContext ctx) {
        if (element.getNodeType()     != Node.ELEMENT_NODE) return;
        String eNS = element.getNamespaceURI();
        if ((!eNS.equals(getNamespaceURI())) &&
            (!eNS.equals(SVG_NAMESPACE_URI)))
            return;
        if (element.getLocalName()    != BATIK_EXT_FLOW_TEXT_TAG) {
            // System.out.println("Elem: " + element);
            super.addGlyphPositionAttributes(as, element, ctx);
            return;
        }

        for (Node n = element.getFirstChild();
             n != null; n = n.getNextSibling()) {
            if (n.getNodeType()     != Node.ELEMENT_NODE) continue;
            String nNS = n.getNamespaceURI();
            if ((!getNamespaceURI().equals(nNS)) &&
                (!SVG_NAMESPACE_URI.equals(nNS))) {
                continue;
            }
            Element e = (Element)n;
            String ln = e.getLocalName();
            if (ln.equals(BATIK_EXT_FLOW_DIV_TAG)) {
                // System.out.println("D Elem: " + e);
                super.addGlyphPositionAttributes(as, e, ctx);
                return;
            }
        }
    }

    protected void addChildGlyphPositionAttributes(AttributedString as,
                                                   Element element,
                                                   BridgeContext ctx) {
        // Add Paint attributres for children of text element
        for (Node child = element.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String cNS = child.getNamespaceURI();
            if ((!getNamespaceURI().equals(cNS)) &&
                (!SVG_NAMESPACE_URI.equals(cNS))) {
                continue;
            }
            String ln = child.getLocalName();
            if (ln.equals(BATIK_EXT_FLOW_PARA_TAG) ||
                ln.equals(BATIK_EXT_FLOW_REGION_BREAK_TAG) ||
                ln.equals(BATIK_EXT_FLOW_LINE_TAG) ||
                ln.equals(BATIK_EXT_FLOW_SPAN_TAG) ||
                ln.equals(SVG_A_TAG) ||
                ln.equals(SVG_TREF_TAG)) {
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
        if (element.getNodeType()     != Node.ELEMENT_NODE) return;
        String eNS = element.getNamespaceURI();
        if ((!eNS.equals(getNamespaceURI())) &&
            (!eNS.equals(SVG_NAMESPACE_URI)))
            return;
        if (element.getLocalName()    != BATIK_EXT_FLOW_TEXT_TAG) {
            // System.out.println("Elem: " + element);
            super.addPaintAttributes(as, element, node, textDecoration, ctx);
            return;
        }

        for (Node n = element.getFirstChild();
             n != null; n = n.getNextSibling()) {
            if (n.getNodeType()     != Node.ELEMENT_NODE) continue;
            if (!getNamespaceURI().equals(n.getNamespaceURI())) continue;
            Element e = (Element)n;
            String ln = e.getLocalName();
            if (ln.equals(BATIK_EXT_FLOW_DIV_TAG)) {
                // System.out.println("D Elem: " + e);
                super.addPaintAttributes(as, e, node, textDecoration, ctx);
                return;
            }
        }
    }

    protected void addChildPaintAttributes(AttributedString as,
                                           Element element,
                                           TextNode node,
                                           TextDecoration textDecoration,
                                           BridgeContext ctx) {
        // Add Paint attributres for children of text element
        for (Node child = element.getFirstChild();
             child != null;
             child = child.getNextSibling()) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String cNS = child.getNamespaceURI();
            if ((!getNamespaceURI().equals(cNS)) &&
                (!SVG_NAMESPACE_URI.equals(cNS))) {
                continue;
            }
            String ln = child.getLocalName();
            if (ln.equals(BATIK_EXT_FLOW_PARA_TAG) ||
                ln.equals(BATIK_EXT_FLOW_REGION_BREAK_TAG) ||
                ln.equals(BATIK_EXT_FLOW_LINE_TAG) ||
                ln.equals(BATIK_EXT_FLOW_SPAN_TAG) ||
                ln.equals(SVG_A_TAG) ||
                ln.equals(SVG_TREF_TAG)) {
                Element childElement = (Element)child;
                TextDecoration td = getTextDecoration(childElement, node,
                                                      textDecoration, ctx);
                addPaintAttributes(as, childElement, node, td, ctx);
            }
        }
    }

    protected AttributedString getFlowDiv
        (BridgeContext ctx, Element element) {
        for (Node n = element.getFirstChild();
             n != null; n = n.getNextSibling()) {
            if (n.getNodeType()     != Node.ELEMENT_NODE) continue;
            if (n.getNamespaceURI() != getNamespaceURI()) continue;
            Element e = (Element)n;

            String ln = n.getLocalName();
            if (ln.equals(BATIK_EXT_FLOW_DIV_TAG)) {
                return gatherFlowPara(ctx, e);
            }
        }
        return null;
    }

    protected AttributedString gatherFlowPara
        (BridgeContext ctx, Element div) {
        AttributedStringBuffer asb = new AttributedStringBuffer();
        List paraEnds  = new ArrayList();
        List paraElems = new ArrayList();
        List lnLocs    = new ArrayList();
        for (Node n = div.getFirstChild();
             n != null; n = n.getNextSibling()) {
            if (n.getNodeType()     != Node.ELEMENT_NODE) continue;
            if (n.getNamespaceURI() != getNamespaceURI()) continue;
            Element e = (Element)n;

            String ln = e.getLocalName();
            if (ln.equals(BATIK_EXT_FLOW_PARA_TAG)) {
                fillAttributedStringBuffer(ctx, e, true, asb, lnLocs);

                paraElems.add(e);
                paraEnds.add(new Integer(asb.length()));
            } else if (ln.equals(BATIK_EXT_FLOW_REGION_BREAK_TAG)) {
                fillAttributedStringBuffer(ctx, e, true, asb, lnLocs);

                paraElems.add(e);
                paraEnds.add(new Integer(asb.length()));
            }
        }

        // Layer in the PARAGRAPH/LINE_BREAK Attributes so we can
        // break up text chunks.
        AttributedString ret = asb.toAttributedString();

        // Note: The Working Group (in conjunction with XHTML working
        // group) has decided that multiple line elements collapse.
        int prevLN = 0;
        int lnCount = 0;
        Iterator lnIter = lnLocs.iterator();
        while (lnIter.hasNext()) {
            int nextLN = ((Integer)lnIter.next()).intValue();
            if (nextLN == prevLN) continue;

            ret.addAttribute(FLOW_LINE_BREAK, 
                             new Object(),
                             prevLN, nextLN);
            // System.out.println("Attr: [" + prevLN + "," + nextLN + "]");
            prevLN  = nextLN;
        }

        int start=0;
        int end;
        List emptyPara = null;
        for (int i=0; i<paraElems.size(); i++, start=end) {
            Element elem = (Element)paraElems.get(i);
            end  = ((Integer)paraEnds.get(i)).intValue();
            if (start == end) {
                if (emptyPara == null)
                    emptyPara = new LinkedList();
                emptyPara.add(makeMarginInfo(elem));
                continue;
            }
            // System.out.println("Para: [" + start + ", " + end + "]");
            ret.addAttribute(FLOW_PARAGRAPH, makeMarginInfo(elem), start, end);
            if (emptyPara != null) {
                ret.addAttribute(FLOW_EMPTY_PARAGRAPH, emptyPara, start, end);
                emptyPara = null;
            }
        }

        return ret;
    }

    protected List getRegions(BridgeContext ctx, Element element)  {
        for (Node n = element.getFirstChild();
             n != null; n = n.getNextSibling()) {
            if (n.getNodeType()     != Node.ELEMENT_NODE) continue;
            if (n.getNamespaceURI() != getNamespaceURI()) continue;
            Element e = (Element)n;

            String ln = n.getLocalName();
            if (ln.equals(BATIK_EXT_FLOW_REGION_TAG)) {
                return gatherRects(ctx, e);
            }
        }
        return null;
    }
    
    protected List gatherRects(BridgeContext ctx, Element rgn) {
        List ret = new LinkedList();
        for (Node n = rgn.getFirstChild(); 
             n != null; n = n.getNextSibling()) {
            if (n.getNodeType()     != Node.ELEMENT_NODE) continue;
            if (n.getNamespaceURI() != getNamespaceURI()) continue;
            Element e = (Element)n;

            String ln = n.getLocalName();
            if (ln.equals(BATIK_EXT_REGION_TAG)) {
                UnitProcessor.Context uctx;
                uctx = UnitProcessor.createContext(ctx, e);
                Rectangle2D r2d = buildRect(uctx, e);
                if (r2d != null)
                    ret.add(r2d);
            }
        }

        return ret;
    }

    protected Rectangle2D buildRect(UnitProcessor.Context uctx,
                                    Element e) {
        String s;

        // 'x' attribute - default is 0
        s = e.getAttributeNS(null, BATIK_EXT_X_ATTRIBUTE);
        float x = 0;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, BATIK_EXT_X_ATTRIBUTE, uctx);
        }

        // 'y' attribute - default is 0
        s = e.getAttributeNS(null, BATIK_EXT_Y_ATTRIBUTE);
        float y = 0;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, BATIK_EXT_Y_ATTRIBUTE, uctx);
        }

        // 'width' attribute - required
        s = e.getAttributeNS(null, BATIK_EXT_WIDTH_ATTRIBUTE);
        float w;
        if (s.length() != 0) {
            w = UnitProcessor.svgHorizontalLengthToUserSpace
                (s, BATIK_EXT_WIDTH_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_MISSING,
                 new Object[] {BATIK_EXT_WIDTH_ATTRIBUTE, s});
        }
	// A value of zero disables rendering of the element
	if (w == 0) {
	    return null;
	}

        // 'height' attribute - required
        s = e.getAttributeNS(null, BATIK_EXT_HEIGHT_ATTRIBUTE);
        float h;
        if (s.length() != 0) {
            h = UnitProcessor.svgVerticalLengthToUserSpace
                (s, BATIK_EXT_HEIGHT_ATTRIBUTE, uctx);
        } else {
            throw new BridgeException
                (e, ERR_ATTRIBUTE_MISSING,
                 new Object[] {BATIK_EXT_HEIGHT_ATTRIBUTE, s});
        }
	// A value of zero disables rendering of the element
	if (h == 0) {
	    return null;
	}

        return new Rectangle2D.Float(x,y,w,h);
    }

    /**
     * Fills the given AttributedStringBuffer.
     */
    protected void fillAttributedStringBuffer(BridgeContext ctx,
                                              Element element,
                                              boolean top,
                                              AttributedStringBuffer asb,
                                              List lnLocs) {
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent())) {
            return;
        }
        
        String  s        = XMLSupport.getXMLSpace(element);
        boolean preserve = s.equals(SVG_PRESERVE_VALUE);
        boolean first = true;
        boolean last;
        boolean stripFirst  = !preserve;
        boolean stripLast   = !preserve;
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
                // System.out.println("Element: " + n);
                if ((n.getNamespaceURI() != getNamespaceURI()) &&
                    (n.getNamespaceURI() != SVG_NAMESPACE_URI)) {
                    break;
                }
                
                nodeElement = (Element)n;
                String ln = n.getLocalName();
                if (ln.equals(BATIK_EXT_FLOW_LINE_TAG)) {
                    fillAttributedStringBuffer(ctx, nodeElement, 
                                               false, asb, lnLocs);
                    // System.out.println("Line: " + asb.length() + 
                    //                    " - '" +  asb + "'");
                    lnLocs.add(new Integer(asb.length()));
                } else if (ln.equals(BATIK_EXT_FLOW_SPAN_TAG) ||
                           ln.equals(SVG_ALT_GLYPH_TAG)) {
                    fillAttributedStringBuffer(ctx, nodeElement,
                                               false, asb, lnLocs);
                } else if (ln.equals(SVG_A_TAG)) {
                    EventTarget target = (EventTarget)nodeElement;
                    UserAgent ua = ctx.getUserAgent();
                    target.addEventListener
                        (SVG_EVENT_CLICK, 
                         new SVGAElementBridge.AnchorListener(ua),
                         false);
                    
                    target.addEventListener
                        (SVG_EVENT_MOUSEOVER,
                         new SVGAElementBridge.CursorMouseOverListener(ua),
                         false);
                    
                    target.addEventListener
                        (SVG_EVENT_MOUSEOUT,
                         new SVGAElementBridge.CursorMouseOutListener(ua),
                         false);
                    fillAttributedStringBuffer(ctx,
                                               nodeElement,
                                               false,
                                               asb, lnLocs);
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
                        Map m = getAttributeMap(ctx, nodeElement, null);
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
                        map = getAttributeMap(ctx, element, null);
                    }
                    asb.append(s, map);
                }
            }
            first = false;
        }
    }

    protected void checkMap(Map attrs) {
        if (attrs.containsKey(TEXTPATH)) {
            return; // Problem, unsupported attr
        }

        if (attrs.containsKey(ANCHOR_TYPE)) {
            return; // Problem, unsupported attr
        }

        if (attrs.containsKey(LETTER_SPACING)) {
            return; // Problem, unsupported attr
        }

        if (attrs.containsKey(WORD_SPACING)) {
            return; // Problem, unsupported attr
        }

        if (attrs.containsKey(KERNING)) {
            return; // Problem, unsupported attr
        }
    }

    protected final static 
        GVTAttributedCharacterIterator.TextAttribute TEXTPATH = 
        GVTAttributedCharacterIterator.TextAttribute.TEXTPATH;

    protected final static 
        GVTAttributedCharacterIterator.TextAttribute ANCHOR_TYPE = 
        GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE;

    protected final static 
        GVTAttributedCharacterIterator.TextAttribute LETTER_SPACING = 
        GVTAttributedCharacterIterator.TextAttribute.LETTER_SPACING;

    protected final static 
        GVTAttributedCharacterIterator.TextAttribute WORD_SPACING = 
        GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING;

    protected final static 
        GVTAttributedCharacterIterator.TextAttribute KERNING = 
        GVTAttributedCharacterIterator.TextAttribute.KERNING;

    public static class LineBreakInfo {
        int     breakIdx;
        float   lineAdvAdj;
        boolean relative;
        /**
         * @param breakIdx   The character after which to break.
         * @param lineAdvAdj The line advance adjustment.
         * @param relative   If true lineAdvAdj must be multiplied by
         *                   the line height.
         */
        public LineBreakInfo(int breakIdx, float lineAdvAdj, boolean relative){
            this.breakIdx = breakIdx;
            this.lineAdvAdj = lineAdvAdj;
            this.relative = relative;
        }
        public int     getBreakIdx()   { return breakIdx; }
        public boolean isRelative()    { return relative; }
        public float   getLineAdvAdj() { return lineAdvAdj; }
    }

    public MarginInfo makeMarginInfo(Element e) {
        String s;
        float top=0, right=0, bottom=0, left=0;

        s = e.getAttributeNS(null, BATIK_EXT_MARGIN_ATTRIBUTE);
        try {
            if (s.length() != 0) {
                float f = Float.parseFloat(s);
                top=right=bottom=left=f;
            }
        } catch(NumberFormatException nfe) { /* nothing */ }

        s = e.getAttributeNS(null, BATIK_EXT_TOP_MARGIN_ATTRIBUTE);
        try {
            if (s.length() != 0) {
                float f = Float.parseFloat(s);
                top = f;
            }
        } catch(NumberFormatException nfe) { /* nothing */ }
        s = e.getAttributeNS(null, BATIK_EXT_RIGHT_MARGIN_ATTRIBUTE);
        try {
            if (s.length() != 0) {
                float f = Float.parseFloat(s);
                right = f;
            }
        } catch(NumberFormatException nfe) { /* nothing */ }
        s = e.getAttributeNS(null, BATIK_EXT_BOTTOM_MARGIN_ATTRIBUTE);
        try {
            if (s.length() != 0) {
                float f = Float.parseFloat(s);
                bottom = f;
            }
        } catch(NumberFormatException nfe) { /* nothing */ }
        s = e.getAttributeNS(null, BATIK_EXT_LEFT_MARGIN_ATTRIBUTE);
        try {
            if (s.length() != 0) {
                float f = Float.parseFloat(s);
                left = f;
            }
        } catch(NumberFormatException nfe) { /* nothing */ }

        float flLeft  = left;
        float flRight = right;

        s = e.getAttributeNS(null, BATIK_EXT_FIRST_LINE_LEFT_MARGIN_ATTRIBUTE);
        try {
            if (s.length() != 0) {
                float f = Float.parseFloat(s);
                flLeft = f;
            }
        } catch(NumberFormatException nfe) { /* nothing */ }

        s = e.getAttributeNS(null,BATIK_EXT_FIRST_LINE_RIGHT_MARGIN_ATTRIBUTE);
        try {
            if (s.length() != 0) {
                float f = Float.parseFloat(s);
                flRight = f;
            }
        } catch(NumberFormatException nfe) { /* nothing */ }


        int justification = MarginInfo.JUSTIFY_START;
        s = e.getAttributeNS(null, BATIK_EXT_JUSTIFICATION_ATTRIBUTE);
        try {
            if (s.length() != 0) {
                if (s.equals("start")) {
                    justification = MarginInfo.JUSTIFY_START;
                } else if (s.equals("middle")) {
                    justification = MarginInfo.JUSTIFY_MIDDLE;
                } else if (s.equals("end")) {
                    justification = MarginInfo.JUSTIFY_END;
                } else if (s.equals("full")) {
                    justification = MarginInfo.JUSTIFY_FULL;
                }
            }
        } catch(NumberFormatException nfe) { /* nothing */ }

        String ln = e.getLocalName();
        boolean rgnBr = ln.equals(BATIK_EXT_FLOW_REGION_BREAK_TAG);
        return new MarginInfo(top, right, bottom, left, flLeft, flRight,
                              justification, rgnBr);
    }


}
