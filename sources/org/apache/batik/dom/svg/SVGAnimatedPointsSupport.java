/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.util.SVGConstants;
import org.w3c.dom.svg.SVGPointList;

/**
 * This class provide support for the SVGAnimatedPoints 
 * interface.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public class SVGAnimatedPointsSupport {

    /**
     * Default value for the 'points' attribute.
     */
    public static final String POINTS_DEFAULT_VALUE
        = "";

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPoints#getPoints()}.
     */
    public static SVGPointList getPoints(AbstractElement e){
        SVGOMAnimatedPoints result =(SVGOMAnimatedPoints)
            e.getLiveAttributeValue(null, SVGConstants.SVG_POINTS_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedPoints(e, null,
                                             SVGConstants.SVG_POINTS_ATTRIBUTE,
                                             POINTS_DEFAULT_VALUE);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_POINTS_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result.getPoints();
    }


    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPoints#getAnimatedPoints()}.
     */
    public static SVGPointList getAnimatedPoints(AbstractElement e){

        SVGOMAnimatedPoints result =(SVGOMAnimatedPoints)
            e.getLiveAttributeValue(null, SVGConstants.SVG_POINTS_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedPoints(e, null,
                                             SVGConstants.SVG_POINTS_ATTRIBUTE,
                                             POINTS_DEFAULT_VALUE);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_POINTS_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result.getAnimatedPoints();
    }

}
