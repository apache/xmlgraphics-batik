/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.image.BufferedImage;
import java.util.EventObject;
import java.util.List;

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
     * The dirty areas, as a List of Rectangles.
     */
    protected List dirtyAreas;

    /**
     * Creates a new UpdateManagerEvent.
     * @param source the object that originated the event, ie. the
     *               UpdateManager.
     * @param bi the image to paint.
     * @param r The dirty area.
     */
    public UpdateManagerEvent(Object source, BufferedImage bi, List das) {
        super(source);
        image = bi;
        dirtyAreas = das;
    }

    /**
     * Returns the image to display, or null if the rendering failed.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns the dirty areas (list of rectangles)
     */
    public List getDirtyAreas() {
        return dirtyAreas;
    }
}
