/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.RendererFactory;

/**
 * This class is a factory for DynamicRenderers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DynamicRendererFactory extends StaticRendererFactory {
    /**
     * Creates a new renderer.
     */
    public Renderer createRenderer() {
        return new DynamicRenderer(getRenderContext());
    }
}
