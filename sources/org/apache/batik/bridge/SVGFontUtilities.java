/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.bridge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XMLBaseSupport;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import org.apache.batik.util.SVGConstants;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.FontFaceRule;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class for SVG fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public abstract class SVGFontUtilities implements SVGConstants {

    public static List getFontFaces(Document doc,
                                    BridgeContext ctx) {
        // check fontFamilyMap to see if we have already created an
        // FontFamily that matches
        Map fontFamilyMap = ctx.getFontFamilyMap();
        List ret = (List)fontFamilyMap.get(doc);
        if (ret != null) 
            return ret;

        ret = new LinkedList();

        NodeList fontFaceElements = doc.getElementsByTagNameNS
	    (SVG_NAMESPACE_URI, SVG_FONT_FACE_TAG);

        SVGFontFaceElementBridge fontFaceBridge;
        fontFaceBridge = (SVGFontFaceElementBridge)ctx.getBridge
            (SVG_NAMESPACE_URI, SVG_FONT_FACE_TAG);

        for (int i = 0; i < fontFaceElements.getLength(); i++) {
            Element fontFaceElement = (Element)fontFaceElements.item(i);
            ret.add(fontFaceBridge.createFontFace
                    (ctx, fontFaceElement));
        }

        CSSEngine engine = ((SVGOMDocument)doc).getCSSEngine();
        List sms = engine.getFontFaces();
        Iterator iter = sms.iterator();
        while (iter.hasNext()) {
            FontFaceRule ffr = (FontFaceRule)iter.next();
            ret.add(CSSFontFace.createCSSFontFace(engine, ffr));
        }
        return ret;
    }
                                       

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

        List fontFaces = (List)fontFamilyMap.get(doc);
        
        if (fontFaces == null) {
            fontFaces = getFontFaces(doc, ctx);
            fontFamilyMap.put(doc, fontFaces);
        }

        
        Iterator iter = fontFaces.iterator();
        List svgFontFamilies = new LinkedList();
        while (iter.hasNext()) {
            FontFace fontFace = (FontFace)iter.next();

            if (!fontFace.hasFamilyName(fontFamilyName)) {
                continue;
            }

            String fontFaceStyle = fontFace.getFontStyle();
            if (fontFaceStyle.equals(SVG_ALL_VALUE) ||
                fontFaceStyle.indexOf(fontStyle) != -1) {
                GVTFontFamily ffam = fontFace.getFontFamily(ctx);
                if (ffam != null) 
                    svgFontFamilies.add(ffam);
            }
        }

        if (svgFontFamilies.size() == 1) {
            // only found one matching svg font family
            fontFamilyMap.put(fontKeyName, svgFontFamilies.get(0));
            return (GVTFontFamily)svgFontFamilies.get(0);
            
        } else if (svgFontFamilies.size() > 1) {
            // need to find font face that matches the font-weight closest
            String fontWeightNumber = getFontWeightNumberString(fontWeight);

            // create lists of font weight numbers for each font family
            List fontFamilyWeights = new ArrayList(svgFontFamilies.size());
            Iterator ffiter = svgFontFamilies.iterator();
            while(ffiter.hasNext()) {
                GVTFontFace fontFace;
                fontFace = ((GVTFontFamily)ffiter.next()).getFontFace();
                String fontFaceWeight = fontFace.getFontWeight();
                fontFaceWeight = getFontWeightNumberString(fontFaceWeight);
                fontFamilyWeights.add(fontFaceWeight);
            }

            // make sure that each possible font-weight has been
            // assigned to a font-face, if not then need to "fill the
            // holes"

            List newFontFamilyWeights = new ArrayList(fontFamilyWeights);
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
                        newFontFamilyWeights.get(minDifferenceIndex) +
                        ", " + weightString;
                    newFontFamilyWeights.set(minDifferenceIndex, 
                                             newFontFamilyWeight);
                }
            }


            // now find matching font weight
            for (int i = 0; i < svgFontFamilies.size(); i++) {
                String fontFaceWeight = (String)newFontFamilyWeights.get(i);
                if (fontFaceWeight.indexOf(fontWeightNumber) > -1) {
                    fontFamilyMap.put(fontKeyName, svgFontFamilies.get(i));
                    return (GVTFontFamily)svgFontFamilies.get(i);
                }
            }
            // should not get here, just return the first svg font family
            fontFamilyMap.put(fontKeyName, svgFontFamilies.get(0));
            return (GVTFontFamily) svgFontFamilies.get(0);

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
}
