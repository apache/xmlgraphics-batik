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

import java.util.List;
import java.util.LinkedList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.batik.dom.svg.XMLBaseSupport;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.util.ParsedURL;

/**
 * Bridge class for the &lt;font-face> element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGFontFaceElementBridge extends AbstractSVGBridge
                                      implements ErrorConstants {

    /**
     * Constructs a new bridge for the &lt;font-face> element.
     */
    public SVGFontFaceElementBridge() {
    }

    /**
     * Returns 'font-face'.
     */
    public String getLocalName() {
        return SVG_FONT_FACE_TAG;
    }

    /**
     * Creates an SVGFontFace that repesents the specified
     * &lt;font-face> element.
     *
     * @param ctx The current bridge context.
     * @param fontFaceElement The &lt;font-face> element.
     *
     * @return A new SVGFontFace.
     */
    public SVGFontFace createFontFace(BridgeContext ctx,
                                      Element fontFaceElement) {

        // get all the font-face attributes

        String familyNames = fontFaceElement.getAttributeNS
            (null, SVG_FONT_FAMILY_ATTRIBUTE);

        // units per em
        String unitsPerEmStr = fontFaceElement.getAttributeNS
            (null, SVG_UNITS_PER_EM_ATTRIBUTE);
        if (unitsPerEmStr.length() == 0) {
            unitsPerEmStr = SVG_FONT_FACE_UNITS_PER_EM_DEFAULT_VALUE;
        }
        float unitsPerEm;
        try {
            unitsPerEm = SVGUtilities.convertSVGNumber(unitsPerEmStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_UNITS_PER_EM_ATTRIBUTE, unitsPerEmStr});
        }

        // font-weight
        String fontWeight = fontFaceElement.getAttributeNS
            (null, SVG_FONT_WEIGHT_ATTRIBUTE);
        if (fontWeight.length() == 0) {
            fontWeight = SVG_FONT_FACE_FONT_WEIGHT_DEFAULT_VALUE;
        }

        // font-style
        String fontStyle = fontFaceElement.getAttributeNS
            (null, SVG_FONT_STYLE_ATTRIBUTE);
        if (fontStyle.length() == 0) {
            fontStyle = SVG_FONT_FACE_FONT_STYLE_DEFAULT_VALUE;
        }

        // font-variant
        String fontVariant = fontFaceElement.getAttributeNS
            (null, SVG_FONT_VARIANT_ATTRIBUTE);
         if (fontVariant.length() == 0) {
            fontVariant = SVG_FONT_FACE_FONT_VARIANT_DEFAULT_VALUE;
        }

        // font-stretch
        String fontStretch = fontFaceElement.getAttributeNS
            (null, SVG_FONT_STRETCH_ATTRIBUTE);
         if (fontStretch.length() == 0) {
            fontStretch = SVG_FONT_FACE_FONT_STRETCH_DEFAULT_VALUE;
        }

        // slopeStr
        String slopeStr = fontFaceElement.getAttributeNS
            (null, SVG_SLOPE_ATTRIBUTE);
        if (slopeStr.length() == 0) {
            slopeStr = SVG_FONT_FACE_SLOPE_DEFAULT_VALUE;
        }
        float slope;
        try {
            slope = SVGUtilities.convertSVGNumber(slopeStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, slopeStr});
        }

        // panose-1
        String panose1 = fontFaceElement.getAttributeNS
            (null, SVG_PANOSE_1_ATTRIBUTE);
         if (panose1.length() == 0) {
            panose1 = SVG_FONT_FACE_PANOSE_1_DEFAULT_VALUE;
        }

        // ascent
        String ascentStr = fontFaceElement.getAttributeNS
            (null, SVG_ASCENT_ATTRIBUTE);
        if (ascentStr.length() == 0) {
            // set it to be unitsPerEm * .8
            ascentStr = String.valueOf(unitsPerEm*.8);
        }
        float ascent;
        try {
           ascent = SVGUtilities.convertSVGNumber(ascentStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, ascentStr});
        }

        // descent
        String descentStr = fontFaceElement.getAttributeNS
            (null, SVG_DESCENT_ATTRIBUTE);
        if (descentStr.length() == 0) {
            // set it to be unitsPerEm *.2.
            descentStr = String.valueOf(unitsPerEm*.2);
        }
        float descent;
        try {
            descent = SVGUtilities.convertSVGNumber(descentStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, descentStr});
        }

        // underline-position
        String underlinePosStr = fontFaceElement.getAttributeNS
            (null, SVG_UNDERLINE_POSITION_ATTRIBUTE);
        if (underlinePosStr.length() == 0) {
            underlinePosStr = String.valueOf(-3*unitsPerEm/40);
        }
        float underlinePos;
        try {
            underlinePos = SVGUtilities.convertSVGNumber(underlinePosStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE,
                               underlinePosStr});
        }


        // underline-thickness
        String underlineThicknessStr = fontFaceElement.getAttributeNS
            (null, SVG_UNDERLINE_THICKNESS_ATTRIBUTE);
        if (underlineThicknessStr.length() == 0) {
            underlineThicknessStr = String.valueOf(unitsPerEm/20);
        }
        float underlineThickness;
        try {
            underlineThickness =
                SVGUtilities.convertSVGNumber(underlineThicknessStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE,
                               underlineThicknessStr});
        }


        // strikethrough-position
        String strikethroughPosStr = fontFaceElement.getAttributeNS
            (null, SVG_STRIKETHROUGH_POSITION_ATTRIBUTE);
        if (strikethroughPosStr.length() == 0) {
            strikethroughPosStr = String.valueOf(3*ascent/8);
        }
        float strikethroughPos;
        try {
            strikethroughPos =
                SVGUtilities.convertSVGNumber(strikethroughPosStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE,
                               strikethroughPosStr});
        }


        // strikethrough-thickness
        String strikethroughThicknessStr = fontFaceElement.getAttributeNS
            (null, SVG_STRIKETHROUGH_THICKNESS_ATTRIBUTE);
        if (strikethroughThicknessStr.length() == 0) {
            strikethroughThicknessStr = String.valueOf(unitsPerEm/20);
        }
        float strikethroughThickness;
        try {
            strikethroughThickness =
                SVGUtilities.convertSVGNumber(strikethroughThicknessStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE,
                               strikethroughThicknessStr});
        }

        // overline-position
        String overlinePosStr = fontFaceElement.getAttributeNS
            (null, SVG_OVERLINE_POSITION_ATTRIBUTE);
         if (overlinePosStr.length() == 0) {
            overlinePosStr = String.valueOf(ascent);
        }
        float overlinePos;
        try {
            overlinePos = SVGUtilities.convertSVGNumber(overlinePosStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE,
                               overlinePosStr});
        }


        // overline-thickness
        String overlineThicknessStr = fontFaceElement.getAttributeNS
            (null, SVG_OVERLINE_THICKNESS_ATTRIBUTE);
        if (overlineThicknessStr.length() == 0) {
            overlineThicknessStr = String.valueOf(unitsPerEm/20);
        }
        float overlineThickness;
        try {
            overlineThickness =
                SVGUtilities.convertSVGNumber(overlineThicknessStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE,
                               overlineThicknessStr});
        }

        List srcs = null;
        Element fontElt = SVGUtilities.getParentElement(fontFaceElement);
        if (!fontElt.getNamespaceURI().equals(SVG_NAMESPACE_URI) ||
            !fontElt.getLocalName().equals(SVG_FONT_TAG)) {
            srcs = getFontFaceSrcs(fontFaceElement);
        }

        // TODO: get the rest of the attributes
        return new SVGFontFace(fontFaceElement, srcs,
                               familyNames, unitsPerEm, fontWeight, fontStyle,
                               fontVariant, fontStretch, slope, panose1,
                               ascent, descent, strikethroughPos,
                               strikethroughThickness, underlinePos,
                               underlineThickness, overlinePos,
                               overlineThickness);
    }


    public List getFontFaceSrcs(Element fontFaceElement) {
        // Search for a font-face-src element
        Element ffsrc = null;
        for (Node n = fontFaceElement.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            if ((n.getNodeType() == Node.ELEMENT_NODE) &&
                n.getNamespaceURI().equals(SVG_NAMESPACE_URI) &&
                n.getLocalName().equals(SVG_FONT_FACE_SRC_TAG)) {
                    ffsrc = (Element)n;
                    break;
            }
        }
        if (ffsrc == null)
            return null;

        List ret = new LinkedList();

        // Search for a font-face-uri, or font-face-name elements
        for (Node n = ffsrc.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            if ((n.getNodeType() != Node.ELEMENT_NODE) ||
                !n.getNamespaceURI().equals(SVG_NAMESPACE_URI))
                continue;

            if (n.getLocalName().equals(SVG_FONT_FACE_URI_TAG)) {
                Element ffuri = (Element)n;
                String uri = XLinkSupport.getXLinkHref(ffuri);
                String base = XMLBaseSupport.getCascadedXMLBase(ffuri);
                ParsedURL purl;
                if (base != null) purl = new ParsedURL(base, uri);
                else              purl = new ParsedURL(uri);
                ret.add(purl);
                continue;
            }
            if (n.getLocalName().equals(SVG_FONT_FACE_NAME_TAG)) {
                Element ffname = (Element)n;
                String s = ffname.getAttribute("name");
                if (s.length() != 0)
                    ret.add(s);
            }
        }
        return ret;
    }
}
