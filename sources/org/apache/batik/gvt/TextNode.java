/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

import java.awt.font.FontRenderContext;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;

import java.util.List;

import org.apache.batik.gvt.renderer.StrokingTextPainter;

import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.Mark;

/**
 * A graphics node that represents text.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TextNode extends AbstractGraphicsNode implements Selectable {

    /**
     * Location of this text node (inherited, independent of explicit
     * X and Y attributes applied to children).
     */
    protected Point2D location = new Point2D.Float(0, 0);

    /**
     * Attributed Character Iterator describing the text
     */
    protected AttributedCharacterIterator aci;

    /**
     * The text of this <tt>TextNode</tt>.
     */
    protected String text;

    /**
     * The begin mark.
     */
    protected Mark beginMark = null;

    /**
     * The end mark.
     */
    protected Mark endMark = null;

    /**
     * The list of text runs.
     */
    protected List textRuns;

    /**
     * An array of text chunks.
     */
    protected AttributedCharacterIterator[] chunkACIs = null;

    /**
     * The text painter used to display the text of this text node.
     */
    protected TextPainter textPainter = new StrokingTextPainter();

    /**
     * Internal Cache: Bounds for this text node, without taking any of the
     * rendering attributes (e.g., stroke) into account
     */
    private Rectangle2D geometryBounds;

    /**
     * Internal Cache: Primitive Bounds.
     */
    private Rectangle2D primitiveBounds;

    /**
     * Internal Cache: the outline.
     */
    private Shape outline;

    /**
     * Constructs a new empty <tt>TextNode</tt>.
     */
    public TextNode() {
    }


    /**
     * Returns a list of text runs.
     */
    public List getTextRuns() {
        return textRuns;
    }

    /**
     * Sets the list of text runs of this text node.
     *
     * @param textRuns the new list of text runs
     */
    public void setTextRuns(List textRuns) {
        this.textRuns = textRuns;
    }

    /**
     * Returns an array of text chuncks as AttributedCharacterIterator.
     */
    public AttributedCharacterIterator[] getChunkACIs() {
        return chunkACIs;
    }

    /**
     * Sets the text chunks of this text node.
     *
     * @param chunkACIs the new text chunks of this text node
     */
    public void setChunkACIs(AttributedCharacterIterator[] chunkACIs) {
        this.chunkACIs = chunkACIs;
    }

    /**
     * Returns the text of this <tt>TextNode</tt> as a string.
     */
    public String getText() {
        if (text == null) {
            StringBuffer buf = new StringBuffer(aci.getEndIndex());
            for (char c = aci.first(); 
		 c != CharacterIterator.DONE; 
		 c = aci.next()) {
                buf.append(c);
            }
            text = buf.toString();
        }
        return text;
    }

    /**
     * Sets the location of this text node.
     *
     * @param newLocation the new location of this text node
     */
    public void setLocation(Point2D newLocation){
        invalidateGeometryCache();
        this.location = newLocation;
    }

    /**
     * Returns the location of this text node.
     *
     * @return the location of this text node
     */
    public Point2D getLocation(){
        return location;
    }

    /**
     * Sets the attributed character iterator of this text node.
     *
     * @param newAci the new attributed character iterator
     */
    public void setAttributedCharacterIterator(AttributedCharacterIterator
                                               newAci) {
        invalidateGeometryCache();
        this.aci = newAci;
        text = null;
        textRuns = null;
        chunkACIs = null;
    }

    /**
     * Returns the attributed character iterator of this text node.
     *
     * @return the attributed character iterator
     */
    public AttributedCharacterIterator getAttributedCharacterIterator(){
        return aci;
    }

    //
    // Geometric methods
    //

    /**
     * Invalidates this <tt>TextNode</tt>. This node and all its ancestors have
     * been informed that all its cached values related to its bounds must be
     * recomputed.  
     */
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        primitiveBounds = null;
        geometryBounds = null;
	outline = null;
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds(){
        if (primitiveBounds == null) {
            if (aci != null) {
                primitiveBounds = textPainter.getPaintedBounds(this);
            }
        }
        return primitiveBounds;
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into account. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example.
     */
    public Rectangle2D getGeometryBounds(){
        if (geometryBounds == null){
            if (aci != null) {
                geometryBounds = textPainter.getBounds(this);
            }
	}
        return geometryBounds;
    }

    /**
     * Returns true if the specified Point2D is inside the boundary of this
     * node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     */
    public boolean contains(Point2D p) {
	Rectangle2D b = getBounds();
	if (b != null) {
	    return b.contains(p.getX(), p.getY());
	} else {
	    return false;
	}
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline() {
	if (outline == null) {
	    if (aci != null) {
		outline = textPainter.getDecoratedShape(this);
	    }
	}
        return outline;
    }

    //
    // Selection methods
    //

    /**
     * Initializes the current selection to begin with the character at (x, y).
     *
     * @param the anchor of this node
     */
    public boolean selectAt(double x, double y) {
	beginMark = textPainter.selectAt(x, y, aci, this);
	return true; // assume this always changes selection, for now.
    }

    /**
     * Extends the current selection to the character at (x, y)..
     *
     * @param the anchor of this node
     */
    public boolean selectTo(double x, double y) {
        Mark tmpMark = textPainter.selectTo(x, y, beginMark, aci, this);
        boolean result = false;
        if (tmpMark != endMark) {
            endMark = tmpMark;
            result = true;
        }
        return result;
    }

    /**
     * Extends the current selection to the character at (x, y)..
     *
     * @param the anchor of this node
     */
    public boolean selectAll(double x, double y) {
        beginMark = textPainter.selectFirst(x, y, aci, this);
        endMark = textPainter.selectLast(x, y, aci, this);
        return true;
    }

    /**
     * Gets the current text selection.
     *
     * @return an object containing the selected content.
     */
    public Object getSelection() {

        int[] ranges = textPainter.getSelected(aci, beginMark, endMark);
        Object o = null;

	// TODO: later we can return more complex things like
        // noncontiguous selections
        if ((ranges != null) && (ranges.length > 1)) {
            // make sure that they are in order
            if (ranges[0] > ranges[1]) {
                int temp = ranges[1];
                ranges[1] = ranges[0];
                ranges[0] = temp;
            }
            o = new AttributedCharacterSpanIterator
		(aci, ranges[0], ranges[1]+1);
        }
        return o;
    }

    /**
     * Returns the shape used to outline this text node.
     *
     * @return a Shape which encloses the current text selection.
     */
    public Shape getHighlightShape() {
        Shape highlightShape = 
	    textPainter.getHighlightShape(beginMark, endMark);
        AffineTransform t = getGlobalTransform();
        highlightShape = t.createTransformedShape(highlightShape);
        return highlightShape;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        if (isVisible) {
            super.paint(g2d, rc);
        }
    }

    /**
     * Paints this node without applying Filter, Mask, Composite, and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        //
        // DO NOT REMOVE: THE FOLLOWING IS A WORK AROUND
        // A BUG IN THE JDK 1.2 RENDERING PIPELINE WHEN
        // THE CLIP IS A RECTANGLE
        //
        Shape clip = g2d.getClip();
        if(clip != null && !(clip instanceof GeneralPath)){
            g2d.setClip(new GeneralPath(clip));
        }
        // Paint the text
	textPainter.paint(this, g2d, rc);
    }

    /**
     * Defines where the text of a <tt>TextNode</tt> can be anchored
     * relative to its location.
     */
    public static final class Anchor implements java.io.Serializable {

        /**
         * The type of the START anchor.
         */
        public static final int ANCHOR_START  = 0;

        /**
         * The type of the MIDDLE anchor.
         */
        public static final int ANCHOR_MIDDLE = 1;

        /**
         * The type of the END anchor.
         */
        public static final int ANCHOR_END    = 2;

        /**
         * The anchor which enables the rendered characters to be aligned such
         * that the start of the text string is at the initial current text
         * location.  
	 */
        public static final Anchor START = new Anchor(ANCHOR_START);

        /**
         * The anchor which enables the rendered characters to be aligned such
         * that the middle of the text string is at the initial current text
         * location.  
	 */
        public static final Anchor MIDDLE = new Anchor(ANCHOR_MIDDLE);

        /**
         * The anchor which enables the rendered characters to be aligned such
         * that the end of the text string is at the initial current text
         * location.  
	 */
        public static final Anchor END = new Anchor(ANCHOR_END);

	/**
	 * The anchor type.
	 */
        private int type;

        /** 
	 * No instance of this class.
	 */
        private Anchor(int type) {
            this.type = type;
        }

        /**
         * Returns the type of this anchor.
         */
        public int getType() {
            return type;
        }

        /**
         * This is called by the serialization code before it returns
         * an unserialized object. To provide for unicity of
         * instances, the instance that was read is replaced by its
         * static equivalent. See the serialiazation specification for
         * further details on this method's logic.
         */
        private Object readResolve() throws java.io.ObjectStreamException {
            switch(type){
            case ANCHOR_START:
                return START;
            case ANCHOR_MIDDLE:
                return MIDDLE;
            case ANCHOR_END:
                return END;
            default:
                throw new Error("Unknown Anchor type");
            }
        }
    }
}


