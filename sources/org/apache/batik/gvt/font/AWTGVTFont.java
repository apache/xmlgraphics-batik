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
import java.awt.font.LineMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.text.CharacterIterator;
import java.util.Map;
import java.util.HashMap;
import java.awt.geom.AffineTransform;


/**
 * This is a wrapper class for a java.awt.Font instance.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public final class AWTGVTFont implements GVTFont {

    private Font awtFont;
    private Font tenPtFont;

    /**
     * Creates a new AWTGVTFont that wraps the given Font.
     */
    public AWTGVTFont(Font font) {
        awtFont = font;
        tenPtFont = awtFont.deriveFont(10f);
    }

    /**
     * Creates a new AWTGVTFont with the specified attributes.
     */
    public AWTGVTFont(Map attributes) {
        awtFont = new Font(attributes);
        tenPtFont = awtFont.deriveFont(10f);
    }

    /**
     * Creates a new AWTGVTFont from the specified name, style and point size.
     */
    public AWTGVTFont(String name, int style, int size) {
        awtFont = new Font(name, style, size);
        tenPtFont = awtFont.deriveFont(10f);
    }

    /**
     * Checks if this Font has a glyph for the specified character.
     */
    public boolean canDisplay(char c) {
        return awtFont.canDisplay(c);
    }

    /**
     *  Indicates whether or not this Font can display the characters in the
     *  specified text starting at start and ending at limit.
     */
    public int canDisplayUpTo(char[] text, int start, int limit) {
        return awtFont.canDisplayUpTo(text, start, limit);
    }

    /**
     *  Indicates whether or not this Font can display the the characters in
     *  the specified CharacterIterator starting at start and ending at limit.
     */
    public int canDisplayUpTo(CharacterIterator iter, int start, int limit) {
        return awtFont.canDisplayUpTo(iter, start, limit);
    }

    /**
     *  Indicates whether or not this Font can display a specified String.
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
        if (getSize() < 1) {
            return new AWTGVTGlyphVector(tenPtFont.createGlyphVector(frc, chars), new AWTGVTFont(tenPtFont), getSize()/10f);
        }
        return new AWTGVTGlyphVector(awtFont.createGlyphVector(frc, chars), this, 1);
    }

    /**
     * Returns a new GlyphVector object created with the specified
     * CharacterIterator and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            CharacterIterator ci) {
        if (getSize() < 1) {
            return new AWTGVTGlyphVector(tenPtFont.createGlyphVector(frc, ci), new AWTGVTFont(tenPtFont), getSize()/10f);
        }
        return new AWTGVTGlyphVector(awtFont.createGlyphVector(frc, ci), this, 1);
    }

    /**
     *  Returns a new GlyphVector object created with the specified integer
     *  array and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            int[] glyphCodes) {
        if (getSize() < 1) {
            return new AWTGVTGlyphVector(tenPtFont.createGlyphVector(frc, glyphCodes), new AWTGVTFont(tenPtFont), getSize()/10f);
        }
        return new AWTGVTGlyphVector(awtFont.createGlyphVector(frc, glyphCodes),
                                     this, 1);
    }

    /**
     * Returns a new GlyphVector object created with the specified String and
     * the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc, String str) {
        if (getSize() < 1) {
            return new AWTGVTGlyphVector(tenPtFont.createGlyphVector(frc, str), new AWTGVTFont(tenPtFont), getSize()/10f);
        }
        return new AWTGVTGlyphVector(awtFont.createGlyphVector(frc, str), this, 1);
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
    public GVTLineMetrics getLineMetrics(char[] chars, int beginIndex, int limit,
                                         FontRenderContext frc) {
        if (getSize() < 1) {
            return new GVTLineMetrics(tenPtFont.getLineMetrics(chars, beginIndex, limit, frc), getSize()/10f);
        }
        return new GVTLineMetrics(awtFont.getLineMetrics(chars, beginIndex, limit, frc));
    }

    /**
     * Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(CharacterIterator ci, int beginIndex,
                                         int limit, FontRenderContext frc) {
        if (getSize() < 1) {
            return new GVTLineMetrics(tenPtFont.getLineMetrics(ci, beginIndex, limit, frc), getSize()/10f);
        }
        return new GVTLineMetrics(awtFont.getLineMetrics(ci, beginIndex, limit, frc));
    }

    /**
     *  Returns a GVTLineMetrics object created with the specified String and
     *  FontRenderContext.
     */
    public GVTLineMetrics getLineMetrics(String str, FontRenderContext frc) {
        if (getSize() < 1) {
            return new GVTLineMetrics(tenPtFont.getLineMetrics(str, frc), getSize()/10f);
        }
        return new GVTLineMetrics(awtFont.getLineMetrics(str, frc));
    }

    /**
     * Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(String str, int beginIndex, int limit,
                                         FontRenderContext frc) {
        if (getSize() < 1) {
            return new GVTLineMetrics(tenPtFont.getLineMetrics(str, beginIndex, limit, frc), getSize()/10f);
        }
        return new GVTLineMetrics(awtFont.getLineMetrics(str, beginIndex, limit, frc));
    }

    /**
     * Returns the size of this font.
     */
    public float getSize() {
        return awtFont.getSize2D();
    }

    /**
     * Returns the horizontal kerning value of this glyph pair.
     */
    public float getHKern(int glyphCode1, int glyphCode2) {
        return 0f;
    }

    /**
     * Returns the vertical kerning value of this glyph pair.
     */
    public float getVKern(int glyphCode1, int glyphCode2) {
        return 0f;
    }

    public String toString() {
        return awtFont.getFontName();
    }
}

