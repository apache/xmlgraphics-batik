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
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

/**
 * Class that performs layout of attributed text strings into
 * glyph sets paintable by TextPainter instances.
 * Similar to java.awt.font.TextLayout in function and purpose.
 * Note that while this utility interface is provided for the convenience of
 * <tt>TextPainter</tt> implementations, conforming <tt>TextPainter</tt>s
 * are not required to use this class.
 * @see java.awt.font.TextLayout
 * @see org.apache.batik.gvt.TextPainter.
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */
public interface TextSpanLayout {

    public int DECORATION_UNDERLINE = 0x1;
    public int DECORATION_STRIKETHROUGH = 0x2;
    public int DECORATION_OVERLINE = 0x4;
    public int DECORATION_ALL = DECORATION_UNDERLINE |
                                DECORATION_OVERLINE |
                                DECORATION_STRIKETHROUGH;

    /**
     * Paints the specified text layout using the
     * specified Graphics2D and rendering context.
     * @param g2d the Graphics2D to use
     * @param x the x position of the rendered layout origin.
     * @param y the y position of the rendered layout origin.
     */
    public void draw(Graphics2D g2d, float x, float y);

    /**
     * Returns the outline of the completed glyph layout, transformed
     * by an AffineTransform.
     * @param t an AffineTransform to apply to the outline before returning it.
     */
    public Shape getOutline(AffineTransform t);

    /**
     * Returns the outline of the specified decorations on the glyphs,
     * transformed by an AffineTransform.
     * @param decorationType an integer indicating the type(s) of decorations
     *     included in this shape.  May be the result of "OR-ing" several
     *     values together:
     * e.g. <tt>DECORATION_UNDERLINE | DECORATION_STRIKETHROUGH</tt>
     * @param t an AffineTransform to apply to the outline before returning it.
     */
    public Shape getDecorationOutline(int decorationType, AffineTransform t);

    /**
     * Returns the rectangular bounds of the completed glyph layout.
     */
    public Rectangle2D getBounds();

    /**
     * Returns the rectangular bounds of the completed glyph layout,
     * inclusive of "decoration" (underline, overline, etc.)
     */
    public Rectangle2D getDecoratedBounds();

    /**
     * Returns the dimension of the completed glyph layout in the
     * primary text advance direction (e.g. width, for RTL or LTR text).
     * (This is the dimension that should be used for positioning
     * adjacent layouts.)
     */
    public float getAdvance();

    /**
     * Returns a Shape which encloses the currently selected glyphs
     * as specified by glyph indices <tt>begin/tt> and <tt>end</tt>.
     * @param begin the index of the first glyph in the contiguous selection.
     * @param end the index of the last glyph in the contiguous selection.
     */
    public Shape getLogicalHighlightShape(int begin, int end);

    /**
     * Perform hit testing for coordinate at x, y.
     * @return a TextHit object encapsulating the character index for
     *     successful hits and whether the hit is on the character
     *     leading edge.
     * @param x the x coordinate of the point to be tested.
     * @param y the y coordinate of the point to be tested.
     */
    public TextHit hitTestChar(float x, float y);

    /**
     * Returns true if the advance direction of this text is vertical.
     */
    public boolean isVertical();

    /**
     * Returns the number of characters in this layout.
     */
    public int getCharacterCount();

}
