/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.ext.swing;

import java.awt.GridBagConstraints;

/**
 * <tt>GridBagConstraints</tt> constants.
 *
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface GridBagConstants {
    /**
     * Specify that this component is the 
     * last component in its column or row. 
     * @since   JDK1.0
     */
    public static final int REMAINDER = GridBagConstraints.REMAINDER;

    /**
     * Do not resize the component. 
     * @since   JDK1.0
     */
    public static final int NONE = GridBagConstraints.NONE;

    /**
     * Resize the component both horizontally and vertically. 
     * @since   JDK1.0
     */
    public static final int BOTH = GridBagConstraints.BOTH;

    /**
     * Resize the component horizontally but not vertically. 
     * @since   JDK1.0
     */
    public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;

    /**
     * Resize the component vertically but not horizontally. 
     * @since   JDK1.0
     */
    public static final int VERTICAL = GridBagConstraints.VERTICAL;

    /**
     * Put the component in the center of its display area.
     * @since    JDK1.0
     */
    public static final int CENTER = GridBagConstraints.CENTER;

    /**
     * Put the component at the top of its display area,
     * centered horizontally. 
     * @since   JDK1.0
     */
    public static final int NORTH = GridBagConstraints.NORTH;

    /**
     * Put the component at the top-right corner of its display area. 
     * @since   JDK1.0
     */
    public static final int NORTHEAST = GridBagConstraints.NORTHEAST;

    /**
     * Put the component on the left side of its display area, 
     * centered vertically.
     * @since    JDK1.0
     */
    public static final int EAST = GridBagConstraints.EAST;

    /**
     * Put the component at the bottom-right corner of its display area. 
     * @since   JDK1.0
     */
    public static final int SOUTHEAST = GridBagConstraints.SOUTHEAST;

    /**
     * Put the component at the bottom of its display area, centered 
     * horizontally. 
     * @since   JDK1.0
     */
    public static final int SOUTH = GridBagConstraints.SOUTH;

    /**
     * Put the component at the bottom-left corner of its display area. 
     * @since   JDK1.0
     */
    public static final int SOUTHWEST = GridBagConstraints.SOUTHWEST;

    /**
     * Put the component on the left side of its display area, 
     * centered vertically.
     * @since    JDK1.0
     */
    public static final int WEST = GridBagConstraints.WEST;

    /**
     * Put the component at the top-left corner of its display area. 
     * @since   JDK1.0
     */
    public static final int NORTHWEST = GridBagConstraints.NORTHWEST;

}
