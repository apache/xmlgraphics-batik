/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEBlendElement;

/**
 * This class implements {@link SVGFEBlendElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEBlendElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEBlendElement {

    /**
     * The 'mode' attribute values.
     */
    protected final static String[] MODE_VALUES = {
        "",
        SVG_NORMAL_VALUE,
        SVG_MULTIPLY_VALUE,
        SVG_SCREEN_VALUE,
        SVG_DARKEN_VALUE,
        SVG_LIGHTEN_VALUE
    };

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
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEBlendElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
        return getAnimatedStringAttribute(null, SVG_IN2_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEBlendElement#getMode()}.
     */
    public SVGAnimatedEnumeration getMode() {
        return getAnimatedEnumerationAttribute
            (null, SVG_MODE_ATTRIBUTE, MODE_VALUES, (short)1);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEBlendElement();
    }
}
