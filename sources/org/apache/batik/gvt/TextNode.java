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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
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
     * Internal Cache: Bounds for this text node, without taking any of the
     * rendering attributes (e.g., stroke) into account
     */
    private Rectangle2D geometryBounds;

    /**
     * Internal Cache: Primitive Bounds.
     */
    private Rectangle2D primitiveBounds;

    /**
     * Constructs a new empty <tt>TextNode</tt>.
     */
    public TextNode() {}

    /**
     * Sets the location of this raster text node.
     * @param newLocation the new location of this raster image node
     */
    public void setLocation(Point2D newLocation){
        invalidateGeometryCache();
        this.location = newLocation;
    }

    /**
     * Returns the location of this raster image node.
     * @return the location of this raster image node
     */
    public Point2D getLocation(){
        return location;
    }

    /**
     * Sets the attributed character iterator of this text node.
     * @param newAci the new attributed character iterator
     */
    public void setAttributedCharacterIterator(AttributedCharacterIterator
                                               newAci) {
        invalidateGeometryCache();
        this.aci = newAci;
    }

    /**
     * Returns the attributed character iterator of this text node.
     * @return the attributed character iterator
     */
    public AttributedCharacterIterator getAttributedCharacterIterator(){
        return aci;
    }

    //
    // Geometric methods
    //

    /**
     * Invalidates this <tt>TextNode</tt>. This node and all its
     * ancestors have been informed that all its cached values related
     * to its bounds must be recomputed.
     */
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        primitiveBounds = null;
        geometryBounds = null;
    }

    /**
     * Returns the primitive bounds in user space of this text node.
     */
    public Rectangle2D getPrimitiveBounds(GraphicsNodeRenderContext rc){

        if (primitiveBounds == null) {
            if (aci != null) {
                primitiveBounds = rc.getTextPainter().getPaintedBounds(this,
                                       rc.getFontRenderContext());
            } else {
                // Don't cache if ACI is null
                return new Rectangle2D.Float(0, 0, 0, 0);
            }
        }

        return primitiveBounds;
    }

    /**
     * Returns the geometric bounds in user space of this text node.
     */
    public Rectangle2D getGeometryBounds(GraphicsNodeRenderContext rc){
        if (geometryBounds == null){
            if (aci != null) {
                geometryBounds = rc.getTextPainter().getBounds(this,
                                      rc.getFontRenderContext());
            } else {
                // Don't cache if ACI is null
                return new Rectangle2D.Float(0, 0, 0, 0);
            }
        }

        return geometryBounds;
    }

    /**
     * Returns whether a given point is enclosed by the text node's bounds.
     */
    public boolean contains(Point2D p, GraphicsNodeRenderContext rc) {
        return getBounds(rc).contains(p.getX(), p.getY());
    }

    /**
     * Returns a shape which matches the text's geometry.
     */
    public Shape getOutline(GraphicsNodeRenderContext rc) {
        Shape outline;
        if (aci != null) {
            outline = rc.getTextPainter().getDecoratedShape(
                                              this, rc.getFontRenderContext());
        } else {
            outline = new Rectangle2D.Float(0, 0, 0, 0);
        }
        return outline;
    }

    //
    // Selection methods
    //

    Mark beginMark = null;
    Mark endMark = null;

    /**
     * Initializes the current selection to begin with the character at (x, y).
     * @param the anchor of this node
     */
    public boolean selectAt(double x, double y, GraphicsNodeRenderContext rc) {
         beginMark = rc.getTextPainter().selectAt(x, y, aci, rc);
         return true; // assume this always changes selection, for now.
    }

    /**
     * Extends the current selection to the character at (x, y)..
     * @param the anchor of this node
     */
    public boolean selectTo(double x, double y, GraphicsNodeRenderContext rc) {
        Mark tmpMark = rc.getTextPainter().selectTo(x, y, beginMark, aci, rc);
        boolean result = false;

        if (tmpMark != endMark) {
            endMark = tmpMark;
            result = true;
        }

        return result;
    }

    /**
     * Extends the current selection to the character at (x, y)..
     * @param the anchor of this node
     */
    public boolean selectAll(double x, double y, GraphicsNodeRenderContext rc) {
        endMark = rc.getTextPainter().selectAll(x, y, aci, rc);
        beginMark = endMark;
        return true;
    }

    /**
     * Gets the current text selection.
     * @return an object containing the selected content.
     */
    public Object getSelection(GraphicsNodeRenderContext rc) {
        int[] ranges = rc.getTextPainter().getSelected(
                                               aci, beginMark, endMark);
        Object o = null;

        // TODO: later we can return more complex things like
        // noncontiguous selections

        if (endMark == beginMark) {
            // XXX HACK WARNING we should do this better;:
            // for now use this as the signal for select all
            o = aci;
        } else {
            if ((ranges != null) && (ranges.length > 1)
                && (ranges[1] > ranges[0])) {
                o = new AttributedCharacterSpanIterator(
                                           aci, ranges[0], ranges[1]);
            }
        }
        // TODO: later we will replace with
        // AttributedCharacterMultiSpanIterator(aci, ranges);
        return o;
    }

    /**
     * @return a Shape which encloses the current text selection.
     */
    public Shape getHighlightShape(GraphicsNodeRenderContext rc) {
        Shape highlightShape;
        highlightShape =
            rc.getTextPainter().getHighlightShape(beginMark,
                                                  endMark);

        AffineTransform t = getGlobalTransform();
        highlightShape = t.createTransformedShape(highlightShape);
        return highlightShape;
    }

    //
    // Drawing methods
    //

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
        TextPainter textPainter = rc.getTextPainter();
        if(textPainter != null) {
            textPainter.paint(this, g2d, rc);
        }

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
         * The anchor which enables the rendered characters to be
         * aligned such that the start of the text string is at the
         * initial current text location.
         */
        public static final Anchor START = new Anchor(ANCHOR_START);

        /**
         * The anchor which enables the rendered characters to be
         * aligned such that the middle of the text string is at the
         * initial current text location.
         */
        public static final Anchor MIDDLE = new Anchor(ANCHOR_MIDDLE);

        /**
         * The anchor which enables the rendered characters to be
         * aligned such that the end of the text string is at the
         * initial current text location.
         */
        public static final Anchor END = new Anchor(ANCHOR_END);

        private int type;

        /** No instance of this class. */
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
