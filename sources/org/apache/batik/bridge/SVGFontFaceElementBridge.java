/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.w3c.dom.Element;

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
     * Returns 'fontFace'.
     */
    public String getLocalName() {
        return SVG_FONT_FACE_TAG;
    }

    /**
     * Creates an SVGFontFace that repesents the specified &lt;font-face> element.
     *
     * @param ctx The current bridge context.
     * @param fontFaceElement The &lt;font-face> element.
     *
     * @return A new SVGFontFace.
     */
    public SVGFontFace createFontFace(BridgeContext ctx,
                                      Element fontFaceElement) {

        // get all the font-face attributes

        String familyNames = fontFaceElement.getAttributeNS(null, SVG_FONT_FAMILY_ATTRIBUTE);

        // units per em
        String unitsPerEmStr = fontFaceElement.getAttributeNS(null, SVG_UNITS_PER_EM_ATTRIBUTE);
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
        String fontWeight = fontFaceElement.getAttributeNS(null, SVG_FONT_WEIGHT_ATTRIBUTE);
        if (fontWeight.length() == 0) {
            fontWeight = SVG_FONT_FACE_FONT_WEIGHT_DEFAULT_VALUE;
        }

        // font-style
        String fontStyle = fontFaceElement.getAttributeNS(null, SVG_FONT_STYLE_ATTRIBUTE);
        if (fontStyle.length() == 0) {
            fontStyle = SVG_FONT_FACE_FONT_STYLE_DEFAULT_VALUE;
        }

        // font-variant
        String fontVariant = fontFaceElement.getAttributeNS(null, SVG_FONT_VARIANT_ATTRIBUTE);
         if (fontVariant.length() == 0) {
            fontVariant = SVG_FONT_FACE_FONT_VARIANT_DEFAULT_VALUE;
        }

        // font-stretch
        String fontStretch = fontFaceElement.getAttributeNS(null, SVG_FONT_STRETCH_ATTRIBUTE);
         if (fontStretch.length() == 0) {
            fontStretch = SVG_FONT_FACE_FONT_STRETCH_DEFAULT_VALUE;
        }

        // slopeStr
        String slopeStr = fontFaceElement.getAttributeNS(null, SVG_SLOPE_ATTRIBUTE);
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
        String panose1 = fontFaceElement.getAttributeNS(null, SVG_PANOSE_1_ATTRIBUTE);
         if (panose1.length() == 0) {
            panose1 = SVG_FONT_FACE_PANOSE_1_DEFAULT_VALUE;
        }

        // ascent
        String ascentStr = fontFaceElement.getAttributeNS(null, SVG_ASCENT_ATTRIBUTE);
        if (ascentStr.length() == 0) {
            // set it to be unitsPerEm/2, not sure if this is correct or not
            ascentStr = String.valueOf(unitsPerEm/2);
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
        String descentStr = fontFaceElement.getAttributeNS(null, SVG_DESCENT_ATTRIBUTE);
        if (descentStr.length() == 0) {
            // set it to be unitsPerEm/2, not sure if this is correct or not
            descentStr = String.valueOf(unitsPerEm/2);
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
        String underlinePosStr = fontFaceElement.getAttributeNS(null, SVG_UNDERLINE_POSITION_ATTRIBUTE);
        if (underlinePosStr.length() == 0) {
            underlinePosStr = "0";
        }
        float underlinePos;
        try {
            underlinePos = SVGUtilities.convertSVGNumber(underlinePosStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, underlinePosStr});
        }


        // underline-thickness
        String underlineThicknessStr = fontFaceElement.getAttributeNS(null, SVG_UNDERLINE_THICKNESS_ATTRIBUTE);
        if (underlineThicknessStr.length() == 0) {
            underlineThicknessStr = String.valueOf(unitsPerEm/20);
        }
        float underlineThickness;
        try {
            underlineThickness = SVGUtilities.convertSVGNumber(underlineThicknessStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, underlineThicknessStr});
        }


        // strikethrough-position
        String strikethroughPosStr = fontFaceElement.getAttributeNS(null, SVG_STRIKETHROUGH_POSITION_ATTRIBUTE);
        if (strikethroughPosStr.length() == 0) {
            strikethroughPosStr = String.valueOf(ascent/3);
        }
        float strikethroughPos;
        try {
            strikethroughPos = SVGUtilities.convertSVGNumber(strikethroughPosStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, strikethroughPosStr});
        }


        // strikethrough-thickness
        String strikethroughThicknessStr = fontFaceElement.getAttributeNS(null, SVG_STRIKETHROUGH_THICKNESS_ATTRIBUTE);
        if (strikethroughThicknessStr.length() == 0) {
            strikethroughThicknessStr = String.valueOf(unitsPerEm/20);
        }
        float strikethroughThickness;
        try {
            strikethroughThickness = SVGUtilities.convertSVGNumber(strikethroughThicknessStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, strikethroughThicknessStr});
        }

        // overline-position
        String overlinePosStr = fontFaceElement.getAttributeNS(null, this.SVG_OVERLINE_POSITION_ATTRIBUTE);
         if (overlinePosStr.length() == 0) {
            overlinePosStr = String.valueOf(ascent);
        }
        float overlinePos;
        try {
            overlinePos = SVGUtilities.convertSVGNumber(overlinePosStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, overlinePosStr});
        }


        // overline-thickness
        String overlineThicknessStr = fontFaceElement.getAttributeNS(null, SVG_OVERLINE_THICKNESS_ATTRIBUTE);
        if (overlineThicknessStr.length() == 0) {
            overlineThicknessStr = String.valueOf(unitsPerEm/20);
        }
        float overlineThickness;
        try {
            overlineThickness = SVGUtilities.convertSVGNumber(overlineThicknessStr);
        } catch (NumberFormatException ex) {
            throw new BridgeException
                (fontFaceElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                new Object [] {SVG_FONT_FACE_SLOPE_DEFAULT_VALUE, overlineThicknessStr});
        }


        // TODO: get the rest of the attributes

        // should really pass in a HashMap containing the attribute values
        return new SVGFontFace(familyNames, unitsPerEm, fontWeight, fontStyle,
                               fontVariant, fontStretch, slope, panose1, ascent,
                               descent, strikethroughPos, strikethroughThickness,
                               underlinePos, underlineThickness,
                               overlinePos, overlineThickness);
    }
}
