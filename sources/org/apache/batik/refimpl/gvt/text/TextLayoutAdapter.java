/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.text;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.font.TextLayout;
import java.awt.font.TextHitInfo;
import org.apache.batik.gvt.text.TextSpanLayout;
import org.apache.batik.gvt.text.TextHit;

/**
 * Implementation of TextSpanLayout that uses java.awt.font.TextLayout
 * for its internals.
 * @see java.awt.font.TextLayout
 * @see org.apache.batik.gvt.TextPainter.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public class TextLayoutAdapter implements TextSpanLayout {

    private TextLayout layout;

    public TextLayoutAdapter(TextLayout layout) {
        this.layout = layout;
    }

    /**
     * Paints the specified text layout using the
     * specified Graphics2D and rendering context.
     * @param g2d the Graphics2D to use
     * @param x the x position of the rendered layout origin.
     * @param y the y position of the rendered layout origin.
     */
    public void draw(Graphics2D g2d, float x, float y) {
        layout.draw(g2d, x, y);
    }

    /**
     * Returns the outline of the completed glyph layout, transformed
     * by an AffineTransform.
     * @param t an AffineTransform to apply to the outline before returning it.
     */
    public Shape getOutline(AffineTransform t) {
        return layout.getOutline(t);
    }

    /**
     * Returns the rectangular bounds of the completed glyph layout.
     */
    public Rectangle2D getBounds() {
        return layout.getBounds();
    }

    /**
     * Returns the dimension of the completed glyph layout in the
     * primary text advance direction (e.g. width, for RTL or LTR text).
     * (This is the dimension that should be used for positioning 
     * adjacent layouts.)
     */
    public float getAdvance() {
        return layout.getAdvance();
    }

    /**
     * Returns a Shape which encloses the currently selected glyphs
     * as specified by glyph indices <tt>begin/tt> and <tt>end</tt>.
     * @param begin the index of the first glyph in the contiguous selection.
     * @param end the index of the last glyph in the contiguous selection.
     */
    public Shape getLogicalHighlightShape(int begin, int end) {
        return layout.getLogicalHighlightShape(begin, end);
    }

    /**
     * Perform hit testing for coordinate at x, y.
     * @return a TextHit object encapsulating the character index for
     *     successful hits and whether the hit is on the character 
     *     leading edge.
     * @param x the x coordinate of the point to be tested.
     * @param y the y coordinate of the point to be tested.
     */
    public TextHit hitTestChar(float x, float y) {
        TextHitInfo hit = layout.hitTestChar(x, y);
        return new TextHit(hit.getCharIndex(), hit.isLeadingEdge());
    }

    public int getCharacterCount() {
        return layout.getCharacterCount();
    }

    public float getAscent() {
        return layout.getAscent();
    }

    public float getDescent() {
        return layout.getDescent();
    }

    public float[] getBaselineOffsets() {
        return layout.getBaselineOffsets();
    }

    public boolean isVertical() {
        return layout.isVertical();
    }

}