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

    public BidiAttributedCharacterIterator(AttributedCharacterIterator aci, FontRenderContext frc) {

        this.frc = frc;
        aci.first();

        TextLayout tl = new TextLayout(aci, frc);
        int aciBeginIndex = aci.getBeginIndex();
        int numChars = tl.getCharacterCount();
        AttributedString as = new AttributedString(aci);
        for (int i = 0; i < numChars; i++) {
            as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL,
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

        // now construct the string in the new order
        String reorderedString = "";
        for (int i = 0; i < numChars; i++) {
            reorderedString += this.aci.setIndex(newCharOrder[i]);
        }

        // now construct the reordered ACI
        AttributedString reorderedAS = new AttributedString(reorderedString);
        for (int i = 0; i < numChars; i++) {
            this.aci.setIndex(newCharOrder[i]);
            Map attributes = this.aci.getAttributes();
            reorderedAS.addAttributes(attributes, i, i+1);
        }

        // transfer any position atttributs to the new first char
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
        reorderedACI = reorderedAS.getIterator();
    }

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



    public Object clone() {
        return new BidiAttributedCharacterIterator((AttributedCharacterIterator)aci.clone(), frc);
    }

    public char current() {
        return reorderedACI.current();
    }

    public char first() {
        return reorderedACI.first();
    }

    public int getBeginIndex() {
        return reorderedACI.getBeginIndex();
    }

    public int getEndIndex() {
        return reorderedACI.getEndIndex();
    }

    public int getIndex() {
        return reorderedACI.getIndex();
    }

    public char last() {
        return reorderedACI.last();
    }

    public char next() {
        return reorderedACI.next();
    }

    public char previous() {
        return reorderedACI.previous();
    }

    public char setIndex(int position) {
       return reorderedACI.setIndex(position);
    }


}