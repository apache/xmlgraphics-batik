/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.*;
import java.awt.font.TextAttribute;
import org.w3c.dom.*;

/**
 * Utility class that converts a Font object into a set of SVG
 * font attributes
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFont extends AbstractSVGConverter{
    public static final float EXTRA_LIGHT = TextAttribute.WEIGHT_EXTRA_LIGHT.floatValue();
    public static final float LIGHT = TextAttribute.WEIGHT_LIGHT.floatValue();
    public static final float DEMILIGHT = TextAttribute.WEIGHT_DEMILIGHT.floatValue();
    public static final float REGULAR = TextAttribute.WEIGHT_REGULAR.floatValue();
    public static final float SEMIBOLD = TextAttribute.WEIGHT_SEMIBOLD.floatValue();
    public static final float MEDIUM = TextAttribute.WEIGHT_MEDIUM.floatValue();
    public static final float DEMIBOLD = TextAttribute.WEIGHT_DEMIBOLD.floatValue();
    public static final float BOLD = TextAttribute.WEIGHT_BOLD.floatValue();
    public static final float HEAVY = TextAttribute.WEIGHT_HEAVY.floatValue();
    public static final float EXTRABOLD = TextAttribute.WEIGHT_EXTRABOLD.floatValue();
    public static final float ULTRABOLD = TextAttribute.WEIGHT_ULTRABOLD.floatValue();

    public static final float POSTURE_REGULAR = TextAttribute.POSTURE_REGULAR.floatValue();
    public static final float POSTURE_OBLIQUE = TextAttribute.POSTURE_OBLIQUE.floatValue();

    /**
     * Contains threshold value for the various Font styles. If a given
     * style is in an interval, then it is mapped to the style at the top
     * of that interval.
     * @see #styleToSVG
     */
    static final float fontStyles[] = { POSTURE_REGULAR + (POSTURE_OBLIQUE - POSTURE_REGULAR)/2 };

    /**
     * SVG Styles corresponding to the fontStyles
     */
    static final String svgStyles[] = {
        /*POSTURE_REGULAR*/   VALUE_FONT_STYLE_NORMAL,
                              /*POSTURE_OBLIQUE*/   VALUE_FONT_STYLE_ITALIC
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
        /*EXTRA_LIGHT*/ VALUE_FONT_WEIGHT_100,
                        /*LIGHT*/       VALUE_FONT_WEIGHT_200,
                        /*DEMILIGHT*/   VALUE_FONT_WEIGHT_300,
                        /*REGULAR*/     VALUE_FONT_WEIGHT_NORMAL,
                        /*SEMIBOLD*/    VALUE_FONT_WEIGHT_500,
                        /*MEDIUM*/      VALUE_FONT_WEIGHT_500,
                        /*DEMIBOLD*/    VALUE_FONT_WEIGHT_600,
                        /*BOLD*/        VALUE_FONT_WEIGHT_BOLD,
                        /*HEAVY*/       VALUE_FONT_WEIGHT_800,
                        /*EXTRABOLD*/   VALUE_FONT_WEIGHT_800,
                        /*ULTRABOLD*/   VALUE_FONT_WEIGHT_900
    };

    /**
     * Logical fonts mapping
     */
    static Hashtable logicalFontMap = new Hashtable();

    static{
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
     * @see org.apache.batik.util.awt.svg.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc){
        return toSVG(gc.getFont());
    }

    /**
     * @param font Font object which should be converted to a set
     *        of SVG attributes
     * @return description of attribute values that describe the font
     */
    public static SVGFontDescriptor toSVG(Font font){
        String fontSize = "" + font.getSize() + "pt";
        String fontWeight = weightToSVG(font);
        String fontStyle = styleToSVG(font);
        String fontFamilyStr = font.getFamily();
        StringBuffer fontFamily = new StringBuffer("'");
        fontFamily.append(fontFamilyStr);
        fontFamily.append("'");

        fontFamilyStr = fontFamily.toString();

        String logicalFontFamily = (String)logicalFontMap.get(font.getName().toLowerCase());
        if(logicalFontFamily != null)
            fontFamilyStr = logicalFontFamily;

        return new SVGFontDescriptor(fontSize, fontWeight,
                                     fontStyle, fontFamilyStr);
    }

    /**
     * @param font whose style should be converted to an SVG string
     *        value.
     */
    private static String styleToSVG(Font font){
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
    private static String weightToSVG(Font font){
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

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception{
        Font fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        // traceFonts(fonts);

        Object customFontAttributes[][] = {
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRA_LIGHT},
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMILIGHT },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD },
            { TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR },
            { TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE }
        };

        Map defaultAttrMap = new Hashtable();
        defaultAttrMap.put(TextAttribute.SIZE, new Float(45));
        defaultAttrMap.put(TextAttribute.FAMILY, "Serif");
        defaultAttrMap.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        defaultAttrMap.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);

        Font customFonts[] = new Font[customFontAttributes.length];
        for(int i=0; i<customFonts.length; i++){
            Map fontAttrMap = new Hashtable(defaultAttrMap);
            fontAttrMap.put(customFontAttributes[i][0],
                            customFontAttributes[i][1]);

            customFonts[i] = new Font(fontAttrMap);
        }

        // traceFonts(customFonts);

        Font logicalFonts[] = { new Font("dialog", Font.PLAIN, 12),
                                new Font("dialoginput", Font.PLAIN, 12),
                                new Font("monospaced", Font.PLAIN, 12),
                                new Font("serif", Font.PLAIN, 12),
                                new Font("sansserif", Font.PLAIN, 12),
                                new Font("symbol", Font.PLAIN, 12) };

        traceFonts(logicalFonts);

    }

    /**
     * For Unit testing only
     */
    public static void traceFonts(Font fonts[]) throws Exception{
        Document domFactory = TestUtil.getDocumentPrototype();
        Element group = domFactory.createElement(SVG_G_TAG);
        SVGFont converter = new SVGFont();

        for(int i=0; i<fonts.length; i++){
            Font font = fonts[i];
            Map attrMap = converter.toSVG(font).getAttributeMap(null);
            Element textElement = domFactory.createElement(SVG_TEXT_TAG);
            Iterator iter = attrMap.keySet().iterator();
            while(iter.hasNext()){
                String attrName = (String)iter.next();
                String attrValue = (String)attrMap.get(attrName);
                textElement.setAttribute(attrName, attrValue);
            }
            textElement.setAttribute(SVG_FONT_SIZE_ATTRIBUTE, "30");
            textElement.setAttribute(SVG_X_ATTRIBUTE, "30");
            textElement.setAttribute(SVG_Y_ATTRIBUTE, "" + (40*(i+1)));
            textElement.appendChild(domFactory.createTextNode(font.getFamily()));
            group.appendChild(textElement);
        }

        SVGCSSStyler.style(group);
        TestUtil.trace(group, System.out);
    }
}
