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
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A shape painter which consists of multiple shape painters.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class CompositeShapePainter implements ShapePainter {

    /**
     * The shape associated with this painter
     */
    protected Shape shape;

    /**
     * The enclosed <tt>ShapePainter</tt>s of this composite shape painter.
     */
    protected ShapePainter [] painters;

    /**
     * The number of shape painter.
     */
    protected int count;

    /**
     * Constructs a new empty <tt>CompositeShapePainter</tt>.
     */
    public CompositeShapePainter(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
    }

    /**
     * Adds the specified shape painter to the shape painter..
     *
     * @param shapePainter the shape painter to add
     */
    public void addShapePainter(ShapePainter shapePainter) {
        if (shapePainter == null) {
            return;
        }
        if (this.shape != shapePainter.getShape()) {
            shapePainter.setShape(shape);
        }
        if (painters == null) {
            painters = new ShapePainter[2];
        }
        if (count == painters.length) {
            ShapePainter [] newPainters = new ShapePainter[(count*3)/2 + 1];
            System.arraycopy(painters, 0, newPainters, 0, count);
            painters = newPainters;
        }
        painters[count++] = shapePainter;
    }

    /**
     * Sets to the specified index, the specified ShapePainter.
     *
     * @param index the index where to set the ShapePainter
     * @param shapePainter the ShapePainter to set
     */
    /*    public void setShapePainter(int index, ShapePainter shapePainter) {
        if (shapePainter == null) {
            return;
        }
        if (this.shape != shapePainter.getShape()) {
            shapePainter.setShape(shape);
        }
        if (painters == null || index >= painters.length) {
            throw new IllegalArgumentException("Bad index: "+index);
        }
        painters[index] = shapePainter;
        }*/

    /**
     * Returns the shape painter at the specified index.
     *
     * @param index the index of the shape painter to return
     */
    public ShapePainter getShapePainter(int index) {
        return painters[index];
    }

    /**
     * Returns the number of shape painter of this composite shape painter.
     */
    public int getShapePainterCount() {
        return count;
    }

    /**
     * Paints the specified shape using the specified Graphics2D.
     *
     * @param g2d the Graphics2D to use
     */
    public void paint(Graphics2D g2d) {
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                painters[i].paint(g2d);
            }
        }
    }

    /**
     * Returns the area painted by this shape painter.
     */
    public Shape getPaintedArea(){
        if (painters == null)
            return null;
        Area paintedArea = new Area();
        for (int i=0; i < count; ++i) {
            Shape s = painters[i].getPaintedArea();
            if (s != null) {
                paintedArea.add(new Area(s));
            }
        }
        return paintedArea;
    }

    /**
     * Returns the bounds of the area painted by this shape painter
     */
    public Rectangle2D getPaintedBounds2D(){
        if (painters == null) 
            return null;

        Rectangle2D bounds = null;
        for (int i=0; i < count; ++i) {
            Rectangle2D pb = painters[i].getPaintedBounds2D();
            if (pb == null) continue;
            if (bounds == null) bounds = (Rectangle2D)pb.clone();
            else                bounds.add(pb);
        }
        return bounds;
    }

    /**
     * Returns true if pt is in the area painted by this shape painter
     */
    public boolean inPaintedArea(Point2D pt){
        if (painters == null) 
            return false;
        for (int i=0; i < count; ++i) {
            if (painters[i].inPaintedArea(pt))
                return true;
        }
        return false;
    }

    /**
     * Returns the area covered by this shape painter (even if nothing
     * is painted there).
     */
    public Shape getSensitiveArea() {
        if (painters == null)
            return null;
        Area paintedArea = new Area();
        for (int i=0; i < count; ++i) {
            Shape s = painters[i].getSensitiveArea();
            if (s != null) {
                paintedArea.add(new Area(s));
            }
        }
        return paintedArea;
    }

    /**
     * Returns the bounds of the area painted by this shape painter
     */
    public Rectangle2D getSensitiveBounds2D() {
        if (painters == null) 
            return null;

        Rectangle2D bounds = null;
        for (int i=0; i < count; ++i) {
            Rectangle2D pb = painters[i].getSensitiveBounds2D();
            if (bounds == null) bounds = (Rectangle2D)pb.clone();
            else                bounds.add(pb);
        }
        return bounds;
    }

    /**
     * Returns true if pt is in the area painted by this shape painter
     */
    public boolean inSensitiveArea(Point2D pt){
        if (painters == null) 
            return false;
        for (int i=0; i < count; ++i) {
            if (painters[i].inSensitiveArea(pt))
                return true;
        }
        return false;
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
        if (painters != null) {
            for (int i=0; i < count; ++i) {
                painters[i].setShape(shape);
            }
        }
        this.shape = shape;
    }

    /**
     * Gets the Shape this shape painter is associated with.
     *
     * @return shape associated with this painter
     */
    public Shape getShape(){
        return shape;
    }
}
