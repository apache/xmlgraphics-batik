/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.text.Mark;

/**
 * Renders the attributed character iterator of a <tt>TextNode</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface TextPainter {

    /**
     * Paints the specified attributed character iterator using the specified
     * Graphics2D and context and font context.
     *
     * @param node the TextNode to paint
     * @param g2d the Graphics2D to use
     * @param context the rendering context.
     */
    void paint(TextNode node, Graphics2D g2d);

    /**
     * Initiates a text selection on a particular AttributedCharacterIterator,
     * using the text/font metrics employed by this TextPainter instance.
     */
    Mark selectAt(double x, double y, TextNode node);

    /**
     * Continues a text selection on a particular AttributedCharacterIterator,
     * using the text/font metrics employed by this TextPainter instance.
     */
    Mark selectTo(double x, double y, Mark beginMark);

    /**
     * Selects the first glyph in the text node.
     */
    Mark selectFirst(TextNode node);


    /**
     * Selects the last glyph in the text node.
     */
    Mark selectLast(TextNode node);

    /**
     * Returns a mark for the char at index in node's
     * AttributedCharacterIterator.  Leading edge indicates if the 
     * mark should be considered immediately 'before' glyph or
     * after
     */
     Mark getMark(TextNode node, int index, boolean beforeGlyph);

    /**
     * Get an array of index pairs corresponding to the indices within an
     * AttributedCharacterIterator regions bounded by two Marks.
     *
     * Note that the instances of Mark passed to this function <em>must
     * come</em> from the same TextPainter that generated them via selectAt()
     * and selectTo(), since the TextPainter implementation may rely on hidden
     * implementation details of its own Mark implementation.  */
    int[] getSelected(Mark start, Mark finish);
    

    /**
     * Get a Shape in userspace coords which encloses the textnode
     * glyphs bounded by two Marks.
     * Note that the instances of Mark passed to this function
     * <em>must come</em>
     * from the same TextPainter that generated them via selectAt() and
     * selectTo(), since the TextPainter implementation may rely on hidden
     * implementation details of its own Mark implementation.
     */
    Shape getHighlightShape(Mark beginMark, Mark endMark);

    /**
     * Get a Shape in userspace coords which defines the textnode 
     * glyph outlines.
     * @param node the TextNode to measure
     */
    Shape getOutline(TextNode node);

    /**
     * Get a Rectangle2D in userspace coords which encloses the textnode
     * glyphs rendered bounds (includes stroke etc).
     * @param node the TextNode to measure
     */
    Rectangle2D getBounds2D(TextNode node);

    /**
     * Get a Rectangle2D in userspace coords which encloses the textnode
     * glyphs just including the geometry info.
     * @param node the TextNode to measure
     */
    Rectangle2D getGeometryBounds(TextNode node);
}

