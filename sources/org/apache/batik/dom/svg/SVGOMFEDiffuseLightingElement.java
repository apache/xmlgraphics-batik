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
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEDiffuseLightingElement;

/**
 * This class implements {@link SVGFEDiffuseLightingElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFEDiffuseLightingElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFEDiffuseLightingElement {

    /**
     * Creates a new SVGOMFEDiffuseLightingElement object.
     */
    protected SVGOMFEDiffuseLightingElement() {
    }

    /**
     * Creates a new SVGOMFEDiffuseLightingElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFEDiffuseLightingElement(String prefix,
                                         AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_DIFFUSE_LIGHTING_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEDiffuseLightingElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDiffuseLightingElement#getSurfaceScale()}.
     */
    public SVGAnimatedNumber getSurfaceScale() {
        return getAnimatedNumberAttribute(null,
                                          SVG_SURFACE_SCALE_ATTRIBUTE,
                                          1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFEDiffuseLightingElement#getDiffuseConstant()}.
     */
    public SVGAnimatedNumber getDiffuseConstant() {
        return getAnimatedNumberAttribute(null,
                                          SVG_DIFFUSE_CONSTANT_ATTRIBUTE,
                                          1f);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEDiffuseLightingElement();
    }
}
