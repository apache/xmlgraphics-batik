/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Composite;
import java.awt.BasicStroke;

import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;

import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.TextPainter;

import org.apache.batik.gvt.font.FontFamilyResolver;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.BidiAttributedCharacterIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.Mark;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.text.TextPath;
import org.apache.batik.gvt.text.TextSpanLayout;


/**
 * More sophisticated implementation of TextPainter which
 * renders the attributed character iterator of a <tt>TextNode</tt>.
 * <em>StrokingTextPainter includes support for stroke, fill, opacity,
 * text-decoration, and other attributes.</em>
 *
 * @see org.apache.batik.gvt.TextPainter
 * @see org.apache.batik.gvt.text.GVTAttributedCharacterIterator
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class StrokingTextPainter extends BasicTextPainter {

    public static final 
        AttributedCharacterIterator.Attribute FLOW_REGIONS =
        GVTAttributedCharacterIterator.TextAttribute.FLOW_REGIONS;

    public static final 
        AttributedCharacterIterator.Attribute FLOW_PARAGRAPH =
        GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;

    public static final 
        AttributedCharacterIterator.Attribute TEXT_COMPOUND_DELIMITER 
        = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER;

    public static final 
        AttributedCharacterIterator.Attribute GVT_FONT 
        = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT;

    public static final 
        AttributedCharacterIterator.Attribute GVT_FONT_FAMILIES 
        = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT_FAMILIES;

    public static final 
        AttributedCharacterIterator.Attribute BIDI_LEVEL
        = GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL;

    public static final 
        AttributedCharacterIterator.Attribute XPOS
        = GVTAttributedCharacterIterator.TextAttribute.X;

    public static final 
        AttributedCharacterIterator.Attribute YPOS
        = GVTAttributedCharacterIterator.TextAttribute.Y;

    public static final 
        AttributedCharacterIterator.Attribute TEXTPATH
        = GVTAttributedCharacterIterator.TextAttribute.TEXTPATH;


    private static final AttributedCharacterIterator.Attribute WRITING_MODE
        = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE;

    private static final Integer WRITING_MODE_TTB
        = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_TTB;

    private static final Integer WRITING_MODE_RTL
        = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_RTL;

    public static final 
        AttributedCharacterIterator.Attribute ANCHOR_TYPE
        = GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE;

    public static final Integer ADJUST_SPACING =
        GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING;
    public static final Integer ADJUST_ALL =
        GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL;

    static Set extendedAtts = new HashSet();

    static {
        extendedAtts.add(FLOW_PARAGRAPH);
        extendedAtts.add(TEXT_COMPOUND_DELIMITER);
        extendedAtts.add(GVT_FONT);
        extendedAtts.add(BIDI_LEVEL);
    }

    /**
     * A unique instance of this class.
     */
    protected static TextPainter singleton = new StrokingTextPainter();

    /**
     * Returns a unique instance of this class.
     */
    public static TextPainter getInstance() {
	return singleton;
    }

    /**
     * Paints the specified text node using the specified Graphics2D.
     *
     * @param node the text node to paint
     * @param g2d the Graphics2D to use
     */
    public void paint(TextNode node, Graphics2D g2d) {
        AttributedCharacterIterator aci;
        aci = node.getAttributedCharacterIterator();
        List textRuns = getTextRuns(node, aci);

        // draw the underline and overline first, then the actual text
        // and finally the strikethrough
        paintDecorations(textRuns, g2d, TextSpanLayout.DECORATION_UNDERLINE);
        paintDecorations(textRuns, g2d, TextSpanLayout.DECORATION_OVERLINE);
        paintTextRuns(textRuns, g2d);
        paintDecorations
            (textRuns, g2d, TextSpanLayout.DECORATION_STRIKETHROUGH);
    }

    private void printAttrs(AttributedCharacterIterator aci) {
        aci.first();
        int start = aci.getBeginIndex();
        System.out.print("AttrRuns: ");
        while (aci.current() != CharacterIterator.DONE) {
            int end   = aci.getRunLimit();
            System.out.print(""+(end-start)+", ");
            aci.setIndex(end);
            start = end;
        }
        System.out.println("");
    }

    // static long reorderTime, fontMatchingTime, layoutTime;
    public List getTextRuns(TextNode node, AttributedCharacterIterator aci) {
        List textRuns = node.getTextRuns();
        if (textRuns != null) {
            return textRuns;
        }

        AttributedCharacterIterator[] chunkACIs = getTextChunkACIs(aci);
        int [][] chunkCharMaps = new int[chunkACIs.length][];

        // long t0, t1;
        // t0 = System.currentTimeMillis();
        // reorder each chunk ACI for bidi text
        int chunkStart = aci.getBeginIndex();
        for (int i = 0; i < chunkACIs.length; i++) {
            BidiAttributedCharacterIterator iter;
            iter = new BidiAttributedCharacterIterator
                (chunkACIs[i], fontRenderContext, chunkStart);
            chunkACIs    [i] = iter;
            chunkCharMaps[i] = iter.getCharMap();
            // t1 = System.currentTimeMillis();
            // reorderTime += t1-t0;
            // t0=t1;
            chunkACIs    [i] = createModifiedACIForFontMatching
                (node, chunkACIs[i]);
            
            chunkStart += (chunkACIs[i].getEndIndex()-
                           chunkACIs[i].getBeginIndex());
            // t1 = System.currentTimeMillis();
            // fontMatchingTime += t1-t0;
            // t0 = t1;
        }

        // create text runs for each chunk and add them to the list
        textRuns = new ArrayList();
        TextChunk chunk, prevChunk=null;
        int currentChunk = 0;
        do {
	    // Text Chunks contain one or more TextRuns, which they
	    // create from the ACI.
            chunkACIs[currentChunk].first();

            chunk = getTextChunk(node, 
                                 chunkACIs[currentChunk], 
                                 chunkCharMaps[currentChunk],
                                 textRuns,
                                 prevChunk);
	    
            // Adjust according to text-anchor property value
            chunkACIs[currentChunk].first();
            if (chunk != null) {
                adjustChunkOffsets(textRuns, chunk);
            }
            prevChunk = chunk;
            currentChunk++;
	    
        } while (chunk != null && currentChunk < chunkACIs.length);


        aci.first();
        List rgns = (List)aci.getAttribute(FLOW_REGIONS);

        if (rgns != null) {
            Iterator i = textRuns.iterator();
            List chunkLayouts = new ArrayList();
            TextRun tr = (TextRun)i.next();
            List layouts = new ArrayList();
            chunkLayouts.add(layouts);
            layouts.add(tr.getLayout());
            while (i.hasNext()) {
                tr = (TextRun)i.next();
                if (tr.isFirstRunInChunk()) {
                    layouts = new ArrayList();
                    chunkLayouts.add(layouts);
                }
                layouts.add(tr.getLayout());
            }

            org.apache.batik.gvt.text.GlyphLayout.textWrapTextChunk
                (chunkACIs, chunkLayouts, rgns);
        }

        // t1 = System.currentTimeMillis();
        // layoutTime += t1-t0;
        // System.out.println("Reorder: " + reorderTime + " FontMatching: " + fontMatchingTime + " Layout: " + layoutTime);
        // cache the textRuns so don't need to recalculate
        node.setTextRuns(textRuns);
        return textRuns;
    }

    /**
     * Returns an array of ACIs, one for each text chunk within the given
     * text node.
     */
    private AttributedCharacterIterator[] getTextChunkACIs
        (AttributedCharacterIterator aci) {

        List aciList = new ArrayList();
        int chunkStartIndex = aci.getBeginIndex();
        while (aci.setIndex(chunkStartIndex) != CharacterIterator.DONE) {
            TextPath prevTextPath = null;
            for (int start=chunkStartIndex, end=0; 
                 aci.setIndex(start) != CharacterIterator.DONE; start=end) {

                TextPath textPath = (TextPath) aci.getAttribute(TEXTPATH);

                if (start != chunkStartIndex) {
                    // If we aren't the first composite in a chunck see
                    // if we need to form a new TextChunk...
                    Float runX = (Float) aci.getAttribute(XPOS);
                    Float runY = (Float) aci.getAttribute(YPOS);

                    // Check if we have an absolute location
                    if (  ((runX != null) && !runX.isNaN())
                        ||((runY != null) && !runY.isNaN()))
                        break; // If so end of chunk...

                    // do additional check for the start of a textPath
                    if ((prevTextPath == null) && (textPath != null))
                        break;  // If so end of chunk.
                }

                prevTextPath = textPath;

                // We need to text chunk based on flow paragraphs.
                // This prevents BIDI reordering across paragraphs.
                if (aci.getAttribute(FLOW_PARAGRAPH) != null) {
                    end = aci.getRunLimit(FLOW_PARAGRAPH);
                    // System.out.println("End: " + end);
                    aci.setIndex(end);
                    break;
                }

                // find end of compound.
                end   = aci.getRunLimit(TEXT_COMPOUND_DELIMITER);

                if (start != chunkStartIndex)
                    // If we aren't starting a new chunk then we know
                    // we don't have any absolute positioning so there
                    // is no reason to consider spliting the chunk further.
                    continue;
                
                // We are starting a new chunk
                // So check if we need to split it further...
                TextNode.Anchor anchor;
                anchor = (TextNode.Anchor) aci.getAttribute(ANCHOR_TYPE);
                if (anchor == TextNode.Anchor.START)
                    continue;

                // We need to check if we have a list of X's & Y's if
                // so we need to create TextChunk ACI's for each char
                // (technically we have to do this for
                // text-anchor:start as well but since that is the
                // default layout it doesn't matter in that case.
                Float runX = (Float) aci.getAttribute(XPOS);
                Float runY = (Float) aci.getAttribute(YPOS);
                if (((runX == null) || runX.isNaN()) &&
                    ((runY == null) || runY.isNaN()))
                    // No absolute positioning in this compound so continue.
                    continue;

                // Splitting the compound into one char chunks until
                // we run out of Xs.
                for (int i=start+1; i< end; i++) {
                    aci.setIndex(i);
                    runX = (Float) aci.getAttribute(XPOS);
                    runY = (Float) aci.getAttribute(YPOS);
                    if (((runX == null) || runX.isNaN()) &&
                        ((runY == null) || runY.isNaN()))
                        break;
                    aciList.add 
                        (new AttributedCharacterSpanIterator(aci, i-1, i));
                    chunkStartIndex = i;
                }
            }

            // found the end of a text chunck
            int chunkEndIndex = aci.getIndex();
            // System.out.println("Bounds: " + chunkStartIndex + 
            //                    "," + chunkEndIndex);
            aciList.add(new AttributedCharacterSpanIterator
                (aci, chunkStartIndex, chunkEndIndex));

            chunkStartIndex = chunkEndIndex;
        }

        // copy the text chunks into an array
        AttributedCharacterIterator[] aciArray = 
            new AttributedCharacterIterator[aciList.size()];
        Iterator iter = aciList.iterator();
        for (int i=0; iter.hasNext(); ++i) {
            aciArray[i] = (AttributedCharacterIterator)iter.next();
        }
        return aciArray;
    }

    private TextChunk getTextChunk(TextNode node,
                                   AttributedCharacterIterator aci,
                                   int [] charMap,
                                   List textRuns,
                                   TextChunk prevChunk) {
        int beginChunk = 0;
        if (prevChunk != null)
            beginChunk = prevChunk.end;
        int endChunk = beginChunk;
        int begin = aci.getIndex();
        // System.out.println("New Chunk");
        if (aci.current() == CharacterIterator.DONE) 
            return null;

        Point2D advance = new Point2D.Float(0,0);
        Point2D location;
        if (prevChunk == null) {
            location = node.getLocation();
        } else {
            location = new Point2D.Float
                ((float)(prevChunk.absLoc.getX()+prevChunk.advance.getX()),
                 (float)(prevChunk.absLoc.getY()+prevChunk.advance.getY()));
        }
        boolean  isChunkStart        = true;
        TextPath prevTextPath        = null;
        Point2D  prevTextPathAdvance = null;
        do {
            int start = aci.getRunStart(extendedAtts);
            int end   = aci.getRunLimit(extendedAtts);

            AttributedCharacterIterator runaci;
            runaci = new AttributedCharacterSpanIterator(aci, start, end);
            runaci.first();

            boolean vertical = 
                (aci.getAttribute(WRITING_MODE) == WRITING_MODE_TTB);
            Float runX = (Float) runaci.getAttribute(XPOS);
            Float runY = (Float) runaci.getAttribute(YPOS);

            TextPath textPath =  (TextPath) runaci.getAttribute(TEXTPATH);
		
            Point2D.Float offset;
            if (textPath == null) {
                if ((prevTextPath != null) && 
                    (prevTextPathAdvance != null)) {
                    // this text is directly after some text on a path
                    offset = new Point2D.Float
                        ((float)prevTextPathAdvance.getX(),
                         (float)prevTextPathAdvance.getY());
                } else {
                    offset = new Point2D.Float
                        ((float) (location.getX()+advance.getX()),
                         (float) (location.getY()+advance.getY()));
                }

                // Of course X and Y override all that...
                if ((runX != null) && (!runX.isNaN()))
                    offset.x = runX.floatValue();

                if ((runY != null) && (!runY.isNaN()))
                    offset.y = runY.floatValue();
            } else {
                // is on a text path so ignore the text node's location
                offset = new Point2D.Float((float)advance.getX(),
                                           (float)advance.getY());
                // Only use the x or y in writing direction...
                if (vertical) {
                    if ((runY != null) && (!runY.isNaN()))
                        offset.y = runY.floatValue();
                } else {
                    if ((runX != null) && (!runX.isNaN()))
                        offset.x = runX.floatValue();
                }
            }
                    
            int [] subCharMap = new int[end-start];
            for (int i=0; i<subCharMap.length; i++) {
                subCharMap[i] = charMap[i+start-begin];
            }
            TextSpanLayout layout = getTextLayoutFactory().
                createTextLayout(runaci, subCharMap, 
                                 offset, fontRenderContext);
            // System.out.println("TextRun: " + start +  "->" + end + 
            //                    " Start: " + isChunkStart);
            TextRun run = new TextRun(layout, runaci, isChunkStart);
            textRuns.add(run);
            Point2D layoutAdvance = layout.getAdvance2D();
            if (isChunkStart)
                location = layout.getOffset();

            // System.out.println("layoutAdv: " + layoutAdvance);

            advance = new Point2D.Float
                ((float) (advance.getX()+layoutAdvance.getX()),
                 (float) (advance.getY()+layoutAdvance.getY()));
            ++endChunk;
            prevTextPath = textPath;
            prevTextPathAdvance = layout.getTextPathAdvance();
            if (aci.setIndex(end) == CharacterIterator.DONE) break;
            isChunkStart = false;
        } while (true);
        // System.out.println("Adv: " + advance);
        return new TextChunk(beginChunk, endChunk, location, advance);
    }



    /**
     * Returns a new AttributedCharacterIterator that contains resolved GVTFont
     * attributes. This is then used when creating the text runs so that the
     * text can be split on changes of font as well as tspans and trefs.
     *
     * @param node The text node that the aci belongs to.
     * @param aci The aci to be modified should already be split into
     *            text chunks.
     *
     * @return The new modified aci.  
     */
    private AttributedCharacterIterator createModifiedACIForFontMatching
        (TextNode node, AttributedCharacterIterator aci) {

        aci.first();
        AttributedString as = null; 
        int asOff = 0;
        int begin = aci.getBeginIndex();
        boolean moreChunks = true;
        int start, end   = aci.getRunStart(TEXT_COMPOUND_DELIMITER);
        while (moreChunks) {
            start = end;
            end = aci.getRunLimit(TEXT_COMPOUND_DELIMITER);
            int aciLength = end-start;

            Vector fontFamilies;
            fontFamilies = (Vector)aci.getAttributes().get(GVT_FONT_FAMILIES);
            if (fontFamilies == null) {
                // no font families set this chunk so just increment...
                asOff += aciLength;
                moreChunks = (aci.setIndex(end) != aci.DONE);
                continue;
            }

            // resolve any unresolved font families in the list
            List resolvedFontFamilies = new ArrayList(fontFamilies.size());
            for (int i = 0; i < fontFamilies.size(); i++) {
                GVTFontFamily fontFamily = (GVTFontFamily)fontFamilies.get(i);
                if (fontFamily instanceof UnresolvedFontFamily) {
                    fontFamily = FontFamilyResolver.resolve
                        ((UnresolvedFontFamily)fontFamily);
                }
                if (fontFamily != null) // Add font family if resolved
                    resolvedFontFamilies.add(fontFamily);
            }

            // if could not resolve at least one of the fontFamilies
            // then use the default font
            if (resolvedFontFamilies.size() == 0) {
                resolvedFontFamilies.add(FontFamilyResolver.defaultFont);
            }

            // create a list of fonts of the correct size
            float fontSize = 12;
            Float fsFloat = (Float)aci.getAttributes().get(TextAttribute.SIZE);
            if (fsFloat != null) {
                fontSize = fsFloat.floatValue();
            }

            // now for each char or group of chars in the string,
            // find a font that can display it.
            boolean[] fontAssigned = new boolean[aciLength];

            if (as == null)
                as = new AttributedString(aci);

            GVTFont defaultFont = null;;
            int numSet=0;
            int firstUnset=start;
            boolean firstUnsetSet;
            for (int i = 0; i < resolvedFontFamilies.size(); i++) {
                // assign this font to all characters it can display if it has
                // not already been assigned
                int currentIndex = firstUnset;
                firstUnsetSet = false;
                aci.setIndex(currentIndex);

                GVTFontFamily ff;
                ff = ((GVTFontFamily)resolvedFontFamilies.get(i));
                GVTFont font = ff.deriveFont(fontSize, aci);
                if (defaultFont == null)
                    defaultFont = font;

                while (currentIndex < end) {
                    int displayUpToIndex = font.canDisplayUpTo
                        (aci, currentIndex, end);

                    if (displayUpToIndex == -1) {
                        // Can handle the whole thing...
                        displayUpToIndex = end;
                    }

                    if (displayUpToIndex <= currentIndex) {
                        if (!firstUnsetSet) {
                            firstUnset = currentIndex;
                            firstUnsetSet = true;
                        }
                        // couldn't display the current char
                        currentIndex++;
                    } else {
                        // could display some text, so for each
                        // char it can display, if char not already
                        // assigned a font, assign this font to it
                        int runStart = -1;
                        for (int j = currentIndex; j < displayUpToIndex; j++) {
                            if (fontAssigned[j - start]) {
                                if (runStart != -1) {
				    // System.out.println("Font 1: " + font);
                                    as.addAttribute(GVT_FONT, font, 
                                                    runStart-begin, j-begin);
                                    runStart=-1;
                                }
                            } else {
                                if (runStart == -1)
                                    runStart = j;
                            }
                            fontAssigned[j - start] = true;
                            numSet++;
                        }
                        if (runStart != -1) {
			    // System.out.println("Font 2: " + font);
                            as.addAttribute(GVT_FONT, font, 
                                            runStart-begin, 
                                            displayUpToIndex-begin);
                        }

                        // set currentIndex to be one after the char
                        // that couldn't display
                        currentIndex = displayUpToIndex+1;
                    }
                }

                if (numSet == aciLength) // all chars have font set;
                    break;
            }

            // assign the first font to any chars haven't alreay been assigned
            int           runStart = -1;
            GVTFontFamily prevFF   = null;
            GVTFont       prevF    = defaultFont;
            for (int i = 0; i < aciLength; i++) {
                if (fontAssigned[i]) {
                    if (runStart != -1) {
			// System.out.println("Font 3: " + prevF);
                        as.addAttribute(GVT_FONT, prevF, 
                                        runStart+asOff, i+asOff);
                        runStart = -1;
                        prevF  = null;
                        prevFF = null;
                    }
                } else {
                    char c = aci.setIndex(start+i);
                    GVTFontFamily fontFamily;
                    fontFamily = FontFamilyResolver.getFamilyThatCanDisplay(c);

                    if (runStart == -1) {
                        // Starting a new run...
                        runStart = i;
                        prevFF   = fontFamily;
                        if (prevFF == null)
                            prevF = defaultFont;
                        else
                            prevF = fontFamily.deriveFont(fontSize, aci);
                    } else if (prevFF != fontFamily) {
                        // Font family changed...
			// System.out.println("Font 4: " + prevF);
                        as.addAttribute(GVT_FONT, prevF, 
                                        runStart+asOff, i+asOff);
                    
                        runStart = i;
                        prevFF = fontFamily;
                        if (prevFF == null)
                            prevF = defaultFont;
                        else
                            prevF = fontFamily.deriveFont(fontSize, aci);
                    }
                }
            }
            if (runStart != -1) {
		// System.out.println("Font 5: " + prevF);
                as.addAttribute(GVT_FONT, prevF, 
                                runStart+asOff, aciLength+asOff);
	    }

            asOff += aciLength;
            if (aci.setIndex(end) == aci.DONE) {
                moreChunks = false;
            }
            start = end;
        }
        if (as != null)
            return as.getIterator();

        // Didn't do anything return original ACI
        return aci;
    }



    /**
     * Adjusts the position of the text runs within the specified text chunk
     * to account for any text anchor properties.
     */
    private void adjustChunkOffsets(List textRuns, 
                                    TextChunk chunk) {
        TextRun r          = (TextRun) textRuns.get(chunk.begin);
        int     anchorType = r.getAnchorType();
        Float   length     = r.getLength();
        Integer lengthAdj  = r.getLengthAdjust();
        Point2D advance    = chunk.advance;

        boolean doAdjust = true;
        if ((length == null) || length.isNaN())
            doAdjust = false;
        
        int numChars = 0;
        for (int n=chunk.begin; n<chunk.end; ++n) {
            r = (TextRun) textRuns.get(n);
            AttributedCharacterIterator aci = r.getACI();
            numChars += aci.getEndIndex()-aci.getBeginIndex();
        }
        if ((lengthAdj == 
             GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING) &&
            (numChars == 1)) 
            doAdjust = false;

        float xScale = 1;
        float yScale = 1;
        if (doAdjust) {
            // We have to do this here since textLength needs to be
            // handled at the text chunk level. Otherwise tspans get
            // messed up.
            float delta = 0;
            r = (TextRun)textRuns.get(chunk.end-1);
            TextSpanLayout  layout          = r.getLayout();
            GVTGlyphMetrics lastMetrics = 
                layout.getGlyphMetrics(layout.getGlyphCount()-1);
            Rectangle2D     lastBounds  = lastMetrics.getBounds2D();

            if (layout.isVertical()) {
                if (lengthAdj == ADJUST_SPACING) {
                    yScale = (float)
                        ((length.floatValue()-lastBounds.getHeight())/
                         (advance.getY()-lastMetrics.getVerticalAdvance()));
                } else {
                    double adv = (advance.getY()-
                                  lastMetrics.getVerticalAdvance() +
                                  lastBounds.getHeight());
                    xScale = (float)(length.floatValue()/adv);
                }
            } else {
                if (lengthAdj == ADJUST_SPACING) {
                    xScale = (float)
                        ((length.floatValue()-lastBounds.getWidth())/
                         (advance.getX()-lastMetrics.getHorizontalAdvance()));
                } else {
                    double adv = (advance.getX()-
                                  lastMetrics.getHorizontalAdvance() +
                                  lastBounds.getWidth());
                    xScale = (float)(length.floatValue()/adv);
                }
            }

            // System.out.println("Adv: " + advance + " Len: " + length +
            //                    " scale: [" + xScale + ", " + yScale + "]");
            Point2D.Float adv = new Point2D.Float(0,0);
            for (int n=chunk.begin; n<chunk.end; ++n) {
                r = (TextRun) textRuns.get(n);
                layout = r.getLayout();
                layout.setScale(xScale, yScale, lengthAdj==ADJUST_SPACING);
                Point2D lAdv = layout.getAdvance2D();
                adv.x += lAdv.getX();
                adv.y += lAdv.getY();
            }
            chunk.advance = adv;
        }

        advance = chunk.advance;

        float dx = 0f;
        float dy = 0f;
        switch(anchorType){
        case TextNode.Anchor.ANCHOR_MIDDLE:
            dx = (float) (-advance.getX()/2d);
            dy = (float) (-advance.getY()/2d);
            break;
        case TextNode.Anchor.ANCHOR_END:
            dx = (float) (-advance.getX());
            dy = (float) (-advance.getY());
            break;
        default:
            break;
            // leave untouched
        }

        
        r = (TextRun) textRuns.get(chunk.begin);
            TextSpanLayout layout = r.getLayout();
            Point2D        offset = layout.getOffset();
        float initX = (float)offset.getX();
        float initY = (float)offset.getY();

        for (int n=chunk.begin; n<chunk.end; ++n) {
            r = (TextRun) textRuns.get(n);
            layout = r.getLayout();
            offset = layout.getOffset();
            if (layout.isVertical()) {
                float adj = (float)((offset.getY()-initY)*yScale);
                offset = new Point2D.Float((float) offset.getX(),
                                           (float)initY+adj+dy);
            } else {
                float adj = (float)((offset.getX()-initX)*xScale);
                offset = new Point2D.Float((float)initX+adj+dx,
                                           (float) offset.getY());
            }
            layout.setOffset(offset);
        }
    }

    /**
     * Paints decorations of the specified type.
     */
    private void paintDecorations(List textRuns, 
                                  Graphics2D g2d,
                                  int decorationType) {

        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        Rectangle2D decorationRect = null;

        for (int i = 0; i < textRuns.size(); i++) {
            TextRun textRun = (TextRun)textRuns.get(i);
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();

            Composite opacity = (Composite)
                  runaci.getAttribute(GVTAttributedCharacterIterator.
                                              TextAttribute.OPACITY);
            if (opacity != null) {
                g2d.setComposite(opacity);
            }

            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            switch (decorationType) {
                case TextSpanLayout.DECORATION_UNDERLINE :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE_PAINT);
                    break;
                case TextSpanLayout.DECORATION_OVERLINE :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_STROKE_PAINT);
                    break;
                case TextSpanLayout.DECORATION_STRIKETHROUGH :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_STROKE_PAINT);
                    break;
                default:
                    // should never get here
                    return;
            }

            if (textRun.isFirstRunInChunk() || paint != prevPaint
                || stroke != prevStroke || strokePaint != prevStrokePaint) {

                // if there is a current decoration, draw it now
                if (decorationRect != null) {

                    if (prevPaint != null) {
                        // fill the decoration
                        g2d.setPaint(prevPaint);
                        g2d.fill(decorationRect);
                    }
                    if (prevStroke != null && prevStrokePaint != null) {
                        // stroke the decoration
                        g2d.setPaint(prevStrokePaint);
                        g2d.setStroke(prevStroke);
                        g2d.draw(decorationRect);
                    }
                    decorationRect = null;
                }
            }

            if ((paint != null || strokePaint != null)
                && !textRun.getLayout().isVertical()
                && !textRun.getLayout().isOnATextPath()) {

                // this text run should be decorated with the specified decoration type
                // note: decorations are only supported for plain horizontal layouts

                Shape decorationShape = textRun.getLayout().getDecorationOutline(decorationType);
                if (decorationRect == null) {
                    // create a new one
                    decorationRect = decorationShape.getBounds2D();
                } else {
                    // extend the current one
                    Rectangle2D bounds = decorationShape.getBounds2D();
                    decorationRect.setRect(decorationRect.getMinX(), decorationRect.getMinY(),
                        bounds.getMaxX() - decorationRect.getMinX(), decorationRect.getHeight());
                }
            }
            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
        }

        // if there is a decoration rect that hasn't been drawn yet, draw it now

        if (decorationRect != null) {

            if (prevPaint != null) {
                // fill the decoration
                g2d.setPaint(prevPaint);
                g2d.fill(decorationRect);
            }
            if (prevStroke != null && prevStrokePaint != null) {
                // stroke the decoration
                g2d.setPaint(prevStrokePaint);
                g2d.setStroke(prevStroke);
                g2d.draw(decorationRect);
            }
        }
    }


    /**
     * Paints the text in each text run. Decorations are not painted here.
     */
    private void paintTextRuns(List textRuns, 
                               Graphics2D g2d) {

        for (int i = 0; i < textRuns.size(); i++) {
            TextRun textRun = (TextRun)textRuns.get(i);
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();

            Composite opacity = (Composite)
                  runaci.getAttribute(GVTAttributedCharacterIterator.
                                              TextAttribute.OPACITY);
            if (opacity != null) {
                g2d.setComposite(opacity);
            }
            textRun.getLayout().draw(g2d);
        }
    }


    /**
     * Get a Rectangle2D in userspace coords which encloses the textnode
     * glyphs composed from an AttributedCharacterIterator.
     */
     protected Rectangle2D getBounds(TextNode node,
				     boolean includeDecoration,
				     boolean includeStrokeWidth) {

         Rectangle2D bounds = getOutline(node, includeDecoration).getBounds2D();

         if (includeStrokeWidth) {
             Shape strokeOutline = getStrokeOutline(node, includeDecoration);

             if (strokeOutline != null) {
                bounds = bounds.createUnion(strokeOutline.getBounds2D());
             }
         }

        return bounds;
     }

    /**
     * Get a Shape in userspace coords which defines the textnode glyph outlines.
     * @param node the TextNode to measure
     * @param includeDecoration whether to include text decoration
     *            outlines.
     */
    protected Shape getOutline(TextNode node, boolean includeDecoration) {

        GeneralPath outline = null;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();

        // get the list of text runs
        List textRuns = getTextRuns(node, aci);

        // for each text run, get its outline and append it to the overall
        // outline

        for (int i = 0; i < textRuns.size(); ++i) {
            TextRun textRun = (TextRun)textRuns.get(i);
            TextSpanLayout textRunLayout = textRun.getLayout();
            GeneralPath textRunOutline = 
		new GeneralPath(textRunLayout.getOutline());

            if (outline == null) {
               outline = textRunOutline;
            } else {
                outline.setWindingRule(GeneralPath.WIND_NON_ZERO);
                outline.append(textRunOutline, false);
            }
        }

        // append any decoration outlines
        if (includeDecoration) {

            Shape underline = getDecorationOutline
		(textRuns, TextSpanLayout.DECORATION_UNDERLINE);

            Shape strikeThrough = getDecorationOutline
		(textRuns, TextSpanLayout.DECORATION_STRIKETHROUGH);

            Shape overline = getDecorationOutline
		(textRuns, TextSpanLayout.DECORATION_OVERLINE);

            if (underline != null) {
                if (outline == null) {
                    outline = new GeneralPath(underline);
                } else {
                    outline.setWindingRule(GeneralPath.WIND_NON_ZERO);
                    outline.append(underline, false);
                }
            }
            if (strikeThrough != null) {
                 if (outline == null) {
                    outline = new GeneralPath(strikeThrough);
                } else {
                    outline.setWindingRule(GeneralPath.WIND_NON_ZERO);
                    outline.append(strikeThrough, false);
                }
            }
            if (overline != null) {
                if (outline == null) {
                    outline = new GeneralPath(overline);
                } else {
                    outline.setWindingRule(GeneralPath.WIND_NON_ZERO);
                    outline.append(overline, false);
                }
            }
        }

        return outline;
    }

   /**
    * Get a Shape in userspace coords which defines the
    * stroked textnode glyph outlines.
    * @param node the TextNode to measure
    * @param includeDecoration whether to include text decoration
    *            outlines.
    */
    protected Shape getStrokeOutline(TextNode node, boolean includeDecoration) {

        GeneralPath outline = null;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();

        // get the list of text runs
        List textRuns = getTextRuns(node, aci);

        // for each text run, get its stroke outline and append it to the overall outline
        for (int i = 0; i < textRuns.size(); ++i) {
            Shape textRunStrokeOutline = null;

            TextRun textRun = (TextRun)textRuns.get(i);
            AttributedCharacterIterator textRunACI = textRun.getACI();
            textRunACI.first();

            TextSpanLayout textRunLayout = textRun.getLayout();

            Stroke stroke = (Stroke) textRunACI.getAttribute
		(GVTAttributedCharacterIterator.TextAttribute.STROKE);

            Paint strokePaint = (Paint) textRunACI.getAttribute
		(GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);

            if (stroke != null && strokePaint != null) {
                // this textRun is stroked
                Shape textRunOutline = 
		    textRunLayout.getOutline();
                textRunStrokeOutline = 
		    stroke.createStrokedShape(textRunOutline);
            }

            if (textRunStrokeOutline != null) {
                if (outline == null) {
                    outline = new GeneralPath(textRunStrokeOutline);
                } else {
                    outline.setWindingRule(GeneralPath.WIND_NON_ZERO);
                    outline.append(textRunStrokeOutline, false);
                }
            }
        }

        // append any stroked decoration outlines
        if (includeDecoration) {

            Shape underline = getDecorationStrokeOutline
		(textRuns, TextSpanLayout.DECORATION_UNDERLINE);

            Shape strikeThrough = getDecorationStrokeOutline
		(textRuns, TextSpanLayout.DECORATION_STRIKETHROUGH);

            Shape overline = getDecorationStrokeOutline
		(textRuns, TextSpanLayout.DECORATION_OVERLINE);

            if (underline != null) {
                if (outline == null) {
                    outline = new GeneralPath(underline);
                } else {
                    outline.setWindingRule(GeneralPath.WIND_NON_ZERO);
                    outline.append(underline, false);
                }
            }
            if (strikeThrough != null) {
                 if (outline == null) {
                    outline = new GeneralPath(strikeThrough);
                } else {
                    outline.setWindingRule(GeneralPath.WIND_NON_ZERO);
                    outline.append(strikeThrough, false);
                }
            }
            if (overline != null) {
                if (outline == null) {
                    outline = new GeneralPath(overline);
                } else {
                    outline.setWindingRule(GeneralPath.WIND_NON_ZERO);
                    outline.append(overline, false);
                }
            }
        }

        return outline;
    }


    /**
     * Returns the outline of the specified decoration type.
     *
     * @param textRuns The list of text runs to get the decoration outline for.
     * @param decoratonType Indicates the type of decoration required.
     * eg. underline, overline or strikethrough.
     *
     * @return The decoration outline or null if the text is not decorated.
     */
    private Shape getDecorationOutline(List textRuns, int decorationType) {

        GeneralPath outline = null;

        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        Rectangle2D decorationRect = null;

        for (int i = 0; i < textRuns.size(); i++) {
            TextRun textRun = (TextRun)textRuns.get(i);
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();

            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            switch (decorationType) {
                case TextSpanLayout.DECORATION_UNDERLINE :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE_PAINT);
                    break;
                case TextSpanLayout.DECORATION_OVERLINE :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_STROKE_PAINT);
                    break;
                case TextSpanLayout.DECORATION_STRIKETHROUGH :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_STROKE_PAINT);
                    break;
                default:
                    // should never get here
                    return null;
            }

            if (textRun.isFirstRunInChunk() || 
		paint != prevPaint || 
		stroke != prevStroke || 
		strokePaint != prevStrokePaint) {

                // if there is a current decoration, added it to the overall
                // outline
                if (decorationRect != null) {
                    if (outline == null) {
                        outline = new GeneralPath(decorationRect);
                    } else {
                        outline.append(decorationRect, false);
                    }
                    decorationRect = null;
                }
            }

            if ((paint != null || strokePaint != null)
                && !textRun.getLayout().isVertical()
                && !textRun.getLayout().isOnATextPath()) {

                // this text run should be decorated with the specified
                // decoration type note: decorations are only supported for
                // plain horizontal layouts

                Shape decorationShape = 
		    textRun.getLayout().getDecorationOutline(decorationType);

                if (decorationRect == null) {
                    // create a new one
                    decorationRect = decorationShape.getBounds2D();
                } else {
                    // extend the current one
                    Rectangle2D bounds = decorationShape.getBounds2D();
                    decorationRect.setRect
			(decorationRect.getMinX(), 
			 decorationRect.getMinY(),
			 bounds.getMaxX() - decorationRect.getMinX(), 
			 decorationRect.getHeight());
                }
            }

            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
        }

        // if there is a decoration rect that hasn't been added to the overall outline
        if (decorationRect != null) {
            if (outline == null) {
                outline = new GeneralPath(decorationRect);
            } else {
                outline.append(decorationRect, false);
            }
        }

        return outline;
    }

    /**
     * Returns the stroke outline of the specified decoration type.
     *
     * @param textRuns The list of text runs to get the decoration outline for.
     * @param decoratonType Indicates the type of decoration required.
     * eg. underline, overline or strikethrough.
     *
     * @return The decoration outline or null if the text is not decorated.
     */
    private Shape getDecorationStrokeOutline
	(List textRuns, int decorationType) {

        GeneralPath outline = null;

        Paint prevPaint = null;
        Paint prevStrokePaint = null;
        Stroke prevStroke = null;
        Rectangle2D decorationRect = null;

        for (int i = 0; i < textRuns.size(); i++) {

            TextRun textRun = (TextRun)textRuns.get(i);
            AttributedCharacterIterator runaci = textRun.getACI();
            runaci.first();

            Paint paint = null;
            Stroke stroke = null;
            Paint strokePaint = null;
            switch (decorationType) {
                case TextSpanLayout.DECORATION_UNDERLINE :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE_PAINT);
                    break;
                case TextSpanLayout.DECORATION_OVERLINE :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.OVERLINE_STROKE_PAINT);
                    break;
                case TextSpanLayout.DECORATION_STRIKETHROUGH :
                    paint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_PAINT);
                    stroke = (Stroke) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_STROKE);
                    strokePaint = (Paint) runaci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_STROKE_PAINT);
                    break;
                default:
                    // should never get here
                    return null;
            }

            if (textRun.isFirstRunInChunk() || 
		paint != prevPaint || 
		stroke != prevStroke || 
		strokePaint != prevStrokePaint) {

                // if there is a current decoration, added it to the overall
                // outline
                if (decorationRect != null  && 
		    prevStroke != null && 
		    prevStrokePaint != null) {

                    if (outline == null) {
                        outline = new GeneralPath
			    (prevStroke.createStrokedShape(decorationRect));
                    } else {
                        outline.append(prevStroke.createStrokedShape
				       (decorationRect), false);
                    }
                    decorationRect = null;
                }
            }
	    
            if ((paint != null || strokePaint != null)
                && !textRun.getLayout().isVertical()
                && !textRun.getLayout().isOnATextPath()) {

                // this text run should be decorated with the specified
                // decoration type note: decorations are only supported for
                // plain horizontal layouts

                Shape decorationShape = 
		    textRun.getLayout().getDecorationOutline(decorationType);

                if (decorationRect == null) {
                    // create a new one
                    decorationRect = decorationShape.getBounds2D();
                } else {
                    // extend the current one
                    Rectangle2D bounds = decorationShape.getBounds2D();
                    decorationRect.setRect
			(decorationRect.getMinX(), 
			 decorationRect.getMinY(),
			 bounds.getMaxX() - decorationRect.getMinX(), 
			 decorationRect.getHeight());
                }
            }

            prevPaint = paint;
            prevStroke = stroke;
            prevStrokePaint = strokePaint;
        }

        // if there is a decoration rect that hasn't been added to the overall
        // outline
        if (decorationRect != null && 
	    prevStroke != null && 
	    prevStrokePaint != null) {

	    if (outline == null) {
                outline = new GeneralPath(prevStroke.createStrokedShape
					  (decorationRect));
            } else {
                outline.append
		    (prevStroke.createStrokedShape(decorationRect), false);
            }
        }

        return outline;
    }


    public Mark getMark(TextNode node, int index, boolean leadingEdge) {
        AttributedCharacterIterator aci;
        aci = node.getAttributedCharacterIterator();
        if ((index < aci.getBeginIndex()) ||
            (index > aci.getEndIndex()))
            return null;

        TextHit textHit = new TextHit(index, leadingEdge);
        return new BasicTextPainter.BasicMark(node, textHit);
    }

    protected Mark hitTest(double x, double y, TextNode node) {
        AttributedCharacterIterator aci;
        aci = node.getAttributedCharacterIterator();
                           
        // get the list of text runs
        List textRuns = getTextRuns(node, aci);

        // for each text run, see if its been hit
        for (int i = 0; i < textRuns.size(); ++i) {
            TextRun textRun = (TextRun)textRuns.get(i);
            TextSpanLayout layout = textRun.getLayout();
            TextHit textHit = layout.hitTestChar((float) x, (float) y);
            if (textHit != null && layout.getBounds().contains(x,y)) {
                return new BasicTextPainter.BasicMark(node, textHit);
            }
        }

        return null;
    }

    /**
     * Selects the first glyph in the text node.
     */
    public Mark selectFirst(TextNode node) {
        AttributedCharacterIterator aci;
        aci = node.getAttributedCharacterIterator();
        TextHit textHit = new TextHit(aci.getBeginIndex(), false);
        return new BasicTextPainter.BasicMark(node, textHit);
    }

    /**
     * Selects the last glyph in the text node.
     */
    public Mark selectLast(TextNode node) {
        AttributedCharacterIterator aci;
        aci = node.getAttributedCharacterIterator();
        TextHit textHit = new TextHit(aci.getEndIndex(), false);
        return  new BasicTextPainter.BasicMark(node, textHit);
    }

    /**
     * Returns an array of ints representing begin/end index pairs into
     * an AttributedCharacterIterator which represents the text
     * selection delineated by two Mark instances.
     * <em>Note: The Mark instances passed must have been instantiated by
     * an instance of this enclosing TextPainter implementation.</em>
     */
    public int[] getSelected(Mark startMark,
                             Mark finishMark) {

        if (startMark == null || finishMark == null) {
            return null;
        }
        BasicTextPainter.BasicMark start;
        BasicTextPainter.BasicMark finish;
        try {
            start = (BasicTextPainter.BasicMark) startMark;
            finish = (BasicTextPainter.BasicMark) finishMark;
        } catch (ClassCastException cce) {
            throw new
            Error("This Mark was not instantiated by this TextPainter class!");
        }

        TextNode textNode = start.getTextNode();
        if (textNode != finish.getTextNode()) 
            throw new Error("Markers are from different TextNodes!");

        AttributedCharacterIterator aci;
        aci = textNode.getAttributedCharacterIterator();
                             
        int[] result = new int[2];
        result[0] = start.getHit().getCharIndex();
        result[1] = finish.getHit().getCharIndex();

        // get the list of text runs
        List textRuns = getTextRuns(textNode, aci);
        Iterator trI = textRuns.iterator();
        int startGlyphIndex = -1;
        int endGlyphIndex = -1;
        TextSpanLayout startLayout=null, endLayout=null;
        while (trI.hasNext()) {
            TextRun tr = (TextRun)trI.next();
            TextSpanLayout tsl = tr.getLayout();
            if (startGlyphIndex == -1) {
                startGlyphIndex  = tsl.getGlyphIndex(result[0]);
                if (startGlyphIndex != -1)
                    startLayout = tsl;
            }
                
            if (endGlyphIndex == -1) {
                endGlyphIndex = tsl.getGlyphIndex(result[1]);
                if (endGlyphIndex != -1)
                    endLayout = tsl;
            }
            if ((startGlyphIndex != -1) && (endGlyphIndex != -1))
                break;
        }
        if ((startLayout == null) || (endLayout == null))
            return null;
                
        int startCharCount = startLayout.getCharacterCount
            (startGlyphIndex, startGlyphIndex);
        int endCharCount = endLayout.getCharacterCount
            (endGlyphIndex, endGlyphIndex);
        if (startCharCount > 1) {
            if (result[0] > result[1] && startLayout.isLeftToRight()) {
                result[0] += startCharCount-1;
            } else if (result[1] > result[0] && !startLayout.isLeftToRight()) {
                result[0] -= startCharCount-1;
            }
        }
        if (endCharCount > 1) {
            if (result[1] > result[0] && endLayout.isLeftToRight()) {
                result[1] += endCharCount-1;
            } else if (result[0] > result[1] && !endLayout.isLeftToRight()) {
                result[1] -= endCharCount-1;
            }
        }

        return result;
    }

   /**
     * Return a Shape, in the coordinate system of the text layout,
     * which encloses the text selection delineated by two Mark instances.
     * <em>Note: The Mark instances passed must have been instantiated by
     * an instance of this enclosing TextPainter implementation.</em>
     */
    public Shape getHighlightShape(Mark beginMark, Mark endMark) {

        if (beginMark == null || endMark == null) {
            return null;
        }

        BasicTextPainter.BasicMark begin;
        BasicTextPainter.BasicMark end;
        try {
            begin = (BasicTextPainter.BasicMark) beginMark;
            end = (BasicTextPainter.BasicMark) endMark;
        } catch (ClassCastException cce) {
            throw new Error
                ("This Mark was not instantiated by this TextPainter class!");
        }

        TextNode textNode = begin.getTextNode();
        if (textNode != end.getTextNode()) 
            throw new Error("Markers are from different TextNodes!");
        if (textNode == null)
            return null;

        int beginIndex = begin.getHit().getCharIndex();
        int endIndex   = end.getHit().getCharIndex();
        if (beginIndex > endIndex) {
            // Swap them...
            BasicTextPainter.BasicMark tmpMark = begin;
            begin = end; end = tmpMark;

            int tmpIndex = beginIndex;
            beginIndex = endIndex; endIndex = tmpIndex;
        }

        // get the list of text runs
        List textRuns = getTextRuns
            (textNode, textNode.getAttributedCharacterIterator());

        GeneralPath highlightedShape = new GeneralPath();

        // for each text run, append any highlight it may contain for
        // the current selection
        for (int i = 0; i < textRuns.size(); ++i) {
            TextRun textRun = (TextRun)textRuns.get(i);
            TextSpanLayout layout = textRun.getLayout();

            Shape layoutHighlightedShape = layout.getHighlightShape
                (beginIndex, endIndex);

            // append the highlighted shape of this layout to the
            // overall hightlighted shape
            if (( layoutHighlightedShape != null) && 
                (!layoutHighlightedShape.getBounds().isEmpty())) {
                highlightedShape.append(layoutHighlightedShape, false);
            }
        }
        return highlightedShape;
    }

