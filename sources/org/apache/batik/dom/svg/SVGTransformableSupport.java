/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedTransformList;

/**
 * This class provides support for the SVGTransformable interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGTransformableSupport {
    /**
     * Creates a new SVGTransformableSupport.
     */
    public SVGTransformableSupport() {
    }
    
    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getTransform()}.
     */
    public static SVGAnimatedTransformList getTransform(Element elt) {
	throw new RuntimeException(" !!! TODO: getTransform()");
    }
}
