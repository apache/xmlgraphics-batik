/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.DefaultCommonCSSContext;

/**
 * This class is the default implementation of the SVGCSSContext.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultSVGCSSContext
    extends    DefaultCommonCSSContext
    implements SVGCSSContext {

    /**
     * The viewport width.
     */
    protected float viewportWidth;

    /**
     * The viewport height.
     */
    protected float viewportHeight;

    /**
     * Returns the width of the viewport.
     */
    public float getViewportWidth() {
        return viewportWidth;
    }

    /**
     * Sets the width of the viewport.
     */
    public void setViewportWidth(float f) {
        viewportWidth = f;
    }

    /**
     * Returns the height of the viewport.
     */
    public float getViewportHeight() {
        return viewportHeight;
    }

    /**
     * Sets the height of the viewport.
     */
    public void setViewportHeight(float f) {
        viewportHeight = f;
    }
}
