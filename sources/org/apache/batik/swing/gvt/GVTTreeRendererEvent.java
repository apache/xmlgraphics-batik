/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.swing.gvt;

import java.awt.image.BufferedImage;
import java.util.EventObject;

/**
 * This class represents an event which indicate an event originated
 * from a GVTTreeRenderer instance.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GVTTreeRendererEvent extends EventObject {

    /**
     * The buffered image.
     */
    protected BufferedImage image;
    
    /**
     * Creates a new GVTTreeRendererEvent.
     * @param source the object that originated the event, ie. the
     *               GVTTreeRenderer.
     * @param bi the image to paint.
     */
    public GVTTreeRendererEvent(Object source, BufferedImage bi) {
        super(source);
        image = bi;
    }

    /**
     * Returns the image to display, or null if the rendering failed.
     */
    public BufferedImage getImage() {
        return image;
    }
}
