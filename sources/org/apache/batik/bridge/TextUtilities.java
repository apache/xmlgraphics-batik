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

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.util.CSSConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSPrimitiveValue;

/**
 * A collection of utility method for text.
 *
 * @author <a href="stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public abstract class TextUtilities implements CSSConstants, ErrorConstants {

    /**
     * Returns the content of the given element.
     */
    public static String getElementContent(Element e) {
        StringBuffer result = new StringBuffer();
        for (Node n = e.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            switch (n.getNodeType()) {
            case Node.ELEMENT_NODE:
                result.append(getElementContent((Element)n));
                break;
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
                result.append(n.getNodeValue());
            }
        }
        return result.toString();
    }

    /**
     * Returns the float list that represents a set of horizontal
     * values or percentage.
     *
     * @param element the element that defines the specified coordinates
     * @param attrName the name of the attribute (used by error handling)
     * @param valueStr the delimited string containing values of the coordinate
     * @param ctx the bridge context
     */
    public static
        ArrayList svgHorizontalCoordinateArrayToUserSpace(Element element,
                                                          String attrName,
                                                          String valueStr,
                                                          BridgeContext ctx) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        ArrayList values = new ArrayList();
        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        while (st.hasMoreTokens()) {
            values.add
                (new Float(UnitProcessor.svgHorizontalCoordinateToUserSpace
                           (st.nextToken(), attrName, uctx)));
        }
        return values;
    }

    /**
     * Returns the float list that represents a set of values or percentage.
     *
     *
     * @param element the element that defines the specified coordinates
     * @param attrName the name of the attribute (used by error handling)
     * @param valueStr the delimited string containing values of the coordinate
     * @param ctx the bridge context
     */
    public static
        ArrayList svgVerticalCoordinateArrayToUserSpace(Element element,
                                                        String attrName,
                                                        String valueStr,
                                                        BridgeContext ctx) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        ArrayList values = new ArrayList();
        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        while (st.hasMoreTokens()) {
            values.add
                (new Float(UnitProcessor.svgVerticalCoordinateToUserSpace
                           (st.nextToken(), attrName, uctx)));
        }
        return values;
    }


    public static ArrayList svgRotateArrayToFloats(Element element,
                                                   String attrName,
                                                   String valueStr,
                                                   BridgeContext ctx) {

        StringTokenizer st = new StringTokenizer(valueStr, ", ", false);
        ArrayList values = new ArrayList();
        String s;
        while (st.hasMoreTokens()) {
            try {
                s = st.nextToken();
                values.add
                    (new Float(Math.toRadians
                               (SVGUtilities.convertSVGNumber(s))));
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (element, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object [] {attrName, valueStr});
            }
        }
        return values;
    }

    /**
     * Converts the font-size CSS value to a float value.
     * @param e the element
     */
    public static Float convertFontSize(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.FONT_SIZE_INDEX);
        return new Float(v.getFloatValue());
    }

    /**
     * Converts the font-style CSS value to a float value.
     * @param e the element
     */
    public static Float convertFontStyle(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.FONT_STYLE_INDEX);
        switch (v.getStringValue().charAt(0)) {
        case 'n':
            return TextAttribute.POSTURE_REGULAR;
        default:
            return TextAttribute.POSTURE_OBLIQUE;
        }
    }

    /**
     * Converts the font-stretch CSS value to a float value.
     * @param e the element
     */
    public static Float convertFontStretch(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.FONT_STRETCH_INDEX);
        String s = v.getStringValue();
        switch (s.charAt(0)) {
        case 'u':
            if (s.charAt(6) == 'c') {
                return TextAttribute.WIDTH_CONDENSED;
            } else {
                return TextAttribute.WIDTH_EXTENDED;
            }

        case 'e':
            if (s.charAt(6) == 'c') {
                return TextAttribute.WIDTH_CONDENSED;
            } else {
                if (s.length() == 8) {
                    return TextAttribute.WIDTH_SEMI_EXTENDED;
                } else {
                    return TextAttribute.WIDTH_EXTENDED;
                }
            }

        case 's':
            if (s.charAt(6) == 'c') {
                return TextAttribute.WIDTH_SEMI_CONDENSED;
            } else {
                return TextAttribute.WIDTH_SEMI_EXTENDED;
            }

        default:
            return TextAttribute.WIDTH_REGULAR;
        }
    }

    /**
     * Converts the font-weight CSS value to a float value.
     * @param e the element
     */
    public static Float convertFontWeight(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.FONT_WEIGHT_INDEX);
        float f = v.getFloatValue();
        switch ((int)f) {
        case 100:
            return TextAttribute.WEIGHT_EXTRA_LIGHT;
        case 200:
            return TextAttribute.WEIGHT_LIGHT;
        case 300:
            return TextAttribute.WEIGHT_DEMILIGHT;
        case 400:
            return TextAttribute.WEIGHT_REGULAR;
        case 500:
            return TextAttribute.WEIGHT_SEMIBOLD;
        default:
            return TextAttribute.WEIGHT_BOLD;
            /* Would like to do this but the JDK 1.3 & 1.4
               seems to drop back to 'REGULAR' instead of 'BOLD'
               if there is not a match.
        case 700:
            return TextAttribute.WEIGHT_HEAVY;
        case 800:
            return TextAttribute.WEIGHT_EXTRABOLD;
        case 900:
            return TextAttribute.WEIGHT_ULTRABOLD;
            */
        }
    }

    /**
     * Converts the text-anchor CSS value to a TextNode.Anchor.
     * @param e the element
     */
    public static TextNode.Anchor convertTextAnchor(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.TEXT_ANCHOR_INDEX);
        switch (v.getStringValue().charAt(0)) {
        case 's':
            return TextNode.Anchor.START;
        case 'm':
            return TextNode.Anchor.MIDDLE;
        default:
            return TextNode.Anchor.END;
        }
    }

    /**
     * Converts a baseline-shift CSS value to a value usable as a text
     * attribute, or null.
     * @param e the element
     */
    public static Object convertBaselineShift(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.BASELINE_SHIFT_INDEX);
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            String s = v.getStringValue();
            switch (s.charAt(2)) {
            case 'p': //suPerscript
                return TextAttribute.SUPERSCRIPT_SUPER;

            case 'b': //suBscript
                return TextAttribute.SUPERSCRIPT_SUB;

            default:
                return null;
            }
        } else {
            return new Float(v.getFloatValue());
        }
    }

    /**
     * Converts a kerning CSS value to a value usable as a text
     * attribute, or null.
     * @param e the element
     */
    public static Float convertKerning(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.KERNING_INDEX);
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            return null;
        }
        return new Float(v.getFloatValue());
    }

    /**
     * Converts a letter-spacing CSS value to a value usable as a text
     * attribute, or null.
     * @param e the element
     */
    public static Float convertLetterSpacing(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.LETTER_SPACING_INDEX);
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            return null;
        }
        return new Float(v.getFloatValue());
    }

    /**
     * Converts a word-spacing CSS value to a value usable as a text
     * attribute, or null.
     * @param e the element
     */
    public static Float convertWordSpacing(Element e) {
        Value v = CSSUtilities.getComputedStyle
            (e, SVGCSSEngine.WORD_SPACING_INDEX);
        if (v.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
            return null;
        }
        return new Float(v.getFloatValue());
    }
}
