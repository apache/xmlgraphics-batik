/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.gvt.renderer;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.TextPainter;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;

import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.RendererFactory;

import org.apache.batik.refimpl.gvt.filter.ConcreteGraphicsNodeRableFactory;

/**
 * This class is a factory for StaticRenderers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StaticRendererFactory implements RendererFactory {

    private GraphicsNodeRenderContext nodeRenderContext = null;

    /**
     * Creates a new renderer.
     * @param im The buffered image used to render.
     */
    public Renderer createRenderer(BufferedImage im) {
        return new StaticRenderer(im, getRenderContext());
    }

    /**
     * @return a GraphicsNodeRenderContext suitable for use 
     * with this factory's Renderers.
     */
    public GraphicsNodeRenderContext getRenderContext() {
        if (nodeRenderContext == null) { 
            RenderingHints hints = new RenderingHints(null);
            hints.put(RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_ON);

            hints.put(RenderingHints.KEY_INTERPOLATION,
                  RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            FontRenderContext fontRenderContext =
                new FontRenderContext(new AffineTransform(), true, true);

            TextPainter textPainter = new StrokingTextPainter();

            GraphicsNodeRableFactory gnrFactory =
                new ConcreteGraphicsNodeRableFactory();

            nodeRenderContext =
                new GraphicsNodeRenderContext(new AffineTransform(),
                                          null,
                                          hints,
                                          fontRenderContext,
                                          textPainter,
                                          gnrFactory);
            }

        return nodeRenderContext;   
    }

}
