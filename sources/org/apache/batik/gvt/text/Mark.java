/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.text;

/**
 * Marker interface, mostly, that encapsulates information about a selection
 * gesture.
 * @author <a href="bill.haneman@ireland.sun.com>Bill Haneman</a>
 * @version $Id$
 */

public interface Mark {

    /*
     * Return the x coordinate associated with this mark.
     */
    public double getX();

    /*
     * Return the y coordinate associated with this mark.
     */
    public double getY();

}
