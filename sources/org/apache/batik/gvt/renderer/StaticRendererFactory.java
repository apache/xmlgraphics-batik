/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;

import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;

import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.RendererFactory;

import org.apache.batik.gvt.filter.ConcreteGraphicsNodeRableFactory;

/**
 * This class is a factory for StaticRenderers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StaticRendererFactory implements ImageRendererFactory {

    /**
     * Creates a new renderer.
     */
    public Renderer createRenderer() {
        return createImageRenderer();
    }

    /**
     * Creates a new renderer
     */
    public ImageRenderer createImageRenderer(){
        return new StaticRenderer();
    }
}
