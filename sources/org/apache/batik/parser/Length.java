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
 * SVG lengths.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Length {
    // The values returned by getType(). 
    /**
     * To represent an length in centimeters.
     */
    int CM = 0;
    
    /**
     * To represent an length in ems.
     */
    int EM = 1;
    
    /**
     * To represent an length in exs.
     */
    int EX = 2;
    
    /**
     * To represent an length in inches.
     */
    int IN = 3;
    
    /**
     * To represent an length in millimeters.
     */
    int MM = 4;
    
    /**
     * To represent an length in picas.
     */
    int PC = 5;
    
    /**
     * To represent an length in points.
     */
    int PT = 6;
    
    /**
     * To represent an length in pixels.
     */
    int PX = 7;
    
    /**
     * To represent an length in percentage.
     */
    int PERCENTAGE = 8;
    
    /**
     * Returns this length type.
     */
    int getType();

    /**
     * Returns this length value.
     */
    float getValue();
}
