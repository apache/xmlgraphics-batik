/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.text.AttributedCharacterIterator;

import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.Selectable;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.text.Mark;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

/**
 * A graphics node that represents text.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ConcreteTextNode
    extends    AbstractGraphicsNode
    implements TextNode,
               Selectable {

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
     * Cache: Bounds for this text node, without taking any of the
     * rendering attributes (e.g., stroke) into account
     */
    private Rectangle2D geometryBounds;

    /**
     * Cache: Primitive Bounds.
     */
    private Rectangle2D primitiveBounds;

    /**
     * Sets the location of this raster text node.
     * @param newLocation the new location of this raster image node
     */
    public void setLocation(Point2D newLocation){
        invalidateGeometryCache();
        Point2D oldLocation = location;
        this.location = newLocation;
        firePropertyChange("location", oldLocation, location);
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
        AttributedCharacterIterator oldAci = this.aci;
        this.aci = newAci;
        firePropertyChange("attributedCharacterIterator", oldAci, newAci);
    }

    /**
     * Returns the attributed character iterator of this text node.
     * @return the attributed character iterator
     */
    public AttributedCharacterIterator getAttributedCharacterIterator(){
        return aci;
    }

    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        primitiveBounds = null;
        geometryBounds = null;
    }

    /**
     * Primitive bounds are in user space.
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
     * Geometric bounds are in user space.
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
     * @return a Shape which matches the text's geometry.
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
     * Get the current text selection.
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

    public boolean hasProgressivePaint() {
        // <!> FIXME : TODO
        throw new Error("Not yet implemented");
    }

    public void progressivePaint(Graphics2D g2d, GraphicsNodeRenderContext rc) {
        // <!> FIXME : TODO
        throw new Error("Not yet implemented");
    }

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

}
