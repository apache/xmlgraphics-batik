/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Rectangle;

import java.awt.image.BufferedImage;

import java.util.EventObject;

/**
 * This class represents an event which indicate an event originated
 * from a UpdateManager instance.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class UpdateManagerEvent extends EventObject {

    /**
     * The buffered image.
     */
    protected BufferedImage image;
    
    /**
     * The dirty area.
     */
    protected Rectangle dirtyArea;

    /**
     * Creates a new UpdateManagerEvent.
     * @param source the object that originated the event, ie. the
     *               UpdateManager.
     * @param bi the image to paint.
     * @param r The dirty area.
     */
    public UpdateManagerEvent(Object source, BufferedImage bi, Rectangle r) {
        super(source);
        image = bi;
        dirtyArea = r;
    }

    /**
     * Returns the image to display, or null if the rendering failed.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns the dirty area.
     */
    public Rectangle getDirtyArea() {
        return dirtyArea;
    }
}
