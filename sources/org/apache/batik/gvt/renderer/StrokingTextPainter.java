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
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import org.apache.batik.gvt.font.FontFamilyResolver;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.text.TextPath;
import org.apache.batik.gvt.text.BidiAttributedCharacterIterator;


/**
 * More sophisticated implementation of TextPainter which
 * renders the attributed character iterator of a <tt>TextNode</tt>.
 * <em>StrokingTextPainter includes support for stroke, fill, opacity,
 * text-decoration, and other attributes.</em>
 * @see org.apache.batik.gvt.TextPainter
 * @see org.apache.batik.gvt.text.GVTAttributedCharacterIterator
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class StrokingTextPainter extends BasicTextPainter {

    static Set extendedAtts = new HashSet();

    static {
        extendedAtts.add(GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
        extendedAtts.add(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT);
        extendedAtts.add(GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL);
    }

    /**
     * Paints the specified attributed character iterator using the
     * specified Graphics2D and rendering context.
     * Note that the GraphicsNodeRenderContext contains a TextPainter
     * reference.
     * @see org.apache.batik.gvt.TextPainter
     * @see org.apache.batik.gvt.GraphicsNodeRenderContext
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param context the rendering context.
     */
    public void paint(TextNode node, Graphics2D g2d,
                           GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();
        List textRuns = getTextRuns(node, aci, frc);

        // draw the underline and overline first, then the actual text
        // and finally the strikethrough
        paintDecorations(textRuns, g2d, TextSpanLayout.DECORATION_UNDERLINE);
        paintDecorations(textRuns, g2d, TextSpanLayout.DECORATION_OVERLINE);
        paintTextRuns(textRuns, g2d, context);
        paintDecorations(textRuns, g2d, TextSpanLayout.DECORATION_STRIKETHROUGH);
    }


    private List getTextRuns(TextNode node,
                             AttributedCharacterIterator aci,
                             FontRenderContext frc) {

        List textRuns = node.getTextRuns();
        if (textRuns != null) {
            return textRuns;
        }

        AttributedCharacterIterator[] chunkACIs = node.getChunkACIs();
        if (chunkACIs == null) {

            // add char position attributes to the aci
            // these will be needed later if any reordering is done
            AttributedString as = new AttributedString(aci);
            for (int i = aci.getBeginIndex(); i < aci.getEndIndex(); i++) {
                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.CHAR_INDEX, new Integer(i), i, i+1);
            }

            aci = as.getIterator();
            node.setAttributedCharacterIterator(aci);

            // add font attributed to the aci
            AttributedCharacterIterator fontaci = createModifiedACIForFontMatching(node, aci);
            fontaci.first();

            // break the aci up into text chunks
            chunkACIs = getTextChunkACIs(fontaci);

            // reorder each chunk ACI for bidi text
            for (int i = 0; i < chunkACIs.length; i++) {
                chunkACIs[i] = new BidiAttributedCharacterIterator(chunkACIs[i], frc);
            }
            node.setChunkACIs(chunkACIs);
        }

        // create text runs for each chunk and add them to the list
        textRuns = new ArrayList();
        TextChunk chunk;
        int beginChunk = 0;
        Point2D lastChunkAdvance = new Point2D.Float(0,0);
        int currentChunk = 0;
        do {
             /*
              * Text Chunks contain one or more TextRuns,
              * which they create from the ACI.
              */
            chunkACIs[currentChunk].first();
            chunk = getTextChunk(node, chunkACIs[currentChunk], textRuns,
                                 beginChunk, lastChunkAdvance, frc);

            /* Adjust according to text-anchor property value */

            if (chunk != null) {
                adjustChunkOffsets(textRuns, chunk.advance, chunk.begin, chunk.end);
                beginChunk = chunk.end;
                lastChunkAdvance = chunk.advance;
            }
            currentChunk++;

        } while (chunk != null && currentChunk < chunkACIs.length);
        return textRuns;
    }


    /**
     * Returns an array of ACIs, one for each text chunck within the given
     * text node.
     */
    private AttributedCharacterIterator[] getTextChunkACIs(AttributedCharacterIterator aci) {

        Vector aciVector = new Vector();
        aci.first();

        while (aci.current() != CharacterIterator.DONE) {

            int chunkStartIndex = aci.getIndex();
            boolean inChunk = true;
            boolean isChunkStart = true;
            TextPath prevTextPath = null;

            while (inChunk) {

                int start = aci.getRunStart(
                    GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
                int end = aci.getRunLimit(
                    GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);

                AttributedCharacterIterator runaci =
                    new AttributedCharacterSpanIterator(aci, start, end);

                Float runX = (Float) runaci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.X);
                Float runY = (Float) runaci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.Y);
                TextPath textPath = (TextPath) runaci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.TEXTPATH);

                inChunk = (isChunkStart)
                        || ((runX == null || runX.isNaN())
                          &&(runY == null || runY.isNaN()));

                // do additional check for the start of a textPath
                if (prevTextPath == null && textPath != null && !isChunkStart) {
                    inChunk = false;
                }

                if (inChunk) {
                    prevTextPath = textPath;
                    aci.setIndex(end);
                    if (aci.current() == CharacterIterator.DONE) break;
                } else {
                    aci.setIndex(start);
                }
                isChunkStart = false;
            }

            // found the end of a text chunck
            int chunkEndIndex = aci.getIndex();
            AttributedCharacterIterator chunkACI =
                    new AttributedCharacterSpanIterator(aci, chunkStartIndex, chunkEndIndex);
            aci.setIndex(chunkEndIndex);  // need to do this because creating the
                                          // new ACI above looses the current index
            aciVector.add(chunkACI);
        }

        AttributedCharacterIterator[] aciArray = new AttributedCharacterIterator[aciVector.size()];
        for (int i = 0; i < aciVector.size(); i++) {
            aciArray[i] = (AttributedCharacterIterator)aciVector.elementAt(i);
        }
        return aciArray;
    }

    private TextChunk getTextChunk(TextNode node,
                                   AttributedCharacterIterator aci,
                                   List textRuns,
                                   int beginChunk,
                                   Point2D lastChunkAdvance,
                                   FontRenderContext frc) {

        int endChunk = beginChunk;
        AttributedCharacterIterator runaci;
        boolean inChunk = true;
        Point2D advance = lastChunkAdvance;
        Point2D location = node.getLocation();
        if (aci.current() != CharacterIterator.DONE) {
            int chunkStartIndex = aci.getIndex();

            // find out if this chunck is the start or end of a text path chunck
            // if it is, then we ignore any previous advance
            TextPath chunkTextPath = (TextPath) aci.getAttribute(
                   GVTAttributedCharacterIterator.TextAttribute.TEXTPATH);
            TextPath prevChunkTextPath = null;
            if (chunkStartIndex > 0) {
                aci.setIndex(chunkStartIndex-1);
                prevChunkTextPath = (TextPath) aci.getAttribute(
                   GVTAttributedCharacterIterator.TextAttribute.TEXTPATH);
                aci.setIndex(chunkStartIndex);
            }
            if (prevChunkTextPath != chunkTextPath) {
                advance = new Point2D.Float(0,0);
            }

            boolean isChunkStart = true;
            TextPath prevTextPath = null;
            Point2D prevTextPathAdvance = null;
            do {

                int start = aci.getRunStart(extendedAtts);
                int end = aci.getRunLimit(extendedAtts);

                runaci =
                    new AttributedCharacterSpanIterator(aci, start, end);

                Float runX = (Float) runaci.getAttribute(
                     GVTAttributedCharacterIterator.TextAttribute.X);
                Float runY = (Float) runaci.getAttribute(
                     GVTAttributedCharacterIterator.TextAttribute.Y);

                TextPath textPath =  (TextPath) runaci.getAttribute(
                   GVTAttributedCharacterIterator.TextAttribute.TEXTPATH);

                inChunk = (isChunkStart)
                        || ((runX == null || runX.isNaN())
                          &&(runY == null || runY.isNaN()));

                // do additional check for the start/end of a textPath
                if (prevTextPath == null && textPath != null && !isChunkStart) {
                    inChunk = false;
                }

                if (inChunk) {

                    Point2D offset;
                    if (textPath == null) {
                        if (prevTextPath != null && prevTextPathAdvance != null) {
                            // this text is directly after some text on a path
                            offset = new Point2D.Float((float)prevTextPathAdvance.getX(),
                                                       (float)prevTextPathAdvance.getY());
                        } else {
                            offset = new Point2D.Float(
                                (float) (location.getX()+advance.getX()),
                                (float) (location.getY()+advance.getY()));
                        }
                    } else {
                        // is on a text path so ignore the text node's location
                        offset = new Point2D.Float((float)advance.getX(),
                                                   (float)advance.getY());
                    }
                    TextSpanLayout layout = getTextLayoutFactory().
                                       createTextLayout(runaci, offset, frc);
                    TextRun run = new TextRun(layout, runaci, isChunkStart);
                    textRuns.add(run);
                    Point2D layoutAdvance = layout.getAdvance2D();
                    advance = new Point2D.Float(
                       (float) (advance.getX()+layoutAdvance.getX()),
                       (float) (advance.getY()+layoutAdvance.getY()));
                    ++endChunk;
                    prevTextPath = textPath;
                    prevTextPathAdvance = layout.getTextPathAdvance();
                    if (aci.setIndex(end) == CharacterIterator.DONE) break;
                } else {
                    aci.setIndex(start);
                }
                isChunkStart = false;
            } while (inChunk);
            return new TextChunk(beginChunk, endChunk, advance);
        } else {
            return null;
        }
    }



    /**
     * Returns a new AttributedCharacterIterator that contains resolved GVTFont
     * attributes. This is then used when creating the text runs so that the
     * text can be split on changes of font as well as tspans and trefs.
     *
     * @param node The text node that the aci belongs to.
     * @param aci The aci to be modified.
     *
     * @return The new modified aci.
     */
    private AttributedCharacterIterator createModifiedACIForFontMatching(
                               TextNode node, AttributedCharacterIterator aci) {

        aci.first();
        AttributedCharacterSpanIterator acsi
            = new AttributedCharacterSpanIterator(aci, aci.getBeginIndex(), aci.getEndIndex());
        AttributedString as = new AttributedString(acsi);
        aci.first();

        boolean moreChunks = true;
        while (moreChunks) {
            int start = aci.getRunStart(
                GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
            int end = aci.getRunLimit(
                GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);

            AttributedCharacterSpanIterator runaci
                = new AttributedCharacterSpanIterator(aci, start, end);

            Vector fontFamilies = (Vector)runaci.getAttributes().get(
                GVTAttributedCharacterIterator.TextAttribute.GVT_FONT_FAMILIES);

            if (fontFamilies == null) {
                // no font families set, just return the same aci
                return aci;
            }

            // resolve any unresolved font families in the list
            Vector resolvedFontFamilies = new Vector();
            for (int i = 0; i < fontFamilies.size(); i++) {
                GVTFontFamily fontFamily = (GVTFontFamily) fontFamilies.get(i);
                if (fontFamily instanceof UnresolvedFontFamily) {
                    GVTFontFamily resolvedFontFamily
                        = FontFamilyResolver.resolve((UnresolvedFontFamily)fontFamily);
                    if (resolvedFontFamily != null) {
                        // font family was successfully resolved
                        resolvedFontFamilies.add(resolvedFontFamily);
                    }
                } else {
                    // already resolved
                    resolvedFontFamilies.add(fontFamily);
                }
            }
            // if could not resolve at least one of the fontFamilies then use
            // the default faont
            if (resolvedFontFamilies.size() == 0) {
                resolvedFontFamilies.add(FontFamilyResolver.defaultFont);
            }

            // create a list of fonts of the correct size
            Float fontSizeFloat = (Float)runaci.getAttributes().get(TextAttribute.SIZE);
            float fontSize = 12;
            if (fontSizeFloat != null) {
                fontSize = fontSizeFloat.floatValue();
            }
            Vector gvtFonts = new Vector();
            for (int i = 0; i < resolvedFontFamilies.size(); i++) {
                GVTFont font = ((GVTFontFamily)resolvedFontFamilies.get(i)).deriveFont(fontSize, runaci);
                gvtFonts.add(font);
            }

            // now for each char or group of chars in the string,
            // find a font that can display it

            int runaciLength = end-start;
            boolean[] fontAssigned = new boolean[runaciLength];
            for (int i = 0; i < runaciLength; i++) {
                fontAssigned[i] = false;
            }

            for (int i = 0; i < gvtFonts.size();  i++) {
                // assign this font to all characters it can display if it has
                // not already been assigned

                GVTFont font = (GVTFont)gvtFonts.get(i);

                int currentRunIndex = runaci.getBeginIndex();
                while (currentRunIndex < runaci.getEndIndex()) {

                    int displayUpToIndex = font.canDisplayUpTo(runaci, currentRunIndex, end);

                    if (displayUpToIndex == -1) {
                        // for each char, if not already assigned a font,
                        // assign this font to it
                        for (int j = currentRunIndex; j < end; j++) {
                            if (!fontAssigned[j - start]) {
                                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT, font, j, j+1);
                                fontAssigned[j - start] = true;
                            }
                        }
                        currentRunIndex = runaci.getEndIndex();

                    } else if (displayUpToIndex > currentRunIndex) {
                        // could display some but not all
                        // for each char it can display,
                        // if not already assigned a font, assign this font to it
                        for (int j = currentRunIndex; j < displayUpToIndex; j++) {
                            if (!fontAssigned[j - start]) {
                                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT, font, j, j+1);
                                fontAssigned[j - start] = true;
                            }
                        }
                        // set currentRunIndex to be one after the char couldn't display
                        currentRunIndex = displayUpToIndex+1;
                    } else {
                        // couldn't display the current char
                        currentRunIndex++;
                    }
                }
            }
            // assign the first font to any chars haven't alreay been assigned
            for (int i = 0; i < runaciLength; i++) {
                if (!fontAssigned[i]) {
                    GVTFontFamily fontFamily
                        = FontFamilyResolver.getFamilyThatCanDisplay(runaci.setIndex(start+i));
                    if (fontFamily != null) {
                        GVTFont font = fontFamily.deriveFont(fontSize, runaci);
                        as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT,
                                   font, start+i, start+i+1);
                    } else {
                        // no available fonts can display it, just use the first font in the list
                        as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT,
                                        gvtFonts.get(0), start+i, start+i+1);
                    }
                }
            }
            if (aci.setIndex(end) == aci.DONE) {
                moreChunks = false;
            }
        }
        return as.getIterator();
    }



    /**
     * Adjusts the position of the text runs within the specified text chunk
     * to account for any text anchor properties.
     */
    private void adjustChunkOffsets(List textRuns, Point2D advance,
                                    int beginChunk, int endChunk) {

        for (int n=beginChunk; n<endChunk; ++n) {
            TextRun r = (TextRun) textRuns.get(n);

            int anchorType = r.getAnchorType();

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
                // leave untouched
            }

            TextSpanLayout layout = r.getLayout();
            Point2D offset = layout.getOffset();

            if (layout.isVertical()) {
                layout.setOffset(new Point2D.Float(
                                     (float) offset.getX(),
                                     (float) offset.getY()+dy));
            } else {
                layout.setOffset(new Point2D.Float(
                                    (float) offset.getX()+dx,
                                    (float) offset.getY()));
            }
        }
    }

    /**
     * Paints decorations of the specified type.
     */
    private void paintDecorations(List textRuns, Graphics2D g2d,
                                  int decorationType) {

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
    private void paintTextRuns(List textRuns, Graphics2D g2d,
                               GraphicsNodeRenderContext context) {

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
            textRun.getLayout().draw(g2d, context);
        }
    }


    /*
     * Get a Rectangle2D in userspace coords which encloses the textnode
     * glyphs composed from an AttributedCharacterIterator.
     * @param node the TextNode to measure
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     * @param includeDecoration whether to include text decoration
     *            in bounds computation.
     * @param includeStrokeWidth whether to include the effect of stroke width
     *            in bounds computation.
     */
     protected Rectangle2D getBounds(TextNode node,
               FontRenderContext context,
               boolean includeDecoration,
               boolean includeStrokeWidth) {

         Rectangle2D bounds = getOutline(node, context, includeDecoration).getBounds2D();

         if (includeStrokeWidth) {
             Shape strokeOutline = getStrokeOutline(node, context, includeDecoration);
             if (strokeOutline != null) {
                bounds = bounds.createUnion(strokeOutline.getBounds2D());
             }
         }
        return bounds;

     }

    /**
     * Get a Shape in userspace coords which defines the textnode glyph outlines.
     * @param node the TextNode to measure
     * @param frc the font rendering context.
     * @param includeDecoration whether to include text decoration
     *            outlines.
     */
    protected Shape getOutline(TextNode node, FontRenderContext frc,
                                    boolean includeDecoration) {

        Shape outline = null;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();

        // get the list of text runs
        List textRuns = getTextRuns(node, aci, frc);

        // for each text run, get its outline and append it to the overall outline
        for (int i = 0; i < textRuns.size(); ++i) {
            TextRun textRun = (TextRun)textRuns.get(i);
            TextSpanLayout textRunLayout = textRun.getLayout();
            Shape textRunOutline = textRunLayout.getOutline();

            if (includeDecoration && !textRunLayout.isVertical() && !textRunLayout.isOnATextPath()) {
                AttributedCharacterIterator textRunACI = textRun.getACI();
                int decorationTypes = 0;
                if (textRunACI.getAttribute(GVTAttributedCharacterIterator.
                                            TextAttribute.UNDERLINE) != null) {
                    decorationTypes |= TextSpanLayout.DECORATION_UNDERLINE;
                }
                if (textRunACI.getAttribute(GVTAttributedCharacterIterator.
                                            TextAttribute.OVERLINE) != null) {
                    decorationTypes |= TextSpanLayout.DECORATION_OVERLINE;
                }
                if (textRunACI.getAttribute(GVTAttributedCharacterIterator.
                                            TextAttribute.STRIKETHROUGH) != null) {
                    decorationTypes |= TextSpanLayout.DECORATION_STRIKETHROUGH;
                }
                if (decorationTypes != 0) {
                    if (!(textRunOutline instanceof GeneralPath)) {
                        textRunOutline = new GeneralPath(textRunOutline);
                    }
                    ((GeneralPath) textRunOutline).setWindingRule(
                                            GeneralPath.WIND_NON_ZERO);
                    ((GeneralPath) textRunOutline).append(
                        textRunLayout.getDecorationOutline(decorationTypes), false);
                }
            }

            if (outline == null) {
               outline = textRunOutline;
            } else {
                if (!(outline instanceof GeneralPath)) {
                    outline = new GeneralPath(outline);
                }
                ((GeneralPath) outline).setWindingRule(
                                                    GeneralPath.WIND_NON_ZERO);
                ((GeneralPath) outline).append(textRunOutline, false);
            }

        }
        return outline;
    }

   /**
    * Get a Shape in userspace coords which defines the
    * stroked textnode glyph outlines.
    * @param node the TextNode to measure
    * @param frc the font rendering context.
    * @param includeDecoration whether to include text decoration
    *            outlines.
    */
    protected Shape getStrokeOutline(TextNode node, FontRenderContext frc,
                                     boolean includeDecoration) {

        Shape outline = null;
        AttributedCharacterIterator aci = node.getAttributedCharacterIterator();

        // get the list of text runs
        List textRuns = getTextRuns(node, aci, frc);

        // for each text run, get its stroke outline and append it to the overall outline
        for (int i = 0; i < textRuns.size(); ++i) {
            Shape textRunStrokeOutline = null;

            TextRun textRun = (TextRun)textRuns.get(i);
            AttributedCharacterIterator textRunACI = textRun.getACI();
            TextSpanLayout textRunLayout = textRun.getLayout();

            Stroke stroke = (Stroke) textRunACI.getAttribute(
                GVTAttributedCharacterIterator.TextAttribute.STROKE);
            Paint strokePaint = (Paint) textRunACI.getAttribute(
                GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);

            if (stroke != null && strokePaint != null) {
                // this textRun is stroked
                Shape textRunOutline = textRunLayout.getOutline();
                textRunStrokeOutline = stroke.createStrokedShape(textRunOutline);
            }

            if (includeDecoration && !textRunLayout.isVertical() && !textRunLayout.isOnATextPath()) {
                Shape decorationStrokeOutline = null;

                // look for stroked underline
                if (textRunACI.getAttribute(GVTAttributedCharacterIterator.
                                            TextAttribute.UNDERLINE) != null) {
                    Stroke decorationStroke = (Stroke) textRunACI.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE);
                    Paint decorationStrokePaint = (Paint) textRunACI.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE_PAINT);
                    if (decorationStroke != null && decorationStrokePaint != null) {
                        Shape textRunUnderlineOutline = textRunLayout.getDecorationOutline(
                            TextSpanLayout.DECORATION_UNDERLINE);
                        decorationStrokeOutline
                            = decorationStroke.createStrokedShape(textRunUnderlineOutline);
                    }

                }
                if (textRunACI.getAttribute(GVTAttributedCharacterIterator.
                                            TextAttribute.OVERLINE) != null) {
                    Stroke decorationStroke = (Stroke) textRunACI.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.OVERLINE_STROKE);
                    Paint decorationStrokePaint = (Paint) textRunACI.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.OVERLINE_STROKE_PAINT);
                    if (decorationStroke != null && decorationStrokePaint != null) {
                        Shape textRunOverlineOutline = textRunLayout.getDecorationOutline(
                            TextSpanLayout.DECORATION_OVERLINE);
                        if (decorationStrokeOutline == null) {
                            decorationStrokeOutline
                                = decorationStroke.createStrokedShape(textRunOverlineOutline);
                        } else {
                            if (!(decorationStrokeOutline instanceof GeneralPath)) {
                                decorationStrokeOutline = new GeneralPath(decorationStrokeOutline);
                            }
                            ((GeneralPath)decorationStrokeOutline).append(
                                decorationStroke.createStrokedShape(textRunOverlineOutline), false);
                        }
                    }

                }
                if (textRunACI.getAttribute(GVTAttributedCharacterIterator.
                                            TextAttribute.STRIKETHROUGH) != null) {
                    Stroke decorationStroke = (Stroke) textRunACI.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_STROKE);
                    Paint decorationStrokePaint = (Paint) textRunACI.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.STRIKETHROUGH_STROKE_PAINT);
                    if (decorationStroke != null && decorationStrokePaint != null) {
                        Shape textRunStrikethroughOutline = textRunLayout.getDecorationOutline(
                            TextSpanLayout.DECORATION_STRIKETHROUGH);
                         if (decorationStrokeOutline == null) {
                            decorationStrokeOutline
                                = decorationStroke.createStrokedShape(textRunStrikethroughOutline);
                        } else {
                            if (!(decorationStrokeOutline instanceof GeneralPath)) {
                                decorationStrokeOutline = new GeneralPath(decorationStrokeOutline);
                            }
                            ((GeneralPath)decorationStrokeOutline).append(
                                decorationStroke.createStrokedShape(textRunStrikethroughOutline), false);
                        }
                    }
                }
                if (decorationStrokeOutline != null) {
                    if (textRunStrokeOutline != null) {
                        if (!(textRunStrokeOutline instanceof GeneralPath)) {
                            textRunStrokeOutline = new GeneralPath(textRunStrokeOutline);
                        }
                        ((GeneralPath) textRunStrokeOutline).setWindingRule(
                                            GeneralPath.WIND_NON_ZERO);
                        ((GeneralPath) textRunStrokeOutline).append(decorationStrokeOutline, false);
                    } else {
                        textRunStrokeOutline = decorationStrokeOutline;
                    }
                }
            }

            if (textRunStrokeOutline != null) {
                if (outline == null) {
                    outline = textRunStrokeOutline;
                } else {
                    if (!(outline instanceof GeneralPath)) {
                        outline = new GeneralPath(outline);
                    }
                    ((GeneralPath) outline).setWindingRule(
                                                    GeneralPath.WIND_NON_ZERO);
                    ((GeneralPath) outline).append(textRunStrokeOutline, false);
                }
            }

        }
        return outline;
    }





    TextNode cachedNode;

    protected org.apache.batik.gvt.text.Mark hitTest(
                         double x, double y, AttributedCharacterIterator aci,
                         TextNode node,
                         GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();

        // get the list of text runs
        List textRuns = getTextRuns(node, aci, frc);

        // store the textRuns in the textNode for much quicker highlighting
        // note that we can't set this earlier because of problems with
        // caching the info
        node.setTextRuns(textRuns);


        // for each text run, see if its been hit
        for (int i = 0; i < textRuns.size(); ++i) {
            TextRun textRun = (TextRun)textRuns.get(i);
            TextSpanLayout layout = textRun.getLayout();
            TextHit textHit = layout.hitTestChar((float) x, (float) y);
            if (textHit != null && layout.getBounds().contains(x,y)) {
                textHit.setTextNode(node);
                textHit.setFontRenderContext(frc);
                cachedMark = new BasicTextPainter.Mark(x, y, layout, textHit);
                cachedNode = node;
                return cachedMark;
            }
        }

        if (cachedNode != node) {
            // did not hit any of the layouts and the cachedMark is invalid for
            // this text node, so create a dummy mark
            TextHit textHit = new TextHit(0, false);
            textHit.setTextNode(node);
            textHit.setFontRenderContext(frc);
            cachedMark = new BasicTextPainter.Mark(x,y,((TextRun)textRuns.get(0)).getLayout(), textHit);
            cachedNode = node;
        }
         return cachedMark;
    }

    /**
     * Selects the first glyph in the text node.
     */
    public org.apache.batik.gvt.text.Mark selectFirst (
                         double x, double y, AttributedCharacterIterator aci,
                         TextNode node,
                         GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();

        // get the list of text runs
        List textRuns = getTextRuns(node, aci, frc);

        // store the textRuns in the textNode for much quicker highlighting
        // note that we can't set this earlier because of problems with
        // caching the info
        node.setTextRuns(textRuns);

        aci.first();
        int charIndex = ((Integer)aci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.CHAR_INDEX)).intValue();
        TextHit textHit = new TextHit(charIndex, false);
        textHit.setTextNode(node);
        textHit.setFontRenderContext(frc);
        cachedMark = new BasicTextPainter.Mark(x,y,((TextRun)textRuns.get(0)).getLayout(), textHit);
        cachedNode = node;
        return cachedMark;
    }

    /**
     * Selects the last glyph in the text node.
     */
    public org.apache.batik.gvt.text.Mark selectLast (
                         double x, double y, AttributedCharacterIterator aci,
                         TextNode node,
                         GraphicsNodeRenderContext context) {

        FontRenderContext frc = context.getFontRenderContext();

        // get the list of text runs
        List textRuns = getTextRuns(node, aci, frc);

        // store the textRuns in the textNode for much quicker highlighting
        // note that we can't set this earlier because of problems with
        // caching the info
        node.setTextRuns(textRuns);

        TextSpanLayout lastLayout = ((TextRun)textRuns.get(textRuns.size()-1)).getLayout();
        int lastGlyphIndex = lastLayout.getGlyphCount()-1;
        aci.last();
        int charIndex = ((Integer)aci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.CHAR_INDEX)).intValue();
        TextHit textHit = new TextHit(charIndex, false);
        textHit.setTextNode(node);
        textHit.setFontRenderContext(frc);
        cachedMark = new BasicTextPainter.Mark(x,y,lastLayout,textHit);
        cachedNode = node;
        return cachedMark;
    }

    /**
     * Returns an array of ints representing begin/end index pairs into
     * an AttributedCharacterIterator which represents the text
     * selection delineated by two Mark instances.
     * <em>Note: The Mark instances passed must have been instantiated by
     * an instance of this enclosing TextPainter implementation.</em>
     */
    public int[] getSelected(AttributedCharacterIterator aci,
                             org.apache.batik.gvt.text.Mark startMark,
                             org.apache.batik.gvt.text.Mark finishMark) {

        BasicTextPainter.Mark start;
        BasicTextPainter.Mark finish;
        try {
            start = (BasicTextPainter.Mark) startMark;
            finish = (BasicTextPainter.Mark) finishMark;
        } catch (ClassCastException cce) {
            throw new
            Error("This Mark was not instantiated by this TextPainter class!");
        }
        int[] result = new int[2];
        result[0] = start.getHit().getCharIndex();
        result[1] = finish.getHit().getCharIndex();
        return result;
    }

   /**
     * Return a Shape, in the coordinate system of the text layout,
     * which encloses the text selection delineated by two Mark instances.
     * <em>Note: The Mark instances passed must have been instantiated by
     * an instance of this enclosing TextPainter implementation.</em>
     */
    public Shape getHighlightShape(org.apache.batik.gvt.text.Mark beginMark,
                                   org.apache.batik.gvt.text.Mark endMark) {

        BasicTextPainter.Mark begin;
        BasicTextPainter.Mark end;
        try {
            begin = (BasicTextPainter.Mark) beginMark;
            end = (BasicTextPainter.Mark) endMark;
        } catch (ClassCastException cce) {
            throw new
            Error("This Mark was not instantiated by this TextPainter class!");
        }

        int beginIndex = begin.getHit().getCharIndex();
        int endIndex = end.getHit().getCharIndex();

        TextSpanLayout beginLayout = null;
        TextSpanLayout endLayout = null;
        if (begin != null && end != null) {
            beginLayout = begin.getLayout();
            endLayout = end.getLayout();
        }
        if (beginLayout == null || endLayout == null) {
            return null;
        }

        // get the list of text runs
        TextNode textNode = begin.getHit().getTextNode();
        FontRenderContext frc = begin.getHit().getFontRenderContext();
        List textRuns = getTextRuns(textNode, textNode.getAttributedCharacterIterator(), frc);

        // determine whether selection is right to left or not, ie. whether
        // beginLayout is before endLayout or not
        boolean leftToRight = true;
        for (int i = 0; i < textRuns.size(); ++i) {
            TextRun textRun = (TextRun)textRuns.get(i);
            TextSpanLayout layout = textRun.getLayout();
            if (layout == beginLayout) {
                break;
            }
            if (layout == endLayout) {
                leftToRight = false;
                break;
            }
        }

        GeneralPath highlightedShape = new GeneralPath();

        // for each text run, append any highlight it may contain for the current selection
        for (int i = 0; i < textRuns.size(); ++i) {
            TextRun textRun = (TextRun)textRuns.get(i);
            TextSpanLayout layout = textRun.getLayout();

            Shape layoutHighlightedShape = layout.getHighlightShape(beginIndex, endIndex, leftToRight);

            // append the highlighted shape of this layout to the
            // overall hightlighted shape
            if (layoutHighlightedShape != null && !layoutHighlightedShape.getBounds().isEmpty()) {
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

        public TextChunk(int begin, int end, Point2D advance) {
            this.begin = begin;
            this.end = end;
            this.advance = new Point2D.Float((float) advance.getX(),
                                             (float) advance.getY());
        }
    }


    /**
     * Inner convenience class for associating a TextLayout for
     * sub-spans, and the ACI which iterates over that subspan.
     */
    class TextRun {
        private AttributedCharacterIterator aci;
        private TextSpanLayout layout;
        private int anchorType;
        private boolean firstRunInChunk;

        public TextRun(TextSpanLayout layout, AttributedCharacterIterator aci, boolean firstRunInChunk) {
            this.layout = layout;
            this.aci = aci;
            this.aci.first();
            this.firstRunInChunk = firstRunInChunk;
            TextNode.Anchor anchor = (TextNode.Anchor) aci.getAttribute(
                     GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE);
            anchorType = TextNode.Anchor.ANCHOR_START;
            if (anchor != null) {
                anchorType = anchor.getType();
            }
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

        public boolean isFirstRunInChunk() {
            return firstRunInChunk;
        }

    }
}
