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
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEMorphologyElement;

/**
 * This class implements {@link SVGFEMorphologyElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEMorphologyElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEMorphologyElement {

    /**
     * The attribute initializer.
     */
    protected final static AttributeInitializer attributeInitializer;
    static {
        attributeInitializer = new AttributeInitializer(1);
        attributeInitializer.addAttribute(null,
                                          null,
                                          SVG_OPERATOR_ATTRIBUTE,
                                          SVG_ERODE_VALUE);
    }

    /**
     * Creates a new SVGOMFEMorphologyElement object.
     */
    protected SVGOMFEMorphologyElement() {
    }

    /**
     * Creates a new SVGOMFEMorphologyElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEMorphologyElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_MORPHOLOGY_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEMorphologyElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        throw new RuntimeException("!!! TODO getIn1()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEMorphologyElement#getOperator()}.
     */
    public SVGAnimatedEnumeration getOperator() {
        throw new RuntimeException(" !!! TODO getOperator()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEMorphologyElement#getRadiusX()}.
     */
    public SVGAnimatedLength getRadiusX() {
        throw new RuntimeException(" !!! TODO getRadiusX()");
    } 

    /**
     * <b>DOM</b>: Implements {@link SVGFEMorphologyElement#getRadiusY()}.
     */
    public SVGAnimatedLength getRadiusY() {
        throw new RuntimeException(" !!! TODO getRadiusY()");
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
        return new SVGOMFEMorphologyElement();
    }
}
