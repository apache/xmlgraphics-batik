/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.font.GVTGlyphVector;

public class GlyphIterator {
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK 
        = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;

    public static final AttributedCharacterIterator.Attribute 
        TEXT_COMPOUND_DELIMITER 
        = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER;
    public static final 
        AttributedCharacterIterator.Attribute GVT_FONT 
        = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;

    // Glyph index of current glyph
    int   idx         = -1;
    // Glyph index of last 'printing' glyph.
    int   chIdx       = -1;
    int   lineIdx     = -1;

    // The ACI index of current glyph.
    int   aciIdx      = -1;
    // The total advance for line including last non-space glyph
    float adv         =  0;
    // The total advance for line including spaces at end of line.
    float adj         =  0;
    // The current font size
    float fontSize    = 0;
    // The runLimit (for current font)
    int   runLimit    = 0;
    // The max font size on line (printable chars only).  
    float maxFontSize = 0;

    float width = 0;
    // The current char (from ACI)
    char ch = 0;
    // The number of glyphs in gv.
    int numGlyphs = 0;
    // The AttributedCharacterIterator.
    AttributedCharacterIterator aci;
    GVTGlyphVector gv;
    float [] gp;
    
    public GlyphIterator(AttributedCharacterIterator aci,
                         GVTGlyphVector gv) {
        this.aci      = aci;
        this.gv       = gv;

        this.idx      = 0;
        this.chIdx    = 0;
        this.lineIdx  = 0;
        this.aciIdx   = aci.getBeginIndex();
        this.ch       = aci.first();
        this.chIdx    = 0;

        this.fontSize = 12;
        Float fsFloat = (Float)aci.getAttributes().get(TextAttribute.SIZE);
        if (fsFloat != null) {
            this.fontSize = fsFloat.floatValue();
        }
        this.maxFontSize = this.fontSize;

        // Figure out where the font size might change again...
        this.runLimit  = aci.getRunLimit(TEXT_COMPOUND_DELIMITER);

        this.numGlyphs   = gv.getNumGlyphs();
        this.gp          = gv.getGlyphPositions(0, this.numGlyphs+1, null);
        this.adv = this.adj = getAdvance();
    }

    public GlyphIterator(GlyphIterator gi) {
        gi.copy(this);
    }

    public GlyphIterator copy() {
        return new GlyphIterator(this);
    }

    public GlyphIterator copy(GlyphIterator gi) {
        if (gi == null)
            return new GlyphIterator(this);

        gi.idx         = this.idx;
        gi.chIdx       = this.chIdx;
        gi.aciIdx      = this.aciIdx;
        gi.adv         = this.adv;
        gi.adj         = this.adj;
        gi.fontSize    = this.fontSize;
        gi.maxFontSize = this.maxFontSize;
        gi.runLimit    = this.runLimit;
        gi.ch          = this.ch;
        gi.chIdx       = this.chIdx;
        gi.numGlyphs   = this.numGlyphs;

        return gi;
    }

    public int getGlyphIndex() { return idx; }

    public int getACIIndex() { return aciIdx; }

    public float getAdv() { return adv; }

    public float getAdj() { return adj; }

    public float getFontSize() { return fontSize; }

    public float getMaxFontSize() { return maxFontSize; }

    public boolean isLastChar() {
        return (idx == (numGlyphs-1));
    }

    public boolean done() {
        return (idx >= numGlyphs);
    }

    public boolean isBreakChar() {
        return ((ch == ' ') || (ch == '\t'));
    }

    public int getLineBreaks() {
        Integer i = (Integer)aci.getAttribute(FLOW_LINE_BREAK);
        if (i == null) return 0;
        return i.intValue();
    }

    /**
     * Move iterator to the next char.
     */
    public void nextChar() {
        aciIdx += gv.getCharacterCount(idx,idx);
        ch = aci.setIndex(aciIdx);
        idx++;
        if (idx == numGlyphs) return;

        if (aciIdx >= runLimit) {
            runLimit = aci.getRunLimit(TEXT_COMPOUND_DELIMITER);
            Float fsFloat = (Float)aci.getAttributes().get(TextAttribute.SIZE);
            if (fsFloat != null) {
                fontSize = fsFloat.floatValue();
            } else {
                fontSize = 12;
            }
        }

        if (fontSize > maxFontSize)
            maxFontSize = fontSize;

        float chAdv = getAdvance();
        adj += chAdv;
        if (isPrinting()) {
            chIdx = idx;
            adv = adj;
        }
    }

    public LineInfo getLineInfo(Point2D.Float loc, 
                                float lineWidth, 
                                boolean partial) {
        // Tweak line advance to account for visual bounds of last 
        // printing glyph.
        Rectangle2D lcBound = gv.getGlyphVisualBounds(chIdx).getBounds2D();
        Point2D     lcLoc   = gv.getGlyphPosition(chIdx);
        float       charW   = (float)(lcBound.getX()+lcBound.getWidth()-
                                      lcLoc.getX());
        float charAdv = getAdvance(chIdx);
        adv -= charAdv-charW;
        
        return new LineInfo(aci, gv, lineIdx, idx+1, loc, adv, adj,
                            charW, lineWidth, partial);
    }

    public void newLine() {
        adv=0;
        adj=0;
        maxFontSize = fontSize;

        nextChar();
        lineIdx = idx;
    }

    protected boolean isPrinting(char tstCH) {
        return !((ch == ' ') || (ch == '\t'));
    }        
       
    public boolean isPrinting() {
        return isPrinting(ch);
    }

    /**
     * Get the advance associated with the current glyph
     */
    public float getAdvance() {
        return getAdvance(idx);
    }

    /**
     * Get the advance associated with any glyph
     */
    protected float getAdvance(int gvIdx) {
        return gp[2*gvIdx+2] - gp[2*gvIdx];
    }
}
