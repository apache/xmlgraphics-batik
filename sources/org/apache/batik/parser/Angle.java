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
 * SVG angles.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Angle {
    // The values returned by getType(). 
    /**
     * To represent an angle in degrees.
     */
    int DEG = 0;
    
    /**
     * To represent an angle in radians.
     */
    int RAD = 1;
    
    /**
     * To represent an angle in gradians.
     */
    int GRAD = 2;
    
    /**
     * Returns this angle type.
     */
    int getType();

    /**
     * Returns this angle value.
     */
    float getValue();
}
