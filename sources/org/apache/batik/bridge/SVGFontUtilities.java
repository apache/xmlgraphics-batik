/*****************************************************************************
 * Copyrightp (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XMLBaseSupport;

import org.apache.batik.dom.util.XLinkSupport;

import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.UnresolvedFontFamily;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.svg.SVGDocument;

/**
 * Utility class for SVG fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public abstract class SVGFontUtilities implements SVGConstants {

    /**
     * Given a font family name tries to find a matching SVG font
     * object.  If finds one, returns an SVGFontFamily otherwise
     * returns an UnresolvedFontFamily.
     *
     * @param textElement The text element that the font family will
     * be attached to.
     * @param ctx The bridge context, used to search for a matching
     * SVG font element.
     * @param fontFamilyName The name of the font family to search
     * for.
     * @param fontWeight The weight of the font to use when trying to
     * match an SVG font family.
     * @param fontStyle The style of the font to use when trying to
     * match as SVG font family.
     *
     * @return A GVTFontFamily for the specified font attributes. This
     * will be unresolved unless a matching SVG font was found.
     */
    public static GVTFontFamily getFontFamily(Element textElement,
                                             BridgeContext ctx,
                                             String fontFamilyName,
                                             String fontWeight,
                                             String fontStyle) {

        // TODO: should match against font-variant as well
        String fontKeyName = fontFamilyName.toLowerCase() + " " +
            fontWeight + " " + fontStyle;

        // check fontFamilyMap to see if we have already created an
        // FontFamily that matches
        Map fontFamilyMap = ctx.getFontFamilyMap();
        GVTFontFamily fontFamily =
            (GVTFontFamily)fontFamilyMap.get(fontKeyName);
        if (fontFamily != null) {
            return fontFamily;
        }

        // try to find a matching SVGFontFace element
        Document doc = textElement.getOwnerDocument();
        NodeList fontFaceElements = doc.getElementsByTagNameNS
	    (SVG_NAMESPACE_URI, SVG_FONT_FACE_TAG);

        Vector svgFontFamilies = new Vector();

        for (int i = 0; i < fontFaceElements.getLength(); i++) {

            Element fontFaceElement = (Element)fontFaceElements.item(i);

            // find matching font element
            Element fontElement = findFontElement(fontFaceElement,
                                                  fontFamilyName,
                                                  ctx);
            if (fontElement != null) {
                // create a font face
                Element fontFaceChild = null;
                for (Node n = fontElement.getFirstChild();
                     n != null;
                     n = n.getNextSibling()) {
                    if (n.getNodeType() == n.ELEMENT_NODE) {
                        if (n.getNamespaceURI().equals(SVG_NAMESPACE_URI) &&
                            n.getLocalName().equals(SVG_FONT_FACE_TAG)) {
                            fontFaceChild = (Element)n;
                            break;
                        }
                    }
                }
                if (fontFaceChild == null) {
                    continue;
                }
                SVGFontFaceElementBridge fontFaceBridge =
                    (SVGFontFaceElementBridge)ctx.getBridge(fontFaceChild);
                SVGFontFace fontFace =
                    fontFaceBridge.createFontFace(ctx, fontFaceChild);
                
                // see if the font face is ok for the font-weight and style etc
                
                String fontFaceStyle = fontFace.getFontStyle();
                
                if (fontFaceStyle.equals(SVG_ALL_VALUE) ||
                    fontFaceStyle.indexOf(fontStyle) != -1) {
                    
                    // create a new SVGFontFamily
                    GVTFontFamily gvtFontFamily =
                        new SVGFontFamily(fontFace, fontElement, ctx);
                    svgFontFamilies.add(gvtFontFamily);
                }
            }
        }

        if (svgFontFamilies.size() == 1) {
            // only found one matching svg font family
            fontFamilyMap.put(fontKeyName, svgFontFamilies.elementAt(0));
            return (GVTFontFamily)svgFontFamilies.elementAt(0);
            
        } else if (svgFontFamilies.size() > 1) {
            // need to find font face that matches the font-weight closest
            String fontWeightNumber = getFontWeightNumberString(fontWeight);

            // create lists of font weight numbers for each font family
            Vector fontFamilyWeights = new Vector();
            for (int i = 0; i < svgFontFamilies.size(); i++) {
                SVGFontFace fontFace =
                    ((SVGFontFamily)svgFontFamilies.get(i)).getFontFace();
                String fontFaceWeight = fontFace.getFontWeight();
                fontFaceWeight = getFontWeightNumberString(fontFaceWeight);
                fontFamilyWeights.add(fontFaceWeight);
            }

            // make sure that each possible font-weight has been
            // assigned to a font-face, if not then need to "fill the
            // holes"

            Vector newFontFamilyWeights = (Vector)fontFamilyWeights.clone();
            for (int i = 100; i <= 900; i+= 100) {
                String weightString = String.valueOf(i);
                boolean matched = false;
                int minDifference = 1000;
                int minDifferenceIndex = 0;
                for (int j = 0; j < fontFamilyWeights.size(); j++) {
                    String fontFamilyWeight = (String)fontFamilyWeights.get(j);
                    if (fontFamilyWeight.indexOf(weightString) > -1) {
                        matched = true;
                        break;
                    }
                    StringTokenizer st =
                        new StringTokenizer(fontFamilyWeight, " ,");
                    while (st.hasMoreTokens()) {
                        int weightNum = Integer.parseInt(st.nextToken());
                        int difference = (int)Math.abs(weightNum - i);
                        if (difference < minDifference) {
                            minDifference = difference;
                            minDifferenceIndex = j;
                        }
                    }
                }
                if (!matched) {
                    String newFontFamilyWeight =
                        newFontFamilyWeights.elementAt(minDifferenceIndex) +
                        ", " + weightString;
                    newFontFamilyWeights.setElementAt
                        (newFontFamilyWeight, minDifferenceIndex);
                }
            }


            // now find matching font weight
            for (int i = 0; i < svgFontFamilies.size(); i++) {
                String fontFaceWeight = (String)newFontFamilyWeights.get(i);
                if (fontFaceWeight.indexOf(fontWeightNumber) > -1) {
                    fontFamilyMap.put(fontKeyName, svgFontFamilies.get(i));
                    return (GVTFontFamily)svgFontFamilies.elementAt(i);
                }
            }
            // should not get here, just return the first svg font family
            fontFamilyMap.put(fontKeyName, svgFontFamilies.elementAt(0));
            return (GVTFontFamily) svgFontFamilies.elementAt(0);

        } else {
            // couldn't find one so return an UnresolvedFontFamily object
            GVTFontFamily gvtFontFamily =
                new UnresolvedFontFamily(fontFamilyName);
            fontFamilyMap.put(fontKeyName, gvtFontFamily);
            return gvtFontFamily;
        }
    }

    /**
     * Returns a string that contains all of the font weight numbers for the
     * specified font weight attribute value.
     *
     * @param fontWeight The font-weight attribute value.
     *
     * @return The font weight expressed as font weight numbers.
     *         e.g. "normal" becomes "400".
     */
    protected static String getFontWeightNumberString(String fontWeight) {
        if (fontWeight.equals(SVG_NORMAL_VALUE)) {
            return SVG_400_VALUE;
        } else if (fontWeight.equals(SVG_BOLD_VALUE)) {
            return SVG_700_VALUE;
        } else if (fontWeight.equals(SVG_ALL_VALUE)) {
            return "100, 200, 300, 400, 500, 600, 700, 800, 900";
        }
        return fontWeight;
    }

    /**
     * Finds the font element corresponding to the given font-face element.
     */
    protected static Element findFontElement(Element ffElt, String family,
                                             BridgeContext ctx) {
        String ffname = ffElt.getAttributeNS(null, SVG_FONT_FAMILY_ATTRIBUTE);

        if (ffname.length() < family.length()) {
            return null;
        }

        ffname = ffname.toLowerCase();

        int idx = ffname.indexOf(family.toLowerCase());

        if (idx == -1) {
            return null;
        }

        // see if the family name is not the part of a bigger family name.
        if (ffname.length() > family.length()) {
            boolean quote = false;
            if (idx > 0) {
                char c = ffname.charAt(idx - 1);
                switch (c) {
                default:
                    return null;
                case ' ':
                    loop: for (int i = idx - 2; i >= 0; --i) {
                        switch (ffname.charAt(i)) {
                        default:
                            return null;
                        case ' ':
                            continue;
                        case '"':
                        case '\'':
                            quote = true;
                            break loop;
                        }
                    }
                    break;
                case '"':
                case '\'':
                    quote = true;
                case ',':
                }
            }
            if (idx + family.length() < ffname.length()) {
                char c = ffname.charAt(idx + family.length());
                switch (c) {
                default:
                    return null;
                case ' ':
                    loop: for (int i = idx + family.length() + 1;
                         i < ffname.length(); i++) {
                        switch (ffname.charAt(i)) {
                        default:
                            return null;
                        case ' ':
                            continue;
                        case '"':
                        case '\'':
                            if (!quote) {
                                return null;
                            }
                            break loop;
                        }
                    }
                    break;
                case '"':
                case '\'':
                    if (!quote) {
                        return null;
                    }
                case ',':
                }
            }
        }

        Element fontElt = SVGUtilities.getParentElement(ffElt);
        if (fontElt.getNamespaceURI() == SVG_NAMESPACE_URI &&
            fontElt.getLocalName().equals(SVG_FONT_TAG)) {
            return fontElt;
        }

        // Search for a font-face-src element
        Element ffsrc = null;
        for (Node n = ffElt.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            if (n.getNodeType() == n.ELEMENT_NODE) {
                if (n.getNamespaceURI().equals(SVG_NAMESPACE_URI) &&
                    n.getLocalName().equals(SVG_FONT_FACE_SRC_TAG)) {
                    ffsrc = (Element)n;
                    break;
                }
            }
        }
        if (ffsrc == null) {
            return null;
        }

        // Search for a font-face-uri element
        Element ffuri = null;
        for (Node n = ffsrc.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            if (n.getNodeType() == n.ELEMENT_NODE) {
                if (n.getNamespaceURI().equals(SVG_NAMESPACE_URI) &&
                    n.getLocalName().equals(SVG_FONT_FACE_URI_TAG)) {
                    ffuri = (Element)n;
                    break;
                }
            }
        }
        if (ffuri == null) {
            return null;
        }
        
        String uri = XLinkSupport.getXLinkHref(ffuri);
        Element ref = ctx.getReferencedElement(ffuri, uri);
        if (ref.getNamespaceURI() != SVG_NAMESPACE_URI ||
            ref.getLocalName() != SVG_FONT_TAG) {
            return null;
        }

        SVGOMDocument doc = (SVGOMDocument)ffuri.getOwnerDocument();
        SVGOMDocument rdoc = (SVGOMDocument)ref.getOwnerDocument();

        boolean isLocal = doc == rdoc;
        fontElt = (isLocal) ? ref : (Element)doc.importNode(ref, true);
        
        if (!isLocal) {
            String base = XMLBaseSupport.getCascadedXMLBase(ffuri);
            Element g = doc.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
            g.appendChild(fontElt);
            g.setAttributeNS(XMLBaseSupport.XML_NAMESPACE_URI,
                             "xml:base",
                             base);
            CSSUtilities.computeStyleAndURIs(ref, fontElt, uri);
        }
        return fontElt;
    }
}
