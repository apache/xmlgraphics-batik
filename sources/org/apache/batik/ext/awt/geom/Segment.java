/**************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.     *
 * ---------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software      *
 * License version 1.1, a copy of which has been included with this       *
 * distribution in the LICENSE file.                                      *
 **************************************************************************/

package org.apache.batik.ext.awt.geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Segment extends Cloneable {
    public double minX();
    public double maxX();
    public double minY();
    public double maxY();
    public Rectangle2D getBounds2D();

    public Point2D.Double evalDt(double t);
    public Point2D.Double eval(double t);

    public Segment getSegment(double t0, double t1);
    public Segment splitBefore(double t);
    public Segment splitAfter(double t);
    public void    subdivide(Segment s0, Segment s1);
    public void    subdivide(double t, Segment s0, Segment s1);
    public double  getLength();
    public double  getLength(double maxErr);

    public SplitResults split(double y);

    public static class SplitResults {
        Segment [] above;
        Segment [] below;
        SplitResults(Segment []below, Segment []above) {
            this.below = below;
            this.above = above;
        }

        Segment [] getBelow() {
            return below;
        }
        Segment [] getAbove() {
            return above;
        }
    }
}
