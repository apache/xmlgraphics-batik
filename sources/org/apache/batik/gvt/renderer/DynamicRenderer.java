/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.image.BufferedImage;
import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * Simple implementation of the Renderer that supports dynamic updates.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DynamicRenderer extends StaticRenderer {

    /**
     * Constructs a new dynamic renderer with the specified buffer image.
     * @param offScreen the offscreen buffer to use
     */
    public DynamicRenderer(BufferedImage offScreen) {
        super(offScreen);
    }

    /**
     * Constructs a new dynamic renderer with the specified buffer image.
     * @param offScreen the offscreen buffer to use
     * @param rc the GraphicsNodeRenderContext to use
     */
    public DynamicRenderer(BufferedImage offScreen,
                           GraphicsNodeRenderContext rc) {
        super(offScreen, rc);
    }
}
