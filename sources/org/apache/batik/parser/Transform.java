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
 * SVG transform values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Transform {
    // The types of transforms.
    /**
     * To represent a generic matrix.
     */
    int MATRIX = 0;

    /**
     * To represent a rotation matrix.
     */
    int ROTATE = 1;

    /**
     * To represent a scaling matrix.
     */
    int SCALE = 2;

    /**
     * To represent a x shearing matrix.
     */
    int SKEWX = 3;

    /**
     * To represent a y shearing matrix.
     */
    int SKEWY = 4;

    /**
     * To represent a translation matrix.
     */
    int TRANSLATE = 5;

    /**
     * Returns the type of this matrix.
     */
    int getType();

    /**
     * Returns the angle in case the type of this transform is one of
     * ROTATE, SKEWX or SKEWY.
     * @exception IllegalStateException if the matrix type is not one
     *            of ROTATE, SKEWX or SKEWY.
     */
    float getAngle();

    /**
     * Returns the content of the cell (0, 0).
     */
    float getA();

    /**
     * An alias for getA().
     */
    float getScaleX();

    /**
     * Returns the content of the cell (0, 1).
     */
    float getB();

    /**
     * An alias for getB().
     */
    float getSkewX();

    /**
     * Returns the content of the cell (0, 2).
     */
    float getC();

    /**
     * An alias for getC().
     */
    float getTranslateX();

    /**
     * Returns the content of the cell (1, 0).
     */
    float getD();

    /**
     * An alias for getD().
     */
    float getSkewY();

    /**
     * Returns the content of the cell (1, 1).
     */
    float getE();

    /**
     * An alias for getE().
     */
    float getScaleY();

    /**
     * Returns the content of the cell (1, 2).
     */
    float getF();

    /**
     * An alias for getF().
     */
    float getTranslateY();
}
