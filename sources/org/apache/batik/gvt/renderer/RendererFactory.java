/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import org.apache.batik.gvt.GraphicsNodeRenderContext;

/**
 * Interface for GVT renderer factory.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface RendererFactory {
    /**
     * Creates a new renderer.
     */
    Renderer createRenderer();

    /**
     * @return a GraphicsNodeRenderContext of the type used by the renderers.
     * This may be a shared instance or a new instance, clients of this
     * API should not depend on which.
     */
    GraphicsNodeRenderContext getRenderContext();
}
