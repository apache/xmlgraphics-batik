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

package org.apache.batik.ext.awt.geom;

import java.awt.geom.PathIterator;

/**
 * The <code>ExtendedPathIterator</code> class represents a geometric
 * path constructed from straight lines, quadratic and cubic (Bezier)
 * curves and elliptical arcs.  This interface is identical to that of
 * PathIterator except it can return SEG_ARCTO from currentSegment,
 * also the array of values passed to currentSegment must be of length
 * 7 or an error will be thrown.
 * 
 * This does not extend PathIterator as it would break the interface
 * contract for that class.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$ */
public interface ExtendedPathIterator {

    /**
     * The segment type constant that specifies that the preceding
     * subpath should be closed by appending a line segment back to
     * the point corresponding to the most recent SEG_MOVETO.
     */
    public static final int SEG_CLOSE   = PathIterator.SEG_CLOSE;
    /** 
     * The segment type constant for a point that specifies the end
     * point of a line to be drawn from the most recently specified
     * point.  */
    public static final int SEG_MOVETO  = PathIterator.SEG_MOVETO;
    /**
     * The segment type constant for a point that specifies the end
     * point of a line to be drawn from the most recently specified
     * point.
     */
    public static final int SEG_LINETO  = PathIterator.SEG_LINETO;
    /**
     * The segment type constant for the pair of points that specify a
     * quadratic parametric curve to be drawn from the most recently
     * specified point. The curve is interpolated by solving the
     * parametric control equation in the range (t=[0..1]) using the
     * most recently specified (current) point (CP), the first control
     * point (P1), and the final interpolated control point (P2). 
     */
    public static final int SEG_QUADTO  = PathIterator.SEG_QUADTO;
    /**
     * The segment type constant for the set of 3 points that specify
     * a cubic parametric curve to be drawn from the most recently
     * specified point. The curve is interpolated by solving the
     * parametric control equation in the range (t=[0..1]) using the
     * most recently specified (current) point (CP), the first control
     * point (P1), the second control point (P2), and the final
     * interpolated control point (P3).
     */
    public static final int SEG_CUBICTO = PathIterator.SEG_CUBICTO;

    /** The segment type constant for an elliptical arc.  This consists of
     *  Seven values [rx, ry, angle, largeArcFlag, sweepFlag, x, y].
     *  rx, ry are the radious of the ellipse.
     *  angle is angle of the x axis of the ellipse.
     *  largeArcFlag is zero if the smaller of the two arcs are to be used.
     *  sweepFlag is zero if the 'left' branch is taken one otherwise.
     *  x and y are the destination for the ellipse.  */
    public static final int SEG_ARCTO = 4321;

    /** The winding rule constant for specifying an even-odd rule for
     * determining the interior of a path. The even-odd rule specifies
     * that a point lies inside the path if a ray drawn in any
     * direction from that point to infinity is crossed by path
     * segments an odd number of times.  
     */ 
    public static final int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD; 
    /**
     * The winding rule constant for specifying a non-zero rule for
     * determining the interior of a path. The non-zero rule specifies
     * that a point lies inside the path if a ray drawn in any
     * direction from that point to infinity is crossed by path
     * segments a different number of times in the counter-clockwise
     * direction than the clockwise direction.
     */
     public static final int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;


    public int currentSegment(double[] coords);
    public int currentSegment(float[] coords);
    public int getWindingRule(); 
    public boolean isDone();
    public void next();
}

