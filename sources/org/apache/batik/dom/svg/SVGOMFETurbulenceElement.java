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
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFETurbulenceElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGFETurbulenceElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFETurbulenceElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFETurbulenceElement {

    /**
     * The DefaultAttributeValueProducer for numOctaves.
     */
    protected final static DefaultAttributeValueProducer
        NUM_OCTAVES_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_DEFAULT_VALUE_FE_TURBULENCE_NUM_OCTAVES;
                }
            };

    /**
     * The DefaultAttributeValueProducer for seed.
     */
    protected final static DefaultAttributeValueProducer
        SEED_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_DEFAULT_VALUE_FE_TURBULENCE_SEED;
                }
            };

    /**
     * The reference to the numOctaves attribute.
     */
    protected transient WeakReference numOctavesReference;

    /**
     * The reference to the seed attribute.
     */
    protected transient WeakReference seedReference;

    /**
     * The reference to the stitchTiles attribute.
     */
    protected transient WeakReference stitchTilesReference;

    /**
     * The reference to the type attribute.
     */
    protected transient WeakReference typeReference;

    /**
     * The attribute-value map map.
     */
    protected static Map attributeValues = new HashMap(2);
    static {
        Map values = new HashMap(3);
        values.put(SVG_STITCH_TILES_ATTRIBUTE, SVG_NO_STITCH_VALUE);
        values.put(SVG_TYPE_ATTRIBUTE, SVG_TURBULENCE_VALUE);
        attributeValues.put(null, values);
    }

    // The enumeration maps
    protected final static Map STRING_TO_SHORT_STITCH_TILES = new HashMap(3);
    protected final static Map SHORT_TO_STRING_STITCH_TILES = new HashMap(3);
    protected final static Map STRING_TO_SHORT_TYPE = new HashMap(3);
    protected final static Map SHORT_TO_STRING_TYPE = new HashMap(3);
    static {
        STRING_TO_SHORT_STITCH_TILES.put(SVG_STITCH_VALUE,
                                         SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_STITCH_TILES.put(SVG_NO_STITCH_VALUE,
                                         SVGOMAnimatedEnumeration.createShort((short)2));

        SHORT_TO_STRING_STITCH_TILES.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                         SVG_STITCH_VALUE);
        SHORT_TO_STRING_STITCH_TILES.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                         SVG_NO_STITCH_VALUE);

        STRING_TO_SHORT_TYPE.put(SVG_FRACTAL_NOISE_VALUE,
                                 SVGOMAnimatedEnumeration.createShort((short)1));
        STRING_TO_SHORT_TYPE.put(SVG_TURBULENCE_VALUE,
                                 SVGOMAnimatedEnumeration.createShort((short)2));

        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)1),
                                 SVG_FRACTAL_NOISE_VALUE);
        SHORT_TO_STRING_TYPE.put(SVGOMAnimatedEnumeration.createShort((short)2),
                                 SVG_TURBULENCE_VALUE);
    }

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
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_TURBULENCE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFETurbulenceElement#getBaseFrequencyX()}.
     */
    public SVGAnimatedNumber getBaseFrequencyX() {
        throw new RuntimeException("!!! TODO");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFETurbulenceElement#getBaseFrequencyY()}.
     */
    public SVGAnimatedNumber getBaseFrequencyY() {
        throw new RuntimeException("!!! TODO");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFETurbulenceElement#getNumOctaves()}.
     */
    public SVGAnimatedInteger getNumOctaves() {
	SVGAnimatedInteger result;
	if (numOctavesReference == null ||
	    (result = (SVGAnimatedInteger)numOctavesReference.get()) == null) {
	    result = new SVGOMAnimatedInteger(this, null, SVG_NUM_OCTAVES_ATTRIBUTE,
                                              NUM_OCTAVES_DEFAULT_VALUE_PRODUCER);
	    numOctavesReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFETurbulenceElement#getSeed()}.
     */
    public SVGAnimatedNumber getSeed() {
	SVGAnimatedNumber result;
	if (seedReference == null ||
	    (result = (SVGAnimatedNumber)seedReference.get()) == null) {
	    result = new SVGOMAnimatedNumber(this, null, SVG_SEED_ATTRIBUTE,
                                             SEED_DEFAULT_VALUE_PRODUCER);
	    seedReference = new WeakReference(result);
	}
	return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFETurbulenceElement#getStitchTiles()}.
     */
    public SVGAnimatedEnumeration getStitchTiles() {
        SVGAnimatedEnumeration result;
        if (stitchTilesReference == null ||
            (result = (SVGAnimatedEnumeration)stitchTilesReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_STITCH_TILES_ATTRIBUTE,
                                                  STRING_TO_SHORT_STITCH_TILES,
                                                  SHORT_TO_STRING_STITCH_TILES,
                                                  null);
            stitchTilesReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFETurbulenceElement#getType()}.
     */
    public SVGAnimatedEnumeration getType() {
        SVGAnimatedEnumeration result;
        if (typeReference == null ||
            (result = (SVGAnimatedEnumeration)typeReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_TYPE_ATTRIBUTE,
                                                  STRING_TO_SHORT_TYPE,
                                                  SHORT_TO_STRING_TYPE,
                                                  null);
            typeReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * Returns the default attribute values in a map.
     * @return null if this element has no attribute with a default value.
     */
    protected Map getDefaultAttributeValues() {
        return attributeValues;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFETurbulenceElement();
    }
}
