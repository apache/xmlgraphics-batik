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
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFETurbulenceElement;

/**
 * This class implements {@link SVGFETurbulenceElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFETurbulenceElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFETurbulenceElement {

    /**
     * The 'stitchTiles' attribute values.
     */
    protected final static String[] STITCH_TILES_VALUES = {
        "",
        SVG_STITCH_VALUE,
        SVG_NO_STITCH_VALUE
    };

    /**
     * The 'type' attribute values.
     */
    protected final static String[] TYPE_VALUES = {
        "",
        SVG_FRACTAL_NOISE_VALUE,
        SVG_TURBULENCE_VALUE
    };

    /**
     * Creates a new SVGOMFETurbulence object.
     */
    protected SVGOMFETurbulenceElement() {
    }

    /**
     * Creates a new SVGOMFETurbulenceElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFETurbulenceElement(String prefix,
                                    AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_TURBULENCE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFETurbulenceElement#getBaseFrequencyX()}.
     */
    public SVGAnimatedNumber getBaseFrequencyX() {
        throw new RuntimeException("!!! TODO getBaseFrequencyX()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFETurbulenceElement#getBaseFrequencyY()}.
     */
    public SVGAnimatedNumber getBaseFrequencyY() {
        throw new RuntimeException("!!! TODO getBaseFrequencyY()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFETurbulenceElement#getNumOctaves()}.
     */
    public SVGAnimatedInteger getNumOctaves() {
        return getAnimatedIntegerAttribute(null, SVG_NUM_OCTAVES_ATTRIBUTE, 1);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFETurbulenceElement#getSeed()}.
     */
    public SVGAnimatedNumber getSeed() {
        return getAnimatedNumberAttribute(null, SVG_SEED_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFETurbulenceElement#getStitchTiles()}.
     */
    public SVGAnimatedEnumeration getStitchTiles() {
        return getAnimatedEnumerationAttribute
            (null, SVG_STITCH_TILES_ATTRIBUTE, STITCH_TILES_VALUES, (short)2);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFETurbulenceElement#getType()}.
     */
    public SVGAnimatedEnumeration getType() {
        return getAnimatedEnumerationAttribute
            (null, SVG_TYPE_ATTRIBUTE, TYPE_VALUES, (short)2);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFETurbulenceElement();
    }
}
