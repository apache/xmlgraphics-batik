/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.GlyphVector;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Vector;
import org.w3c.dom.Element;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.font.HKern;
import org.apache.batik.gvt.font.HKernTable;
import org.apache.batik.util.SVGConstants;

/**
 * Represents an SVG font.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public final class SVGGVTFont implements GVTFont, SVGConstants {

    private float fontSize;
    private SVGFontFace fontFace;
    private String[] glyphUnicodes;
    private String[] glyphNames;
    private Element[] glyphElements;
    private Element[] hkernElements;
    private BridgeContext ctx;
    private Element textElement;
    private Element missingGlyphElement;
    private HKernTable kerningTable;

    public SVGGVTFont(float fontSize, SVGFontFace fontFace, String[] glyphUnicodes,
                      String[] glyphNames,
                      BridgeContext ctx, Element[] glyphElements,
                      Element missingGlyphElement,
                      Element[] hkernElements,
                      Element textElement) {
        this.fontFace = fontFace;
        this.fontSize = fontSize;
        this.glyphUnicodes = glyphUnicodes;
        this.glyphNames = glyphNames;
        this.ctx = ctx;
        this.glyphElements = glyphElements;
        this.missingGlyphElement = missingGlyphElement;
        this.hkernElements = hkernElements;
        this.textElement = textElement;

        createKerningTable();
    }


    /**
     * Creates the kerning table for this font
     */

    private void createKerningTable() {

        HKern[] entries = new HKern[hkernElements.length];
        for (int i=0; i < hkernElements.length; i++) {
            Element hkernElement = hkernElements[i];
            SVGHKernElementBridge hkernBridge = (SVGHKernElementBridge)ctx.getBridge(hkernElement);
            HKern hkern = hkernBridge.createHKern(ctx, hkernElement, fontFace, this);
            entries[i] = hkern;
        }

        kerningTable = new HKernTable(entries);

    }

    /**
     * Returns the kerning value of this character pair.
     */
    public float getKerning(String unicode1, String unicode2) {
        if (unicode1 != null && unicode1.length() > 0 &&
            unicode2 != null && unicode2.length() > 0) {
            return kerningTable.getKerningValue(unicode1.charAt(0),
                                                unicode2.charAt(0));
        } else {
            return 0f;
        }
    }

    /**
     * Checks if this Font has a glyph for the glyph name.
     *
     * @param name The glyph-name to look for.
     */
    public boolean canDisplayGivenName(String name) {
        for (int i = 0; i < glyphNames.length; i++) {
            if (glyphNames[i] != null && glyphNames[i].equals(name)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns the unicode character that corresponds to this glyph.
     *
     * @param name The glyph-name to look for.
     */
    public char unicodeForName(String name) {
        for (int i = 0; i < glyphNames.length; i++) {
            if (glyphNames[i] != null && glyphNames[i].equals(name)) {
                return glyphUnicodes[i].charAt(0);
            }
        }
        return 0;
    }

    /**
     * Checks if this Font has a glyph for the specified character.
     */
    public boolean canDisplay(char c) {
        for (int i = 0; i < glyphUnicodes.length; i++) {
            if (glyphUnicodes[i].indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Indicates whether or not this Font can display the characters in the
     *  specified text starting at start and ending at limit.
     *  Returns the index of the first character it can't display. Returns -1 if
     *  it can display the whole string.
     */
    public int canDisplayUpTo(char[] text, int start, int limit) {
        StringCharacterIterator sci = new StringCharacterIterator(new String(text));
        return canDisplayUpTo(sci, start, limit);
    }

    /**
     *  Indicates whether or not this Font can display the the characters in
     *  the specified CharacterIterator starting at start and ending at limit.
     *  Returns the index of the first character it can't display. Returns -1 if
     *  it can display the whole string.
     */
    public int canDisplayUpTo(CharacterIterator iter, int start, int limit) {

        char c = iter.setIndex(start);
        int currentIndex = start;

        while (c != iter.DONE && currentIndex < limit) {

            boolean foundMatchingGlyph = false;

            for (int i = 0; i < glyphUnicodes.length; i++) {
                if (glyphUnicodes[i].indexOf(c) == 0) {  // found a possible match

                    if (glyphUnicodes[i].length() == 0)  { // not a ligature
                        foundMatchingGlyph = true;
                        break;

                    } else {
                        // glyphCodes[i] is a ligature so try and
                        // match the rest of the glyphCode chars
                        boolean matched = true;
                        String ligature = "" + c;
                        for (int j = 1; j < glyphUnicodes[i].length(); j++) {
                            c = iter.next();
                            if (glyphUnicodes[i].charAt(j) != c) {
                                matched = false;
                                break;
                            }
                            ligature += c;
                        }
                        if (matched) { // found a matching ligature!
                            foundMatchingGlyph = true;
                            break;

                        } else {
                            // did not match ligature, keep looking for another glyph
                            c = iter.setIndex(currentIndex);
                        }
                    }
                }
            }
            if (!foundMatchingGlyph) {
              return currentIndex;
            }
            c = iter.next();
            currentIndex = iter.getIndex();
        }
        return -1;
    }

    /**
     *  Indicates whether or not this Font can display a specified String.
     *  Returns the index of the first character it can't display. Returns -1 if
     *  it can display the whole string.
     */
    public int canDisplayUpTo(String str) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return canDisplayUpTo(sci, 0, str.length());
    }

    /**
     *  Returns a new GlyphVector object created with the specified array of
     *  characters and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            char[] chars) {
         StringCharacterIterator sci = new StringCharacterIterator(new String(chars));
         return createGlyphVector(frc, sci);
    }

    /**
     * Returns a new GlyphVector object created with the specified
     * CharacterIterator and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            CharacterIterator ci) {

        Vector glyphs = new Vector();
        char c = ci.first();
        while (c != ci.DONE) {
            boolean foundMatchingGlyph = false;
            for (int i = 0; i < glyphUnicodes.length; i++) {
                if (glyphUnicodes[i].indexOf(c) == 0) {  // found a possible match

                    if (glyphUnicodes[i].length() == 0)  { // not a ligature
                        Element glyphElement = glyphElements[i];
                        SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)ctx.getBridge(glyphElement);
                        Glyph glyph = glyphBridge.createGlyph(ctx, glyphElement, textElement, i, fontSize, fontFace);
                        glyphs.add(glyph);
                        foundMatchingGlyph = true;
                        break;

                    } else {
                        // glyphCodes[i] is a ligature so try and
                        // match the rest of the glyphCode chars
                        int current = ci.getIndex();
                        boolean matched = true;
                        for (int j = 1; j < glyphUnicodes[i].length(); j++) {
                            c = ci.next();
                            if (glyphUnicodes[i].charAt(j) != c) {
                                matched = false;
                                break;
                            }
                        }
                        if (matched) { // found a matching ligature!

                            Element glyphElement = glyphElements[i];
                            SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)ctx.getBridge(glyphElement);
                            Glyph glyph = glyphBridge.createGlyph(ctx, glyphElement, textElement, i, fontSize, fontFace);
                            glyphs.add(glyph);
                            foundMatchingGlyph = true;
                            break;

                        } else {
                            // did not match ligature, keep looking for another glyph
                            c = ci.setIndex(current);
                        }
                    }
                }
            }
            if (!foundMatchingGlyph) {
                // add the missing glyph
                SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)ctx.getBridge(missingGlyphElement);
                Glyph glyph = glyphBridge.createGlyph(ctx, missingGlyphElement, textElement, -1, fontSize, fontFace);
                glyphs.add(glyph);

            }
            c = ci.next();
        }

        // turn the vector of glyphs into an array;
        int numGlyphs = glyphs.size();
        Glyph[] glyphArray = new Glyph[numGlyphs];
        for (int i =0; i < numGlyphs; i++) {
            glyphArray[i] = (Glyph)glyphs.get(i);
        }
        // return a new SVGGVTGlyphVector
        return new SVGGVTGlyphVector(this, glyphArray, frc);
    }

    /**
     *  Returns a new GlyphVector object created with the specified integer
     *  array and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            int[] glyphCodes) {
        // costruct a string from the glyphCodes
        String str = "";
        for (int i = 0; i < glyphCodes.length; i++) {
            str += glyphUnicodes[glyphCodes[i]];
        }
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return createGlyphVector(frc, sci);
    }

    /**
     * Returns a new GlyphVector object created with the specified String and
     * the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc, String str) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return createGlyphVector(frc, sci);
    }

    /**
     * Creates a new Font object by replicating the current Font object and
     * applying a new size to it.
     */
    public GVTFont deriveFont(float size) {
        return new SVGGVTFont(size, fontFace, glyphUnicodes, glyphNames, ctx,
            glyphElements, missingGlyphElement, hkernElements, textElement);
    }

    /**
     *  Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(char[] chars, int beginIndex, int limit,
                                      FontRenderContext frc) {
        StringCharacterIterator sci = new StringCharacterIterator(new String(chars));
        return getLineMetrics(sci, beginIndex, limit, frc);
    }

    /**
     * Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(CharacterIterator ci, int beginIndex,
                                      int limit, FontRenderContext frc) {

        // first create the character iterator that represents the subset
        // from beginIndex to limit
        String s = "";
        char c = ci.setIndex(beginIndex);
        int currentIndex = beginIndex;

        while (c != ci.DONE && currentIndex < limit) {
            s += c;
            currentIndex++;
            c = ci.next();
        }

        StringCharacterIterator sci = new StringCharacterIterator(s);
        GVTGlyphVector gv = createGlyphVector(frc, sci);

        float fontHeight = fontFace.getUnitsPerEm();
        float scale = fontSize/fontHeight;

        float ascent = fontFace.getAscent() * scale;
        float descent = fontFace.getDescent() * scale;

        int numGlyphs = gv.getNumGlyphs();

        float[] baselineOffsets = new float[numGlyphs];
        for (int i = 0; i < numGlyphs; i++) {
            baselineOffsets[i] = (float)( gv.getGlyphMetrics(i).getBounds2D().getMaxY()
                                - gv.getGlyphPosition(i).getY());
        }

        float strikethroughOffset = fontFace.getStrikethroughPosition() * -scale;
        float strikethroughThickness = fontFace.getStrikethroughThickness() * scale;
        float underlineOffset = fontFace.getUnderlinePosition() * scale;
        float underlineThickness = fontFace.getUnderlineThickness() * scale;
        float overlineOffset = fontFace.getOverlinePosition() * -scale;
        float overlineThickness = fontFace.getOverlineThickness() * scale;


        return new GVTLineMetrics(ascent, Font.ROMAN_BASELINE, baselineOffsets, descent,
                                  fontHeight, fontHeight, numGlyphs, strikethroughOffset,
                                  strikethroughThickness, underlineOffset,
                                  underlineThickness, overlineOffset, overlineThickness);
    }

    /**
     *  Returns a GVTLineMetrics object created with the specified String and
     *  FontRenderContext.
     */
    public GVTLineMetrics getLineMetrics(String str, FontRenderContext frc) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return getLineMetrics(sci, 0, str.length(), frc);
    }

    /**
     * Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(String str, int beginIndex, int limit,
                                      FontRenderContext frc) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return getLineMetrics(sci, beginIndex, limit, frc);
    }

    /**
     * Returns the size of this font.
     */
    public float getSize() {
        return fontSize;
    }

    public String toString() {
        return fontFace.getFamilyName() + " " + fontFace.getFontWeight() + " "
              + fontFace.getFontStyle();
    }
}
