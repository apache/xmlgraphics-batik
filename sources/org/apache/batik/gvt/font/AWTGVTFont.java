/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.awt.Font;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;

import java.awt.geom.AffineTransform;

import java.text.CharacterIterator;

import java.util.HashMap;
import java.util.Map;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.StringCharacterIterator;

import org.apache.batik.gvt.text.ArabicTextHandler;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;


/**
 * This is a wrapper class for a java.awt.Font instance.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class AWTGVTFont implements GVTFont {

    private Font awtFont;
    private Font tenPtFont;

    /**
     * Creates a new AWTGVTFont that wraps the given Font.
     *
     * @param font The font object to wrap.
     */
    public AWTGVTFont(Font font) {
        awtFont = font;
        tenPtFont = awtFont.deriveFont(10f);
    }

    /**
     * Creates a new AWTGVTFont with the specified attributes.
     *
     * @param attributes Contains attributes of the font to create.
     */
    public AWTGVTFont(Map attributes) {
        awtFont = new Font(attributes);
        tenPtFont = awtFont.deriveFont(10f);
    }

    /**
     * Creates a new AWTGVTFont from the specified name, style and point size.
     *
     * @param name The name of the new font.
     * @param style The required font style.
     * @param size The required font size.
     */
    public AWTGVTFont(String name, int style, int size) {
        awtFont = new Font(name, style, size);
        tenPtFont = awtFont.deriveFont(10f);
    }

    /**
     * Checks if this font can display the specified character.
     *
     * @param c The character to check.
     * @return Whether or not the character can be displayed.
     */
    public boolean canDisplay(char c) {
        return awtFont.canDisplay(c);
    }

    /**
     * Indicates whether or not this font can display the characters in the
     * specified text starting at start and ending at limit.
     *
     * @param text An array containing the characters to check.
     * @param start The index of the first character to check.
     * @param limit The index of the last character to check.
     *
     * @return The index of the first char this font cannot display. Will be
     * -1 if it can display all characters in the specified range.
     */
    public int canDisplayUpTo(char[] text, int start, int limit) {
        return awtFont.canDisplayUpTo(text, start, limit);
    }

    /**
     *  Indicates whether or not this font can display the the characters in
     *  the specified CharacterIterator starting at start and ending at limit.
     */
    public int canDisplayUpTo(CharacterIterator iter, int start, int limit) {
        return awtFont.canDisplayUpTo(iter, start, limit);
    }

    /**
     *  Indicates whether or not this font can display a specified String.
     */
    public int canDisplayUpTo(String str) {
        return awtFont.canDisplayUpTo(str);
    }

    /**
     *  Returns a new GlyphVector object created with the specified array of
     *  characters and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            char[] chars) {

        StringCharacterIterator sci = 
	    new StringCharacterIterator(new String(chars));

        if (getSize() < 1) {
            // Because of a bug with GlyphVectors created by fonts with sizes <
            // 1pt, we need to use a 10pt font and then apply a scale factor to
            // reduce it to its correct size.  Have a look at AWTGVTGlyphVector.
            return new AWTGVTGlyphVector
		(tenPtFont.createGlyphVector(frc, chars),
		 new AWTGVTFont(tenPtFont), getSize()/10f, sci);
        } else {
	    return new AWTGVTGlyphVector
		(awtFont.createGlyphVector(frc, chars), this, 1, sci);
	}
    }

    /**
     * Returns a new GlyphVector object created with the specified
     * CharacterIterator and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            CharacterIterator ci) {

        AWTGVTGlyphVector awtGlyphVector;

        if (getSize() < 1) {
            // Because of a bug with GlyphVectors created by fonts with sizes <
            // 1pt, we need to use a 10pt font and then apply a scale factor to
            // reduce it to its correct size.  Have a look at AWTGVTGlyphVector.
            awtGlyphVector = new AWTGVTGlyphVector
		(tenPtFont.createGlyphVector(frc, ci),
		 new AWTGVTFont(tenPtFont), getSize()/10f,
		 ci);
        } else {
            awtGlyphVector = new AWTGVTGlyphVector
		(awtFont.createGlyphVector(frc, ci), this, 1, ci);
        }

        if (ci instanceof AttributedCharacterIterator) {
            AttributedCharacterIterator aci = (AttributedCharacterIterator)ci;
            AttributedString as = new AttributedString(aci);
            if (ArabicTextHandler.containsArabic(as)) {
                String substString = 
		    ArabicTextHandler.createSubstituteString(aci);

                return createGlyphVector(frc, substString);
            }
        }
        return awtGlyphVector;
    }

    /**
     *  Returns a new GlyphVector object created with the specified integer
     *  array and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            int[] glyphCodes, 
					    CharacterIterator ci) {
        if (getSize() < 1) {
            // Because of a bug with GlyphVectors created by fonts with sizes <
            // 1pt, we need to use a 10pt font and then apply a scale factor to
            // reduce it to its correct size.  Have a look at AWTGVTGlyphVector.
            return new AWTGVTGlyphVector
		(tenPtFont.createGlyphVector(frc, glyphCodes),
		 new AWTGVTFont(tenPtFont), getSize()/10f, 
		 ci);
        }
        return new AWTGVTGlyphVector
	    (awtFont.createGlyphVector(frc, glyphCodes), this, 1, ci);
    }

    /**
     * Returns a new GlyphVector object created with the specified String and
     * the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc, String str) {

        StringCharacterIterator sci = new StringCharacterIterator(str);

        if (getSize() < 1) {
            // Because of a bug with GlyphVectors created by fonts with sizes <
            // 1pt, we need to use a 10pt font and then apply a scale factor to
            // reduce it to its correct size.  Have a look at AWTGVTGlyphVector.
            return new AWTGVTGlyphVector
		(tenPtFont.createGlyphVector(frc, str),
		 new AWTGVTFont(tenPtFont), getSize()/10f, sci);
        }
        return new AWTGVTGlyphVector
	    (awtFont.createGlyphVector(frc, str), this, 1, sci);
    }

    /**
     * Creates a new Font object by replicating the current Font object and
     * applying a new size to it.
     */
    public GVTFont deriveFont(float size) {
        Font newFont = awtFont.deriveFont(size);
        return new AWTGVTFont(newFont);
    }

    /**
     *  Returns a LineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(char[] chars, 
					 int beginIndex, 
					 int limit,
                                         FontRenderContext frc) {
        if (getSize() < 1) {
            // Because of a bug with LineMetrics created by fonts with sizes <
            // 1pt, we need to use a 10pt font and then apply a scale factor to
            // reduce the metrics to their correct size.  Have a look at
            // GVTLineMetrics.
            return new GVTLineMetrics
		(tenPtFont.getLineMetrics(chars, beginIndex, limit, frc), 
		 getSize()/10f);
        }
        return new GVTLineMetrics
	    (awtFont.getLineMetrics(chars, beginIndex, limit, frc));
    }

    /**
     * Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(CharacterIterator ci, 
					 int beginIndex,
                                         int limit, 
					 FontRenderContext frc) {
        if (getSize() < 1) {
            // Because of a bug with LineMetrics created by fonts with sizes <
            // 1pt, we need to use a 10pt font and then apply a scale factor to
            // reduce the metrics to their correct size.  Have a look at
            // GVTLineMetrics.
            return new GVTLineMetrics
		(tenPtFont.getLineMetrics(ci, beginIndex, limit, frc),
		 getSize()/10f);
        }
        return new GVTLineMetrics
	    (awtFont.getLineMetrics(ci, beginIndex, limit, frc));
    }

    /**
     *  Returns a GVTLineMetrics object created with the specified String and
     *  FontRenderContext.
     */
    public GVTLineMetrics getLineMetrics(String str, FontRenderContext frc) {
        if (getSize() < 1) {
            // Because of a bug with LineMetrics created by fonts with sizes <
            // 1pt, we need to use a 10pt font and then apply a scale factor to
            // reduce the metrics to their correct size.  Have a look at
            // GVTLineMetrics.
            return new GVTLineMetrics(tenPtFont.getLineMetrics(str, frc),
                                      getSize()/10f);
        }
        return new GVTLineMetrics(awtFont.getLineMetrics(str, frc));
    }

    /**
     * Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(String str, 
					 int beginIndex, 
					 int limit,
                                         FontRenderContext frc) {
        if (getSize() < 1) {
            // Because of a bug with LineMetrics created by fonts with sizes <
            // 1pt, we need to use a 10pt font and then apply a scale factor to
            // reduce the metrics to their correct size.  Have a look at
            // GVTLineMetrics.
            return new GVTLineMetrics
		(tenPtFont.getLineMetrics(str, beginIndex, limit, frc),
		 getSize()/10f);
        }
        return new GVTLineMetrics
	    (awtFont.getLineMetrics(str, beginIndex, limit, frc));
    }

    /**
     * Returns the size of this font.
     */
    public float getSize() {
        return awtFont.getSize2D();
    }

    /**
     * Returns the horizontal kerning value for this glyph pair.
     */
    public float getHKern(int glyphCode1, int glyphCode2) {
        return 0f;
    }

    /**
     * Returns the vertical kerning value for this glyph pair.
     */
    public float getVKern(int glyphCode1, int glyphCode2) {
        return 0f;
    }

    /**
     * Returns a string representation of this font. This is for debugging
     * purposes only.
     */
    public String toString() {
        return awtFont.getFontName();
    }
}

