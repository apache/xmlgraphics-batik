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
import org.w3c.dom.svg.SVGFEPointLightElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEPointLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEPointLightElement
    extends    SVGOMElement
    implements SVGFEPointLightElement {

    /**
     * The DefaultAttributeValueProducer for x.
     */
    protected final static DefaultAttributeValueProducer X_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_DEFAULT_VALUE_FE_POINT_LIGHT_X;
                }
            };

    /**
     * The DefaultAttributeValueProducer for y.
     */
    protected final static DefaultAttributeValueProducer Y_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_DEFAULT_VALUE_FE_POINT_LIGHT_Y;
                }
            };

    /**
     * The DefaultAttributeValueProducer for z.
     */
    protected final static DefaultAttributeValueProducer Z_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_DEFAULT_VALUE_FE_POINT_LIGHT_Z;
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
     * Creates a new SVGOMFEPointLightElement object.
     */
    protected SVGOMFEPointLightElement() {
    }

    /**
     * Creates a new SVGOMFEPointLightElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEPointLightElement(String prefix,
                                    AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return TAG_FE_POINT_LIGHT;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEPointLightElement#getX()}.
     */
    public SVGAnimatedNumber getX() {
	SVGAnimatedNumber result;
	if (xReference == null ||
	    (result = (SVGAnimatedNumber)xReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_X,
                                             X_DEFAULT_VALUE_PRODUCER);
	    xReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEPointLightElement#getY()}.
     */
    public SVGAnimatedNumber getY() {
	SVGAnimatedNumber result;
	if (yReference == null ||
	    (result = (SVGAnimatedNumber)yReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_Y,
                                             Y_DEFAULT_VALUE_PRODUCER);
	    yReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEPointLightElement#getZ()}.
     */
    public SVGAnimatedNumber getZ() {
	SVGAnimatedNumber result;
	if (zReference == null ||
	    (result = (SVGAnimatedNumber)zReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, ATTR_Z,
                                             Z_DEFAULT_VALUE_PRODUCER);
	    zReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEPointLightElement();
    }
}
