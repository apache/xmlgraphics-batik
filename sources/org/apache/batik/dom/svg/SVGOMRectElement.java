/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGRectElement;

/**
 * This class implements {@link SVGRectElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMRectElement
    extends    SVGGraphicsElement
    implements SVGRectElement {

    /**
     * Creates a new SVGOMRectElement object.
     */
    protected SVGOMRectElement() {
    }

    /**
     * Creates a new SVGOMRectElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMRectElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_RECT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGRectElement#getX()}.
     */
    public SVGAnimatedLength getX() {
        return getAnimatedLengthAttribute
            (null, SVG_X_ATTRIBUTE, SVG_RECT_X_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH);
    } 

    /**
     * <b>DOM</b>: Implements {@link SVGRectElement#getY()}.
     */
    public SVGAnimatedLength getY() {
        return getAnimatedLengthAttribute
            (null, SVG_Y_ATTRIBUTE, SVG_RECT_Y_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGRectElement#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
        return getAnimatedLengthAttribute
            (null, SVG_WIDTH_ATTRIBUTE, "",
             SVGOMAnimatedLength.HORIZONTAL_LENGTH);
    } 

    /**
     * <b>DOM</b>: Implements {@link SVGRectElement#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
        return getAnimatedLengthAttribute
            (null, SVG_HEIGHT_ATTRIBUTE, "",
             SVGOMAnimatedLength.VERTICAL_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGRectElement#getRx()}.
     */
    public SVGAnimatedLength getRx() {
        SVGAnimatedLength result =
            (SVGAnimatedLength)getLiveAttributeValue(null, SVG_RX_ATTRIBUTE);
        if (result == null) {
            result = new AbstractSVGAnimatedLength
                (this, null, SVG_RX_ATTRIBUTE,
                 SVGOMAnimatedLength.HORIZONTAL_LENGTH) {
                    protected String getDefaultValue() {
                        Attr attr = getAttributeNodeNS(null, SVG_RY_ATTRIBUTE);
                        if (attr == null) {
                            return "0";
                        }
                        return attr.getValue();
                    }
                };
            putLiveAttributeValue(null, SVG_RX_ATTRIBUTE,
                                  (LiveAttributeValue)result);
        }
        return result;
    } 

    /**
     * <b>DOM</b>: Implements {@link SVGRectElement#getRy()}.
     */
    public SVGAnimatedLength getRy() {
        SVGAnimatedLength result =
            (SVGAnimatedLength)getLiveAttributeValue(null, SVG_RY_ATTRIBUTE);
        if (result == null) {
            result = new AbstractSVGAnimatedLength
                (this, null, SVG_RY_ATTRIBUTE,
                 SVGOMAnimatedLength.HORIZONTAL_LENGTH) {
                    protected String getDefaultValue() {
                        Attr attr = getAttributeNodeNS(null, SVG_RX_ATTRIBUTE);
                        if (attr == null) {
                            return "0";
                        }
                        return attr.getValue();
                    }
                };
            putLiveAttributeValue(null, SVG_RY_ATTRIBUTE,
                                  (LiveAttributeValue)result);
        }
        return result;
    } 

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMRectElement();
    }
}
