/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Set;
import java.util.HashSet;

import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.AltGlyphHandler;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.AWTGVTFont;

/**
 * Implementation of TextSpanLayout which uses java.awt.font.GlyphVector.
 * @see org.apache.batik.gvt.text.TextSpanLayout
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class GlyphLayout implements TextSpanLayout {

    private GVTGlyphVector gv;
    private GVTFont font;
    private GVTLineMetrics metrics;
    private AttributedCharacterIterator aci;
    private FontRenderContext frc;
    private AffineTransform transform;
    private Point2D advance;
    private Point2D offset;
    private Point2D prevCharPosition;
    private TextPath textPath;
    private Point2D textPathAdvance;
    private int []  charMap;
    private boolean vertical;

    private static final AttributedCharacterIterator.Attribute X
        = GVTAttributedCharacterIterator.TextAttribute.X;

    private static final AttributedCharacterIterator.Attribute Y
        = GVTAttributedCharacterIterator.TextAttribute.Y;

    private static final AttributedCharacterIterator.Attribute DX
        = GVTAttributedCharacterIterator.TextAttribute.DX;

    private static final AttributedCharacterIterator.Attribute DY
        = GVTAttributedCharacterIterator.TextAttribute.DY;

    private static final AttributedCharacterIterator.Attribute ROTATION
        = GVTAttributedCharacterIterator.TextAttribute.ROTATION;

    private static final AttributedCharacterIterator.Attribute BASELINE_SHIFT
        = GVTAttributedCharacterIterator.TextAttribute.BASELINE_SHIFT;

    private static final AttributedCharacterIterator.Attribute WRITING_MODE
        = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE;

    private static final Integer WRITING_MODE_TTB
        = GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_TTB;

    static Set runAtts = new HashSet();

    static {
        runAtts.add(X);
        runAtts.add(Y);
        runAtts.add(DX);
        runAtts.add(DY);
        runAtts.add(ROTATION);
        runAtts.add(BASELINE_SHIFT);
    }



    /**
     * Creates the specified text layout using the
     * specified AttributedCharacterIterator and rendering context.
     *
     * @param aci the AttributedCharacterIterator whose text is to
     *  be laid out
     * @param charMap Indicates how chars in aci map to original
     *                text char array.
     * @param offset The offset position of this text layout
     * @param frc the FontRenderContext to use for generating glyphs.
     */
    public GlyphLayout(AttributedCharacterIterator aci, 
                       int [] charMap,
                       Point2D offset,
                       FontRenderContext frc) {

        this.aci = aci;
        this.frc = frc;
        this.offset = offset;
        this.transform = null;
        this.font = getFont(this.aci);
        this.charMap = charMap;

        this.metrics = font.getLineMetrics
            (aci, aci.getBeginIndex(), aci.getEndIndex(), frc);
        
        // create the glyph vector
        this.gv = null;
        this.aci.first();
        this.vertical = (aci.getAttribute(WRITING_MODE) == WRITING_MODE_TTB);

        AltGlyphHandler altGlyphHandler = (AltGlyphHandler)this.aci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER);
        if (altGlyphHandler != null) {
            // this must be an altGlyph text element, try and create the alternate glyphs
            this.gv = altGlyphHandler.createGlyphVector(frc, this.font.getSize(), this.aci);
        }
        if (this.gv == null) {
            // either not an altGlyph or the altGlyphHandler failed to create a glyph vector
            this.gv = font.createGlyphVector(frc, this.aci);
        }

        // do the glyph layout
        this.gv.performDefaultLayout();
        doExplicitGlyphLayout(false);
        adjustTextSpacing();
        doPathLayout(false);
    }

    /**
     * Paints the text layout using the
     * specified Graphics2D and rendering context.
     * @param g2d the Graphics2D to use
     * @param context The current render context
     */
    public void draw(Graphics2D g2d) {
        AffineTransform t;
        if (transform != null) {
            t = g2d.getTransform();
            g2d.transform(transform);
            gv.draw(g2d, aci);
            g2d.setTransform(t);
        } else {
            gv.draw(g2d, aci);
        }
    }

    /**
     * Returns the outline of the completed glyph layout.
     */
    public Shape getOutline() {
        Shape s = gv.getOutline();
        if (transform != null) {
            s = transform.createTransformedShape(s);
        }
        return s;
    }

    /**
     * Returns the current text position at the beginning
     * of glyph layout, before the application of explicit
     * glyph positioning attributes.
     */
    public Point2D getOffset() {
        return offset;
    }

    /**
     * Sets the text position used for the implicit origin
     * of glyph layout. Ignored if multiple explicit glyph
     * positioning attributes are present in ACI
     * (e.g. if the aci has multiple X or Y values).
     */
    public void setOffset(Point2D offset) {
        this.offset = offset;
        this.gv.performDefaultLayout();
        doExplicitGlyphLayout(true);
        adjustTextSpacing();
        doPathLayout(true);
    }

    /**
     * Returns the outline of the specified decorations on the glyphs,
     * @param decorationType an integer indicating the type(s) of decorations
     *     included in this shape.  May be the result of "OR-ing" several
     *     values together:
     * e.g. <tt>DECORATION_UNDERLINE | DECORATION_STRIKETHROUGH</tt>
     */
    public Shape getDecorationOutline(int decorationType) {
        Shape g = new GeneralPath();
        if ((decorationType & DECORATION_UNDERLINE) != 0) {
             ((GeneralPath) g).append(getUnderlineShape(), false);
        }
        if ((decorationType & DECORATION_STRIKETHROUGH) != 0) {
             ((GeneralPath) g).append(getStrikethroughShape(), false);
        }
        if ((decorationType & DECORATION_OVERLINE) != 0) {
             ((GeneralPath) g).append(getOverlineShape(), false);
        }
        if (transform != null) {
            g = transform.createTransformedShape(g);
        }
        return g;
    }

    /**
     * Returns the rectangular bounds of the completed glyph layout.
     */
    public Rectangle2D getBounds() {
        Rectangle2D bounds = gv.getVisualBounds();
        if (transform != null) {
            bounds = transform.createTransformedShape(bounds).getBounds2D();
        }
        return bounds;
    }

    /**
     * Returns the rectangular bounds of the completed glyph layout,
     * inclusive of "decoration" (underline, overline, etc.)
     */
    public Rectangle2D getDecoratedBounds() {
        return getBounds().createUnion(
            getDecorationOutline(
                DECORATION_ALL).getBounds2D());
    }

    /**
     * Returns the current text position at the completion
     * of glyph layout.
     */
    public Point2D getAdvance2D() {
        return advance;
    }

    /**
     * Returns the position to used when drawing a text run after this one.
     * It takes into account the text path layout if there is one.
     */
    public Point2D getTextPathAdvance() {
        if (textPath != null) {
            return textPathAdvance;
        } else {
            return getAdvance2D();
        }
    }


    /**
     * Returns the index of the glyph that has the specified char index.
     *
     * @param charIndex The original index of the character in the text node's
     * text string.
     * @return The index of the matching glyph in this layout's glyph vector,
     *         or -1 if a matching glyph could not be found.
     */
    public int getGlyphIndex(int charIndex) {
        int numGlyphs = getGlyphCount();
        int j=0;
        for (int i = 0; i < numGlyphs; i++) {
            int count = getCharacterCount(i, i);
            for (int n=0; n<count; n++) {
                int glyphCharIndex = charMap[j++];
                if (charIndex == glyphCharIndex) 
                    return i;
                if (j >= charMap.length)
                    return -1;
            }
        }
        return -1;
    }

   /**
     * Returns a Shape which encloses the currently selected glyphs
     * as specified by the character indices.
     *
     * @param beginCharIndex the index of the first char in the contiguous selection.
     * @param endCharIndex the index of the last char in the contiguous selection.
     * @return The highlight shape or null if the spacified char range does not
     * overlap with the chars in this layout.
     */
    public Shape getHighlightShape(int beginCharIndex, int endCharIndex) {

        if (beginCharIndex > endCharIndex) {
            int temp = beginCharIndex;
            beginCharIndex = endCharIndex;
            endCharIndex = temp;
        }
        GeneralPath shape = null;
        int start       = aci.getBeginIndex();
        int numGlyphs = getGlyphCount();

        boolean glyphOrientationAuto = isGlyphOrientationAuto();
        int glyphOrientationAngle = 90;
        if (!glyphOrientationAuto) {
            glyphOrientationAngle = getGlyphOrientationAngle();
        }
        Point2D.Float [] topPts = new Point2D.Float[2*numGlyphs];
        Point2D.Float [] botPts = new Point2D.Float[2*numGlyphs];

        int ptIdx = 0;

        int currentChar = start;
        for (int i = 0; i < numGlyphs; i++) {
            char ch = aci.setIndex(currentChar);
            int glyphCharIndex = charMap[currentChar-start];
            if ((glyphCharIndex >= beginCharIndex) && 
                (glyphCharIndex <= endCharIndex)) {
                Shape gbounds = gv.getGlyphLogicalBounds(i);
                if (gbounds != null) {
                    // We got something...
                    if (shape == null)
                        shape = new GeneralPath();

                    // We are pretty dumb here we assume that we always
                    // get back polygons with four sides to them if
                    // isn't met we are SOL.
                    float [] pts = new float[6];
                    int count = 0;
                    int type = -1;

                    PathIterator pi = gbounds.getPathIterator(null);
                    Point2D.Float firstPt = null;
                    if (isVertical()) {
                        if (glyphOrientationAuto) {
                            if (isLatinChar(ch))
                                glyphOrientationAngle = 90;
                            else
                                glyphOrientationAngle = 0;
                        }
                    }

                    while (!pi.isDone()) {
                        type = pi.currentSegment(pts);
                        if ((type == PathIterator.SEG_MOVETO) ||
                            (type == PathIterator.SEG_LINETO)) {
                            // LINETO or MOVETO
                            if (count > 4) break; // too many lines...
                            if (count == 4) {
                                // make sure we are just closing it..
                                if ((firstPt == null)     ||
                                    (firstPt.x != pts[0]) ||
                                    (firstPt.y != pts[1]))
                                    break;
                            } else {
                                Point2D.Float pt;
                                pt = new Point2D.Float(pts[0], pts[1]);
                                if (count == 0) firstPt = pt;
                                switch(glyphOrientationAngle) {
                                case 0:
                                    // Use sides of rectangle...
                                    switch (count) {
                                    case 0: botPts[ptIdx]   = pt; break;
                                    case 1: topPts[ptIdx]   = pt; break;
                                    case 2: topPts[ptIdx+1] = pt; break;
                                    case 3: botPts[ptIdx+1] = pt; break;
                                    }
                                    break;
                                case 90:
                                    // Use "top" and "bottom"
                                    switch (count) {
                                    case 0: topPts[ptIdx]   = pt; break;
                                    case 1: topPts[ptIdx+1] = pt; break;
                                    case 2: botPts[ptIdx+1] = pt; break;
                                    case 3: botPts[ptIdx]   = pt; break;
                                    }
                                    break;
                                case 180:
                                    // Use reverse sides of rectangle...
                                    switch (count) {
                                    case 0: botPts[ptIdx+1] = pt; break;
                                    case 1: topPts[ptIdx+1] = pt; break;
                                    case 2: topPts[ptIdx]   = pt; break;
                                    case 3: botPts[ptIdx]   = pt; break;
                                    }
                                    break;
                                case 270:
                                    // Use "bottom" and "top"
                                    switch (count) {
                                    case 0: topPts[ptIdx+1] = pt; break;
                                    case 1: topPts[ptIdx]   = pt; break;
                                    case 2: botPts[ptIdx]   = pt; break;
                                    case 3: botPts[ptIdx+1] = pt; break;
                                    }
                                    break;
                                }
                            }
                        } else if (type == PathIterator.SEG_CLOSE) {
                                // Close in the wrong spot?
                            if ((count < 4) || (count > 5)) break;
                        } else {
                            // QUADTO or CUBETO
                            break;
                        }

                        count++;
                        pi.next();
                    }
                    if (pi.isDone()) {
                        // Sucessfully Expressed as a quadralateral...
                        if ((botPts[ptIdx]!=null) &&
                            ((topPts[ptIdx].x != topPts[ptIdx+1].x) ||
                             (topPts[ptIdx].y != topPts[ptIdx+1].y)))
                            // box isn't empty so use it's points...
                            ptIdx += 2;
                    } else {
                        // System.out.println("Type: " + type +
                        //                    " count: " + count);
                        // Wasn't a quadralateral so just add it don't try
                        // and merge it...
                        addPtsToPath(shape, topPts, botPts, ptIdx);
                        ptIdx = 0;
                        shape.append(gbounds, false);
                    }
                }
            }
            currentChar += getCharacterCount(i, i);
        }
        addPtsToPath(shape, topPts, botPts, ptIdx);

        if (transform != null) {
            return transform.createTransformedShape(shape);
        }
        return shape;
    }

    public static final float eps = 0.00001f;

    public static int makeConvexHull(Point2D.Float [] pts, int numPts) {
        // Sort the Pts in X...
        Point2D.Float tmp;
        // System.out.print("Sorting...");
        for (int i=1; i<numPts; i++) {
            // Simple bubble sort (numPts should be small so shouldn't
            // be too bad.).
            if ((pts[i].x < pts[i-1].x) ||
                ((pts[i].x == pts[i-1].x) && (pts[i].y < pts[i-1].y))) {
                tmp = pts[i];
                pts[i] = pts[i-1];
                pts[i-1] = tmp;
                i=0;
                continue;
            }
        }

        // System.out.println("Sorted");
                
        Point2D.Float pt0 = pts[0];
        Point2D.Float pt1 = pts[numPts-1];
        Point2D.Float dxdy = new Point2D.Float(pt1.x-pt0.x, pt1.y-pt0.y);
        float soln, c = dxdy.y*pt0.x-dxdy.x*pt0.y;

        Point2D.Float [] topList = new Point2D.Float[numPts];
        Point2D.Float [] botList = new Point2D.Float[numPts];
        botList[0] = topList[0] = pts[0];
        int nTopPts=1; 
        int nBotPts=1;
        for (int i=1; i<numPts-1; i++) {
            Point2D.Float pt = pts[i];
            soln = dxdy.x*pt.y-dxdy.y*pt.x+c;
            if (soln < 0) {
                // Below line goes into bot pt list...
                while (nBotPts >= 2) {
                    pt0 = botList[nBotPts-2];
                    pt1 = botList[nBotPts-1];
                    float dx = pt1.x-pt0.x;
                    float dy = pt1.y-pt0.y;
                    float c0 = dy*pt0.x-dx*pt0.y;
                    soln = dx*pt.y-dy*pt.x+c0;
                    if (soln > eps) // Left turn add and we are done..
                        break;
                    if (soln > -eps) {
                        // On line take lowest Y of two and keep going
                        if (pt1.y < pt.y) pt = pt1;
                        nBotPts--;
                        break;
                    }
                    // right turn drop prev pt;
                    nBotPts--;
                }
                botList[nBotPts++] = pt;
            } else {
                // Above line goes into top pt list...
                while (nTopPts >= 2) {
                    pt0 = topList[nTopPts-2];
                    pt1 = topList[nTopPts-1];
                    float dx = pt1.x-pt0.x;
                    float dy = pt1.y-pt0.y;
                    float c0 = dy*pt0.x-dx*pt0.y;
                    soln = dx*pt.y-dy*pt.x+c0;
                    if (soln < -eps) // Right turn add and check next point.
                        break;
                    if (soln < eps) {
                        // On line take greatest Y of two and keep going
                        if (pt1.y > pt.y) pt = pt1;
                        nTopPts--;
                        break;
                    }
                    // left turn drop prev pt;
                    nTopPts--;
                }
                topList[nTopPts++] = pt;
            }
        }

        // Check last point in both sets...
        Point2D.Float pt = pts[numPts-1];
        while (nBotPts >= 2) {
            pt0 = botList[nBotPts-2];
            pt1 = botList[nBotPts-1];
            float dx = pt1.x-pt0.x;
            float dy = pt1.y-pt0.y;
            float c0 = dy*pt0.x-dx*pt0.y;
            soln = dx*pt.y-dy*pt.x+c0;
            if (soln > eps) 
                // Left turn add and we are done..
                break;
            if (soln > -eps) {
                // On line take lowest Y of two and keep going
                if (pt1.y >= pt.y) nBotPts--;
                break;
            }
            // right turn drop prev pt;
            nBotPts--;
        }

        while (nTopPts >= 2) {
            pt0 = topList[nTopPts-2];
            pt1 = topList[nTopPts-1];
            float dx = pt1.x-pt0.x;
            float dy = pt1.y-pt0.y;
            float c0 = dy*pt0.x-dx*pt0.y;
            soln = dx*pt.y-dy*pt.x+c0;
            if (soln < -eps) 
                // Right turn done...
                break;
            if (soln < eps) {
                // On line take lowest Y of two and keep going
                if (pt1.y <= pt.y) nTopPts--;
                break;
            }
            // left turn drop prev pt;
            nTopPts--;
        }

        int i=0;
        for (; i<nTopPts; i++)
            pts[i] = topList[i];

        // We always include the 'last' point as it is always on convex hull.
        pts[i++] = pts[numPts-1];

        // don't include botList[0] since it is the same as topList[0].
        for (int n=nBotPts-1; n>0; n--, i++)
            pts[i] = botList[n];

        // System.out.println("CHull has " + i + " pts");
        return i;
    }
    
    public static void addPtsToPath(GeneralPath shape,
                                     Point2D.Float [] topPts,
                                     Point2D.Float [] botPts,
                                     int numPts) {
        if (numPts < 2) return;
        if (numPts == 2) {
            shape.moveTo(topPts[0].x, topPts[0].y);
            shape.lineTo(topPts[1].x, topPts[1].y);
            shape.lineTo(botPts[1].x, botPts[1].y);
            shape.lineTo(botPts[0].x, botPts[0].y);
            shape.lineTo(topPts[0].x, topPts[0].y);
            return;
        }

        // Here we 'connect the dots' the best way we know how...
        // What I do is construct a convex hull between adjacent
        // character boxes, then I union that into the shape.  this
        // does a good job of bridging between adjacent characters,
        // but still closely tracking to text boxes.  The use of the
        // Area class is fairly heavy weight but it seems to keep up
        // in this instanace (probably because all the shapes are very
        // simple polygons).
        Point2D.Float [] boxes = new Point2D.Float[8];
        Point2D.Float [] chull = new Point2D.Float[8];
        boxes[4] = topPts[0];
        boxes[5] = topPts[1];
        boxes[6] = botPts[1];
        boxes[7] = botPts[0];
        Area []areas = new Area[numPts/2];
        int nAreas =0;
        for (int i=2; i<numPts; i+=2) {
            boxes[0] = boxes[4];
            boxes[1] = boxes[5];
            boxes[2] = boxes[6];
            boxes[3] = boxes[7];
            boxes[4] = topPts[i];
            boxes[5] = topPts[i+1];
            boxes[6] = botPts[i+1];
            boxes[7] = botPts[i];

            float delta,sz,dist;
            delta  = boxes[2].x-boxes[0].x;
            dist   = delta*delta;
            delta  = boxes[2].y-boxes[0].y;
            dist  += delta*delta;
            sz     = (float)Math.sqrt(dist);

            delta  = boxes[6].x-boxes[4].x;
            dist   = delta*delta;
            delta  = boxes[6].y-boxes[4].y;
            dist  += delta*delta;
            sz    += (float)Math.sqrt(dist);
            
            delta = ((boxes[0].x+boxes[1].x+boxes[2].x+boxes[3].x)-
                     (boxes[4].x+boxes[5].x+boxes[6].x+boxes[7].x))/4;
            dist = delta*delta;
            delta = ((boxes[0].y+boxes[1].y+boxes[2].y+boxes[3].y)-
                     (boxes[4].y+boxes[5].y+boxes[6].y+boxes[7].y))/4;
            dist += delta*delta;
            dist  = (float)Math.sqrt(dist);
            // Note here that dist is the distance between center
            // points, and sz is the sum of the length of the
            // diagonals of the letter boxes.  In normal cases one
            // would expect dist to be approximately equal to sz/2.
            // So here we merge if the two characters are within four
            // character widths of each other. If they are farther
            // apart than that chances are it's a 'line break' or
            // something similar where we will get better results
            // merging seperately, and anyways with this much space
            // between them the extra outline shouldn't hurt..
            GeneralPath gp = new GeneralPath();
            if (dist < 2*sz) {
                // Close enough to merge with previous char...
                System.arraycopy(boxes, 0, chull, 0, 8);
                int npts = makeConvexHull(chull, 8);
                gp.moveTo(chull[0].x, chull[0].y);
                for(int n=1; n<npts; n++)
                    gp.lineTo(chull[n].x, chull[n].y);
                gp.closePath();
            } else {
                // Merge all previous areas
                mergeAreas(shape, areas, nAreas);
                nAreas = 0; // Start fresh...
                
                // Then just add box (add the previous char box if first pts)
                if (i==2) {
                    gp.moveTo(boxes[0].x, boxes[0].y);
                    gp.lineTo(boxes[1].x, boxes[1].y);
                    gp.lineTo(boxes[2].x, boxes[2].y);
                    gp.lineTo(boxes[3].x, boxes[3].y);
                    gp.closePath();
                    shape.append(gp, false);
                    gp.reset();
                }
                gp.moveTo(boxes[4].x, boxes[4].y);
                gp.lineTo(boxes[5].x, boxes[5].y);
                gp.lineTo(boxes[6].x, boxes[6].y);
                gp.lineTo(boxes[7].x, boxes[7].y);
                gp.closePath();
            }
            areas[nAreas++] = new Area(gp);
        }

        mergeAreas(shape, areas, nAreas);
    }

    public static void mergeAreas(GeneralPath shape, 
                                  Area []shapes, int nShapes) {
        // Merge areas hierarchically, this means that while there are
        // the same number of Area.add calls (n-1) the great majority
        // of them are very simple combinations.  This helps to speed
        // things up a tad...
        while (nShapes > 1) {
            int n=0;
            for (int i=1; i<nShapes;i+=2) {
                shapes[i-1].add(shapes[i]);
                shapes[n++] = shapes[i-1];
                shapes[i] = null;
            }

            // make sure we include the last one if odd.
            if ((nShapes&0x1) == 1)
                shapes[n-1].add(shapes[nShapes-1]);
            nShapes = nShapes/2;
        }
        if (nShapes == 1)
            shape.append(shapes[0], false);
    }

    /**
     * Perform hit testing for coordinate at x, y.
     *
     * @param x the x coordinate of the point to be tested.
     * @param y the y coordinate of the point to be tested.
     *
     * @return a TextHit object encapsulating the character index for
     *     successful hits and whether the hit is on the character
     *     leading edge.
     */
    public TextHit hitTestChar(float x, float y) {
        TextHit textHit = null;

        // if this layout is transformed, need to apply the inverse
        // transform to the point
        if (transform != null) {
            try {
                Point2D p = new Point2D.Float(x, y);
                transform.inverseTransform(p, p);
                x = (float) p.getX();
                y = (float) p.getY();
            } catch (java.awt.geom.NoninvertibleTransformException nite) {;}
        }

        int currentChar = 0;
        for (int i = 0; i < gv.getNumGlyphs(); i++) {
            Shape gbounds = gv.getGlyphLogicalBounds(i);
            if (gbounds != null) {
                Rectangle2D gbounds2d = gbounds.getBounds2D();
                // System.out.println("Hit Test: [" + x + ", " + y + "] - " +
                //                    gbounds2d);
                if (gbounds.contains(x, y)) {
                    boolean isRightHalf =
                        (x > (gbounds2d.getX()+(gbounds2d.getWidth()/2d)));
                    boolean isLeadingEdge = !isRightHalf;
                    int charIndex = charMap[currentChar];
                    textHit = new TextHit(charIndex, isLeadingEdge);
                    return textHit;
                }
            }
            currentChar += getCharacterCount(i, i);
        }
        return textHit;
    }

    /**
     * Returns true if the advance direction of this text is vertical.
     */
    public boolean isVertical() {
        return vertical;
    }

    /**
     * Returns true if this layout in on a text path.
     */
    public boolean isOnATextPath() {
        return (textPath != null);
    }


    /**
     * Returns the number of glyphs in this layout.
     */
    public int getGlyphCount() {
        return gv.getNumGlyphs();
    }


    /**
     * Returns the number of chars represented by the glyphs within the
     * specified range.
     *
     * @param startGlyphIndex The index of the first glyph in the range.
     * @param endGlyphIndex The index of the last glyph in the range.
     *
     * @return The number of chars.
     */
    public int getCharacterCount(int startGlyphIndex, int endGlyphIndex) {
        return gv.getCharacterCount(startGlyphIndex, endGlyphIndex);
    }

    /**
     * Returns true if the text direction in this layout is from left to right.
     */
    public boolean isLeftToRight() {
        aci.first();
        int bidiLevel = ((Integer)aci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.BIDI_LEVEL)).intValue();
        return (Math.floor(bidiLevel/2.0) == Math.floor((bidiLevel+1)/2.0));
    }


