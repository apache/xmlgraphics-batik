/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.DoublyIndexedTable;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEDisplacementMapElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEDisplacementMapElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEDisplacementMapElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEDisplacementMapElement {

    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(2);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_X_CHANNEL_SELECTOR_ATTRIBUTE,
                                          SVG_A_VALUE);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE,
                                          SVG_A_VALUE);
    }

    /**
     * Creates a new SVGOMFEDisplacementMap object.
     */
    protected SVGOMFEDisplacementMapElement() {
    }

    /**
     * Creates a new SVGOMFEDisplacementMapElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEDisplacementMapElement(String prefix,
                                         AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_DISPLACEMENT_MAP_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        throw new RuntimeException("!!! TODO: getIn1()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
        throw new RuntimeException("!!! TODO: getIn2()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEDisplacementMapElement#getScale()}.
     */
    public SVGAnimatedNumber getScale() {
        throw new RuntimeException("!!! TODO: getScale()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getXChannelSelector()}.
     */
    public SVGAnimatedEnumeration getXChannelSelector() {
        throw new RuntimeException("!!! TODO: getXChannelSelector()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDisplacementMapElement#getYChannelSelector()}.
     */
    public SVGAnimatedEnumeration getYChannelSelector() {
        throw new RuntimeException("!!! TODO: getYChannelSelector()");
    }

    /**
     * Returns the AttributeInitializer for this element type.
     * @return null if this element has no attribute with a default value.
     */
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEDisplacementMapElement();
    }
}