// inner classes

    class TextChunk {

        public int begin;
        public int end;
        public Point2D advance;
        public Point2D absLoc;

        public TextChunk(int begin, int end, 
                         Point2D absLoc, Point2D advance) {
            this.begin = begin;
            this.end = end;
            this.absLoc  = new Point2D.Float((float) absLoc.getX(),
                                             (float) absLoc.getY());
            this.advance = new Point2D.Float((float) advance.getX(),
                                             (float) advance.getY());
        }
    }


    /**
     * Inner convenience class for associating a TextLayout for
     * sub-spans, and the ACI which iterates over that subspan.
     */
    public class TextRun {

        private AttributedCharacterIterator aci;
        private TextSpanLayout layout;
        private int anchorType;
        private boolean firstRunInChunk;
        private Float length;
        private Integer lengthAdjust;

        public TextRun(TextSpanLayout layout, 
		       AttributedCharacterIterator aci, 
		       boolean firstRunInChunk) {

            this.layout = layout;
            this.aci = aci;
            this.aci.first();
            this.firstRunInChunk = firstRunInChunk;
            this.anchorType = TextNode.Anchor.ANCHOR_START;

            TextNode.Anchor anchor = (TextNode.Anchor) aci.getAttribute
		(GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE);
            if (anchor != null) {
                this.anchorType = anchor.getType();
            }

            // if writing mode is right to left, then need to reverse the
            // text anchor positions
            if (aci.getAttribute(WRITING_MODE) == WRITING_MODE_RTL) {
                if (anchorType == TextNode.Anchor.ANCHOR_START) {
                    anchorType = TextNode.Anchor.ANCHOR_END;
                } else if (anchorType == TextNode.Anchor.ANCHOR_END) {
                    anchorType = TextNode.Anchor.ANCHOR_START;
                }
                // leave middle as is
            }

            length = (Float) aci.getAttribute
                (GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH);
            lengthAdjust = (Integer) aci.getAttribute
                (GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST);
        }

        public AttributedCharacterIterator getACI() {
            return aci;
        }

        public TextSpanLayout getLayout() {
            return layout;
        }

        public int getAnchorType() {
            return anchorType;
        }

        public Float getLength() {
            return length;
        }

        public Integer getLengthAdjust() {
            return lengthAdjust;
        }

        public boolean isFirstRunInChunk() {
            return firstRunInChunk;
        }

    }
}
