/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.geom;

import java.awt.*;
import java.awt.geom.*;

/**
 * The <code>ExtendedGeneralPath</code> class represents a geometric
 * path constructed from straight lines, quadratic and cubic (Bézier)
 * curves and elliptical arc. This class delegates lines and curves to
 * an enclosed <code>GeneralPath</code>. Elliptical arc is implemented
 * using an <code>Arc2D</code> in float precision.
 *
 * <p><b>Warning</b> : An elliptical arc may be composed of several
 * path segments. For futher details, see the SVG Appendix&nbsp;F.6
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ExtendedGeneralPath implements Shape, Cloneable {

    /** The enclosed general path. */
    protected GeneralPath path;

    /**
     * Constructs a new <code>ExtendedGeneralPath</code>.
     */
    public ExtendedGeneralPath() {
        path = new GeneralPath();
    }

    /**
     * Constructs a new <code>ExtendedGeneralPath</code> with the
     * specified winding rule to control operations that require the
     * interior of the path to be defined.
     */
    public ExtendedGeneralPath(int rule) {
        path = new GeneralPath(rule);
    }

    /**
     * Constructs a new <code>ExtendedGeneralPath</code> object with
     * the specified winding rule and the specified initial capacity
     * to store path coordinates.
     */
    public ExtendedGeneralPath(int rule, int initialCapacity) {
        path = new GeneralPath(rule, initialCapacity);
    }

    /**
     * Constructs a new <code>ExtendedGeneralPath</code> object from
     * an arbitrary <code>Shape</code> object.
     */
    public ExtendedGeneralPath(Shape s) {
        path = new GeneralPath(s);
    }

    /**
     * Adds an elliptical arc, defined by two radii, an angle from the
     * x-axis, a flag to choose the large arc or not, a flag to
     * indicate if we increase or decrease the angles and the final
     * point of the arc.
     *
     * @param rx,&nbsp;ry the radii of the ellipse
     *
     * @param theta the angle from the x-axis of the current
     * coordinate system to the x-axis of the ellipse in degrees.
     *
     * @param largeArcFlag the large arc flag. If true the arc
     * spanning less than or equal to 180 degrees is chosen, otherwise
     * the arc spanning greater than 180 degrees is chosen
     *
     * @param sweepFlag the sweep flag. If true the line joining
     * center to arc sweeps through decreasing angles otherwise it
     * sweeps through increasing angles
     *
     * @param x,&nbsp;y the absolute coordinates of the final point of
     * the arc.
     */
    public synchronized void arcTo(double rx, double ry,
                                   double angle,
                                   boolean largeArcFlag,
                                   boolean sweepFlag,
                                   double x, double y) {
        //
        // Elliptical arc implementation based on the SVG specification notes
        //

        // Ensure radii are valid
        if (rx == 0 || ry == 0) {
            lineTo((float) x, (float) y);
            return;
        }
        // Get the current (x, y) coordinates of the path
        Point2D p2d = path.getCurrentPoint();
        double x0 = p2d.getX();
        double y0 = p2d.getY();
	if (x0 == x && y0 == y) {
	    // If the endpoints (x, y) and (x0, y0) are identical, then this
	    // is equivalent to omitting the elliptical arc segment entirely.
	    return;
	}
        // Compute the half distance between the current and the final point
        double dx2 = (x0 - x) / 2.0;
        double dy2 = (y0 - y) / 2.0;
        // Convert angle from degrees to radians
        angle = Math.toRadians(angle % 360.0);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        //
        // Step 1 : Compute (x1, y1)
        //
        double x1 = (cosAngle * dx2 + sinAngle * dy2);
        double y1 = (-sinAngle * dx2 + cosAngle * dy2);
        // Ensure radii are large enough
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double Prx = rx * rx;
        double Pry = ry * ry;
        double Px1 = x1 * x1;
        double Py1 = y1 * y1;
        // check that radii are large enough
        double radiiCheck = Px1/Prx + Py1/Pry;
        if (radiiCheck > 1) {
            rx = Math.sqrt(radiiCheck) * rx;
            ry = Math.sqrt(radiiCheck) * ry;
            Prx = rx * rx;
            Pry = ry * ry;
        }

        //
        // Step 2 : Compute (cx1, cy1)
        //
        double sign = (largeArcFlag == sweepFlag) ? -1 : 1;
        double sq = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1)) / ((Prx*Py1)+(Pry*Px1));
        sq = (sq < 0) ? 0 : sq;
        double coef = (sign * Math.sqrt(sq));
        double cx1 = coef * ((rx * y1) / ry);
        double cy1 = coef * -((ry * x1) / rx);

        //
        // Step 3 : Compute (cx, cy) from (cx1, cy1)
        //
        double sx2 = (x0 + x) / 2.0;
        double sy2 = (y0 + y) / 2.0;
        double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
        double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);

        //
        // Step 4 : Compute the angleStart (angle1) and the angleExtent (dangle)
        //
        double ux = (x1 - cx1) / rx;
        double uy = (y1 - cy1) / ry;
        double vx = (-x1 - cx1) / rx;
        double vy = (-y1 - cy1) / ry;
        double p, n;
        // Compute the angle start
        n = Math.sqrt((ux * ux) + (uy * uy));
        p = ux; // (1 * ux) + (0 * uy)
        sign = (uy < 0) ? -1d : 1d;
        double angleStart = Math.toDegrees(sign * Math.acos(p / n));

        // Compute the angle extent
        n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
        p = ux * vx + uy * vy;
        sign = (ux * vy - uy * vx < 0) ? -1d : 1d;
        double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
        if(!sweepFlag && angleExtent > 0) {
            angleExtent -= 360f;
        } else if (sweepFlag && angleExtent < 0) {
            angleExtent += 360f;
        }
        angleExtent %= 360f;
        angleStart %= 360f;

        //
        // We can now build the resulting Arc2D in double precision
        //
        Arc2D.Double arc = new Arc2D.Double();
        arc.x = cx - rx;
        arc.y = cy - ry;
        arc.width = rx * 2.0;
        arc.height = ry * 2.0;
        arc.start = -angleStart;
        arc.extent = -angleExtent;
        AffineTransform t = AffineTransform.getRotateInstance(angle, cx, cy);
        Shape s = t.createTransformedShape(arc);
        append(s, true);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized void moveTo(float x, float y) {
        path.moveTo(x, y);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized void lineTo(float x, float y) {
        path.lineTo(x, y);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized void quadTo(float x1, float y1, float x2, float y2) {
        path.quadTo(x1, y1, x2, y2);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized void curveTo(float x1, float y1,
                                     float x2, float y2,
                                     float x3, float y3) {
        path.curveTo(x1, y1, x2, y2, x3, y3);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized void closePath() {
        path.closePath();
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public void append(Shape s, boolean connect) {
        path.append(s, connect);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public void append(PathIterator pi, boolean connect) {
        path.append(pi, connect);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized int getWindingRule() {
        return path.getWindingRule();
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public void setWindingRule(int rule) {
        path.setWindingRule(rule);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized Point2D getCurrentPoint() {
        return path.getCurrentPoint();
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized void reset() {
        path.reset();
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public void transform(AffineTransform at) {
        path.transform(at);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized Shape createTransformedShape(AffineTransform at) {
        return path.createTransformedShape(at);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public java.awt.Rectangle getBounds() {
        return path.getBounds();
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public synchronized Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public boolean contains(Point2D p) {
        return path.contains(p);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public boolean contains(double x, double y, double w, double h) {
        return path.contains(x, y, w, h);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public boolean contains(Rectangle2D r) {
        return path.contains(r);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public boolean intersects(double x, double y, double w, double h) {
        return path.intersects(x, y, w, h);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public boolean intersects(Rectangle2D r) {
        return path.intersects(r);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public PathIterator getPathIterator(AffineTransform at) {
        return path.getPathIterator(at);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return path.getPathIterator(at, flatness);
    }

    /**
     * Delegates to the enclosed <code>GeneralPath</code>.
     */
    public Object clone() {
        try {
            ExtendedGeneralPath result = (ExtendedGeneralPath) super.clone();
            result.path = (GeneralPath) path.clone();
            return result;
        } catch (CloneNotSupportedException ex) {}
        return null;
    }
}
