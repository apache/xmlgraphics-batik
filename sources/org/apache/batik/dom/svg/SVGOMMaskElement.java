/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGMaskElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGMaskElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMMaskElement
    extends    SVGClippingMaskingElement
    implements SVGMaskElement {

    /**
     * The DefaultAttributeValueProducer for maskUnits.
     */
    protected final static DefaultAttributeValueProducer
        MASK_UNITS_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_MASK_MASK_UNITS_DEFAULT_VALUE;
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
     * The reference to the width attribute.
     */
    protected transient WeakReference widthReference;

    /**
     * The reference to the height attribute.
     */
    protected transient WeakReference heightReference;

    /**
     * The reference to the maskUnits attribute.
     */
    protected transient WeakReference maskUnitsReference;

    // The enumeration maps.
    protected final static Map STRING_TO_SHORT_MASK_UNITS = new HashMap(5);
    protected final static Map SHORT_TO_STRING_MASK_UNITS = new HashMap(5);
    static {
        STRING_TO_SHORT_MASK_UNITS.put(SVG_USER_SPACE_ON_USE_VALUE,
                                         SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_MASK_UNITS.put(SVG_OBJECT_BOUNDING_BOX_VALUE,
                                         SVGOMAnimatedEnumeration.createShort((short)2));

        SHORT_TO_STRING_MASK_UNITS.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                         SVG_USER_SPACE_ON_USE_VALUE);
        SHORT_TO_STRING_MASK_UNITS.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                         SVG_OBJECT_BOUNDING_BOX_VALUE);
    }

    /**
     * Creates a new SVGOMMaskElement object.
     */
    protected SVGOMMaskElement() {
    }

    /**
     * Creates a new SVGOMMaskElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMMaskElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_MASK_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGGradientElement#getMaskUnits()}.
     */
    public SVGAnimatedEnumeration getMaskUnits() {
        SVGAnimatedEnumeration result;
        if (maskUnitsReference == null ||
            (result = (SVGAnimatedEnumeration)maskUnitsReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_MASK_UNITS_ATTRIBUTE,
                                                  STRING_TO_SHORT_MASK_UNITS,
                                                  SHORT_TO_STRING_MASK_UNITS,
                                                  MASK_UNITS_DEFAULT_VALUE_PRODUCER);
            maskUnitsReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPatternElement#getMaskContentUnits()}.
     */
    public SVGAnimatedEnumeration getMaskContentUnits() {
        throw new RuntimeException(" !!! TODO: SVGOMMaskElement.getPatternUnits()");
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGRectElement#getX()}.
     */
    public SVGAnimatedLength getX() {
        SVGAnimatedLength result;
        if (xReference == null ||
            (result = (SVGAnimatedLength)xReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_X_ATTRIBUTE, null);
            xReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.svg.SVGRectElement#getY()}.
     */
    public SVGAnimatedLength getY() {
        SVGAnimatedLength result;
        if (yReference == null ||
            (result = (SVGAnimatedLength)yReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_Y_ATTRIBUTE, null);
            yReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRectElement#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
        SVGAnimatedLength result;
        if (widthReference == null ||
            (result = (SVGAnimatedLength)widthReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_WIDTH_ATTRIBUTE, null);
            widthReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGRectElement#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
        SVGAnimatedLength result;
        if (heightReference == null ||
            (result = (SVGAnimatedLength)heightReference.get()) == null) {
            result = new SVGOMAnimatedLength(this, null, SVG_HEIGHT_ATTRIBUTE, null);
            heightReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMMaskElement();
    }
}
