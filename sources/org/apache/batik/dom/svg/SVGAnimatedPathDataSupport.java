/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.svg.SVGPathSegList;

/**
 * This class provide support for the SVGAnimatedPathData 
 * interface.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public class SVGAnimatedPathDataSupport {

    /**
     * Default value for the 'd' attribute.
     */
    public static final String D_DEFAULT_VALUE
        = "";

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPathData#getPathSegList()}.
     */
    public static SVGPathSegList getPathSegList(AbstractElement e){
        SVGOMAnimatedPathData result =(SVGOMAnimatedPathData)
            e.getLiveAttributeValue(null, SVGConstants.SVG_D_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedPathData(e, null,
                                               SVGConstants.SVG_D_ATTRIBUTE,
                                               D_DEFAULT_VALUE);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_D_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result.getPathSegList();
    }


    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPathData#getNormalizedPathSegList()}.
     */
    public static SVGPathSegList getNormalizedPathSegList(AbstractElement e){

        SVGOMAnimatedPathData result =(SVGOMAnimatedPathData)
            e.getLiveAttributeValue(null, SVGConstants.SVG_D_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedPathData(e, null,
                                               SVGConstants.SVG_D_ATTRIBUTE,
                                               D_DEFAULT_VALUE);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_D_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result.getNormalizedPathSegList();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPathData#getAnimatedPathSegList()}.
     */
    public static SVGPathSegList getAnimatedPathSegList(AbstractElement e){
        SVGOMAnimatedPathData result =(SVGOMAnimatedPathData)
            e.getLiveAttributeValue(null, SVGConstants.SVG_D_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedPathData(e, null,
                                               SVGConstants.SVG_D_ATTRIBUTE,
                                               D_DEFAULT_VALUE);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_D_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result.getAnimatedPathSegList();
    }


    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPathData#getNormalizedPathSegList()}.
     */
    public static SVGPathSegList getAnimatedNormalizedPathSegList(AbstractElement e){

        SVGOMAnimatedPathData result =(SVGOMAnimatedPathData)
            e.getLiveAttributeValue(null, SVGConstants.SVG_D_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedPathData(e, null,
                                               SVGConstants.SVG_D_ATTRIBUTE,
                                               D_DEFAULT_VALUE);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_D_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result.getAnimatedNormalizedPathSegList();
    }

}
