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
 * SVG preserveAspectRatio attribute value.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface PreserveAspectRatio {
    // The values returned by getAlign().
    /**
     * To represent the 'none' alignment.
     */
    int NONE = 0;
    
    /**
     * To represent the 'xMinYMin' alignment.
     */
    int XMINYMIN = 1;
    
    /**
     * To represent the 'xMidYMin' alignment.
     */
    int XMIDYMIN = 2;
    
    /**
     * To represent the 'xMaxYMin' alignment.
     */
    int XMAXYMIN = 3;
    
    /**
     * To represent the 'xMinYMid' alignment.
     */
    int XMINYMID = 4;
    
    /**
     * To represent the 'xMidYMid' alignment.
     */
    int XMIDYMID = 5;
    
    /**
     * To represent the 'xMaxYMid' alignment.
     */
    int XMAXYMID = 6;
    
    /**
     * To represent the 'xMinYMax' alignment.
     */
    int XMINYMAX = 7;
    
    /**
     * To represent the 'xMidYMax' alignment.
     */
    int XMIDYMAX = 8;
    
    /**
     * To represent the 'xMaxYMax' alignment.
     */
    int XMAXYMAX = 9;
    
    // The values returned by getMeetOrSlice().
    /**
     * To represent the 'meet' value.
     */
    int MEET = 0;
    
    /**
     * To represent the 'slice' value.
     */
    int SLICE = 1;

    /**
     * Returns the align value of the represented attribute.
     */
    int getAlign();

    /**
     * Returns the meet or slice value of the represented attribute.
     */
    int getMeetOrSlice();
}
