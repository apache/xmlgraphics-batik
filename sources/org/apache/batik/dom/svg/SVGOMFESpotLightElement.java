/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;

import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFESpotLightElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFESpotLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFESpotLightElement
    extends    SVGOMElement
    implements SVGFESpotLightElement {

    /**
     * The DefaultAttributeValueProducer for x.
     */
    protected final static DefaultAttributeValueProducer X_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_SPOT_LIGHT_X_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for y.
     */
    protected final static DefaultAttributeValueProducer Y_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_SPOT_LIGHT_Y_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for z.
     */
    protected final static DefaultAttributeValueProducer Z_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_SPOT_LIGHT_Z_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for pointsAtX.
     */
    protected final static DefaultAttributeValueProducer
        POINTS_AT_X_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_SPOT_LIGHT_POINTS_AT_X_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for pointsAtY.
     */
    protected final static DefaultAttributeValueProducer
        POINTS_AT_Y_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_SPOT_LIGHT_POINTS_AT_Y_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for pointsAtZ.
     */
    protected final static DefaultAttributeValueProducer
        POINTS_AT_Z_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_SPOT_LIGHT_POINTS_AT_Z_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for specularExponent.
     */
    protected final static DefaultAttributeValueProducer
        SPECULAR_EXPONENT_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_SPOT_LIGHT_SPECULAR_EXPONENT_DEFAULT_VALUE;
                }
            };

    /**
     * The DefaultAttributeValueProducer for limitingConeAngle.
     */
    protected final static DefaultAttributeValueProducer
        LIMITING_CONE_ANGLE_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_FE_SPOT_LIGHT_LIMITING_CONE_ANGLE_DEFAULT_VALUE;
                }
            };

    /**
     * The reference to the x attribute.
     */
    protected transient WeakReference xReference;

    /**
     * The reference to the y attribute.
     */
    protected transient WeakReference yReference;

    /**
     * The reference to the z attribute.
     */
    protected transient WeakReference zReference;

    /**
     * The reference to the pointsAtX attribute.
     */
    protected transient WeakReference pointsAtXReference;

    /**
     * The reference to the pointsAtY attribute.
     */
    protected transient WeakReference pointsAtYReference;

    /**
     * The reference to the pointsAtZ attribute.
     */
    protected transient WeakReference pointsAtZReference;

    /**
     * The reference to the specularExponent attribute.
     */
    protected transient WeakReference specularExponentReference;

    /**
     * The reference to the limitingConeAngle attribute.
     */
    protected transient WeakReference limitingConeAngleReference;

    /**
     * Creates a new SVGOMFESpotLightElement object.
     */
    protected SVGOMFESpotLightElement() {
    }

    /**
     * Creates a new SVGOMFESpotLightElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFESpotLightElement(String prefix,
                                   AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_SPOT_LIGHT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getX()}.
     */
    public SVGAnimatedNumber getX() {
        SVGAnimatedNumber result;
        if (xReference == null ||
            (result = (SVGAnimatedNumber)xReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_X_ATTRIBUTE,
                                             X_DEFAULT_VALUE_PRODUCER);
            xReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getY()}.
     */
    public SVGAnimatedNumber getY() {
        SVGAnimatedNumber result;
        if (yReference == null ||
            (result = (SVGAnimatedNumber)yReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_Y_ATTRIBUTE,
                                             Y_DEFAULT_VALUE_PRODUCER);
            yReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getZ()}.
     */
    public SVGAnimatedNumber getZ() {
        SVGAnimatedNumber result;
        if (zReference == null ||
            (result = (SVGAnimatedNumber)zReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_Z_ATTRIBUTE,
                                             Z_DEFAULT_VALUE_PRODUCER);
            zReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getPointsAtX()}.
     */
    public SVGAnimatedNumber getPointsAtX() {
        SVGAnimatedNumber result;
        if (pointsAtXReference == null ||
            (result = (SVGAnimatedNumber)pointsAtXReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_POINTS_AT_X_ATTRIBUTE,
                                             POINTS_AT_X_DEFAULT_VALUE_PRODUCER);
            pointsAtXReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getPointsAtY()}.
     */
    public SVGAnimatedNumber getPointsAtY() {
        SVGAnimatedNumber result;
        if (pointsAtYReference == null ||
            (result = (SVGAnimatedNumber)pointsAtYReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_POINTS_AT_Y_ATTRIBUTE,
                                             POINTS_AT_Y_DEFAULT_VALUE_PRODUCER);
            pointsAtYReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getPointsAtZ()}.
     */
    public SVGAnimatedNumber getPointsAtZ() {
        SVGAnimatedNumber result;
        if (pointsAtZReference == null ||
            (result = (SVGAnimatedNumber)pointsAtZReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null, SVG_POINTS_AT_Z_ATTRIBUTE,
                                             POINTS_AT_Z_DEFAULT_VALUE_PRODUCER);
            pointsAtZReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
        SVGAnimatedNumber result;
        if (specularExponentReference == null ||
            (result = (SVGAnimatedNumber)specularExponentReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null,
                                             SVG_SPECULAR_EXPONENT_ATTRIBUTE,
                                             SPECULAR_EXPONENT_DEFAULT_VALUE_PRODUCER);
            specularExponentReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFESpotLightElement#getLimitingConeAngle()}.
     */
    public SVGAnimatedNumber getLimitingConeAngle() {
        SVGAnimatedNumber result;
        if (limitingConeAngleReference == null ||
            (result = (SVGAnimatedNumber)limitingConeAngleReference.get()) == null) {
            result = new SVGOMAnimatedNumber(this, null,
                                             SVG_LIMITING_CONE_ANGLE_ATTRIBUTE,
                                             LIMITING_CONE_ANGLE_DEFAULT_VALUE_PRODUCER);
            limitingConeAngleReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpotLightElement();
    }
}