//protected

    /**
     * Returns a shape describing the overline decoration for a given ACI.
     */
    protected Shape getOverlineShape() {
        double y = metrics.getOverlineOffset();
        float overlineThickness = metrics.getOverlineThickness();

        // need to move the overline a bit lower,
        // not sure if this is correct behaviour or not
        y += overlineThickness;

        Stroke overlineStroke =
            new BasicStroke(overlineThickness);
        Rectangle2D logicalBounds = gv.getLogicalBounds();

        return overlineStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           logicalBounds.getMinX() + overlineThickness/2.0, offset.getY()+y,
                           logicalBounds.getMaxX() - overlineThickness/2.0, offset.getY()+y));
    }

    /**
     * Returns a shape describing the strikethrough line for a given ACI.
     */
    protected Shape getUnderlineShape() {

        double y = metrics.getUnderlineOffset();
        float underlineThickness = metrics.getUnderlineThickness();

        // need to move the underline a bit lower,
        // not sure if this is correct behaviour or not
        y += underlineThickness*1.5;

        BasicStroke underlineStroke =
            new BasicStroke(underlineThickness);

        Rectangle2D logicalBounds = gv.getLogicalBounds();

        return underlineStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           logicalBounds.getMinX() + underlineThickness/2.0, offset.getY()+y,
                           logicalBounds.getMaxX() - underlineThickness/2.0, offset.getY()+y));
    }

    /**
     * Returns a shape describing the strikethrough line for a given ACI.
     */
    protected Shape getStrikethroughShape() {
        double y = metrics.getStrikethroughOffset();
        float strikethroughThickness = metrics.getStrikethroughThickness();

        Stroke strikethroughStroke =
            new BasicStroke(strikethroughThickness);

        Rectangle2D logicalBounds = gv.getLogicalBounds();
        return strikethroughStroke.createStrokedShape(
                           new java.awt.geom.Line2D.Double(
                           logicalBounds.getMinX() + strikethroughThickness/2.0, offset.getY()+y,
                           logicalBounds.getMaxX() - strikethroughThickness/2.0, offset.getY()+y));
    }

    /**
     * Returns the GVTFont to use when rendering the specified character iterator.
     * This should already be set as an attribute on the aci.
     *
     * @param aci The character iterator to get the font attribute from.
     *
     * @return The GVTFont to use.
     */
    protected GVTFont getFont(AttributedCharacterIterator aci) {
        aci.first();
        GVTFont gvtFont = (GVTFont)aci.getAttributes().get(
                GVTAttributedCharacterIterator.TextAttribute.GVT_FONT);
        if (gvtFont != null) {
            return gvtFont;
        } else {
            // shouldn't get here
            return new AWTGVTFont(aci.getAttributes());
        }
    }

    /**
     * If this layout is on a text path, positions the characters along the
     * path.
     *
     * @param offsetApplied Indicates whether or not the text position offset
     *        has already been applied.
     */
    protected void doPathLayout(boolean offsetApplied) {

        aci.first();
        textPath =  (TextPath) aci.getAttribute(
               GVTAttributedCharacterIterator.TextAttribute.TEXTPATH);

        // if doesn't have an attached text path, just return
        if (textPath == null) {
            return;
        }

        boolean horizontal = !isVertical();

        boolean glyphOrientationAuto = isGlyphOrientationAuto();
        int glyphOrientationAngle = 0;
        if (!glyphOrientationAuto) {
            glyphOrientationAngle = getGlyphOrientationAngle();
        }

        float pathLength = textPath.lengthOfPath();
        float startOffset = textPath.getStartOffset();

        // make sure all glyphs visible again, this maybe just a change in
        // offset so they may have been made invisible in a previous
        // pathLayout call
        for (int i = 0; i < gv.getNumGlyphs(); i++) {
            gv.setGlyphVisible(i, true);
        }

        // calculate the total length of the glyphs, this will become be
        // the length along the path that is used by the text
        float glyphsLength;
        if (horizontal) {
            glyphsLength = (float) gv.getLogicalBounds().getWidth();
        } else {
            glyphsLength = (float) gv.getLogicalBounds().getHeight();
        }

        // check that pathLength and glyphsLength are not 0
        if (pathLength == 0f || glyphsLength == 0f) {
            return;
        }

        // the current start point of the character on the path
        float currentPosition;
        if (horizontal) {
            currentPosition = (float)offset.getX() + startOffset;
        } else {
            currentPosition = (float)offset.getY() + startOffset;
        }
        int currentChar = aci.getBeginIndex();

        // calculate the offset of the first glyph
        // the offset will be 0 if the glyph is on the path (ie. not adjusted by
        // a dy or dx)
        Point2D firstGlyphPosition = gv.getGlyphPosition(0);
        float glyphOffset = 0;   // offset perpendicular to path
        if (offsetApplied) {
            if (horizontal) {
                glyphOffset = (float)firstGlyphPosition.getY();
            } else {
                glyphOffset = (float)firstGlyphPosition.getX();
            }
        }

        char ch = aci.first();
        int lastGlyphDrawn = -1;
        float lastGlyphAdvance = 0;

        // iterate through the GlyphVector placing each glyph
        for (int i = 0; i < gv.getNumGlyphs(); i++) {

            Point2D currentGlyphPosition = gv.getGlyphPosition(i);

            // calculate the advance and offset for the next glyph, do it
            // now before we modify the current glyph position

            float glyphAdvance = 0;  // along path
            float nextGlyphOffset = 0;  // perpendicular to path eg dy or dx
            if (i < gv.getNumGlyphs()-1) {

                Point2D nextGlyphPosition = gv.getGlyphPosition(i+1);
                if (horizontal) {
                    glyphAdvance = (float)(nextGlyphPosition.getX() - currentGlyphPosition.getX());
                    nextGlyphOffset = (float)(nextGlyphPosition.getY() - currentGlyphPosition.getY());
                } else {
                    glyphAdvance = (float)(nextGlyphPosition.getY() - currentGlyphPosition.getY());
                    nextGlyphOffset = (float)(nextGlyphPosition.getX() - currentGlyphPosition.getX());
                }
            } else {
                // last glyph, use the glyph metrics
                GVTGlyphMetrics gm = gv.getGlyphMetrics(i);
                if (horizontal) {
                    glyphAdvance = gm.getHorizontalAdvance();
                } else {
                    if (glyphOrientationAuto) {
                        if (isLatinChar(ch)) {
                            glyphAdvance = gm.getHorizontalAdvance();
                        } else {
                            glyphAdvance = gm.getVerticalAdvance();
                        }
                    } else {
                        if (glyphOrientationAngle == 0 || glyphOrientationAngle == 180) {
                            glyphAdvance = gm.getVerticalAdvance();
                        } else { // 90 || 270
                            glyphAdvance = gm.getHorizontalAdvance();
                        }
                    }
                }
            }

            // calculate the center line position for the glyph
            Rectangle2D glyphBounds = gv.getGlyphOutline(i).getBounds2D();
            float glyphWidth = (float) glyphBounds.getWidth();
            float glyphHeight = (float) glyphBounds.getHeight();

            float charMidPos;
            if (horizontal) {
                charMidPos = currentPosition + glyphWidth / 2f;
            } else {
                charMidPos = currentPosition + glyphHeight / 2f;
            }

            // Calculate the actual point to place the glyph around
            Point2D charMidPoint = textPath.pointAtLength(charMidPos);

            // Check if the glyph is actually on the path
            if (charMidPoint != null) {

                // Calculate the normal to the path (midline of glyph)
                float angle = textPath.angleAtLength(charMidPos);

                // Define the transform of the glyph
                AffineTransform glyphPathTransform = new AffineTransform();

                // rotate midline of glyph to be normal to path
                if (horizontal) {
                    glyphPathTransform.rotate(angle);
                } else {
                    glyphPathTransform.rotate(angle-(Math.PI/2));
                }

                // re-apply any offset eg from tspan, or spacing adjust
                if (horizontal) {
                    glyphPathTransform.translate(0, glyphOffset);
                } else {
                    glyphPathTransform.translate(glyphOffset, 0);
                }

                // translate glyph backwards so we rotate about the
                // center of the glyph
                if (horizontal) {
                    glyphPathTransform.translate(glyphWidth / -2f, 0f);
                } else {
                    if (glyphOrientationAuto) {
                        if (isLatinChar(ch)) {
                           glyphPathTransform.translate(0f, -glyphHeight/2f);
                        } else {
                            glyphPathTransform.translate(0f, glyphHeight/2f);
                        }
                    } else {
                        if (glyphOrientationAngle == 0 ) {
                            glyphPathTransform.translate(0f, glyphHeight/2f);
                        } else { // 90 || 180
                            glyphPathTransform.translate(0f, -glyphHeight/2f);
                        }
                    }
                }

                // set the new glyph position and transform
                AffineTransform glyphTransform = gv.getGlyphTransform(i);
                if (glyphTransform != null) {
                    glyphPathTransform.concatenate(glyphTransform);
                }

                gv.setGlyphTransform(i, glyphPathTransform);
                gv.setGlyphPosition(i, new Point2D.Double(charMidPoint.getX(),
                                                          charMidPoint.getY()));
                // keep track of the last glyph drawn to make calculating the
                // textPathAdvance value easier later
                lastGlyphDrawn = i;
                lastGlyphAdvance = glyphAdvance;

            } else {
                // not on path so don't render
                gv.setGlyphVisible(i, false);
            }
            currentPosition += glyphAdvance;
            glyphOffset += nextGlyphOffset;
            currentChar += gv.getCharacterCount(i,i);
            ch = aci.setIndex(aci.getBeginIndex() + i + gv.getCharacterCount(i,i));
        }

        // store the position where a following glyph should be drawn,
        // note: this will only be used if the following text layout is not
        //       on a text path
        if (lastGlyphDrawn > -1) {
            Point2D lastGlyphPos = gv.getGlyphPosition(lastGlyphDrawn);
            if (horizontal) {
                textPathAdvance = new Point2D.Double(lastGlyphPos.getX()+lastGlyphAdvance, lastGlyphPos.getY());
            } else {
                textPathAdvance = new Point2D.Double(lastGlyphPos.getX(), lastGlyphPos.getY()+lastGlyphAdvance);
            }
        } else {
            textPathAdvance = new Point2D.Double(0,0);
        }
    }

    /**
     * Does any spacing adjustments that may have been specified.
     */
    protected void adjustTextSpacing() {

        aci.first();
        Boolean customSpacing =  (Boolean) aci.getAttribute(
               GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING);
        Float length = (Float) aci.getAttribute(
               GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH);
        Integer lengthAdjust = (Integer) aci.getAttribute(
              GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST);
        if ((customSpacing != null) && customSpacing.booleanValue()) {
            applySpacingParams(length, lengthAdjust,
               (Float) aci.getAttribute(
               GVTAttributedCharacterIterator.TextAttribute.KERNING),
               (Float) aci.getAttribute(
               GVTAttributedCharacterIterator.TextAttribute.LETTER_SPACING),
               (Float) aci.getAttribute(
               GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING));
        }

        if (lengthAdjust ==
            GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL) {
             applyStretchTransform(length);
        }
    }

    /**
     * Stretches the text so that it becomes the specified length.
     *
     * @param length The required length of the text.
     */
    protected void applyStretchTransform(Float length) {
        if (length!= null && !length.isNaN()) {
            double xscale = 1d;
            double yscale = 1d;
            if (vertical) {
                yscale = length.floatValue()/gv.getVisualBounds().getHeight();
            } else {
                xscale = length.floatValue()/gv.getVisualBounds().getWidth();
            }
            Point2D startPos = gv.getGlyphPosition(0);
            for (int i = 0; i < gv.getNumGlyphs(); i++) {

                // transform the glyph position
                Point2D glyphPos = gv.getGlyphPosition(i);
                AffineTransform t = AffineTransform.getTranslateInstance(startPos.getX(), startPos.getY());
                t.scale(xscale,yscale);
                t.translate(-startPos.getX(), -startPos.getY());
                Point2D newGlyphPos = new Point2D.Float();
                t.transform(glyphPos, newGlyphPos);
                gv.setGlyphPosition(i, newGlyphPos);

                // stretch the glyph
                AffineTransform glyphTransform = gv.getGlyphTransform(i);
                if (glyphTransform != null) {
                    glyphTransform.preConcatenate(AffineTransform.getScaleInstance(xscale, yscale));
                    gv.setGlyphTransform(i, glyphTransform);
                } else {
                    gv.setGlyphTransform(i, AffineTransform.getScaleInstance(xscale, yscale));
                }
            }
        }
    }


    /**
     * Adjusts the spacing according to the specified parameters.
     *
     * @param length The required text length.
     * @param lengthAdjust Indicates the method to use when adjusting the text
     * length.
     * @param kern The kerning adjustment to apply to the space between each char.
     * @param letterSpacing The amount of spacing required between each char.
     * @param wordSpacing The amount of spacing required between each word.
     */
    protected void applySpacingParams(Float length,
                                      Integer lengthAdjust,
                                      Float kern,
                                      Float letterSpacing,
                                      Float wordSpacing) {

       /**
        * Two passes required when textLength is specified:
        * First, apply spacing properties,
        * then adjust spacing with new advances based on ratio
        * of expected length to actual advance.
        */

        advance = doSpacing(kern, letterSpacing, wordSpacing);
        if ((lengthAdjust ==
             GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING) &&
                 length!= null && !length.isNaN()) { // adjust if necessary
            float xscale = 1f;
            float yscale = 1f;
            if (!vertical) {
                float lastCharWidth =
                    (float) (gv.getGlyphMetrics(
                        gv.getNumGlyphs()-1).getBounds2D().getWidth());
                xscale = (length.floatValue()-lastCharWidth)/
                         (float) (gv.getVisualBounds().getWidth()-lastCharWidth);
            } else {
                yscale = length.floatValue()/(float) gv.getVisualBounds().getHeight();
            }
            rescaleSpacing(xscale, yscale);
        }
    }

    /**
     * Performs any spacing adjustments required and returns the new advance
     * value.
     *
     * @param kern The kerning adjustment to apply to the space between each char.
     * @param letterSpacing The amount of spacing required between each char.
     * @param wordSpacing The amount of spacing required between each word.
     */
    protected Point2D doSpacing(Float kern,
                                Float letterSpacing,
                                Float wordSpacing) {

        boolean autoKern = true;
        boolean doWordSpacing = false;
        boolean doLetterSpacing = false;
        float kernVal = 0f;
        float letterSpacingVal = 0f;
        float wordSpacingVal = 0f;

        if ((kern instanceof Float) && (!kern.isNaN())) {
            kernVal = kern.floatValue();
            autoKern = false;
            //System.out.println("KERNING: "+kernVal);
        }
        if ((letterSpacing instanceof Float) && (!letterSpacing.isNaN())) {
            letterSpacingVal = letterSpacing.floatValue();
            doLetterSpacing = true;
            //System.out.println("LETTER-SPACING: "+letterSpacingVal);
        }
        if ((wordSpacing instanceof Float) && (!wordSpacing.isNaN())) {
            wordSpacingVal = wordSpacing.floatValue();
            doWordSpacing = true;
            //System.out.println("WORD_SPACING: "+wordSpacingVal);
        }

        int numGlyphs = gv.getNumGlyphs();

        float dx = 0f;
        float dy = 0f;
        Point2D newPositions[] = new Point2D[numGlyphs];
        Point2D prevPos = gv.getGlyphPosition(0);
        float x = (float) prevPos.getX();
        float y = (float) prevPos.getY();

        Point2D lastCharAdvance
            = new Point2D.Double(advance.getX() - (gv.getGlyphPosition(numGlyphs-1).getX() - x),
                                 advance.getY() - (gv.getGlyphPosition(numGlyphs-1).getY() - y));

        try {
            // do letter spacing first
            if ((numGlyphs > 1) && (doLetterSpacing || !autoKern)) {
                for (int i=1; i<numGlyphs; ++i) {
                    Point2D gpos = gv.getGlyphPosition(i);
                    dx = (float)gpos.getX()-(float)prevPos.getX();
                    dy = (float)gpos.getY()-(float)prevPos.getY();
                    if (autoKern) {
                        if (vertical) dy += letterSpacingVal;
                        else dx += letterSpacingVal;
                    } else {
                        // apply explicit kerning adjustments,
                        // discarding any auto-kern dx values
                        if (vertical) {
                            dy = (float)
                            gv.getGlyphMetrics(i-1).getBounds2D().getHeight()+
                                kernVal + letterSpacingVal;
                        } else {
                            dx = (float)
                            gv.getGlyphMetrics(i-1).getBounds2D().getWidth()+
                                kernVal + letterSpacingVal;
                        }
                    }
                    x += dx;
                    y += dy;
                    newPositions[i] = new Point2D.Float(x, y);
                    prevPos = gpos;
                }

                for (int i=1; i<numGlyphs; ++i) { // assign the new positions
                    if (newPositions[i] != null) {
                        gv.setGlyphPosition(i, newPositions[i]);
                    }
                }
            }

             // adjust the advance of the last character
            if (autoKern) {
                if (vertical) {
                    lastCharAdvance.setLocation(lastCharAdvance.getX(),
                            lastCharAdvance.getY() + letterSpacingVal);
                } else {
                    lastCharAdvance.setLocation(lastCharAdvance.getX()
                            + letterSpacingVal, lastCharAdvance.getY());
                }
            } else {
                if (vertical) {
                    lastCharAdvance.setLocation(lastCharAdvance.getX(),
                        gv.getGlyphMetrics(numGlyphs-2).getBounds2D().getHeight()+
                                kernVal + letterSpacingVal);
                } else {
                    lastCharAdvance.setLocation(
                        gv.getGlyphMetrics(numGlyphs-2).getBounds2D().getWidth()+
                                kernVal + letterSpacingVal, lastCharAdvance.getY());
                }
            }


            // now do word spacing
            dx = 0f;
            dy = 0f;
            prevPos = gv.getGlyphPosition(0);
            x = (float) prevPos.getX();
            y = (float) prevPos.getY();

            if ((numGlyphs > 1) && (doWordSpacing)) {
                for (int i = 1; i < numGlyphs; i++) {
                    Point2D gpos = gv.getGlyphPosition(i);
                    dx = (float)gpos.getX()-(float)prevPos.getX();
                    dy = (float)gpos.getY()-(float)prevPos.getY();
                    boolean inWS = false;
                    // while this is whitespace, increment
                    int beginWS = i;
                    int endWS = i;
                    GVTGlyphMetrics gm = gv.getGlyphMetrics(i);

                    // BUG: gm.isWhitespace() fails for latin SPACE glyph!
                    while ((gm.getBounds2D().getWidth()<0.01d) || gm.isWhitespace()) {
                        if (!inWS) inWS = true;
                        if (i == numGlyphs-1) {
                            // white space at the end
                            break;
                        }
                        ++i;
                        ++endWS;
                        gpos = gv.getGlyphPosition(i);
                        gm = gv.getGlyphMetrics(i);
                    }

                    if ( inWS ) {  // apply wordSpacing
                        int nWS = endWS-beginWS;
                        float px = (float) prevPos.getX();
                        float py = (float) prevPos.getY();
                        dx = (float) (gpos.getX() - px)/(nWS+1);
                        dy = (float) (gpos.getY() - py)/(nWS+1);
                        if (vertical) {
                            dy += (float) wordSpacing.floatValue()/(nWS+1);
                        } else {
                            dx += (float) wordSpacing.floatValue()/(nWS+1);
                        }
                        for (int j=beginWS; j<=endWS; ++j) {
                            x += dx;
                            y += dy;
                            newPositions[j] = new Point2D.Float(x, y);
                        }
                    } else {
                        dx = (float) (gpos.getX()-prevPos.getX());
                        dy = (float) (gpos.getY()-prevPos.getY());
                        x += dx;
                        y += dy;
                        newPositions[i] = new Point2D.Float(x, y);
                    }
                    prevPos = gpos;
                }

                for (int i=1; i<numGlyphs; ++i) { // assign the new positions
                    if (newPositions[i] != null) {
                        gv.setGlyphPosition(i, newPositions[i]);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // calculate the new advance
        double advX = gv.getGlyphPosition(numGlyphs-1).getX()
                     - gv.getGlyphPosition(0).getX();
        double advY = gv.getGlyphPosition(numGlyphs-1).getY()
                     - gv.getGlyphPosition(0).getY();
        Point2D newAdvance = new Point2D.Double(advX + lastCharAdvance.getX(),
                                                advY + lastCharAdvance.getY());
        return newAdvance;
    }

    /**
     * Rescales the spacing between each char by the specified scale factors.
     *
     * @param xscale The amount to scale in the x direction.
     * @param yscale The amount to scale in the y direction.
     */
    protected void rescaleSpacing(float xscale, float yscale) {
        Rectangle2D bounds = gv.getVisualBounds();
        float initX = (float) bounds.getX();
        float initY = (float) bounds.getY();
        int numGlyphs = gv.getNumGlyphs();
        float dx = 0f;
        float dy = 0f;
        for (int i = 0; i < numGlyphs; i++) {
            Point2D gpos = gv.getGlyphPosition(i);
            dx = (float)gpos.getX()-initX;
            dy = (float)gpos.getY()-initY;
            gv.setGlyphPosition(i, new Point2D.Float(initX+dx*xscale,
                                                     initY+dy*yscale));
        }
        advance = new Point2D.Float((float)(initX+dx*xscale-offset.getX()),
                                    (float)(initY+dy*yscale-offset.getY()));
    }

    /**
     * Returns true if the specified character is within one of the Latin
     * unicode character blocks.
     *
     * @param c The char to test.
     *
     * @return True if c is latin.
     */
    protected boolean isLatinChar(char c) {

        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);

        if (block == Character.UnicodeBlock.BASIC_LATIN ||
            block == Character.UnicodeBlock.LATIN_1_SUPPLEMENT ||
            block == Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL ||
            block == Character.UnicodeBlock.LATIN_EXTENDED_A ||
            block == Character.UnicodeBlock.LATIN_EXTENDED_B) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Returns whether or not the vertical glyph orientation value is "auto".
     */
    protected boolean isGlyphOrientationAuto() {
        boolean glyphOrientationAuto = true;
        aci.first();
        if (aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION) != null) {
            glyphOrientationAuto = (aci.getAttribute(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION)
                                     == GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_AUTO);
        }
        return glyphOrientationAuto;
    }

    /**
     * Returns the value of the vertical glyph orientation angle. This will be
     * one of 0, 90, 180 or 270.
     */
    protected int getGlyphOrientationAngle() {

        int glyphOrientationAngle = 0;

        aci.first();
        Float angle = (Float)aci.getAttribute(GVTAttributedCharacterIterator.
            TextAttribute.VERTICAL_ORIENTATION_ANGLE);
        if (angle != null) {
            glyphOrientationAngle = (int)angle.floatValue();
        }
        // if not one of 0, 90, 180 or 270, round to nearest value
        if (glyphOrientationAngle != 0 || glyphOrientationAngle != 90
            || glyphOrientationAngle != 180 || glyphOrientationAngle != 270) {
            while (glyphOrientationAngle < 0) {
                glyphOrientationAngle += 360;
            }
            while (glyphOrientationAngle >= 360) {
                glyphOrientationAngle -= 360;
            }
            if (glyphOrientationAngle <= 45 || glyphOrientationAngle > 315) {
                glyphOrientationAngle = 0;
            } else if (glyphOrientationAngle > 45 && glyphOrientationAngle <= 135) {
                glyphOrientationAngle = 90;
            } else if (glyphOrientationAngle > 135 && glyphOrientationAngle <= 225) {
                glyphOrientationAngle = 180;
            } else {
                glyphOrientationAngle = 270;
            }
        }
        return glyphOrientationAngle;
    }


    /**
     * Explicitly lays out each of the glyphs in the glyph
     * vector. This will handle any glyph position adjustments such as
     * dx, dy and baseline offsets.  It will also handle vertical
     * layouts.
     *
     * @param applyOffset Specifies whether or not to add the offset position
     * to each of the glyph positions.  */
    protected void doExplicitGlyphLayout(boolean applyOffset) {

        float baselineAscent = vertical ?
                               (float) gv.getLogicalBounds().getWidth() :
                              (metrics.getAscent() + Math.abs(metrics.getDescent()));

        textPath =  (TextPath) aci.getAttribute(
               GVTAttributedCharacterIterator.TextAttribute.TEXTPATH);

        int numGlyphs = gv.getNumGlyphs();
        float[] gp = new float[numGlyphs*2];
        gp = (float[]) gv.getGlyphPositions(0, numGlyphs, gp).clone();
        float init_x_pos = (float) offset.getX();
        float init_y_pos = (float) offset.getY();
        float curr_x_pos = init_x_pos;
        float curr_y_pos = init_y_pos;
        float verticalFirstOffset = 0f;
        float largestAdvanceY = 0;

        boolean glyphOrientationAuto = isGlyphOrientationAuto();
        int glyphOrientationAngle = 0;
        if (!glyphOrientationAuto) {
            glyphOrientationAngle = getGlyphOrientationAngle();
        }
        int i=0, aciIndex = aci.getBeginIndex();
        char ch = aci.first();
        int runLimit = aciIndex;
        Float x=null, y=null, dx=null, dy=null, rotation=null;
        Object baseline=null;
        boolean firstChar = true;
        while (i < numGlyphs) {
            //System.out.println("limit: " + runLimit + ", " + aciIndex);
            if (aciIndex >= runLimit) {
                runLimit = aci.getRunLimit(runAtts);
                x        = (Float) aci.getAttribute(X);
                y        = (Float) aci.getAttribute(Y);
                dx       = (Float) aci.getAttribute(DX);
                dy       = (Float) aci.getAttribute(DY);
                rotation = (Float) aci.getAttribute(ROTATION);
                baseline = aci.getAttribute(BASELINE_SHIFT);
            }

            GVTGlyphMetrics gm = gv.getGlyphMetrics(i);

            if (firstChar) {
                if (glyphOrientationAuto) {
                    if (isLatinChar(ch)) {
                        // it will be rotated 90
                        verticalFirstOffset = 0f;
                    } else {
                        // it won't be rotated
                        verticalFirstOffset = (float)gm.getBounds2D().getHeight();
                    }
                } else {
                    if (glyphOrientationAngle == 0) {
                        verticalFirstOffset = (float)gm.getBounds2D().getHeight();
                    } else {
                        verticalFirstOffset = 0f;
                    }
                }
            } else {
                if (glyphOrientationAuto && (verticalFirstOffset == 0f)
                    && !isLatinChar(ch)) {

                    verticalFirstOffset = (float)gm.getBounds2D().getHeight();
                }
            }

            // ox and oy are origin adjustments for each glyph,
            // computed on the basis of baseline-shifts, etc.
            float ox = 0f;
            float oy = 0f;
            float verticalGlyphRotation = 0f;
            float glyphRotation = 0f;
            

            if (ch != CharacterIterator.DONE) {
                if (vertical) {
                    if (glyphOrientationAuto) {
                        if (isLatinChar(ch)) {
                            // If character is Latin, then rotate by
                            // 90 degrees
                            verticalGlyphRotation = (float) (Math.PI / 2f);
                        } else {
                            verticalGlyphRotation = 0f;
                        }
                    } else {
                        verticalGlyphRotation = (float)Math.toRadians(glyphOrientationAngle);
                    }
                    if (textPath != null) {
                        // if vertical and on a path, any x's are ignored
                        x = null;
                    }
                } else {
                    if (textPath != null) {
                        // if horizontal and on a path, any y's are ignored
                        y = null;
                    }
                }

                // calculate the total rotation for this glyph
                if (rotation == null || rotation.isNaN()) {
                    glyphRotation = verticalGlyphRotation;
                } else {
                    glyphRotation = (rotation.floatValue() + 
                                     verticalGlyphRotation);
                }

                if (x!= null && !x.isNaN()) {
                    if (i==0) {
                        if (applyOffset) {
                            curr_x_pos = (float) offset.getX();
                        } else {
                            curr_x_pos = x.floatValue();
                            init_x_pos = curr_x_pos;
                        }
                    } else {
                        curr_x_pos = x.floatValue();
                    }
                } else if (dx != null && !dx.isNaN()) {
                    curr_x_pos += dx.floatValue();
                }

                if (y != null && !y.isNaN()) {
                    if (i==0) {
                        if (applyOffset) {
                            curr_y_pos = (float) offset.getY();
                        } else {
                            curr_y_pos = y.floatValue();
                            init_y_pos = curr_y_pos;
                        }
                    } else {
                        curr_y_pos = y.floatValue();
                    }
                } else if (dy != null && !dy.isNaN()) {
                    curr_y_pos += dy.floatValue();
                } else if (i>0) {
                    curr_y_pos += gp[i*2 + 1]-gp[i*2 - 1];
                }

                float baselineAdjust = 0f;
                if (baseline != null) {
                    if (baseline instanceof Integer) {
                        if (baseline==TextAttribute.SUPERSCRIPT_SUPER) {
                            baselineAdjust = baselineAscent*0.5f;
                        } else if (baseline==TextAttribute.SUPERSCRIPT_SUB) {
                            baselineAdjust = -baselineAscent*0.5f;
                        }
                    } else if (baseline instanceof Float) {
                        baselineAdjust = ((Float) baseline).floatValue();
                    }
                    if (vertical) {
                        ox = baselineAdjust;
                    } else {
                        oy = -baselineAdjust;
                    }
                }

                if (vertical) {
                    // offset due to rotation of first character
                    oy += verticalFirstOffset;

                    if (glyphOrientationAuto) {
                        if (isLatinChar(ch)) {
                            ox += metrics.getStrikethroughOffset();
                        } else {
                            Rectangle2D glyphBounds = gv.getGlyphVisualBounds(i).getBounds2D();
                            Point2D glyphPos = gv.getGlyphPosition(i);
                            ox -= (float)((glyphBounds.getMaxX() - glyphPos.getX()) - glyphBounds.getWidth()/2);
                        }
                    } else {
                        // center the character if it's not auto orient
                        Rectangle2D glyphBounds = gv.getGlyphVisualBounds(i).getBounds2D();
                        Point2D glyphPos = gv.getGlyphPosition(i);
                        if (glyphOrientationAngle == 0) {
                            ox -= (float)((glyphBounds.getMaxX() - glyphPos.getX()) - glyphBounds.getWidth()/2);
                        } else if (glyphOrientationAngle == 180) {
                            ox += (float)((glyphBounds.getMaxX() - glyphPos.getX()) - glyphBounds.getWidth()/2);
                        } else if (glyphOrientationAngle == 90) {
                            ox += metrics.getStrikethroughOffset();
                        } else { // 270
                            ox -= metrics.getStrikethroughOffset();
                        }
                    }
                }
            }

            // set the new glyph position
            gv.setGlyphPosition(i, new Point2D.Float(curr_x_pos+ox,curr_y_pos+oy));

            // calculte the position of the next glyph
            if (!ArabicTextHandler.arabicCharTransparent(ch)) {
                // only apply the advance if the current char is not transparen
                if (vertical) {
                    float advanceY = 0;
                    if (glyphOrientationAuto) {
                        if (isLatinChar(ch)) {
                            advanceY = gm.getHorizontalAdvance();
                        } else {
                            advanceY = gm.getVerticalAdvance();
                        }
                    } else {
                        if ((glyphOrientationAngle ==   0) || 
                            (glyphOrientationAngle == 180)) {
                            advanceY = gm.getVerticalAdvance();
                        } else if (glyphOrientationAngle == 90) {
                            advanceY = gm.getHorizontalAdvance();
                        } else { // 270
                            advanceY = gm.getHorizontalAdvance();
                            // need to translate so that the spacing
                            // between chars is correct
                            gv.setGlyphTransform(i, AffineTransform.getTranslateInstance(0, advanceY));
                        }
                    }
                    curr_y_pos += advanceY;
                } else {
                    curr_x_pos += gm.getHorizontalAdvance();
                }
            }

            // rotate the glyph
            if (glyphRotation != 0f) {
                AffineTransform glyphTransform = gv.getGlyphTransform(i);
                if (glyphTransform == null) {
                    glyphTransform = new AffineTransform();
                }
                glyphTransform.rotate(glyphRotation);
                gv.setGlyphTransform(i, glyphTransform);
            }
            
            aciIndex += gv.getCharacterCount(i,i);
            ch = aci.setIndex(aciIndex);
            i++;
            firstChar = false;
        }

        advance = new Point2D.Float((float) (curr_x_pos - offset.getX()),
                                    (float) (curr_y_pos - offset.getY()));

        offset = new Point2D.Float(init_x_pos, init_y_pos);
    }

}
