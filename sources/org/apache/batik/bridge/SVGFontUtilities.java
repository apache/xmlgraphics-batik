/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.util.SVGConstants;

import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XMLBaseSupport;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import java.util.HashMap;
import java.util.Vector;
import java.util.StringTokenizer;

/**
 * Utility class for SVG fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public abstract class SVGFontUtilities implements SVGConstants {


    /**
     * A hash map of all the font families already matched. This is
     * to reduce the number of instances of GVTFontFamilies and to
     * hopefully reduce the time taken to search for a matching SVG font.
     */
    private static HashMap fontFamilyMap;

    /**
     * Keeps track of the currentDocument. This is used to detect when a new
     * document has been loaded.
     */
    private static Document currentDocument = null;

    /**
     * Given a font family name tries to find a matching SVG font object.
     * If finds one, returns an SVGFontFamily otherwise returns an
     * UnresolvedFontFamily.
     *
     * @param textElement The text element that the font family will be attached to.
     * @param ctx The bridge context, used to search for a matching SVG font element.
     * @param fontFamilyName The name of the font family to search for.
     * @param fontWeight The weight of the font to use when trying to match an SVG font family.
     * @param fontStyle The style of the font to use when trying to match as SVG font family.
     *
     * @return A GVTFontFamily for the specified font attributes. This will be
     * unresolved unless a matching SVG font was found.
     */
    public static GVTFontFamily getFontFamily(Element textElement,
                                             BridgeContext ctx,
                                             String fontFamilyName,
                                             String fontWeight,
                                             String fontStyle) {

        // TODO: should match against font-variant as well

        // if this is a new document reset the fontFamilyMap
        if (fontFamilyMap == null || textElement.getOwnerDocument() != currentDocument) {
            fontFamilyMap = new HashMap();
            currentDocument = textElement.getOwnerDocument();
        }

        String fontKeyName = fontFamilyName + " " + fontWeight + " " + fontStyle;

        // check fontFamilyMap to see if we have already created an FontFamily
        // that matches
        GVTFontFamily fontFamily = (GVTFontFamily)fontFamilyMap.get(fontKeyName);
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
            String elemFamilyName
                    = fontFaceElement.getAttribute(SVG_FONT_FAMILY_ATTRIBUTE);

            if (elemFamilyName.indexOf(fontFamilyName) == 0) {  // found one

                // find matching font element

                // see if its the parent node
                Element fontElement = (Element)fontFaceElement.getParentNode();

                if (!fontElement.getTagName().equals(SVG_FONT_TAG)) {

                    // parent element is not the font element
                    // need to look at the font-face-src

                    fontElement = null;

                    NodeList fontFaceSrcNodes = 
			fontFaceElement.getElementsByTagNameNS
			(SVG_NAMESPACE_URI, SVG_FONT_FACE_SRC_TAG);
                    if (fontFaceSrcNodes.getLength() > 0) {
                        Element fontFaceSrcElement = (Element)fontFaceSrcNodes.item(0);
                        // see if there is a fontFaceUri child
                        NodeList fontFaceUriNodes = fontFaceSrcElement.getElementsByTagNameNS(SVG_NAMESPACE_URI, SVG_FONT_FACE_URI_TAG);
                        if (fontFaceUriNodes.getLength() > 0) {
                            Element fontFaceUriElement = (Element)fontFaceUriNodes.item(0);

                            // get the referenced element
                            String uri = XLinkSupport.getXLinkHref(fontFaceUriElement);
                            Element refElement = ctx.getReferencedElement(fontFaceUriElement, uri);
                            // make sure its a font element
                            if (refElement.getTagName().equals(SVG_FONT_TAG)) {
                                SVGOMDocument document
                                    = (SVGOMDocument)fontFaceUriElement.getOwnerDocument();
                                SVGOMDocument refDocument
                                    = (SVGOMDocument)refElement.getOwnerDocument();
                                boolean isLocal = (refDocument == document);
                                // import or clone the referenced element in current document
                                fontElement = (isLocal) ? refElement
                                    : (Element)document.importNode(refElement, true);
                                if (!isLocal) {
                                    String base = XMLBaseSupport.getXMLBase(fontFaceUriElement);
                                    // need to attach the imported
                                    // element to the document and
                                    // then compute the styles and
                                    // uris
                                    Element g = document.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
                                    g.appendChild(fontElement);
                                    g.setAttributeNS(XMLBaseSupport.XML_NAMESPACE_URI,
                                                     "xml:base",
                                                     base);
                                    CSSUtilities.computeStyleAndURIs(refElement, fontElement, uri);
                                }
                            }
                        }
                    }
                }

                if (fontElement != null) {
                    // create a font face
                    NodeList fontFaceChildren = fontElement.getElementsByTagNameNS(SVG_NAMESPACE_URI, SVG_FONT_FACE_TAG);
                    Element fontFaceChild = (Element)fontFaceChildren.item(0);
                    SVGFontFaceElementBridge fontFaceBridge
                        = (SVGFontFaceElementBridge)ctx.getBridge(fontFaceChild);
                    SVGFontFace fontFace = fontFaceBridge.createFontFace(ctx, fontFaceChild);

                    // see if the font face is ok for the font-weight and style etc

                    String fontFaceStyle = fontFace.getFontStyle();

                    if (fontFaceStyle.equals(SVG_ALL_VALUE) || fontFaceStyle.indexOf(fontStyle) != -1) {

                        // create a new SVGFontFamily
                        GVTFontFamily gvtFontFamily = new SVGFontFamily(fontFace, fontElement, ctx);
                        svgFontFamilies.add(gvtFontFamily);
                    }
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
                SVGFontFace fontFace = ((SVGFontFamily)svgFontFamilies.elementAt(i)).getFontFace();
                String fontFaceWeight = fontFace.getFontWeight();
                fontFaceWeight = getFontWeightNumberString(fontFaceWeight);
                fontFamilyWeights.add(fontFaceWeight);
            }

            // make sure that each possible font-weight has been assigned to a font-face
            // if not then need to "fill the holes"

            Vector newFontFamilyWeights = (Vector)fontFamilyWeights.clone();
            for (int i = 100; i <= 900; i+= 100) {
                String weightString = String.valueOf(i);
                boolean matched = false;
                int minDifference = 1000;
                int minDifferenceIndex = 0;
                for (int j = 0; j < fontFamilyWeights.size(); j++) {
                    String fontFamilyWeight = (String)fontFamilyWeights.elementAt(j);
                    if (fontFamilyWeight.indexOf(weightString) > -1) {
                        matched = true;
                        break;
                    }
                    StringTokenizer st = new StringTokenizer(fontFamilyWeight, " ,");
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
                    String newFontFamilyWeight = newFontFamilyWeights.elementAt(minDifferenceIndex)
                                               + ", " + weightString;
                    newFontFamilyWeights.setElementAt(newFontFamilyWeight, minDifferenceIndex);
                }
            }


            // now find matching font weight
            for (int i = 0; i < svgFontFamilies.size(); i++) {
                String fontFaceWeight = (String)newFontFamilyWeights.elementAt(i);
                if (fontFaceWeight.indexOf(fontWeightNumber) > -1) {
                    fontFamilyMap.put(fontKeyName, svgFontFamilies.elementAt(i));
                    return (GVTFontFamily)svgFontFamilies.elementAt(i);
                }
            }
            // should not get here, just return the first svg font family
            fontFamilyMap.put(fontKeyName, svgFontFamilies.elementAt(0));
            return (GVTFontFamily) svgFontFamilies.elementAt(0);

        } else {
            // couldn't find one so return an UnresolvedFontFamily object
            GVTFontFamily gvtFontFamily = new UnresolvedFontFamily(fontFamilyName);
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
    private static String getFontWeightNumberString(String fontWeight) {
        if (fontWeight.equals(SVG_NORMAL_VALUE)) {
            return SVG_400_VALUE;
        } else if (fontWeight.equals(SVG_BOLD_VALUE)) {
            return SVG_700_VALUE;
        } else if (fontWeight.equals(SVG_ALL_VALUE)) {
            return "100, 200, 300, 400, 500, 600, 700, 800, 900";
        }
        return fontWeight;
    }
}
