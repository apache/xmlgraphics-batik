/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.ext.awt.image;

/**
 * Defines the interface expected from a component 
 * transfer function.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface ComponentTransferFunction {
    /**
     * The various transfer types
     */
    public static final int IDENTITY = 0;
    public static final int TABLE    = 1;
    public static final int DISCRETE = 2;
    public static final int LINEAR   = 3;
    public static final int GAMMA    = 4;

    /**
     * Returns the type of this transfer function
     */
    public int getType();

    /**
     * Returns the slope value for this transfer function
     */
    public float getSlope();

    /**
     * Returns the table values for this transfer function
     */
    public float[] getTableValues();

    /**
     * Returns the intercept value for this transfer function
     */
    public float getIntercept();

    /**
     * Returns the amplitude value for this transfer function
     */
    public float getAmplitude();

    /**
     * Returns the exponent value for this transfer function
     */
    public float getExponent();

    /**
     * Returns the offset value for this transfer function
     */
    public float getOffset();
}

