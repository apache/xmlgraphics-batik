/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.Composite;
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
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import org.apache.batik.gvt.font.FontFamilyResolver;
import org.apache.batik.gvt.text.TextHit;

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

        /*
         * Text Chunks contain one or more TextRuns,
         * which they create from the ACI.
         * Each TextRun contains one TextLayout object.
         * We render each of the TextLayout glyphsets
         * in turn.
         */
        for (int i = 0; i < textRuns.size(); ++i) {
            paintTextRun((TextRun) textRuns.get(i), g2d, context);
        }

        // TODO: FONT_VARIANT, SUPERSCRIPT, SUBSCRIPT...
    }


    private List getTextRuns(TextNode node,
                             AttributedCharacterIterator aci,
                             FontRenderContext frc) {

        List textRuns = node.getTextRuns();
        if (textRuns != null) {
            return textRuns;
        }

        textRuns = new ArrayList();
        AttributedCharacterIterator fontaci = createModifiedACIForFontMatching(node, aci);
        fontaci.first();

        /*
         * We iterate through the spans over extended attributes,
         * instantiating TextLayout elements as we go, and
         * accumulate an overall advance for the text display.
         */
        TextChunk chunk;
        int beginChunk = 0;
        do {
             /*
              * Text Chunks contain one or more TextRuns,
              * which they create from the ACI.
              */
            chunk = getTextChunk(node, fontaci, textRuns, beginChunk, frc);

            /* Adjust according to text-anchor property value */

            if (chunk != null) {
                adjustChunkOffsets(textRuns, chunk.advance, chunk.begin, chunk.end);
                beginChunk = chunk.end;
            }

        } while (chunk != null);
        return textRuns;
    }



    private TextChunk getTextChunk(TextNode node,
                                   AttributedCharacterIterator aci,
                                   List textRuns,
                                   int beginChunk,
                                   FontRenderContext frc) {

        int endChunk = beginChunk;
        AttributedCharacterIterator runaci;
        boolean inChunk = true;
        Point2D advance = new Point2D.Float(0f, 0f);
        Point2D location = node.getLocation();
        if (aci.current() != CharacterIterator.DONE) {
            int chunkStartIndex = aci.getIndex();
            boolean isChunkStart = true;
            do {

                int start = aci.getRunStart(extendedAtts);
                int end = aci.getRunLimit(extendedAtts);

                runaci =
                    new AttributedCharacterSpanIterator(aci, start, end);

                Float fx = (Float) runaci.getAttribute(
                     GVTAttributedCharacterIterator.TextAttribute.X);

                inChunk = (isChunkStart) || (fx == null) || (fx.isNaN());
                if (inChunk) {
                    Point2D offset = new Point2D.Float(
                       (float) (location.getX()+advance.getX()),
                       (float) (location.getY()+advance.getY()));
                    TextSpanLayout layout = getTextLayoutFactory().
                                       createTextLayout(runaci, offset, frc);
                    if (layout.isVertical()) {
                        runaci = createModifiedACIForVerticalLayout(runaci);
                    }
                    TextRun run = new TextRun(layout, runaci);
                    textRuns.add(run);
                    Point2D layoutAdvance = layout.getAdvance2D();
                    advance = new Point2D.Float(
                       (float) (advance.getX()+layoutAdvance.getX()),
                       (float) (advance.getY()+layoutAdvance.getY()));
                    ++endChunk;
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


    private AttributedCharacterIterator createModifiedACIForVerticalLayout(
                                           AttributedCharacterIterator runaci) {

        AttributedString as = new AttributedString(runaci);
        if (runaci.getAttribute(GVTAttributedCharacterIterator.
                                TextAttribute.UNDERLINE) != null) {
             as.addAttribute(TextAttribute.UNDERLINE,
                             TextAttribute.UNDERLINE_ON);
        }
        if (runaci.getAttribute(GVTAttributedCharacterIterator.
                                TextAttribute.STRIKETHROUGH) != null) {
             as.addAttribute(TextAttribute.STRIKETHROUGH,
                             TextAttribute.STRIKETHROUGH_ON);
        }
        return as.getIterator();
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
            int start = aci.getRunStart(GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
            int end = aci.getRunLimit(GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);

            AttributedCharacterSpanIterator runaci = new AttributedCharacterSpanIterator(aci, start, end);

            Vector fontFamilies = (Vector)runaci.getAttributes().get(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT_FAMILIES);

            if (fontFamilies == null) {
                // no font families set, just return the same aci
                return aci;
            }

            // resolve any unresolved font families in the list
            Vector resolvedFontFamilies = new Vector();
            for (int i = 0; i < fontFamilies.size(); i++) {
                GVTFontFamily fontFamily = (GVTFontFamily) fontFamilies.get(i);
                if (fontFamily instanceof UnresolvedFontFamily) {
                    GVTFontFamily resolvedFontFamily = FontFamilyResolver.resolve((UnresolvedFontFamily)fontFamily);
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
                        // for each char, if not already assigned a font, assign this font to it
                        for (int j = currentRunIndex; j < end; j++) {
                            if (!fontAssigned[j - start]) {
                                as.addAttribute(GVTAttributedCharacterIterator.TextAttribute.GVT_FONT, font, j, j+1);
                                fontAssigned[j - start] = true;
                            }
                        }
                        currentRunIndex = runaci.getEndIndex();

                    } else if (displayUpToIndex > currentRunIndex) {
                        // could display some but not all
                        // for each char it can display, if not already assigned a font, assign this font to it

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
                    GVTFontFamily fontFamily = FontFamilyResolver.getFamilyThatCanDisplay(runaci.setIndex(start+i));
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
                //System.out.println("offset "+offset+" shift "+dx);
                layout.setOffset(new Point2D.Float(
                                    (float) offset.getX()+dx,
                                    (float) offset.getY()));
            }
        }
    }


    private void paintTextRun(TextRun textRun, Graphics2D g2d,
                           GraphicsNodeRenderContext context) {

        AttributedCharacterIterator runaci = textRun.getACI();
        TextSpanLayout layout = textRun.getLayout();
        runaci.first();

        Composite opacity = (Composite)
                  runaci.getAttribute(GVTAttributedCharacterIterator.
                                              TextAttribute.OPACITY);
        if (opacity != null) {
            g2d.setComposite(opacity);
        }

        boolean underline =
            (runaci.getAttribute(GVTAttributedCharacterIterator.
                                 TextAttribute.UNDERLINE) != null);

        // paint over-and-underlines first, then layer glyphs over them

        if (underline && !layout.isVertical()) {
            paintUnderline(textRun, g2d);
        }
        boolean overline =
            (runaci.getAttribute(GVTAttributedCharacterIterator.
                                 TextAttribute.OVERLINE) != null);

        if (overline && !layout.isVertical()) {
            paintOverline(textRun, g2d);
        }

        layout.draw(g2d, context);

        boolean strikethrough =
            (runaci.getAttribute(GVTAttributedCharacterIterator.
                TextAttribute.STRIKETHROUGH) ==
                    GVTAttributedCharacterIterator.
                        TextAttribute.STRIKETHROUGH_ON);
        // paint strikethrough last
        if (strikethrough && !layout.isVertical()) {
            paintStrikethrough(textRun, g2d);
        }
    }

    /**
     * Paints the overline for a given ACI.
     */
    private void paintOverline(TextRun textRun, Graphics2D g2d) {
        AttributedCharacterIterator runaci = textRun.getACI();
        TextSpanLayout layout = textRun.getLayout();
        java.awt.Shape overlineShape =
                layout.getDecorationOutline(
                           TextSpanLayout.DECORATION_OVERLINE);

        Paint paint = (Paint) runaci.getAttribute(
            TextAttribute.FOREGROUND);
        if (paint != null) {
            g2d.setPaint(paint);
            g2d.fill(overlineShape);
        }
        Stroke stroke = (Stroke) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE);
        paint = (Paint) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
        if ((stroke != null) && (paint != null)) {
            g2d.setStroke(stroke);
            g2d.setPaint(paint);
            g2d.draw(overlineShape);
        }
        // TODO: performance and concision
    }

    /**
     * Paints the underline for a given ACI - does not rely on TextLayout's
     * internal underlining but computes the underline manually, allowing
     * the underline fill and stroke to differ from that of the text glyphs.
     */
    private void paintUnderline(TextRun textRun, Graphics2D g2d) {

        AttributedCharacterIterator runaci = textRun.getACI();
        TextSpanLayout layout = textRun.getLayout();

        Shape underlineShape = layout.getDecorationOutline(
                           TextSpanLayout.DECORATION_UNDERLINE);

        // TODO: change getAdvance to getVisibleAdvance for
        // ACIs which do not inherit their underline attribute
        // (not sure how to implement this yet)

        Paint paint = (Paint) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_PAINT);
        if (paint != null) {
            g2d.setPaint(paint);
            g2d.fill(underlineShape);
            //    System.out.println("Filling "+underlineShape+" with paint "+paint);
        }
        Stroke stroke = (Stroke) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.UNDERLINE_STROKE);
        paint = (Paint) runaci.getAttribute(
            GVTAttributedCharacterIterator.
                TextAttribute.UNDERLINE_STROKE_PAINT);
        if ((stroke != null) && (paint != null)) {
            g2d.setStroke(stroke);
            g2d.setPaint(paint);
            g2d.draw(underlineShape);
        }

    }

    /**
     * Paints the strikethrough line for a given ACI - does not
     * rely on TextLayout's
     * internal strikethrough but computes it manually.
     */
    private void paintStrikethrough(TextRun textRun, Graphics2D g2d) {

        AttributedCharacterIterator runaci = textRun.getACI();
        TextSpanLayout layout = textRun.getLayout();

        java.awt.Shape strikethroughShape =
                layout.getDecorationOutline(
                           TextSpanLayout.DECORATION_STRIKETHROUGH);

        Paint paint = (Paint) runaci.getAttribute(
            TextAttribute.FOREGROUND);
        if (paint != null) {
            g2d.setPaint(paint);
            g2d.fill(strikethroughShape);
        }
        Stroke stroke = (Stroke) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE);
        paint = (Paint) runaci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
        if ((stroke != null) && (paint != null)) {
            g2d.setStroke(stroke);
            g2d.setPaint(paint);
            g2d.draw(strikethroughShape);
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

         Rectangle2D bounds;

         if (includeStrokeWidth) {
             Shape s = getStrokeOutline(node, context, includeDecoration);
             if (s != null) {
                 bounds = s.getBounds2D();
             } else {
                 bounds = getOutline(node, context, false).getBounds2D();
             }
         } else {
             if (includeDecoration) {
                 bounds = getOutline(node, context, true).getBounds2D();
             } else {
                 bounds = getOutline(node, context, false).getBounds2D();
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
            Shape textRunOutline = textRun.getLayout().getOutline();

            if (includeDecoration) {
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
                        textRun.getLayout().getDecorationOutline(decorationTypes), false);
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
            TextHit textHit = new TextHit(0,false);
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

        TextHit textHit = new TextHit(0,false);
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
        TextHit textHit = new TextHit(lastGlyphIndex,false);
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

        TextSpanLayout startLayout = null;
        TextSpanLayout finishLayout = null;
        if (start != null && finish != null) {
            startLayout = start.getLayout();
            finishLayout = finish.getLayout();
        }
        if (startLayout == null || finishLayout == null) {
            return null;
        }

        // if both layouts are the same, make sure the startMark is before the finishMark
        if (startLayout == finishLayout) {
            if (finish.getHit().getGlyphIndex() < start.getHit().getGlyphIndex()) {
                // swap
                BasicTextPainter.Mark temp = start;
                start = finish;
                finish = temp;
            }
        }

        // get the list of text runs
        TextNode textNode = start.getHit().getTextNode();
        FontRenderContext frc = start.getHit().getFontRenderContext();
        List textRuns = getTextRuns(textNode, textNode.getAttributedCharacterIterator(), frc);

        int currentCharCount = 0;
        int currentArrayIndex = 0;
        int[] result = new int[2];

        // for each text run, look for the start and finish mark
        for (int i = 0; i < textRuns.size(); ++i) {
            TextRun textRun = (TextRun)textRuns.get(i);
            TextSpanLayout layout = textRun.getLayout();

            if (layout == startLayout) { // found the first hit

                int firstHit = start.getHit().getGlyphIndex();
                if (currentArrayIndex == 0) { // selection is LTR
                     if (firstHit == 0) {
                        result[currentArrayIndex] = currentCharCount;
                    } else {
                        result[currentArrayIndex] = currentCharCount + layout.getCharacterCount(0, firstHit-1);
                    }
                } else { // selection is RTL
                    result[currentArrayIndex] = currentCharCount + layout.getCharacterCount(0, firstHit) - 1;
                }
                currentArrayIndex++;
                if (currentArrayIndex == 2) {
                    // have found both marks
                    return result;
                }
            }
            if (layout == finishLayout) { // found the last hit

                int lastHit = finish.getHit().getGlyphIndex();
                if (currentArrayIndex == 0) { // selection is RTL
                    if (lastHit == 0) {
                        result[currentArrayIndex] = currentCharCount;
                    } else {
                        result[currentArrayIndex] = currentCharCount + layout.getCharacterCount(0, lastHit-1);
                    }
                } else {
                    result[currentArrayIndex] = currentCharCount + layout.getCharacterCount(0, lastHit) - 1;
                }
                currentArrayIndex++;
                if (currentArrayIndex == 2) {
                    // have found both marks
                    return result;
                }
            }

            // increment the char count

            currentCharCount += (textRun.getACI().getEndIndex() - textRun.getACI().getBeginIndex());
        }
        // shouldn't get here
        return null;
    }

   /**
     * Return a Shape, in the coordinate system of the text layout,
     * which encloses the text selection delineated by two Mark instances.
     * <em>Note: The Mark instances passed must have been instantiated by
     * an instance of this enclosing TextPainter implementation.</em>
     */
    public Shape getHighlightShape(org.apache.batik.gvt.text.Mark beginMark,
                                   org.apache.batik.gvt.text.Mark endMark) {

        // TODO: later we can return more complex things like
        // noncontiguous selections

        BasicTextPainter.Mark begin;
        BasicTextPainter.Mark end;
        try {
            begin = (BasicTextPainter.Mark) beginMark;
            end = (BasicTextPainter.Mark) endMark;
        } catch (ClassCastException cce) {
            throw new
            Error("This Mark was not instantiated by this TextPainter class!");
        }

        TextSpanLayout beginLayout = null;
        TextSpanLayout endLayout = null;
        if (begin != null && end != null) {
            beginLayout = begin.getLayout();
            endLayout = end.getLayout();
        }
        if (beginLayout == null || endLayout == null) {
            return null;
        }

        if (beginLayout == endLayout) {
            int firsthit = 0;
            int lasthit = 0;
            if (begin != end) {
                firsthit = begin.getHit().getGlyphIndex();
                lasthit = end.getHit().getGlyphIndex();
                if (firsthit > lasthit) {
                    int temp = firsthit;
                    firsthit = lasthit;
                    lasthit = temp;
                }
            } else {
                lasthit = beginLayout.getGlyphCount();
            }
            if (firsthit < 0) {
                firsthit = 0;
            }
            return beginLayout.getLogicalHighlightShape(
                                    firsthit,
                                    lasthit);
        } else {
            // selection must span more than one text layout (run)

            // get the list of text runs
            TextNode textNode = begin.getHit().getTextNode();
            FontRenderContext frc = begin.getHit().getFontRenderContext();
            List textRuns = getTextRuns(textNode, textNode.getAttributedCharacterIterator(), frc);

            // find out whether selection is right to left or not, ie. whether
            // beginLayout is before endLayout or not
            boolean leftToRight = true;
            for (int i = 0; i < textRuns.size(); ++i) {
                TextRun textRun = (TextRun)textRuns.get(i);
                TextSpanLayout layout = textRun.getLayout();
                if (layout.getOffset().equals(beginLayout.getOffset())) {
                    break;
                }
                if (layout.getOffset().equals(endLayout.getOffset())) {
                    leftToRight = false;
                    break;
                }
            }
            GeneralPath highlightedShape = new GeneralPath();
            boolean startedHighlight = false;
            boolean finishedHighlight = false;

            // for each text run
            for (int i = 0; i < textRuns.size(); ++i) {
                TextRun textRun = (TextRun)textRuns.get(i);
                TextSpanLayout layout = textRun.getLayout();

                Shape layoutHighlightedShape = null;

                if (leftToRight) {

                    if (layout == beginLayout) { // found the first layout

                        startedHighlight = true;
                        int firsthit = begin.getHit().getGlyphIndex();
                        if (firsthit < 0) {
                          firsthit = 0;
                        }
                        layoutHighlightedShape = layout.getLogicalHighlightShape(
                                                  firsthit, layout.getGlyphCount());

                    } else if (layout == endLayout) {

                        finishedHighlight = true;
                        int lasthit = end.getHit().getGlyphIndex();

                        if (lasthit < 0) {
                            lasthit = layout.getGlyphCount();
                        }
                        layoutHighlightedShape = layout.getLogicalHighlightShape(
                                                        0, lasthit);

                    } else if (startedHighlight) {
                        layoutHighlightedShape = layout.getLogicalHighlightShape(
                                                     0, layout.getGlyphCount());
                    }

                } else {  // right to left

                     if (layout == beginLayout) { // found the first layout
                        finishedHighlight = true;
                        int lasthit = begin.getHit().getGlyphIndex();

                        if (lasthit < 0) {
                            lasthit = layout.getGlyphCount();
                        }
                        layoutHighlightedShape = layout.getLogicalHighlightShape(
                                                  0, lasthit);

                    } else if (layout == endLayout) {
                        startedHighlight = true;
                        int firsthit = end.getHit().getGlyphIndex();
                        if (firsthit < 0) {
                            firsthit = 0;
                        }

                        layoutHighlightedShape = layout.getLogicalHighlightShape(
                                                        firsthit, layout.getGlyphCount());

                    } else if (startedHighlight) {
                        layoutHighlightedShape = layout.getLogicalHighlightShape(
                                                     0, layout.getGlyphCount());
                    }

                }

                // append the highlighted shape of this layout to the
                // overall hightlighted shape
                if (layoutHighlightedShape != null && !layoutHighlightedShape.getBounds().isEmpty()) {
                    highlightedShape.append(layoutHighlightedShape, false);
                }
                // if has appended the last highlight, then don't process any more
                if (finishedHighlight) {
                    break;
                }
            }
            return highlightedShape;
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

        public TextRun(TextSpanLayout layout, AttributedCharacterIterator aci) {
            this.layout = layout;
            this.aci = aci;
            this.aci.first();
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

    }
}
