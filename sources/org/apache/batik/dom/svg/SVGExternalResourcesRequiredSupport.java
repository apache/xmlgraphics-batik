/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGExternalResourcesRequired;

/**
 * Provides support for the SVGExternalResourcesRequired interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGExternalResourcesRequiredSupport {

    /**
     * To implement {@link
     * SVGExternalResourcesRequired#getExternalResourcesRequired()}.
     */
    public static SVGAnimatedBoolean getExternalResourcesRequired(Element elt) {
        throw new RuntimeException("!!! TODO getExternalResourcesRequired()");
    }
}
