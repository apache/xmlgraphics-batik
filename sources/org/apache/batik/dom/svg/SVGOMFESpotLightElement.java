/*

   Copyright 2000-2003  The Apache Software Foundation 

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
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFESpotLightElement;

/**
 * This class implements {@link SVGFESpotLightElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFESpotLightElement
    extends    SVGOMElement
    implements SVGFESpotLightElement {

    /**
     * Creates a new SVGOMFESpotLightElement object.
     */
    protected SVGOMFESpotLightElement() {
    }

    /**
     * Creates a new SVGOMFESpotLightElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFESpotLightElement(String prefix,
                                   AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_SPOT_LIGHT_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getX()}.
     */
    public SVGAnimatedNumber getX() {
        return getAnimatedNumberAttribute(null, SVG_X_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getY()}.
     */
    public SVGAnimatedNumber getY() {
        return getAnimatedNumberAttribute(null, SVG_Y_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getZ()}.
     */
    public SVGAnimatedNumber getZ() {
        return getAnimatedNumberAttribute(null, SVG_Z_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtX()}.
     */
    public SVGAnimatedNumber getPointsAtX() {
        return getAnimatedNumberAttribute(null, SVG_POINTS_AT_X_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtY()}.
     */
    public SVGAnimatedNumber getPointsAtY() {
        return getAnimatedNumberAttribute(null, SVG_POINTS_AT_Y_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtZ()}.
     */
    public SVGAnimatedNumber getPointsAtZ() {
        return getAnimatedNumberAttribute(null, SVG_POINTS_AT_Z_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpotLightElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
        return getAnimatedNumberAttribute
            (null, SVG_SPECULAR_EXPONENT_ATTRIBUTE, 1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpotLightElement#getLimitingConeAngle()}.
     */
    public SVGAnimatedNumber getLimitingConeAngle() {
        return getAnimatedNumberAttribute
            (null, SVG_LIMITING_CONE_ANGLE_ATTRIBUTE, 0f);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpotLightElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_X_ATTRIBUTE)
                    || ln.equals(SVG_Y_ATTRIBUTE)
                    || ln.equals(SVG_Z_ATTRIBUTE)
                    || ln.equals(SVG_POINTS_AT_X_ATTRIBUTE)
                    || ln.equals(SVG_POINTS_AT_Y_ATTRIBUTE)
                    || ln.equals(SVG_POINTS_AT_Z_ATTRIBUTE)
                    || ln.equals(SVG_SPECULAR_EXPONENT_ATTRIBUTE)
                    || ln.equals(SVG_LIMITING_CONE_ANGLE_ATTRIBUTE)) {
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
            if (ln.equals(SVG_X_ATTRIBUTE)
                    || ln.equals(SVG_Y_ATTRIBUTE)
                    || ln.equals(SVG_Z_ATTRIBUTE)
                    || ln.equals(SVG_POINTS_AT_X_ATTRIBUTE)
                    || ln.equals(SVG_POINTS_AT_Y_ATTRIBUTE)
                    || ln.equals(SVG_POINTS_AT_Z_ATTRIBUTE)
                    || ln.equals(SVG_SPECULAR_EXPONENT_ATTRIBUTE)
                    || ln.equals(SVG_LIMITING_CONE_ANGLE_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER;
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
            if (ln.equals(SVG_X_ATTRIBUTE)) {
                updateNumberAttributeValue(getX(), val);
                return;
            } else if (ln.equals(SVG_Y_ATTRIBUTE)) {
                updateNumberAttributeValue(getY(), val);
                return;
            } else if (ln.equals(SVG_Z_ATTRIBUTE)) {
                updateNumberAttributeValue(getZ(), val);
                return;
            } else if (ln.equals(SVG_POINTS_AT_X_ATTRIBUTE)) {
                updateNumberAttributeValue(getPointsAtX(), val);
                return;
            } else if (ln.equals(SVG_POINTS_AT_Y_ATTRIBUTE)) {
                updateNumberAttributeValue(getPointsAtY(), val);
                return;
            } else if (ln.equals(SVG_POINTS_AT_Z_ATTRIBUTE)) {
                updateNumberAttributeValue(getPointsAtZ(), val);
                return;
            } else if (ln.equals(SVG_SPECULAR_EXPONENT_ATTRIBUTE)) {
                updateNumberAttributeValue(getSpecularExponent(), val);
                return;
            } else if (ln.equals(SVG_LIMITING_CONE_ANGLE_ATTRIBUTE)) {
                updateNumberAttributeValue(getLimitingConeAngle(), val);
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }
}
