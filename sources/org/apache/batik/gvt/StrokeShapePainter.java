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
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * A shape painter that can be used to draw the outline of a shape.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class StrokeShapePainter implements ShapePainter {

    /** 
     * Shape painted by this painter.
     */
    protected Shape shape;

    /**
     * Stroked version of the shape.
     */
    protected Shape strokedShape;

    /**
     * The stroke attribute used to draw the outline of the shape.
     */
    protected Stroke stroke;

    /**
     * The paint attribute used to draw the outline of the shape.
     */
    protected Paint paint;

    /**
     * Constructs a new <tt>ShapePainter</tt> that can be used to draw the
     * outline of a <tt>Shape</tt>.
     *
     * @param shape shape to be painted by this painter.
     * Should not be null.
     */
    public StrokeShapePainter(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    /**
     * Sets the stroke used to draw the outline of a shape.
     *
     * @param newStroke the stroke object used to draw the outline of the shape
     */
    public void setStroke(Stroke newStroke) {
        this.stroke       = newStroke;
        this.strokedShape = null;
    }

    /**
     * Sets the paint used to fill a shape.
     *
     * @param newPaint the paint object used to draw the shape
     */
    public void setPaint(Paint newPaint) {
        this.paint = newPaint;
    }

    /**
     * Paints the outline of the specified shape using the specified 
     * Graphics2D.
     *
     * @param g2d the Graphics2D to use 
     */
    public void paint(Graphics2D g2d) {
        if (stroke != null && paint != null) {
            g2d.setPaint(paint);
            g2d.setStroke(stroke);
            g2d.draw(shape);
        }
    }

    /**
     * Returns the area painted by this shape painter.
     */
    public Shape getPaintedArea(){
        if ((paint == null) || (stroke == null))
            return null;

        if (strokedShape == null)
            strokedShape = stroke.createStrokedShape(shape);

        return strokedShape;
    }

    /**
     * Returns the bounds of the area painted by this shape painter
     */
    public Rectangle2D getPaintedBounds2D() {
        Shape painted = getPaintedArea();
        if (painted == null)
            return null;

        return painted.getBounds2D();
    }

    /**
     * Returns the bounds of the area covered by this shape painter
     */
    public boolean inPaintedArea(Point2D pt){
        Shape painted = getPaintedArea();
        if (painted == null)
            return false;
        return painted.contains(pt);
    }
        
    /**
     * Returns the area covered by this shape painter (even if not painted).
     */
    public Shape getSensitiveArea(){
        if (stroke == null)
            return null;

        if (strokedShape == null)
            strokedShape = stroke.createStrokedShape(shape);

        return strokedShape;
    }

    /**
     * Returns the bounds of the area covered by this shape painter
     * (even if not painted).
     */
    public Rectangle2D getSensitiveBounds2D() {
        Shape sensitive = getSensitiveArea();
        if (sensitive == null)
            return null;

        return sensitive.getBounds2D();
    }

    /**
     * Returns the bounds of the area covered by this shape painter
     * (even if not painted).
     */
    public boolean inSensitiveArea(Point2D pt){
        Shape sensitive = getSensitiveArea();
        if (sensitive == null)
            return false;
        return sensitive.contains(pt);
    }
        
    /**
     * Sets the Shape this shape painter is associated with.
     *
     * @param shape new shape this painter should be associated with.
     * Should not be null.
     */
    public void setShape(Shape shape){
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
        this.strokedShape = null;
    }

    /**
     * Gets the Shape this shape painter is associated with.
     *
     * @return shape associated with this painter.
     */
    public Shape getShape(){
        return shape;
    }
}
