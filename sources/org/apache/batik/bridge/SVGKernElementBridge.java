/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.gvt.font.Kern;
import org.apache.batik.gvt.font.UnicodeRange;
import org.w3c.dom.Element;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A base Bridge class for the kerning elements.
 *
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */
public abstract class SVGKernElementBridge extends AbstractSVGBridge {

    /**
     * Creates a Kern object that repesents the specified kerning element.
     *
     * @param ctx The bridge context.
     * @param kernElement The kerning element. Should be either a &lt;hkern>
     * or &lt;vkern> element.
     * @param font The font the kerning is related to.
     *
     * @return kern The new Kern object
     */
    public Kern createKern(BridgeContext ctx,
                           Element kernElement,
                           SVGGVTFont font) {

        // read all of the kern attributes
        String u1 = kernElement.getAttributeNS(null, SVG_U1_ATTRIBUTE);
        String u2 = kernElement.getAttributeNS(null, SVG_U2_ATTRIBUTE);
        String g1 = kernElement.getAttributeNS(null, SVG_G1_ATTRIBUTE);
        String g2 = kernElement.getAttributeNS(null, SVG_G2_ATTRIBUTE);
        String k = kernElement.getAttributeNS(null, SVG_K_ATTRIBUTE);
        if (k.length() == 0) {
            k = SVG_KERN_K_DEFAULT_VALUE;
        }

        // get the kern float value
        float kernValue = Float.parseFloat(k);

        // set up the first and second glyph sets and unicode ranges
        Vector firstGlyphSet = new Vector();
        Vector secondGlyphSet = new Vector();
        Vector firstUnicodeRanges = new Vector();
        Vector secondUnicodeRanges = new Vector();

        // process the u1 attribute
        StringTokenizer st = new StringTokenizer(u1, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.startsWith("U+")) { // its a unicode range
                firstUnicodeRanges.add(new UnicodeRange(token));
            } else {
                int[] glyphCodes = font.getGlyphCodesForUnicode(token);
                for (int i = 0; i < glyphCodes.length; i++) {
                    firstGlyphSet.add(new Integer(glyphCodes[i]));
                }
            }
        }

        // process the u2 attrbute
        st = new StringTokenizer(u2, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.startsWith("U+")) { // its a unicode range
                secondUnicodeRanges.add(new UnicodeRange(token));
            } else {
                int[] glyphCodes = font.getGlyphCodesForUnicode(token);
                for (int i = 0; i < glyphCodes.length; i++) {
                    secondGlyphSet.add(new Integer(glyphCodes[i]));
                }
            }
        }

        // process the g1 attribute
        st = new StringTokenizer(g1, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int[] glyphCodes = font.getGlyphCodesForName(token);
            for (int i = 0; i < glyphCodes.length; i++) {
                firstGlyphSet.add(new Integer(glyphCodes[i]));
            }
        }

        // process the g2 attribute
        st = new StringTokenizer(g2, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int[] glyphCodes = font.getGlyphCodesForName(token);
            for (int i = 0; i < glyphCodes.length; i++) {
                secondGlyphSet.add(new Integer(glyphCodes[i]));
            }
        }

        // construct the arrays
        int[] firstGlyphs = new int[firstGlyphSet.size()];
        int[] secondGlyphs = new int[secondGlyphSet.size()];
        UnicodeRange[] firstRanges = new UnicodeRange[firstUnicodeRanges.size()];
        UnicodeRange[] secondRanges = new UnicodeRange[secondUnicodeRanges.size()];
        for (int i = 0; i < firstGlyphSet.size(); i++) {
            firstGlyphs[i] = ((Integer)firstGlyphSet.elementAt(i)).intValue();
        }
        for (int i = 0; i < secondGlyphSet.size(); i++) {
            secondGlyphs[i] = ((Integer)secondGlyphSet.elementAt(i)).intValue();
        }
        for (int i = 0; i < firstUnicodeRanges.size(); i++) {
            firstRanges[i] = (UnicodeRange)firstUnicodeRanges.elementAt(i);
        }
        for (int i = 0; i < secondUnicodeRanges.size(); i++) {
            secondRanges[i] = (UnicodeRange)secondUnicodeRanges.elementAt(i);
        }

        // return the new Kern object
        return new Kern(firstGlyphs, secondGlyphs, firstRanges, secondRanges, kernValue);
    }
}
