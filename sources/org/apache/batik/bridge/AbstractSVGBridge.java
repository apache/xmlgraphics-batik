/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import org.apache.batik.util.SVGConstants;

/**
 * The base bridge class for SVG elements.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractSVGBridge implements Bridge, SVGConstants {

    /**
     * Constructs a new abstract bridge for SVG elements.
     */
    protected AbstractSVGBridge() {}

    /**
     * Returns the SVG namespace URI.
     */
    public String getNamespaceURI() {
        return SVG_NAMESPACE_URI;
    }
}
