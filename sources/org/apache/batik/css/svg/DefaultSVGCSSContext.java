/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.svg;

import org.apache.batik.css.value.DefaultCommonCSSContext;

import org.w3c.dom.Element;

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
     * Returns the width of the viewport.
     * @throws IllegalStateException if the context was not able to compute
     *         the value.
     */
    public float getViewportWidth(Element e) throws IllegalStateException {
        // !!! TODO
        throw new IllegalStateException();
    }

    /**
     * Returns the height of the viewport.
     * @throws IllegalStateException if the context was not able to compute
     *         the value.
     */
    public float getViewportHeight(Element e) throws IllegalStateException {
        // !!! TODO
        throw new IllegalStateException();
    }

}
