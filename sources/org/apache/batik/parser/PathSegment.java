/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.parser;

/**
 * This interface represents objects which hold informations about
 * SVG path segments.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface PathSegment {
    // The values returned by getType(). 
    /**
     * To represent a 'z' command.
     */
    char CLOSEPATH = 'z';

    /**
     * To represent a 'M' command.
     */
    char MOVETO_ABS = 'M';

    /**
     * To represent a 'm' command.
     */
    char MOVETO_REL = 'm';

    /**
     * To represent a 'L' command.
     */
    char LINETO_ABS = 'L';

    /**
     * To represent a 'l' command.
     */
    char LINETO_REL = 'l';

    /**
     * To represent a 'C' command.
     */
    char CURVETO_CUBIC_ABS = 'C';

    /**
     * To represent a 'c' command.
     */
    char CURVETO_CUBIC_REL = 'c';

    /**
     * To represent a 'Q' command.
     */
    char CURVETO_QUADRATIC_ABS = 'Q';

    /**
     * To represent a 'q' command.
     */
    char CURVETO_QUADRATIC_REL = 'q';

    /**
     * To represent a 'A' command.
     */
    char ARC_ABS = 'A';

    /**
     * To represent a 'a' command.
     */
    char ARC_REL = 'a';

    /**
     * To represent a 'H' command.
     */
    char LINETO_HORIZONTAL_ABS = 'H';

    /**
     * To represent a 'h' command.
     */
    char LINETO_HORIZONTAL_REL = 'h';

    /**
     * To represent a 'V' command.
     */
    char LINETO_VERTICAL_ABS = 'V';

    /**
     * To represent a 'v' command.
     */
    char LINETO_VERTICAL_REL = 'v';

    /**
     * To represent a 'S' command.
     */
    char CURVETO_CUBIC_SMOOTH_ABS = 'S';

    /**
     * To represent a 's' command.
     */
    char CURVETO_CUBIC_SMOOTH_REL = 's';

    /**
     * To represent a 'T' command.
     */
    char CURVETO_QUADRATIC_SMOOTH_ABS = 'T';

    /**
     * To represent a 't' command.
     */
    char CURVETO_QUADRATIC_SMOOTH_REL = 't';

    /**
     * Returns the type of this segment. It is also the command character.
     */
    char getType();

    /**
     * Returns the x coordinate of this segment's end point.
     * @exception IllegalStateException if this segment type is CLOSEPATH,
     *            LINETO_VERTICAL_ABS or LINETO_VERTICAL_REL. 
     */
    float getX();

    /**
     * Returns the y coordinate of this segment's end point.
     * @exception IllegalStateException if this segment type is CLOSEPATH,
     *            LINETO_HORIZONTAL_ABS or LINETO_HORIZONTAL_REL. 
     */
    float getY();

    /**
     * Returns the x coordinate of this segment's first control point if the
     * type is CURVETO_CUBIC_ABS, CURVETO_CUBIC_REL, CURVETO_QUADRATIC_ABS
     * or CURVETO_QUADRATIC_REL.
     * @exception IllegalStateException if this segment type is not an
     *            allowed one.
     */
    float getX1();

    /**
     * Returns the y coordinate of this segment's first control point if the
     * type is CURVETO_CUBIC_ABS, CURVETO_CUBIC_REL, CURVETO_QUADRATIC_ABS
     * or CURVETO_QUADRATIC_REL.
     * @exception IllegalStateException if this segment type is not an
     *            allowed one.
     */
    float getY1();

    /**
     * Returns the x coordinate of this segment's second control point if the
     * type is CURVETO_CUBIC_ABS, CURVETO_CUBIC_REL, CURVETO_CUBIC_SMOOTH_ABS
     * or CURVETO_CUBIC_SMOOTH_REL.
     * @exception IllegalStateException if this segment type is not an
     *            allowed one.
     */
    float getX2();

    /**
     * Returns the y coordinate of this segment's second control point if the
     * type is CURVETO_CUBIC_ABS, CURVETO_CUBIC_REL, CURVETO_CUBIC_SMOOTH_ABS
     * or CURVETO_CUBIC_SMOOTH_REL.
     * @exception IllegalStateException if this segment type is not an
     *            allowed one.
     */
    float getY2();

    /**
     * Returns the x-axis radius for the ellipse if this segment type is
     * ARC_ABS or ARC_REL.
     * @exception IllegalStateException if this segment is not arc.
     */
    float getR1();

    /**
     * Returns the y-axis radius for the ellipse if this segment type is
     * ARC_ABS or ARC_REL.
     * @exception IllegalStateException if this segment is not an arc.
     */
    float getR2();

    /**
     * Returns the rotation angle in degrees for the ellipse's x-axis relative
     * to the x-axis of the user coordinate system if this segment type is
     * ARC_ABS or ARC_REL.
     * @exception IllegalStateException if this segment is not an arc.
     */
    float getAngle();

    /**
     * Returns the large-arc-flag parameter value if this segment type is
     * ARC_ABS or ARC_REL.
     * @exception IllegalStateException if this segment is not an arc.
     */
    boolean getLargeArcFlag();

    /**
     * Returns the sweep-flag parameter value if this segment type is ARC_ABS
     * or ARC_REL.
     * @exception IllegalStateException if this segment is not an arc.
     */
    boolean getSweepFlag();
}
