/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.svg.SVGAnimatedBoolean;

/**
 * Provides support for the SVGExternalResourcesRequired interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGExternalResourcesRequiredSupport implements SVGConstants {

    private final static String ATTR_NAME = SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE;

    /**
     * To implement {@link
     * org.w3c.dom.svg.SVGExternalResourcesRequired#getExternalResourcesRequired()}.
     */
    public static SVGAnimatedBoolean getExternalResourcesRequired(AbstractElement elt) {
        LiveAttributeValue lav;
        lav = elt.getLiveAttributeValue(null, ATTR_NAME);
        if (lav == null) {
            lav = new SVGOMAnimatedBoolean(elt, null, ATTR_NAME,
                                           elt.getAttributeNodeNS(null, ATTR_NAME),
                                           "false");
            elt.putLiveAttributeValue(null, ATTR_NAME, lav);
        }
        return (SVGAnimatedBoolean)lav;
    }
}
