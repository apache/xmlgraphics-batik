/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.awt.font.TextLayout;
import java.awt.font.TextAttribute;
import java.awt.font.FontRenderContext;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * An attributed character iterator that does the reordering of the characters
 * for bidirectional text. It reorders the characters so they are in visual order.
 * It also assigns a BIDI_LEVEL attribute to each character which can be used
 * to split the reordered ACI into text runs based on direction. ie. characters
 * in a text run will all have the same bidi level.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class BidiAttributedCharacterIterator implements AttributedCharacterIterator {

    private AttributedCharacterIterator aci;
    private AttributedCharacterIterator reorderedACI;
    private FontRenderContext frc;

    private final static Map mirroredGlyphs = new HashMap(50);


    /**
     * Constructs a character iterator that represents the visual display order
     * of characters within bidirectional text.
     *
     * @param aci The character iterator containing the characters in logical
     * order.
     * @param frc The current font render context
     */
    public BidiAttributedCharacterIterator(AttributedCharacterIterator aci, 
                                           FontRenderContext           frc) {

        this.frc = frc;
        aci.first();

        TextLayout tl = new TextLayout(aci, frc);
        int aciBeginIndex = aci.getBeginIndex();
        int numChars = tl.getCharacterCount();
        AttributedString as = new AttributedString(aci);
        for (int i = 0; i < numChars; i++) {
            as.addAttribute
                (GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL,
                 new Integer(tl.getCharacterLevel(i)), i, i+1);
        }

        this.aci = as.getIterator();

        //  work out the new character order
        int[] charIndices = new int[numChars];
        int[] charLevels = new int[numChars];
        for (int i = 0; i < numChars; i++) {
            charIndices[i] = i;
            charLevels[i] = tl.getCharacterLevel(i);
        }
        int[] newCharOrder = doBidiReorder(charIndices, charLevels, numChars);

        // construct the string in the new order
        String reorderedString = "";
        char c;
        for (int i = 0; i < numChars; i++) {
            c = this.aci.setIndex(newCharOrder[i]);

            // check for mirrored char
            int bidiLevel = tl.getCharacterLevel(newCharOrder[i]);
            if (Math.floor(bidiLevel/2.0) != Math.floor((bidiLevel+1)/2.0)) {
                // bidi level is odd so writing dir is right to left
                Integer mirrorChar = (Integer)mirroredGlyphs.get(new Integer((int)c));
                if (mirrorChar != null) {
                    // replace with the mirror char
                    c = (char)mirrorChar.intValue();
                }
            }

            reorderedString += c;
        }

        // construct the reordered ACI
        AttributedString reorderedAS = new AttributedString(reorderedString);
        for (int i = 0; i < numChars; i++) {
            this.aci.setIndex(newCharOrder[i]);
            Map attributes = this.aci.getAttributes();
            reorderedAS.addAttributes(attributes, i, i+1);
        }

        // transfer any position atttributes to the new first char
        this.aci.first();
        Float x = (Float) this.aci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.X);
        Float y = (Float) this.aci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.Y);

        if (x != null && !x.isNaN()) {
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X,
                new Float(Float.NaN), newCharOrder[0], newCharOrder[0]+1);
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.X, x, 0, 1);
        }
        if (y != null && !y.isNaN()) {
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y,
                new Float(Float.NaN), newCharOrder[0], newCharOrder[0]+1);
            reorderedAS.addAttribute(GVTAttributedCharacterIterator.TextAttribute.Y, y, 0, 1);
        }

        // assign arabic form attributes to any arabic chars in the string
        reorderedAS = ArabicTextHandler.assignArabicForms(reorderedAS);

        reorderedACI = reorderedAS.getIterator();
    }

    /**
     * Calculates the display order of the characters based on the specified
     * character levels. This method is recursive.
     *
     * @param charIndices An array contianing the original indices of each char.
     * @param charLevels An array containing the current levels of each char.
     * @param numChars The number of chars to reorder.
     *
     * @return An array contianing the reordered character indices.
     */
    private int[] doBidiReorder(int[] charIndices, int[] charLevels, int numChars) {

        // check if all levels are 0, if so then just return the current charIndicies
        boolean allZero = true;
        int highestLevel = 0;
        for (int i = 0; i < numChars; i++) {
            if (charLevels[i] != 0) {
                allZero = false;
            }
            if (charLevels[i] > highestLevel) {
                highestLevel = charLevels[i];
            }
        }

        if (allZero) {
            return charIndices;
        }

        // find all groups of chars at the highest level and reverse their order
        int currentIndex = 0;
        while (currentIndex < numChars) {

            // find the first char at the highest index
            while (currentIndex < numChars && charLevels[currentIndex] < highestLevel) {
                currentIndex++;
                if (currentIndex == numChars) break;
            }
            if (currentIndex == numChars) {
                // have reached the end of the string
                break;
            }
            int startIndex = currentIndex;

            // now find the index where the run at the highestLevel end
            while (currentIndex < numChars && charLevels[currentIndex] == highestLevel) {
                currentIndex++;
                if (currentIndex == numChars) break;
            }
            int endIndex = currentIndex - 1;

            // now reverse the chars between startIndex and endIndex
            int[] temp = new int[endIndex-startIndex+1];
            for (int i = endIndex; i >= startIndex; i--) {
                temp[endIndex-i] = charIndices[i];
                charLevels[i]--;
            }
            for (int i = 0; i < temp.length; i++) {
                charIndices[startIndex+i] = temp[i];
            }
        }
        return doBidiReorder(charIndices, charLevels, numChars);
    }



    /**
     * Get the keys of all attributes defined on the iterator's text range.
     */
    public Set getAllAttributeKeys() {
        return reorderedACI.getAllAttributeKeys();
    }

    /**
     * Get the value of the named attribute for the current
     *     character.
     */
    public Object getAttribute(AttributedCharacterIterator.Attribute attribute) {
        return reorderedACI.getAttribute(attribute);
    }

    /**
     * Returns a map with the attributes defined on the current
     * character.
     */
    public Map getAttributes() {
        return reorderedACI.getAttributes();
    }

    /**
     * Get the index of the first character following the
     *     run with respect to all attributes containing the current
     *     character.
     */
    public int getRunLimit() {
        return reorderedACI.getRunLimit();
    }

    /**
     * Get the index of the first character following the
     *      run with respect to the given attribute containing the current
     *      character.
     */
    public int getRunLimit(AttributedCharacterIterator.Attribute attribute) {
        return reorderedACI.getRunLimit(attribute);
    }

    /**
     * Get the index of the first character following the
     *     run with respect to the given attributes containing the current
     *     character.
     */
    public int getRunLimit(Set attributes) {
        return reorderedACI.getRunLimit(attributes);
    }

    /**
     * Get the index of the first character of the run with
     *    respect to all attributes containing the current character.
     */
    public int getRunStart() {
        return reorderedACI.getRunStart();
    }

    /**
     * Get the index of the first character of the run with
     *      respect to the given attribute containing the current character.
     * @param attribute The attribute for whose appearance the first offset
     *      is requested.
     */
    public int getRunStart(AttributedCharacterIterator.Attribute attribute) {
        return reorderedACI.getRunStart(attribute);
    }

    /**
     * Get the index of the first character of the run with
     *      respect to the given attributes containing the current character.
     * @param attributes the Set of attributes which begins at the returned index.
     */
    public int getRunStart(Set attributes) {
        return reorderedACI.getRunStart(attributes);
    }

    /**
     * Creates a copy of this iterator.
     */
    public Object clone() {
        return new BidiAttributedCharacterIterator((AttributedCharacterIterator)aci.clone(), frc);
    }

    /**
     * Gets the character at the current position (as returned by getIndex()).
     */
    public char current() {
        return reorderedACI.current();
    }

    /**
     * Sets the position to getBeginIndex() and returns the character at
     * that position.
     */
    public char first() {
        return reorderedACI.first();
    }

    /**
     * Returns the start index of the text.
     */
    public int getBeginIndex() {
        return reorderedACI.getBeginIndex();
    }

    /**
     * Returns the end index of the text.
     */
    public int getEndIndex() {
        return reorderedACI.getEndIndex();
    }

    /**
     * Returns the current index.
     */
    public int getIndex() {
        return reorderedACI.getIndex();
    }

    /**
     * Sets the position to getEndIndex()-1 (getEndIndex() if the text is empty)
     * and returns the character at that position.
     */
    public char last() {
        return reorderedACI.last();
    }

    /**
     * Increments the iterator's index by one and returns the character at
     * the new index.
     */
    public char next() {
        return reorderedACI.next();
    }

    /**
     * Decrements the iterator's index by one and returns the character at the new index.
     */
    public char previous() {
        return reorderedACI.previous();
    }

    /**
     * Sets the position to the specified position in the text and returns that character.
     */
    public char setIndex(int position) {
       return reorderedACI.setIndex(position);
    }

    static {
        // set up the mirrored glyph hash map
        mirroredGlyphs.put(new Integer(0x0028), new Integer(0x0029)); //LEFT PARENTHESIS
        mirroredGlyphs.put(new Integer(0x0029), new Integer(0x0028)); //RIGHT PARENTHESIS
        mirroredGlyphs.put(new Integer(0x003C), new Integer(0x003E)); //LESS-THAN SIGN
        mirroredGlyphs.put(new Integer(0x003E), new Integer(0x003C)); //GREATER-THAN SIGN
        mirroredGlyphs.put(new Integer(0x005B), new Integer(0x005D)); //LEFT SQUARE BRACKET
        mirroredGlyphs.put(new Integer(0x005D), new Integer(0x005B)); //RIGHT SQUARE BRACKET
        mirroredGlyphs.put(new Integer(0x007B), new Integer(0x007D)); //LEFT CURLY BRACKET
        mirroredGlyphs.put(new Integer(0x007D), new Integer(0x007B)); //RIGHT CURLY BRACKET
        mirroredGlyphs.put(new Integer(0x00AB), new Integer(0x00BB)); //LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
        mirroredGlyphs.put(new Integer(0x00BB), new Integer(0x00AB)); //RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
        mirroredGlyphs.put(new Integer(0x2039), new Integer(0x203A)); //SINGLE LEFT-POINTING ANGLE QUOTATION MARK
        mirroredGlyphs.put(new Integer(0x203A), new Integer(0x2039)); //SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
        mirroredGlyphs.put(new Integer(0x2045), new Integer(0x2046)); //LEFT SQUARE BRACKET WITH QUILL
        mirroredGlyphs.put(new Integer(0x2046), new Integer(0x2045)); //RIGHT SQUARE BRACKET WITH QUILL
        mirroredGlyphs.put(new Integer(0x207D), new Integer(0x207E)); //SUPERSCRIPT LEFT PARENTHESIS
        mirroredGlyphs.put(new Integer(0x207E), new Integer(0x207D)); //SUPERSCRIPT RIGHT PARENTHESIS
        mirroredGlyphs.put(new Integer(0x208D), new Integer(0x208E)); //SUBSCRIPT LEFT PARENTHESIS
        mirroredGlyphs.put(new Integer(0x208E), new Integer(0x208D)); //SUBSCRIPT RIGHT PARENTHESIS
        mirroredGlyphs.put(new Integer(0x2208), new Integer(0x220B)); //ELEMENT OF
        mirroredGlyphs.put(new Integer(0x2209), new Integer(0x220C)); //NOT AN ELEMENT OF
        mirroredGlyphs.put(new Integer(0x220A), new Integer(0x220D)); //SMALL ELEMENT OF
        mirroredGlyphs.put(new Integer(0x220B), new Integer(0x2208)); //CONTAINS AS MEMBER
        mirroredGlyphs.put(new Integer(0x220C), new Integer(0x2209)); //DOES NOT CONTAIN AS MEMBER
        mirroredGlyphs.put(new Integer(0x220D), new Integer(0x220A)); //SMALL CONTAINS AS MEMBER
        mirroredGlyphs.put(new Integer(0x223C), new Integer(0x223D)); //TILDE OPERATOR
        mirroredGlyphs.put(new Integer(0x223D), new Integer(0x223C)); //REVERSED TILDE
        mirroredGlyphs.put(new Integer(0x2243), new Integer(0x22CD)); //ASYMPTOTICALLY EQUAL TO
        mirroredGlyphs.put(new Integer(0x2252), new Integer(0x2253)); //APPROXIMATELY EQUAL TO OR THE IMAGE OF
        mirroredGlyphs.put(new Integer(0x2253), new Integer(0x2252)); //IMAGE OF OR APPROXIMATELY EQUAL TO
        mirroredGlyphs.put(new Integer(0x2254), new Integer(0x2255)); //COLON EQUALS
        mirroredGlyphs.put(new Integer(0x2255), new Integer(0x2254)); //EQUALS COLON
        mirroredGlyphs.put(new Integer(0x2264), new Integer(0x2265)); //LESS-THAN OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x2265), new Integer(0x2264)); //GREATER-THAN OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x2266), new Integer(0x2267)); //LESS-THAN OVER EQUAL TO
        mirroredGlyphs.put(new Integer(0x2267), new Integer(0x2266)); //GREATER-THAN OVER EQUAL TO
        mirroredGlyphs.put(new Integer(0x2268), new Integer(0x2269)); //[BEST FIT] LESS-THAN BUT NOT EQUAL TO
        mirroredGlyphs.put(new Integer(0x2269), new Integer(0x2268)); //[BEST FIT] GREATER-THAN BUT NOT EQUAL TO
        mirroredGlyphs.put(new Integer(0x226A), new Integer(0x226B)); //MUCH LESS-THAN
        mirroredGlyphs.put(new Integer(0x226B), new Integer(0x226A)); //MUCH GREATER-THAN
        mirroredGlyphs.put(new Integer(0x226E), new Integer(0x226F)); //[BEST FIT] NOT LESS-THAN
        mirroredGlyphs.put(new Integer(0x226F), new Integer(0x226E)); //[BEST FIT] NOT GREATER-THAN
        mirroredGlyphs.put(new Integer(0x2270), new Integer(0x2271)); //[BEST FIT] NEITHER LESS-THAN NOR EQUAL TO
        mirroredGlyphs.put(new Integer(0x2271), new Integer(0x2270)); //[BEST FIT] NEITHER GREATER-THAN NOR EQUAL TO
        mirroredGlyphs.put(new Integer(0x2272), new Integer(0x2273)); //[BEST FIT] LESS-THAN OR EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x2273), new Integer(0x2272)); //[BEST FIT] GREATER-THAN OR EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x2274), new Integer(0x2275)); //[BEST FIT] NEITHER LESS-THAN NOR EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x2275), new Integer(0x2274)); //[BEST FIT] NEITHER GREATER-THAN NOR EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x2276), new Integer(0x2277)); //LESS-THAN OR GREATER-THAN
        mirroredGlyphs.put(new Integer(0x2277), new Integer(0x2276)); //GREATER-THAN OR LESS-THAN
        mirroredGlyphs.put(new Integer(0x2278), new Integer(0x2279)); //NEITHER LESS-THAN NOR GREATER-THAN
        mirroredGlyphs.put(new Integer(0x2279), new Integer(0x2278)); //NEITHER GREATER-THAN NOR LESS-THAN
        mirroredGlyphs.put(new Integer(0x227A), new Integer(0x227B)); //PRECEDES
        mirroredGlyphs.put(new Integer(0x227B), new Integer(0x227A)); //SUCCEEDS
        mirroredGlyphs.put(new Integer(0x227C), new Integer(0x227D)); //PRECEDES OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x227D), new Integer(0x227C)); //SUCCEEDS OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x227E), new Integer(0x227F)); //[BEST FIT] PRECEDES OR EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x227F), new Integer(0x227E)); //[BEST FIT] SUCCEEDS OR EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x2280), new Integer(0x2281)); //[BEST FIT] DOES NOT PRECEDE
        mirroredGlyphs.put(new Integer(0x2281), new Integer(0x2280)); //[BEST FIT] DOES NOT SUCCEED
        mirroredGlyphs.put(new Integer(0x2282), new Integer(0x2283)); //SUBSET OF
        mirroredGlyphs.put(new Integer(0x2283), new Integer(0x2282)); //SUPERSET OF
        mirroredGlyphs.put(new Integer(0x2284), new Integer(0x2285)); //[BEST FIT] NOT A SUBSET OF
        mirroredGlyphs.put(new Integer(0x2285), new Integer(0x2284)); //[BEST FIT] NOT A SUPERSET OF
        mirroredGlyphs.put(new Integer(0x2286), new Integer(0x2287)); //SUBSET OF OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x2287), new Integer(0x2286)); //SUPERSET OF OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x2288), new Integer(0x2289)); //[BEST FIT] NEITHER A SUBSET OF NOR EQUAL TO
        mirroredGlyphs.put(new Integer(0x2289), new Integer(0x2288)); //[BEST FIT] NEITHER A SUPERSET OF NOR EQUAL TO
        mirroredGlyphs.put(new Integer(0x228A), new Integer(0x228B)); //[BEST FIT] SUBSET OF WITH NOT EQUAL TO
        mirroredGlyphs.put(new Integer(0x228B), new Integer(0x228A)); //[BEST FIT] SUPERSET OF WITH NOT EQUAL TO
        mirroredGlyphs.put(new Integer(0x228F), new Integer(0x2290)); //SQUARE IMAGE OF
        mirroredGlyphs.put(new Integer(0x2290), new Integer(0x228F)); //SQUARE ORIGINAL OF
        mirroredGlyphs.put(new Integer(0x2291), new Integer(0x2292)); //SQUARE IMAGE OF OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x2292), new Integer(0x2291)); //SQUARE ORIGINAL OF OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x22A2), new Integer(0x22A3)); //RIGHT TACK
        mirroredGlyphs.put(new Integer(0x22A3), new Integer(0x22A2)); //LEFT TACK
        mirroredGlyphs.put(new Integer(0x22B0), new Integer(0x22B1)); //PRECEDES UNDER RELATION
        mirroredGlyphs.put(new Integer(0x22B1), new Integer(0x22B0)); //SUCCEEDS UNDER RELATION
        mirroredGlyphs.put(new Integer(0x22B2), new Integer(0x22B3)); //NORMAL SUBGROUP OF
        mirroredGlyphs.put(new Integer(0x22B3), new Integer(0x22B2)); //CONTAINS AS NORMAL SUBGROUP
        mirroredGlyphs.put(new Integer(0x22B4), new Integer(0x22B5)); //NORMAL SUBGROUP OF OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x22B5), new Integer(0x22B4)); //CONTAINS AS NORMAL SUBGROUP OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x22B6), new Integer(0x22B7)); //ORIGINAL OF
        mirroredGlyphs.put(new Integer(0x22B7), new Integer(0x22B6)); //IMAGE OF
        mirroredGlyphs.put(new Integer(0x22C9), new Integer(0x22CA)); //LEFT NORMAL FACTOR SEMIDIRECT PRODUCT
        mirroredGlyphs.put(new Integer(0x22CA), new Integer(0x22C9)); //RIGHT NORMAL FACTOR SEMIDIRECT PRODUCT
        mirroredGlyphs.put(new Integer(0x22CB), new Integer(0x22CC)); //LEFT SEMIDIRECT PRODUCT
        mirroredGlyphs.put(new Integer(0x22CC), new Integer(0x22CB)); //RIGHT SEMIDIRECT PRODUCT
        mirroredGlyphs.put(new Integer(0x22CD), new Integer(0x2243)); //REVERSED TILDE EQUALS
        mirroredGlyphs.put(new Integer(0x22D0), new Integer(0x22D1)); //DOUBLE SUBSET
        mirroredGlyphs.put(new Integer(0x22D1), new Integer(0x22D0)); //DOUBLE SUPERSET
        mirroredGlyphs.put(new Integer(0x22D6), new Integer(0x22D7)); //LESS-THAN WITH DOT
        mirroredGlyphs.put(new Integer(0x22D7), new Integer(0x22D6)); //GREATER-THAN WITH DOT
        mirroredGlyphs.put(new Integer(0x22D8), new Integer(0x22D9)); //VERY MUCH LESS-THAN
        mirroredGlyphs.put(new Integer(0x22D9), new Integer(0x22D8)); //VERY MUCH GREATER-THAN
        mirroredGlyphs.put(new Integer(0x22DA), new Integer(0x22DB)); //LESS-THAN EQUAL TO OR GREATER-THAN
        mirroredGlyphs.put(new Integer(0x22DB), new Integer(0x22DA)); //GREATER-THAN EQUAL TO OR LESS-THAN
        mirroredGlyphs.put(new Integer(0x22DC), new Integer(0x22DD)); //EQUAL TO OR LESS-THAN
        mirroredGlyphs.put(new Integer(0x22DD), new Integer(0x22DC)); //EQUAL TO OR GREATER-THAN
        mirroredGlyphs.put(new Integer(0x22DE), new Integer(0x22DF)); //EQUAL TO OR PRECEDES
        mirroredGlyphs.put(new Integer(0x22DF), new Integer(0x22DE)); //EQUAL TO OR SUCCEEDS
        mirroredGlyphs.put(new Integer(0x22E0), new Integer(0x22E1)); //[BEST FIT] DOES NOT PRECEDE OR EQUAL
        mirroredGlyphs.put(new Integer(0x22E1), new Integer(0x22E0)); //[BEST FIT] DOES NOT SUCCEED OR EQUAL
        mirroredGlyphs.put(new Integer(0x22E2), new Integer(0x22E3)); //[BEST FIT] NOT SQUARE IMAGE OF OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x22E3), new Integer(0x22E2)); //[BEST FIT] NOT SQUARE ORIGINAL OF OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x22E4), new Integer(0x22E5)); //[BEST FIT] SQUARE IMAGE OF OR NOT EQUAL TO
        mirroredGlyphs.put(new Integer(0x22E5), new Integer(0x22E4)); //[BEST FIT] SQUARE ORIGINAL OF OR NOT EQUAL TO
        mirroredGlyphs.put(new Integer(0x22E6), new Integer(0x22E7)); //[BEST FIT] LESS-THAN BUT NOT EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x22E7), new Integer(0x22E6)); //[BEST FIT] GREATER-THAN BUT NOT EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x22E8), new Integer(0x22E9)); //[BEST FIT] PRECEDES BUT NOT EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x22E9), new Integer(0x22E8)); //[BEST FIT] SUCCEEDS BUT NOT EQUIVALENT TO
        mirroredGlyphs.put(new Integer(0x22EA), new Integer(0x22EB)); //[BEST FIT] NOT NORMAL SUBGROUP OF
        mirroredGlyphs.put(new Integer(0x22EB), new Integer(0x22EA)); //[BEST FIT] DOES NOT CONTAIN AS NORMAL SUBGROUP
        mirroredGlyphs.put(new Integer(0x22EC), new Integer(0x22ED)); //[BEST FIT] NOT NORMAL SUBGROUP OF OR EQUAL TO
        mirroredGlyphs.put(new Integer(0x22ED), new Integer(0x22EC)); //[BEST FIT] DOES NOT CONTAIN AS NORMAL SUBGROUP OR EQUAL
        mirroredGlyphs.put(new Integer(0x22F0), new Integer(0x22F1)); //UP RIGHT DIAGONAL ELLIPSIS
        mirroredGlyphs.put(new Integer(0x22F1), new Integer(0x22F0)); //DOWN RIGHT DIAGONAL ELLIPSIS
        mirroredGlyphs.put(new Integer(0x2308), new Integer(0x2309)); //LEFT CEILING
        mirroredGlyphs.put(new Integer(0x2309), new Integer(0x2308)); //RIGHT CEILING
        mirroredGlyphs.put(new Integer(0x230A), new Integer(0x230B)); //LEFT FLOOR
        mirroredGlyphs.put(new Integer(0x230B), new Integer(0x230A)); //RIGHT FLOOR
        mirroredGlyphs.put(new Integer(0x2329), new Integer(0x232A)); //LEFT-POINTING ANGLE BRACKET
        mirroredGlyphs.put(new Integer(0x232A), new Integer(0x2329)); //RIGHT-POINTING ANGLE BRACKET
        mirroredGlyphs.put(new Integer(0x3008), new Integer(0x3009)); //LEFT ANGLE BRACKET
        mirroredGlyphs.put(new Integer(0x3009), new Integer(0x3008)); //RIGHT ANGLE BRACKET
        mirroredGlyphs.put(new Integer(0x300A), new Integer(0x300B)); //LEFT DOUBLE ANGLE BRACKET
        mirroredGlyphs.put(new Integer(0x300B), new Integer(0x300A)); //RIGHT DOUBLE ANGLE BRACKET
        mirroredGlyphs.put(new Integer(0x300C), new Integer(0x300D)); //[BEST FIT] LEFT CORNER BRACKET
        mirroredGlyphs.put(new Integer(0x300D), new Integer(0x300C)); //[BEST FIT] RIGHT CORNER BRACKET
        mirroredGlyphs.put(new Integer(0x300E), new Integer(0x300F)); //[BEST FIT] LEFT WHITE CORNER BRACKET
        mirroredGlyphs.put(new Integer(0x300F), new Integer(0x300E)); //[BEST FIT] RIGHT WHITE CORNER BRACKET
        mirroredGlyphs.put(new Integer(0x3010), new Integer(0x3011)); //LEFT BLACK LENTICULAR BRACKET
        mirroredGlyphs.put(new Integer(0x3011), new Integer(0x3010)); //RIGHT BLACK LENTICULAR BRACKET
        mirroredGlyphs.put(new Integer(0x3014), new Integer(0x3015)); //[BEST FIT] LEFT TORTOISE SHELL BRACKET
        mirroredGlyphs.put(new Integer(0x3015), new Integer(0x3014)); //[BEST FIT] RIGHT TORTOISE SHELL BRACKET
        mirroredGlyphs.put(new Integer(0x3016), new Integer(0x3017)); //LEFT WHITE LENTICULAR BRACKET
        mirroredGlyphs.put(new Integer(0x3017), new Integer(0x3016)); //RIGHT WHITE LENTICULAR BRACKET
        mirroredGlyphs.put(new Integer(0x3018), new Integer(0x3019)); //LEFT WHITE TORTOISE SHELL BRACKET
        mirroredGlyphs.put(new Integer(0x3019), new Integer(0x3018)); //RIGHT WHITE TORTOISE SHELL BRACKET
        mirroredGlyphs.put(new Integer(0x301A), new Integer(0x301B)); //LEFT WHITE SQUARE BRACKET
        mirroredGlyphs.put(new Integer(0x301B), new Integer(0x301A)); //RIGHT WHITE SQUARE BRACKET
    }


}
