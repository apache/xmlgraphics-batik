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

import org.apache.batik.util.SVGConstants;

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
     * Default value for the 'transform' attribute.
     */
    public static final String TRANSFORM_DEFAULT_VALUE
        = "";

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGTransformable#getTransform()}.
     */
    public static SVGAnimatedTransformList getTransform(AbstractElement elt) {
        SVGOMAnimatedTransformList result =(SVGOMAnimatedTransformList)
            elt.getLiveAttributeValue(null, SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedTransformList(elt, null,
                                                    SVGConstants.SVG_TRANSFORM_ATTRIBUTE,
                                                    TRANSFORM_DEFAULT_VALUE);
            elt.putLiveAttributeValue(null,
                                      SVGConstants.SVG_TRANSFORM_ATTRIBUTE, 
                                      (LiveAttributeValue)result);
        }
        return result;

    }
}
