/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;


/**
 * The HKernTable class holds a kerning table (a collection of HKern
 * elements). It provides a more convenient method of looking up
 * kerning values when laying out glyphs.
 *
 * @author <a href="mailto:dean.jackson@cmis.csiro.au">Dean Jackson</a>
 * @version $Id$
 */
public class HKernTable {

    private HKern[] entries;

    /**
     * Create an HKernTable from an array of HKern entries.
     *
     * @param entries The array of HKern elements that represent the kerning entries for the font that this kerning table is associated with.
     */

    public HKernTable(HKern[] entries) {
	this.entries = entries;
    }

    /**
     * Returns the amount of kerning that should be added between the
     * given characters. Returns 0 if the characters should not be kerned.
     *
     * @param character1 The first character in the kerning pair
     * @param character2 The second character in the kerning pair
     * @return The amount of kerning to be added when laying out the characters.
     */

    public float getKerningValue(char character1, char character2) {

	for (int i=0; i < entries.length; i++) {
	    if (entries[i].matchesFirstCharacter(character1) &&
		entries[i].matchesSecondCharacter(character2)) {
		return entries[i].getAdjustValue();
	    }
	}

	return 0f;
    }

}
