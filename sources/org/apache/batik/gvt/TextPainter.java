/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.text.AttributedCharacterIterator;
import java.awt.image.renderable.RenderContext;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;

import org.apache.batik.gvt.text.Mark;

/**
 * Renders the attributed character iterator of a <tt>TextNode</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface TextPainter {

    /**
     * Paints the specified attributed character iterator using the
     * specified Graphics2D and context and font context.
     * @param shape the shape to paint
     * @param g2d the Graphics2D to use
     * @param context rendering context.
     */
    void paint(AttributedCharacterIterator aci,
               Point2D location,
               TextNode.Anchor anchor,
               Graphics2D g2d,
               GraphicsNodeRenderContext context);

    /**
     * Initiates a text selection on a particular AttributedCharacterIterator,
     * using the text/font metrics employed by this TextPainter instance.
     * @param x the x coordinate, in the text layout's coordinate system,
     *       of the selection event.
     * @param y the y coordinate, in the text layout's coordinate system,
     *       of the selection event.
     * @param aci the AttributedCharacterIterator describing the text
     * @param anchor the text anchor (alignment) type of this text
     * @param context the GraphicsNodeRenderContext to use when doing text layout.
     * @return an instance of Mark which encapsulates the state necessary to
     * implement hit testing and text selection.
     */
    public Mark selectAt(double x, double y, AttributedCharacterIterator aci,
                         TextNode.Anchor anchor,
                         GraphicsNodeRenderContext context);

    /**
     * Continues a text selection on a particular AttributedCharacterIterator,
     * using the text/font metrics employed by this TextPainter instance.
     * @param x the x coordinate, in the text layout's coordinate system,
     *       of the selection event.
     * @param y the y coordinate, in the text layout's coordinate system,
     *       of the selection event.
     * @param aci the AttributedCharacterIterator describing the text
     * @param anchor the text anchor (alignment) type of this text
     * @param context the GraphicsNodeRenderContext to use when doing text layout.
     * @return an instance of Mark which encapsulates the state necessary to
     * implement hit testing and text selection.
     */
    public Mark selectTo(double x, double y, Mark beginMark,
                            AttributedCharacterIterator aci,
                            TextNode.Anchor anchor,
                            GraphicsNodeRenderContext context);

    /**
     * Select all of the text represented by an AttributedCharacterIterator,
     * using the text/font metrics employed by this TextPainter instance.
     * @param x the x coordinate, in the text layout's coordinate system,
     *       of the selection event.
     * @param y the y coordinate, in the text layout's coordinate system,
     *       of the selection event.
     * @param aci the AttributedCharacterIterator describing the text
     * @param anchor the text anchor (alignment) type of this text
     * @param context the GraphicsNodeRenderContext to use when doing text layout.
     * @return an instance of Mark which encapsulates the state necessary to
     * implement hit testing and text selection.
     */
    public Mark selectAll(double x, double y,
                            AttributedCharacterIterator aci,
                            TextNode.Anchor anchor,
                            GraphicsNodeRenderContext context);

    /*
     * Get an array of index pairs corresponding to the indices within an
     * AttributedCharacterIterator regions bounded by two Marks.
     * Note that the instances of Mark passed to this function
     * <em>must come</em>
     * from the same TextPainter that generated them via selectAt() and
     * selectTo(), since the TextPainter implementation may rely on hidden
     * implementation details of its own Mark implementation.
     */
    public int[] getSelected(AttributedCharacterIterator aci,
                             Mark start, Mark finish);


    /*
     * Get a Shape which encloses the textnode glyphs bounded by two Marks.
     * Note that the instances of Mark passed to this function
     * <em>must come</em>
     * from the same TextPainter that generated them via selectAt() and
     * selectTo(), since the TextPainter implementation may rely on hidden
     * implementation details of its own Mark implementation.
     */
     public Shape getHighlightShape(Mark beginMark, Mark endMark);

}
