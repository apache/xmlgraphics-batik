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
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.text.AttributedCharacterIterator;

import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.TextNode.Anchor;
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
     * Location of this text node
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
     * Text Anchor
     */
    protected Anchor anchor = Anchor.START;

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

    /**
     * Sets the anchor of this text node.
     * @param newAnchor the new anchor of this text node
     */
    public void setAnchor(Anchor newAnchor){
        invalidateGeometryCache();
        Anchor oldAnchor = anchor;
        this.anchor = newAnchor;
        firePropertyChange("anchor", oldAnchor, anchor);
    }

    /**
     * Returns the anchor of this text node.
     * @return the anchor of this node
     */
    public Anchor getAnchor(){
        return anchor;
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
        // HACK, until we change getBounds to take GraphicsNodeRenderContext
        // We don't consider stroke and/or fill yet,
        if (primitiveBounds == null) {
            if (aci != null) {
                java.awt.font.TextLayout layout
                    = new java.awt.font.TextLayout(aci,
                      new java.awt.font.FontRenderContext(
                                            new AffineTransform(),
                                                true, true));

                float tx = (float) location.getX();
                float ty = (float) location.getY();
                if (anchor == Anchor.MIDDLE) {
                    tx -= layout.getAdvance()/2;
                } else if (anchor == Anchor.END) {
                    tx -= layout.getAdvance();
                }

                Rectangle2D layoutBounds = layout.getBounds();
                primitiveBounds = new Rectangle2D.Float((float) (tx+layoutBounds.getX()),
                                  (float) (ty+layoutBounds.getY()),
                                  (float) layout.getAdvance(),
                                  (float) layoutBounds.getHeight());

                if (aci.getAttribute(GVTAttributedCharacterIterator.
                                        TextAttribute.UNDERLINE) != null) {
                    // TODO: check WEIGHT attribute and adjust thickness
                    double decorationThickness = layout.getAscent()/12f;
                    double y =
                        layout.getDescent()/2 + decorationThickness/2f;

                    primitiveBounds.setRect(primitiveBounds.getX(), primitiveBounds.getY(),
                                 primitiveBounds.getWidth(), primitiveBounds.getHeight()+y);
                }

                if (aci.getAttribute(GVTAttributedCharacterIterator.
                                        TextAttribute.OVERLINE) != null) {
                    // TODO: check WEIGHT attribute and adjust thickness
                    double decorationThickness = layout.getAscent()/12f;
                    double dy =
                        layout.getAscent()*0.1 + decorationThickness/2f;
                    primitiveBounds.setRect(primitiveBounds.getX(), primitiveBounds.getY(),
                                 primitiveBounds.getWidth(), primitiveBounds.getHeight()+dy);
                }

                BasicStroke stroke = (BasicStroke) aci.getAttribute(
                        GVTAttributedCharacterIterator.TextAttribute.STROKE);

                if (stroke != null) {
                     float strokeHalfThickness = stroke.getLineWidth();
                     primitiveBounds.setRect(
                           primitiveBounds.getX()-strokeHalfThickness,
                           primitiveBounds.getY()-strokeHalfThickness,
                           primitiveBounds.getWidth()+strokeHalfThickness,
                           primitiveBounds.getHeight()+strokeHalfThickness);
                }

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
                java.awt.font.TextLayout layout
                    = new java.awt.font.TextLayout(aci,
                      new java.awt.font.FontRenderContext(
                                            new AffineTransform(),
                                                true, true));

                float tx = (float) location.getX();
                float ty = (float) location.getY();
                if (anchor == Anchor.MIDDLE) {
                    tx -= layout.getAdvance()/2;
                } else if (anchor == Anchor.END) {
                    tx -= layout.getAdvance();
                }

                Rectangle2D layoutBounds = layout.getBounds();
                geometryBounds = new Rectangle2D.Float((float) (tx+layoutBounds.getX()),
                                  (float) (ty+layoutBounds.getY()),
                                  (float) layout.getAdvance(),
                                  (float) layoutBounds.getHeight());

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

        // HACK, until we change getBounds to take
        // GraphicsNodeRenderContext
        Shape outline;
        if (aci != null) {
            java.awt.font.TextLayout layout
                = new java.awt.font.TextLayout(aci,
                      new java.awt.font.FontRenderContext(
                                            new AffineTransform(),
                                                          true,
                                                          true));
            double tx = location.getX();
            double ty = location.getY();
            if (anchor == Anchor.MIDDLE) {
                tx -= layout.getAdvance()/2;
            } else if (anchor == Anchor.END) {
                tx -= layout.getAdvance();
            }
            AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
            outline = layout.getOutline(t);
            // must we add decorations?
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
         beginMark = rc.getTextPainter().selectAt(x-location.getX(),
                                                 y-location.getY(),
                                                 aci, anchor, rc);
         return true; // assume this always changes selection, for now.
    }

    /**
     * Extends the current selection to the character at (x, y)..
     * @param the anchor of this node
     */
    public boolean selectTo(double x, double y, GraphicsNodeRenderContext rc) {
        Mark tmpMark = rc.getTextPainter().selectTo(x-location.getX(),
                                               y-location.getY(),
                                               beginMark, aci, anchor,
                                               rc);
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
        endMark = rc.getTextPainter().selectAll(x-location.getX(),
                                                y-location.getY(),
                                                aci, anchor, rc);
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
        Shape shape;
        shape =
            rc.getTextPainter().getHighlightShape(beginMark,
                                                  endMark,
                                                  location,
                                                  anchor);

        AffineTransform t = getGlobalTransform();
        shape = t.createTransformedShape(shape);
        return shape;
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
            textPainter.paint(aci, location, anchor, g2d, rc);
        }
        // g2d.translate(-location.getX(), -location.getY());

    }

}
