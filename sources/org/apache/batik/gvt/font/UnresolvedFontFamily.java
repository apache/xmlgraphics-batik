/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.text.AttributedCharacterIterator;

/**
 * A font family class for unresolved fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class UnresolvedFontFamily implements GVTFontFamily {

    protected String familyName;

    /**
     * Constructs an UnresolvedFontFamily with the specified familyName.
     *
     * @param familyName The name of the font family.
     */
    public UnresolvedFontFamily(String familyName) {
        this.familyName = familyName;
    }

    /**
     * Returns the font family name.
     *
     * @return the family name.
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Derives a GVTFont object of the correct size. As this font family is yet
     * to be resolved this will always return null.
     *
     * @param size The required size of the derived font.
     * @param aci The character iterator that will be rendered using the derived
     * font.
     */
    public GVTFont deriveFont(float size, AttributedCharacterIterator aci) {
       return null;
    }
}