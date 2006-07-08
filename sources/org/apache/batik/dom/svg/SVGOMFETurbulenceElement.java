/*

   Copyright 2000-2003,2006  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.svg;

import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.anim.values.AnimatableNumberOptionalNumberValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

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

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_BASE_FREQUENCY_ATTRIBUTE)
                    || ln.equals(SVG_NUM_OCTAVES_ATTRIBUTE)
                    || ln.equals(SVG_SEED_ATTRIBUTE)
                    || ln.equals(SVG_STITCH_TILES_ATTRIBUTE)
                    || ln.equals(SVG_TYPE_ATTRIBUTE)) {
                return true;
            }
        }
        return super.isAttributeAnimatable(ns, ln);
    }

    /**
     * Returns the type of the given attribute.
     */
    public int getAttributeType(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_BASE_FREQUENCY_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_OPTIONAL_NUMBER;
            } else if (ln.equals(SVG_NUM_OCTAVES_ATTRIBUTE)) {
                return SVGTypes.TYPE_INTEGER;
            } else if (ln.equals(SVG_SEED_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER;
            } else if (ln.equals(SVG_STITCH_TILES_ATTRIBUTE)
                    || ln.equals(SVG_TYPE_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            }
        }
        return super.getAttributeType(ns, ln);
    }

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Updates an attribute value in this target.
     */
    public void updateAttributeValue(String ns, String ln,
                                     AnimatableValue val) {
        if (ns == null) {
            if (ln.equals(SVG_TYPE_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getType(), val);
                return;
            } else if (ln.equals(SVG_NUM_OCTAVES_ATTRIBUTE)) {
                updateIntegerAttributeValue(getNumOctaves(), val);
                return;
            } else if (ln.equals(SVG_SEED_ATTRIBUTE)) {
                updateNumberAttributeValue(getSeed(), val);
                return;
            } else if (ln.equals(SVG_STITCH_TILES_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getStitchTiles(), val);
                return;
            } else if (ln.equals(SVG_BASE_FREQUENCY_ATTRIBUTE)) {
                // XXX Needs testing.
                if (val == null) {
                    updateNumberAttributeValue(getBaseFrequencyX(), null);
                    updateNumberAttributeValue(getBaseFrequencyY(), null);
                } else {
                    AnimatableNumberOptionalNumberValue anonv =
                        (AnimatableNumberOptionalNumberValue) val;
                    SVGOMAnimatedNumber an =
                        (SVGOMAnimatedNumber) getBaseFrequencyX();
                    an.setAnimatedValue(anonv.getNumber());
                    an = (SVGOMAnimatedNumber) getBaseFrequencyY();
                    if (anonv.hasOptionalNumber()) {
                        an.setAnimatedValue(anonv.getOptionalNumber());
                    } else {
                        an.setAnimatedValue(anonv.getNumber());
                    }
                }
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }
}
