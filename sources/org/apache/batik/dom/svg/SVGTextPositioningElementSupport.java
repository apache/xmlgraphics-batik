/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedNumberList;

import org.apache.batik.util.SVGConstants;

/**
 * This class provide support for the SVGTextPositionningElement 
 * interface.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public class SVGTextPositioningElementSupport {

    public final static String X_DEFAULT_VALUE
        = "";
    public final static String Y_DEFAULT_VALUE
        = "";
    public final static String DX_DEFAULT_VALUE
        = "";
    public final static String DY_DEFAULT_VALUE
        = "";

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextPositioningElement#getX()}.
     */
    public static SVGAnimatedLengthList getX(AbstractElement e){

        SVGOMAnimatedLengthList result =(SVGOMAnimatedLengthList)
            e.getLiveAttributeValue(null, SVGConstants.SVG_X_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedLengthList(e, null,
                                                 SVGConstants.SVG_X_ATTRIBUTE,
                                                 X_DEFAULT_VALUE,
                                                 AbstractSVGLength.HORIZONTAL_LENGTH);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_X_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextPositioningElement#getY()}.
     */
    public static SVGAnimatedLengthList getY(AbstractElement e){

        SVGOMAnimatedLengthList result =(SVGOMAnimatedLengthList)
            e.getLiveAttributeValue(null, SVGConstants.SVG_Y_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedLengthList(e, null,
                                                 SVGConstants.SVG_Y_ATTRIBUTE,
                                                 Y_DEFAULT_VALUE,
                                                 AbstractSVGLength.VERTICAL_LENGTH);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_Y_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextPositioningElement#getDx()}.
     */
    public static SVGAnimatedLengthList getDx(AbstractElement e){

        SVGOMAnimatedLengthList result =(SVGOMAnimatedLengthList)
            e.getLiveAttributeValue(null, SVGConstants.SVG_DX_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedLengthList(e, null,
                                                 SVGConstants.SVG_DX_ATTRIBUTE,
                                                 DX_DEFAULT_VALUE,
                                                 AbstractSVGLength.HORIZONTAL_LENGTH);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_DX_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextPositioningElement#getDy()}.
     */
    public static SVGAnimatedLengthList getDy(AbstractElement e){

        SVGOMAnimatedLengthList result =(SVGOMAnimatedLengthList)
            e.getLiveAttributeValue(null, SVGConstants.SVG_DY_ATTRIBUTE);
        if (result == null) {
            result = new SVGOMAnimatedLengthList(e, null,
                                                 SVGConstants.SVG_DY_ATTRIBUTE,
                                                 DY_DEFAULT_VALUE,
                                                 AbstractSVGLength.VERTICAL_LENGTH);
            e.putLiveAttributeValue(null,
                                    SVGConstants.SVG_DY_ATTRIBUTE, 
                                    (LiveAttributeValue)result);
        }
        return result;
    }
}
