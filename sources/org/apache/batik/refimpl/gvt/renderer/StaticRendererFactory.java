/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer;

import java.awt.image.BufferedImage;

import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.RendererFactory;

/**
 * This class is a factory for StaticRenderers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StaticRendererFactory implements RendererFactory {
    /**
     * Creates a new renderer.
     * @param im The buffered image used to render.
     */
    public Renderer createRenderer(BufferedImage im) {
        return new StaticRenderer(im);
    }
}
