/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.*;
import java.awt.font.TextAttribute;
import org.w3c.dom.*;

import org.apache.batik.ext.awt.g2d.GraphicContext;

/**
 * Utility class that converts a Font object into a set of SVG
 * font attributes
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFont extends AbstractSVGConverter {
    public static final float EXTRA_LIGHT =
        TextAttribute.WEIGHT_EXTRA_LIGHT.floatValue();
    public static final float LIGHT =
        TextAttribute.WEIGHT_LIGHT.floatValue();
    public static final float DEMILIGHT =
        TextAttribute.WEIGHT_DEMILIGHT.floatValue();
    public static final float REGULAR =
        TextAttribute.WEIGHT_REGULAR.floatValue();
    public static final float SEMIBOLD =
        TextAttribute.WEIGHT_SEMIBOLD.floatValue();
    public static final float MEDIUM =
        TextAttribute.WEIGHT_MEDIUM.floatValue();
    public static final float DEMIBOLD =
        TextAttribute.WEIGHT_DEMIBOLD.floatValue();
    public static final float BOLD =
        TextAttribute.WEIGHT_BOLD.floatValue();
    public static final float HEAVY =
        TextAttribute.WEIGHT_HEAVY.floatValue();
    public static final float EXTRABOLD =
        TextAttribute.WEIGHT_EXTRABOLD.floatValue();
    public static final float ULTRABOLD =
        TextAttribute.WEIGHT_ULTRABOLD.floatValue();

    public static final float POSTURE_REGULAR =
        TextAttribute.POSTURE_REGULAR.floatValue();
    public static final float POSTURE_OBLIQUE =
        TextAttribute.POSTURE_OBLIQUE.floatValue();

    /**
     * Contains threshold value for the various Font styles. If a given
     * style is in an interval, then it is mapped to the style at the top
     * of that interval.
     * @see #styleToSVG
     */
    static final float fontStyles[] = {
        POSTURE_REGULAR + (POSTURE_OBLIQUE - POSTURE_REGULAR)/2
    };

    /**
     * SVG Styles corresponding to the fontStyles
     */
    static final String svgStyles[] = {
        /*POSTURE_REGULAR*/   SVG_FONT_STYLE_NORMAL_VALUE,
        /*POSTURE_OBLIQUE*/   SVG_FONT_STYLE_ITALIC_VALUE
    };

    /**
     * Contains threshold values for the various Font weights. If a given
     * weight is in an interval, then it is mapped to the weight at the top
     * of the interval.
     * @see #weightToSVG
     */
    static final float fontWeights[] = { EXTRA_LIGHT + (LIGHT - EXTRA_LIGHT)/2f,
                                         LIGHT + (DEMILIGHT - LIGHT)/2f,
                                         DEMILIGHT + (REGULAR - DEMILIGHT)/2f,
                                         REGULAR + (SEMIBOLD - REGULAR)/2f,
                                         SEMIBOLD + (MEDIUM - SEMIBOLD)/2f,
                                         MEDIUM + (DEMIBOLD - MEDIUM)/2f,
                                         DEMIBOLD + (BOLD - DEMIBOLD)/2f,
                                         BOLD + (HEAVY - BOLD)/2f,
                                         HEAVY + (EXTRABOLD - HEAVY)/2f,
                                         EXTRABOLD + (ULTRABOLD - EXTRABOLD),
    };

    /**
     * SVG Weights corresponding to the fontWeights
     */
    static final String svgWeights[] = {
        /*EXTRA_LIGHT*/ SVG_FONT_WEIGHT_100_VALUE,
        /*LIGHT*/       SVG_FONT_WEIGHT_200_VALUE,
        /*DEMILIGHT*/   SVG_FONT_WEIGHT_300_VALUE,
        /*REGULAR*/     SVG_FONT_WEIGHT_NORMAL_VALUE,
        /*SEMIBOLD*/    SVG_FONT_WEIGHT_500_VALUE,
        /*MEDIUM*/      SVG_FONT_WEIGHT_500_VALUE,
        /*DEMIBOLD*/    SVG_FONT_WEIGHT_600_VALUE,
        /*BOLD*/        SVG_FONT_WEIGHT_BOLD_VALUE,
        /*HEAVY*/       SVG_FONT_WEIGHT_800_VALUE,
        /*EXTRABOLD*/   SVG_FONT_WEIGHT_800_VALUE,
        /*ULTRABOLD*/   SVG_FONT_WEIGHT_900_VALUE
    };

    /**
     * Logical fonts mapping
     */
    static Hashtable logicalFontMap = new Hashtable();

    static {
        logicalFontMap.put("dialog", "sans-serif");
        logicalFontMap.put("dialoginput", "monospace");
        logicalFontMap.put("monospaced", "monospace");
        logicalFontMap.put("serif", "serif");
        logicalFontMap.put("sansserif", "sans-serif");
        logicalFontMap.put("symbol", "'WingDings'");
    }

    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc) {
        return toSVG(gc.getFont());
    }

    /**
     * @param font Font object which should be converted to a set
     *        of SVG attributes
     * @return description of attribute values that describe the font
     */
    public static SVGFontDescriptor toSVG(Font font) {
        String fontSize = "" + font.getSize();
        String fontWeight = weightToSVG(font);
        String fontStyle = styleToSVG(font);
        String fontFamilyStr = font.getFamily();
        StringBuffer fontFamily = new StringBuffer("'");
        fontFamily.append(fontFamilyStr);
        fontFamily.append("'");

        fontFamilyStr = fontFamily.toString();

        String logicalFontFamily =
            (String)logicalFontMap.get(font.getName().toLowerCase());
        if(logicalFontFamily != null)
            fontFamilyStr = logicalFontFamily;

        return new SVGFontDescriptor(fontSize, fontWeight,
                                     fontStyle, fontFamilyStr);
    }

    /**
     * @param font whose style should be converted to an SVG string
     *        value.
     */
    private static String styleToSVG(Font font) {
        Map attrMap = font.getAttributes();
        Float styleValue = (Float)attrMap.get(TextAttribute.POSTURE);

        if(styleValue == null){
            if(font.isItalic())
                styleValue = TextAttribute.POSTURE_OBLIQUE;
            else
                styleValue = TextAttribute.POSTURE_REGULAR;
        }

        float style = styleValue.floatValue();

        int i = 0;
        for(i=0; i<fontStyles.length; i++){
            if(style<=fontStyles[i])
                break;
        }

        return svgStyles[i];
    }

    /**
     * @param font whose weight should be converted to an SVG string
     *        value. Note that there is loss of precision for
     *        semibold and extrabold.
     */
    private static String weightToSVG(Font font) {
        Map attrMap = font.getAttributes();
        Float weightValue = (Float)attrMap.get(TextAttribute.WEIGHT);
        if(weightValue==null){
            if(font.isBold())
                weightValue = TextAttribute.WEIGHT_BOLD;
            else
                weightValue = TextAttribute.WEIGHT_REGULAR;
        }

        float weight = weightValue.floatValue();

        int i = 0;
        for(i=0; i<fontWeights.length; i++){
            if(weight<=fontWeights[i])
                break;
        }

        return svgWeights[i];
    }
}
