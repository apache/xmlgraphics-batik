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
 * SVG clock values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Clock {
    /**
     * Returns the int value representing the hours.
     */
    int getHours();

    /**
     * Returns the int value representing the minutes.
     */
    int getMinutes();

    /**
     * Returns the int value representing the seconds.
     */
    int getSeconds();

    /**
     * Returns the float value representing the milliseconds.
     */
    float getMilliseconds();
}
