/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

/**
 * The HKern class describes an entry in the "kerning table". It provides
 * a horizontal kerning value to be used when laying out characters side
 * by side.
 *
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */
public class HKern {

    private char[] firstCharacters;
    private char[] secondCharacters;
    private float kerningAdjust;

    /**
     * Create an HKern object with the given character arrays
     * and kerning value.
     *
     * @param firstCharacters The array of characters that are considered as first in this kerning entry.
     * @param secondCharacters The array of characters that are considered as second in this kerning entry.
     * @param adjustValue The kerning adjustment (positive value means the space between characters should decrease).
     */

    public HKern(char[] firstCharacters, char[] secondCharacters, float adjustValue) {

	this.firstCharacters = firstCharacters;
	this.secondCharacters = secondCharacters;
	this.kerningAdjust = adjustValue;

    }

    /**
     * Returns true if the parameter character is one of the characters considered
     * as first by this kerning pair. Returns false otherwise.
     *
     * @param character The character to test
     * @return True if this character is in the list of first characters for the kerning entry 
     */

    public boolean matchesFirstCharacter(char character) {

	// maybe should use a lookup table? Depends how large
	// the list of characters for this kerning entry gets,
	// I can't imagine it to be that big, but I don't
	// know every language and font in the world.
	for (int i=0; i < firstCharacters.length; i++) {
	    if (firstCharacters[i] == character) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Returns true if the parameter character is one of the characters considered
     * as second by this kerning pair. Returns false otherwise.
     *
     * @param character The character to test
     * @return True if this character is in the list of second characters for the kerning entry 
     */

    public boolean matchesSecondCharacter(char character) {

	// maybe should use a lookup table? Depends how large
	// the list of characters for this kerning entry gets,
	// I can't imagine it to be that big but I don't
	// know every language and font in the world.
	for (int i=0; i < secondCharacters.length; i++) {
	    if (secondCharacters[i] == character) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Give the kerning adjustment value for this entry (positive
     * value means the space between characters should decrease) 
     */

    public float getAdjustValue() {
	return kerningAdjust;
    }

}
