/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.text.ConcreteTextLayoutFactory;
import org.apache.batik.gvt.text.Mark;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.text.TextLayoutFactory;

/**
 * Basic implementation of TextPainter which
 * renders the attributed character iterator of a <tt>TextNode</tt>.
 * Suitable for use with "standard" java.awt.font.TextAttributes only.
 * @see java.awt.font.TextAttribute
 *
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @author <a href="vincent.hardy@sun.com>Vincent Hardy</a>
 * @version $Id$
 */
public abstract class BasicTextPainter implements TextPainter {

    private static TextLayoutFactory textLayoutFactory =
	new ConcreteTextLayoutFactory();

    /**
     * The font render context to use.
     */
    protected FontRenderContext fontRenderContext =
	new FontRenderContext(new AffineTransform(), true, true);

    protected TextLayoutFactory getTextLayoutFactory() {
        return textLayoutFactory;
    }

    /**
     * Given an X, y coordinate, AttributedCharacterIterator,
     * return a Mark which encapsulates a "selection start" action.
     * The standard order of method calls for selection is:
     * selectAt(); [selectTo(),...], selectTo(); getSelection().
     */
    public Mark selectAt(double x, double y, TextNode node) {
        return hitTest(x, y, node);
    }

    /**
     * Given an X, y coordinate, starting Mark, AttributedCharacterIterator,
     * return a Mark which encapsulates a "selection continued" action.
     * The standard order of method calls for selection is:
     * selectAt(); [selectTo(),...], selectTo(); getSelection().
     */
    public Mark selectTo(double x, double y, Mark beginMark) {
	if (beginMark == null) {
	    return null;
	} else {
	    return hitTest(x, y, beginMark.getTextNode());
	}
    }


    /**
     * Get a Rectangle2D in userspace coords which encloses the textnode
     * glyphs just including the geometry info.
     * @param node the TextNode to measure
     */
    public Rectangle2D getGeometryBounds(TextNode node) {
        return getOutline(node).getBounds2D();
    }

    /**
     * Returns the mark for the specified parameters.
     */
    protected abstract Mark hitTest(double x, double y, TextNode node);

    // ------------------------------------------------------------------------
    // Inner class - implementation of the Mark interface
    // ------------------------------------------------------------------------

    /**
     * This TextPainter's implementation of the Mark interface.
     */
    protected static class BasicMark implements Mark {
	
        private TextNode       node;
        private TextHit        hit;

	/**
	 * Constructs a new Mark with the specified parameters.
	 */
        protected BasicMark(TextNode node,
                            TextHit hit) {
            this.hit    = hit;
            this.node   = node;
        }

        public TextHit getHit() {
            return hit;
        }

        public TextNode getTextNode() {
            return node;
        }
    }
}


