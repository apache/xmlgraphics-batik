/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.awt.font.TextAttribute;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import org.apache.batik.gvt.font.AWTGVTFont;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;

public class GlyphIterator {
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK 
        = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;

    public static final AttributedCharacterIterator.Attribute 
        TEXT_COMPOUND_DELIMITER 
        = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER;
    public static final 
        AttributedCharacterIterator.Attribute GVT_FONT 
        = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;

    public static final char SOFT_HYPHEN       = 0x00AD;
    public static final char ZERO_WIDTH_SPACE  = 0x200B;
    public static final char ZERO_WIDTH_JOINER = 0x200D;

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
    // The runLimit (for current font)
    int     runLimit   = 0;
    GVTFont font       = null;
    int     fontStart  = 0;
    float   maxAscent  = 0;
    float   maxDescent = 0;
    float   maxFontSize = 0;

    float width = 0;
    // The current char (from ACI)
    char ch = 0;
    // The number of glyphs in gv.
    int numGlyphs = 0;
    // The AttributedCharacterIterator.
    AttributedCharacterIterator aci;
    // The GVTGlyphVector for this Text chunk.
    GVTGlyphVector gv;
    // The GlyphPositions for this GlyphVector (Shared)
    float [] gp;
    // The font render context for this GylphVector.
    FontRenderContext frc;

    // The Indexes of new leftShift amounts (soft-hyphens)
    int   [] leftShiftIdx = null;
    // The amount of new leftShifts (soft-hyphen adv)
    float [] leftShiftAmt = null;
    // The current left shift (inherited from previous line).
    int leftShift = 0;


    
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
        this.frc      = gv.getFontRenderContext();
        
        this.font = (GVTFont)aci.getAttribute(GVT_FONT);
        if (font == null) {
            font = new AWTGVTFont(aci.getAttributes());
        }
        fontStart = aciIdx;
        this.maxFontSize = -Float.MAX_VALUE;
        this.maxAscent   = -Float.MAX_VALUE;
        this.maxDescent  = -Float.MAX_VALUE;

        // Figure out where the font size might change again...
        this.runLimit  = aci.getRunLimit(TEXT_COMPOUND_DELIMITER);

        this.numGlyphs   = gv.getNumGlyphs();
        this.gp          = gv.getGlyphPositions(0, this.numGlyphs+1, null);
        this.adv = getCharWidth();
        this.adj = getCharAdvance();
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

        gi.idx        = this.idx;
        gi.chIdx      = this.chIdx;
        gi.aciIdx     = this.aciIdx;
        gi.adv        = this.adv;
        gi.adj        = this.adj;
        gi.runLimit   = this.runLimit;
        gi.ch         = this.ch;
        gi.chIdx      = this.chIdx;
        gi.numGlyphs  = this.numGlyphs;
        gi.gp         = this.gp;

        gi.frc         = this.frc;
        gi.font        = this.font;
        gi.fontStart   = this.fontStart;
        gi.maxAscent   = this.maxAscent;
        gi.maxDescent  = this.maxDescent;
        gi.maxFontSize = this.maxFontSize;

