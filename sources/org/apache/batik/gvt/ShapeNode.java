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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A graphics node that represents a shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ShapeNode extends AbstractGraphicsNode {

    /**
     * The shape that describes this <tt>ShapeNode</tt>.
     */
    protected Shape shape;

    /**
     * The shape painter used to paint the shape of this shape node.
     */
    protected ShapePainter shapePainter;

    /**
     * Internal Cache: Primitive bounds
     */
    private Rectangle2D primitiveBounds;

    /**
     * Internal Cache: Geometry bounds
     */
    private Rectangle2D geometryBounds;

    /**
     * Internal Cache: Sensitive bounds
     */
    private Rectangle2D sensitiveBounds;

    /**
     * Internal Cache: The painted area.
     */
    private Shape paintedArea;

    /**
     * Internal Cache: The sensitive area.
     */
    private Shape sensitiveArea;

    /**
     * Constructs a new empty <tt>ShapeNode</tt>.
     */
    public ShapeNode() {}

    //
    // Properties methods
    //

    /**
     * Sets the shape of this <tt>ShapeNode</tt>.
     *
     * @param newShape the new shape of this shape node
     */
    public void setShape(Shape newShape) {
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        this.shape = newShape;
        if(this.shapePainter != null){
            if (newShape != null) {
                this.shapePainter.setShape(newShape);
            } else {
                this.shapePainter = null;
            }
        }
        fireGraphicsNodeChangeCompleted();
    }

    /**
     * Returns the shape of this <tt>ShapeNode</tt>.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the <tt>ShapePainter</tt> used by this shape node to render its
     * shape.
     *
     * @param newShapePainter the new ShapePainter to use
     */
    public void setShapePainter(ShapePainter newShapePainter) {
        if (shape == null) // Doesn't matter if we don't have a shape.
            return;
        fireGraphicsNodeChangeStarted();
        invalidateGeometryCache();
        this.shapePainter = newShapePainter;
        if(shapePainter != null && shape != this.shapePainter.getShape()){
            shapePainter.setShape(shape);
        }
        fireGraphicsNodeChangeCompleted();
    }

    /**
     * Returns the <tt>ShapePainter</tt> used by this shape node to render its
     * shape.
     */
    public ShapePainter getShapePainter() {
        return shapePainter;
    }

    //
    // Drawing methods
    //

    /**
     * Paints this node.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d) {
        if (isVisible) {
            super.paint(g2d);
        }

    }

    /**
     * Paints this node without applying Filter, Mask, Composite, and clip.
     *
     * @param g2d the Graphics2D to use
     */
    public void primitivePaint(Graphics2D g2d) {
        if (shapePainter != null) {
            shapePainter.paint(g2d);
        }
    }

    //
    // Geometric methods
    //

    /**
     * Invalidates this <tt>ShapeNode</tt>. This node and all its ancestors have
     * been informed that all its cached values related to its bounds must be
     * recomputed.
     */
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        primitiveBounds = null;
        geometryBounds = null;
        sensitiveBounds = null;
        paintedArea = null;
        sensitiveArea = null;
    }

    /**
     * Returns true if the specified Point2D is inside the boundary of this
     * node, false otherwise.
     *
     * @param p the specified Point2D in the user space
     */
    public boolean contains(Point2D p) {
        switch(pointerEventType) {
        case VISIBLE_PAINTED:
        case VISIBLE_FILL:
        case VISIBLE_STROKE:
        case VISIBLE:
            if (!isVisible) return false;
            // Fall Through
        case PAINTED:
        case FILL:
        case STROKE:
        case ALL: {
            Rectangle2D b = getSensitiveBounds();
            if (b == null || !b.contains(p))
                return false;

            return inSensitiveArea(p);
        }
        case NONE:
        default:
            return false;
        }
    }

    /**
     * Returns true if the interior of this node intersects the interior of a
     * specified Rectangle2D, false otherwise.
     *
     * @param r the specified Rectangle2D in the user node space
     */
    public boolean intersects(Rectangle2D r) {
        Rectangle2D b = getBounds();
        if (b != null) {
            return (b.intersects(r) &&
                    paintedArea != null &&
                    paintedArea.intersects(r));
        }
        return false;
    }

    /**
     * Returns the bounds of the area covered by this node's primitive paint.
     */
    public Rectangle2D getPrimitiveBounds() {
        if (primitiveBounds == null) {
            if (shape == null)
                return null;

            if (shapePainter == null)
                primitiveBounds = shape.getBounds2D();
            else
                primitiveBounds = shapePainter.getPaintedBounds2D();

            // Make sure we haven't been interrupted
            if (Thread.currentThread().isInterrupted()) {
                // The Thread has been interrupted. Invalidate
                // any cached values and proceed.
                invalidateGeometryCache();
            }
        }
        return primitiveBounds;
    }

    public boolean inSensitiveArea(Point2D pt) {
        if (shapePainter == null)
            return false;

        // <!> NOT REALLY NICE CODE BUT NO OTHER WAY
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = shapePainter;
        } else if (shapePainter instanceof FillShapePainter) {
            fillShapePainter = shapePainter;
        } else if (shapePainter instanceof CompositeShapePainter) {
            CompositeShapePainter cp = (CompositeShapePainter)shapePainter;

            for (int i=0; i < cp.getShapePainterCount(); ++i) {
                ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                } else if (sp instanceof FillShapePainter) {
                    fillShapePainter = sp;
                }
            }
        } else {
            return false; // Don't know what we have...
        }

        switch(pointerEventType) {
        case VISIBLE_PAINTED:
        case PAINTED:
            return shapePainter.inPaintedArea(pt);
        case VISIBLE:
        case ALL:
            return shapePainter.inSensitiveArea(pt);
        case VISIBLE_FILL:
        case FILL:
            if (fillShapePainter != null)
                return fillShapePainter.inSensitiveArea(pt);
            break;
        case VISIBLE_STROKE:
        case STROKE:
            if (strokeShapePainter != null)
                return strokeShapePainter.inSensitiveArea(pt);
            break;
        case NONE:
        default:
            // nothing to tdo
        }
        return false;
    }

    /**
     * Returns the bounds of the sensitive area covered by this node,
     * This includes the stroked area but does not include the effects
     * of clipping, masking or filtering.
     */
    public Rectangle2D getSensitiveBounds() {
        if (sensitiveBounds != null)
            return sensitiveBounds;

        if (shapePainter == null)
            return null;

        // <!> NOT REALLY NICE CODE BUT NO OTHER WAY
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = shapePainter;
        } else if (shapePainter instanceof FillShapePainter) {
            fillShapePainter = shapePainter;
        } else if (shapePainter instanceof CompositeShapePainter) {
            CompositeShapePainter cp = (CompositeShapePainter)shapePainter;

            for (int i=0; i < cp.getShapePainterCount(); ++i) {
                ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                } else if (sp instanceof FillShapePainter) {
                    fillShapePainter = sp;
                }
            }
        } else return null; // Don't know what we have...


        switch(pointerEventType) {
        case VISIBLE_PAINTED:
        case PAINTED:
            sensitiveBounds = shapePainter.getPaintedBounds2D();
            break;
        case VISIBLE_FILL:
        case FILL:
            if (fillShapePainter != null) {
                sensitiveBounds = fillShapePainter.getSensitiveBounds2D();
            }
            break;
        case VISIBLE_STROKE:
        case STROKE:
            if (strokeShapePainter != null) {
                sensitiveBounds = strokeShapePainter.getSensitiveBounds2D();
            }
            break;
        case VISIBLE:
        case ALL:
            sensitiveBounds = shapePainter.getSensitiveBounds2D();
            break;
        case NONE:
        default:
            // nothing to tdo
        }
        return sensitiveBounds;
    }

    /**
     * Returns the shape that represents the sensitive area of this graphics
     * node.
     */
    public Shape getSensitiveArea() {
        if (sensitiveArea != null) 
            return sensitiveArea;
        if (shapePainter == null)
            return null;

        // <!> NOT REALLY NICE CODE BUT NO OTHER WAY
        ShapePainter strokeShapePainter = null;
        ShapePainter fillShapePainter = null;
        if (shapePainter instanceof StrokeShapePainter) {
            strokeShapePainter = shapePainter;
        } else if (shapePainter instanceof FillShapePainter) {
            fillShapePainter = shapePainter;
        } else if (shapePainter instanceof CompositeShapePainter) {
            CompositeShapePainter cp = (CompositeShapePainter)shapePainter;

            for (int i=0; i < cp.getShapePainterCount(); ++i) {
                ShapePainter sp = cp.getShapePainter(i);
                if (sp instanceof StrokeShapePainter) {
                    strokeShapePainter = sp;
                } else if (sp instanceof FillShapePainter) {
                    fillShapePainter = sp;
                }
            }
        } else return null; // Don't know what we have...


        switch(pointerEventType) {
        case VISIBLE_PAINTED:
        case PAINTED:
            sensitiveArea = shapePainter.getPaintedArea();
            break;
        case VISIBLE_FILL:
        case FILL:
            if (fillShapePainter != null) {
                sensitiveArea = fillShapePainter.getSensitiveArea();
            }
            break;
        case VISIBLE_STROKE:
        case STROKE:
            if (strokeShapePainter != null) {
                sensitiveArea = strokeShapePainter.getSensitiveArea();
            }
            break;
        case VISIBLE:
        case ALL:
            sensitiveArea = shapePainter.getSensitiveArea();
            break;
        case NONE:
        default:
            // nothing to tdo
        }
        return sensitiveArea;
    }

    /**
     * Returns the bounds of the area covered by this node, without taking any
     * of its rendering attribute into account. i.e., exclusive of any clipping,
     * masking, filtering or stroking, for example.
     */
    public Rectangle2D getGeometryBounds(){
        if (geometryBounds == null) {
            if (shape == null) {
                return null;
            }
            geometryBounds = normalizeRectangle(shape.getBounds2D());
        }
        return geometryBounds;
    }

    /**
     * Returns the outline of this node.
     */
    public Shape getOutline() {
        return shape;
    }
}
