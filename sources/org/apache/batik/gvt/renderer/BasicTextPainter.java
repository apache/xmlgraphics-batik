/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.text.AttributedCharacterIterator;

import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.TextPainter;

import org.apache.batik.gvt.text.ConcreteTextLayoutFactory;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.Mark;
import org.apache.batik.gvt.text.TextHit;
import org.apache.batik.gvt.text.TextLayoutFactory;
import org.apache.batik.gvt.text.TextSpanLayout;

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

    /**
     * Internal Cache.
     */
    protected Mark cachedMark = null;

    /**
     * Internal Cache.
     */
    protected AttributedCharacterIterator cachedACI = null;

    /**
     * Internal Cache.
     */
    protected TextHit cachedHit = null;


    protected TextLayoutFactory getTextLayoutFactory() {
        return textLayoutFactory;
    }

    /**
     * Given an X, y coordinate, AttributedCharacterIterator,
     * return a Mark which encapsulates a "selection start" action.
     * The standard order of method calls for selection is:
     * selectAt(); [selectTo(),...], selectTo(); getSelection().
     */
    public Mark selectAt(double x, double y,
                         AttributedCharacterIterator aci,
                         TextNode node) {
        Mark newMark = hitTest(x, y, aci, node);
        cachedHit = null;
        return newMark;
    }

    /**
     * Given an X, y coordinate, starting Mark, AttributedCharacterIterator,
     * return a Mark which encapsulates a "selection continued" action.
     * The standard order of method calls for selection is:
     * selectAt(); [selectTo(),...], selectTo(); getSelection().
     */
    public Mark selectTo(double x, 
			 double y,
			 Mark beginMark,
			 AttributedCharacterIterator aci,
			 TextNode node) {
        return hitTest(x, y, aci, node);
    }

    /**
     * Select the entire contents of an
     * AttributedCharacterIterator, and
     * return a Mark which encapsulates that selection action.
     */
    public Mark selectAll(double x, 
			  double y,
			  AttributedCharacterIterator aci,
			  TextNode node) {
        return hitTest(x, y, aci, node);
    }


    /**
     * Gets a Rectangle2D in userspace coords which encloses the textnode glyphs
     * composed from an AttributedCharacterIterator.
     *
     * @param node the TextNode to measure
     */
     public Rectangle2D getBounds(TextNode node) {
         return getBounds(node, false, false);
     }

    /**
     * Gets a Rectangle2D in userspace coords which encloses the textnode glyphs
     * composed from an AttributedCharacterIterator, inclusive of glyph
     * decoration (underline, overline, strikethrough).
     *
     * @param node the TextNode to measure
     */
     public Rectangle2D getDecoratedBounds(TextNode node) {
         return getBounds(node, true, false);
     }

    /**
     * Gets a Rectangle2D in userspace coords which encloses the textnode glyphs
     * (as-painted, inclusive of decoration and stroke, but exclusive of
     * filters, etc.) composed from an AttributedCharacterIterator.
     *
     * @param node the TextNode to measure 
     */
     public Rectangle2D getPaintedBounds(TextNode node) {
         return getBounds(node, true, true);
     }

    /**
     * Gets a Shape in userspace coords which defines the textnode glyph
     * outlines.
     *
     * @param node the TextNode to measure
     */
    public Shape getShape(TextNode node) {
        return getOutline(node, false);
    }
    
    /**
     * Gets a Shape in userspace coords which defines the decorated textnode
     * glyph outlines.
     *
     * @param node the TextNode to measure 
     */
    public Shape getDecoratedShape(TextNode node) {
	return getOutline(node, true);
    }

    // ------------------------------------------------------------------------
    // Abstract methods
    // ------------------------------------------------------------------------

    /**
     * Gets a Rectangle2D in userspace coords which encloses the textnode
     * glyphs composed from an AttributedCharacterIterator.
     *
     * @param node the TextNode to measure
     * @param includeDecoration whether to include text decoration in bounds
     * computation.
     * @param includeStrokeWidth whether to include the effect of stroke width
     * in bounds computation.  
     */
     protected abstract Rectangle2D getBounds(TextNode node,
					      boolean includeDecoration,
					      boolean includeStrokeWidth);

    /**
     * Gets a Shape in userspace coords which defines the textnode glyph
     * outlines.
     *
     * @param node the TextNode to measure
     * @param includeDecoration whether to include text decoration outlines
     */
    protected abstract Shape getOutline(TextNode node, 
					boolean includeDecoration);

    /**
     * Gets a Shape in userspace coords which defines the stroked textnode glyph
     * outlines.
     *
     * @param node the TextNode to measure
     * @param includeDecoration whether to include text decoration outlines 
     */
    protected abstract Shape getStrokeOutline(TextNode node,
					      boolean includeDecoration);

    /**
     * Returns the mark for the specified parameters.
     */
    protected abstract Mark hitTest(double x, 
				    double y, 
				    AttributedCharacterIterator aci,
				    TextNode node);


    // ------------------------------------------------------------------------
    // Inner class - implementation of the Mark interface
    // ------------------------------------------------------------------------

    /**
     * This TextPainter's implementation of the Mark interface.
     */
    protected static class BasicMark implements Mark {
	
        private TextHit hit;
        private TextSpanLayout layout;
        private double x;
        private double y;

	/**
	 * Constructs a new Mark with the specified parameters.
	 */
        protected BasicMark(double x, 
			    double y, 
			    TextSpanLayout layout, 
			    TextHit hit) {
            this.x = x;
            this.y = y;
            this.layout = layout;
            this.hit = hit;
        }

        public TextHit getHit() {
            return hit;
        }

        public TextSpanLayout getLayout() {
            return layout;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}