        gi.leftShift    = this.leftShift;
        gi.leftShiftIdx = this.leftShiftIdx;
        gi.leftShiftAmt = this.leftShiftAmt;
        return gi;
    }

    /**
     * @return  The index into glyph vector for current character.
     */
    public int getGlyphIndex() { return idx; }

    /**
     * @return the current character.
     */
    public char getChar() { return ch; }

    /**
     * @return The index into Attributed Character iterator for
     * current character.  
     */
    public int getACIIndex() { return aciIdx; }

    /**
     * @return The current advance for the line, this is the 'visual width'
     * of the current line.
     */
    public float getAdv() { return adv; }

    /**
     * @return The current adjustment for the line.  This is the ammount
     * that needs to be subracted from the following line to get it back
     * to the start of the next line.
     */
    public float getAdj() { return adj; }

    public float getMaxFontSize()  {
        if (aciIdx >= fontStart) {
            updateLineMetrics(aciIdx);
            fontStart = aciIdx + gv.getCharacterCount(idx,idx);
        }
        return maxFontSize; 
    }

    public float getMaxAscent()  { 
        if (aciIdx >= fontStart) {
            updateLineMetrics(aciIdx);
            fontStart = aciIdx + gv.getCharacterCount(idx,idx);
        }
        return maxAscent; 
    }

    public float getMaxDescent() { 
        if (aciIdx >= fontStart) {
            updateLineMetrics(aciIdx);
            fontStart = aciIdx + gv.getCharacterCount(idx,idx);
        }
        return maxDescent; 
    }

    public boolean isLastChar() {
        return (idx == (numGlyphs-1));
    }

    public boolean done() {
        return (idx >= numGlyphs);
    }

    public boolean isBreakChar() {
        switch (ch) {
        case GlyphIterator.ZERO_WIDTH_SPACE:  return true;            
        case GlyphIterator.ZERO_WIDTH_JOINER: return false;            
        case GlyphIterator.SOFT_HYPHEN:       return true;
        case ' ': case '\t':                  return true;
        default:                              return false;
        }
    }

    protected boolean isPrinting(char tstCH) {
        switch (ch) {
        case GlyphIterator.ZERO_WIDTH_SPACE:  return false;            
        case GlyphIterator.ZERO_WIDTH_JOINER: return false;            
        case GlyphIterator.SOFT_HYPHEN:       return true;
        case ' ': case '\t':                  return false;
        default:                              return true;
        }
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
        if ((ch == SOFT_HYPHEN)      || 
            (ch == ZERO_WIDTH_SPACE) ||
            (ch == ZERO_WIDTH_JOINER)) {
            // Special handling for soft hyphens and zero-width spaces
            gv.setGlyphVisible(idx, false);
            float chAdv = getCharAdvance();
            float chW   = getCharWidth();
            adv -= chW;
            adj -= chAdv;
            if (leftShiftIdx == null) {
                leftShiftIdx = new int[1];
                leftShiftIdx[0] = idx;
                leftShiftAmt = new float[1];
                leftShiftAmt[0] = chAdv;
            } else {
                int [] newLeftShiftIdx = new int[leftShiftIdx.length+1];
                for (int i=0; i<leftShiftIdx.length; i++)
                    newLeftShiftIdx[i] = leftShiftIdx[i];
                newLeftShiftIdx[leftShiftIdx.length] = idx;
                leftShiftIdx = newLeftShiftIdx;

                float [] newLeftShiftAmt = new float[leftShiftAmt.length+1];
                for (int i=0; i<leftShiftAmt.length; i++)
                    newLeftShiftAmt[i] = leftShiftAmt[i];
                newLeftShiftAmt[leftShiftAmt.length] = chAdv;
                leftShiftAmt = newLeftShiftAmt;
            }
        }

        int preIncACIIdx = aciIdx;
        aciIdx += gv.getCharacterCount(idx,idx);
        ch = aci.setIndex(aciIdx);
        idx++;
        if (idx == numGlyphs) return;

        if (aciIdx >= runLimit) {
            updateLineMetrics(preIncACIIdx);
            runLimit = aci.getRunLimit(TEXT_COMPOUND_DELIMITER);
            font     = (GVTFont)aci.getAttribute(GVT_FONT);
            if (font == null) {
                font = new AWTGVTFont(aci.getAttributes());
            }
            fontStart = aciIdx;
        }

        float chAdv = getCharAdvance();
        adj += chAdv;
        if (isPrinting()) {
            chIdx = idx;
            float chW   = getCharWidth();
            adv = adj-(chAdv-chW);
        }
    }

    protected void updateLineMetrics(int end) {
        GVTLineMetrics glm = font.getLineMetrics
            (aci, fontStart, end, frc);
        float ascent  = glm.getAscent();
        float descent = glm.getDescent();
        float fontSz  = font.getSize();
        if (ascent >  maxAscent)   maxAscent   = ascent;
        if (descent > maxDescent)  maxDescent  = descent;
        if (fontSz >  maxFontSize) maxFontSize = fontSz;
    }

    public LineInfo getLineInfo(Point2D.Float loc, 
                                float lineWidth, 
                                boolean partial) {
        if (ch == SOFT_HYPHEN) {
            gv.setGlyphVisible(idx, true);
        }

        int lsi = 0;
        int nextLSI;
        if (leftShiftIdx != null) nextLSI = leftShiftIdx[lsi];
        else                      nextLSI = idx+1;
        for (int ci=lineIdx; ci<=idx; ci++) {
            if (ci == nextLSI) {
                leftShift += leftShiftAmt[lsi++];
                if (lsi < leftShiftIdx.length)
                    nextLSI = leftShiftIdx[lsi];
            }
            gv.setGlyphPosition(ci, new Point2D.Float(gp[2*ci]-leftShift,
                                                      gp[2*ci+1]));
        }
        leftShiftIdx = null;
        leftShiftAmt = null;

        return new LineInfo(aci, gv, lineIdx, idx+1, loc, adv, adj,
                            getCharWidth(chIdx), lineWidth, partial);
    }

    public void newLine() {
        adv=0;
        adj=0;
        maxAscent   = -Float.MAX_VALUE;
        maxDescent  = -Float.MAX_VALUE;
        maxFontSize = -Float.MAX_VALUE;

        if ((ch == ZERO_WIDTH_SPACE) ||
            (ch == ZERO_WIDTH_JOINER))
            gv.setGlyphVisible(idx, false);
        ch = 0;  // Disable soft-hyphen/ZWS advance adjustment.
        nextChar();
        lineIdx = idx;
    }

    public boolean isPrinting() {
        return isPrinting(ch);
    }

    /**
     * Get the advance associated with the current glyph
     */
    public float getCharAdvance() {
        return getCharAdvance(idx);
    }

    /**
     * Get the visual advance associated with the current glyph.
     * This is the distance from the location of the glyph to
     * the rightmost part of the glyph.
     */
    public float getCharWidth() {
        return getCharWidth(idx);
    }

    /**
     * Get the advance associated with any glyph
     */
    protected float getCharAdvance(int gvIdx) {
        return gp[2*gvIdx+2] - gp[2*gvIdx];
    }

    /**
     * Get the visual advance associated with the current glyph.
     * This is the distance from the location of the glyph to
     * the rightmost part of the glyph.
     */
    protected float getCharWidth(int gvIdx) {
        Rectangle2D lcBound = gv.getGlyphVisualBounds(chIdx).getBounds2D();
        Point2D     lcLoc   = gv.getGlyphPosition(chIdx);
        return (float)(lcBound.getX()+lcBound.getWidth()- lcLoc.getX());
    }
}
