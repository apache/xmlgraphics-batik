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
import org.apache.batik.gvt.font.Kern;
import org.apache.batik.gvt.font.KerningTable;
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
    private Element[] vkernElements;
    private BridgeContext ctx;
    private Element textElement;
    private Element missingGlyphElement;
    private KerningTable hKerningTable;
    private KerningTable vKerningTable;

    public SVGGVTFont(float fontSize, SVGFontFace fontFace, String[] glyphUnicodes,
                      String[] glyphNames,
                      BridgeContext ctx, Element[] glyphElements,
                      Element missingGlyphElement,
                      Element[] hkernElements,
                      Element[] vkernElements,
                      Element textElement) {
        this.fontFace = fontFace;
        this.fontSize = fontSize;
        this.glyphUnicodes = glyphUnicodes;
        this.glyphNames = glyphNames;
        this.ctx = ctx;
        this.glyphElements = glyphElements;
        this.missingGlyphElement = missingGlyphElement;
        this.hkernElements = hkernElements;
        this.vkernElements = vkernElements;
        this.textElement = textElement;

        createKerningTables();
    }


    /**
     * Creates the kerning table for this font
     */

    private void createKerningTables() {

        Kern[] hEntries = new Kern[hkernElements.length];
        for (int i = 0; i < hkernElements.length; i++) {
            Element hkernElement = hkernElements[i];
            SVGHKernElementBridge hkernBridge = (SVGHKernElementBridge)ctx.getBridge(hkernElement);
            Kern hkern = hkernBridge.createKern(ctx, hkernElement, this);
            hEntries[i] = hkern;
        }
        hKerningTable = new KerningTable(hEntries);

        Kern[] vEntries = new Kern[vkernElements.length];
        for (int i = 0; i < vkernElements.length; i++) {
            Element vkernElement = vkernElements[i];
            SVGVKernElementBridge vkernBridge = (SVGVKernElementBridge)ctx.getBridge(vkernElement);
            Kern vkern = vkernBridge.createKern(ctx, vkernElement, this);
            vEntries[i] = vkern;
        }
        vKerningTable = new KerningTable(vEntries);

    }

    /**
     * Returns the horizontal kerning value of this glyph pair.
     */
    public float getHKern(int glyphCode1, int glyphCode2) {
        if (glyphCode1 < 0 || glyphCode1 >= glyphUnicodes.length
            || glyphCode2 < 0 || glyphCode2 >= glyphUnicodes.length) {
            return 0f;
        }
        return hKerningTable.getKerningValue(glyphCode1, glyphCode2,
                glyphUnicodes[glyphCode1], glyphUnicodes[glyphCode2]);
    }

    /**
     * Returns the vertical kerning value of this glyph pair.
     */
    public float getVKern(int glyphCode1, int glyphCode2) {
        if (glyphCode1 < 0 || glyphCode1 >= glyphUnicodes.length
            || glyphCode2 < 0 || glyphCode2 >= glyphUnicodes.length) {
            return 0f;
        }
        return vKerningTable.getKerningValue(glyphCode1, glyphCode2,
                glyphUnicodes[glyphCode1], glyphUnicodes[glyphCode2]);
    }

    /**
     * Returns an array of glyph codes (unique ids) of the glyphs with the
     * specified name (there may be more than one).
     *
     * @param name The name of the glyph.
     * @return An array of matching glyph codes. This may be empty.
     */
    public int[] getGlyphCodesForName(String name) {
        Vector glyphCodes = new Vector();
        for (int i = 0; i < glyphNames.length; i++) {
            if (glyphNames[i] != null && glyphNames[i].equals(name)) {
                glyphCodes.add(new Integer(i));
            }
        }
        int[] glyphCodeArray = new int[glyphCodes.size()];
        for (int i = 0; i < glyphCodes.size(); i++) {
            glyphCodeArray[i] = ((Integer)glyphCodes.elementAt(i)).intValue();
        }
        return glyphCodeArray;
    }

    /**
     * Returns an array of glyph codes (unique ids) of the glyphs with the
     * specified unicode value (there may be more than one).
     *
     * @param unicode The unicode value of the glyph.
     * @return An array of matching glyph codes. This may be empty.
     */
    public int[] getGlyphCodesForUnicode(String unicode) {
        Vector glyphCodes = new Vector();
        for (int i = 0; i < glyphUnicodes.length; i++) {
            if (glyphUnicodes[i] != null && glyphUnicodes[i].equals(unicode)) {
                glyphCodes.add(new Integer(i));
            }
        }
        int[] glyphCodeArray = new int[glyphCodes.size()];
        for (int i = 0; i < glyphCodes.size(); i++) {
            glyphCodeArray[i] = ((Integer)glyphCodes.elementAt(i)).intValue();
        }
        return glyphCodeArray;
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

                    if (glyphUnicodes[i].length() == 1)  { // not a ligature
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

                    if (glyphUnicodes[i].length() == 1)  { // not a ligature
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
                              glyphElements, missingGlyphElement, hkernElements,
                              vkernElements, textElement);
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


