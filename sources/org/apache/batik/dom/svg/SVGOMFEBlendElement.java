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
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEBlendElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFEBlendElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEBlendElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEBlendElement {

    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(1);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_MODE_ATTRIBUTE,
                                          SVG_NORMAL_VALUE);
    }

    /**
     * Creates a new SVGOMFEBlendElement object.
     */
    protected SVGOMFEBlendElement() {
    }

    /**
     * Creates a new SVGOMFEBlendElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEBlendElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_BLEND_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEBlendElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        throw new RuntimeException("!!! TODO: getIn1()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEBlendElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
        throw new RuntimeException("!!! TODO: getIn2()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEBlendElement#getMode()}.
     */
    public SVGAnimatedEnumeration getMode() {
        throw new RuntimeException("!!! TODO: getMode()");
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
        return new SVGOMFEBlendElement();
    }
}
