/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing;

import java.awt.Image;
import java.beans.SimpleBeanInfo;

/**
 * This class represents a general-purpose SVG component.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class JSVGCanvasBeanInfo extends SimpleBeanInfo {

    /** A color 16x16 icon. */
    protected Image iconColor16x16;

    /** A greyscale 16x16 icon. */
    protected Image iconMono16x16;

    /** A color 32x32 icon. */
    protected Image iconColor32x32;

    /** A greyscale 32x32 icon. */
    protected Image iconMono32x32;

    /**
     * Constructs a new <tt>BeanInfo</tt> for a <tt>JSVGCanvas</tt>.
     */
    public JSVGCanvasBeanInfo() {
        iconColor16x16 = loadImage("resources/batikColor16x16.gif");
        iconMono16x16 = loadImage("resources/batikMono16x16.gif");
        iconColor32x32 = loadImage("resources/batikColor32x32.gif");
        iconMono32x32 = loadImage("resources/batikMono32x32.gif");
    }

    /**
     * Returns an icon for the specified type.
     */
    public Image getIcon(int iconType) {
        switch(iconType) {
        case ICON_COLOR_16x16:
            return iconColor16x16;
        case ICON_MONO_16x16:
            return iconMono16x16;
        case ICON_COLOR_32x32:
            return iconColor32x32;
        case ICON_MONO_32x32:
            return iconMono32x32;
        default:
            return null;
        }
    }
}

