/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.renderer;


/**
 * This class provides a factory for renderers.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ConcreteImageRendererFactory implements ImageRendererFactory {

    /**
     * Creates a new renderer.
     */
    public Renderer createRenderer() {
        return createStaticImageRenderer();
    }

    /**
     * Creates a new static image renderer
     */
    public ImageRenderer createStaticImageRenderer(){
        return new StaticRenderer();
    }

    /**
     * Creates a new dynamic image renderer
     */
    public ImageRenderer createDynamicImageRenderer(){
        return new DynamicRenderer();
    }
}
