/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

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
import org.apache.batik.dom.util.DoublyIndexedTable;
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
     * Table mapping XML attribute names to TraitInformation objects.
     */
    protected static DoublyIndexedTable xmlTraitInformation;
    static {
        DoublyIndexedTable t =
            new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, SVG_X_ATTRIBUTE,
                new TraitInformation(true, SVGTypes.TYPE_NUMBER));
        t.put(null, SVG_Y_ATTRIBUTE,
                new TraitInformation(true, SVGTypes.TYPE_NUMBER));
        t.put(null, SVG_Z_ATTRIBUTE,
                new TraitInformation(true, SVGTypes.TYPE_NUMBER));
        t.put(null, SVG_POINTS_AT_X_ATTRIBUTE,
                new TraitInformation(true, SVGTypes.TYPE_NUMBER));
        t.put(null, SVG_POINTS_AT_Y_ATTRIBUTE,
                new TraitInformation(true, SVGTypes.TYPE_NUMBER));
        t.put(null, SVG_POINTS_AT_Z_ATTRIBUTE,
                new TraitInformation(true, SVGTypes.TYPE_NUMBER));
        t.put(null, SVG_SPECULAR_EXPONENT_ATTRIBUTE,
                new TraitInformation(true, SVGTypes.TYPE_NUMBER));
        t.put(null, SVG_LIMITING_CONE_ANGLE_ATTRIBUTE,
                new TraitInformation(true, SVGTypes.TYPE_NUMBER));
        xmlTraitInformation = t;
    }

    /**
     * The 'x' attribute value.
     */
    protected SVGOMAnimatedNumber x;

    /**
     * The 'y' attribute value.
     */
    protected SVGOMAnimatedNumber y;

    /**
     * The 'z' attribute value.
     */
    protected SVGOMAnimatedNumber z;

    /**
     * The 'pointsAtX' attribute value.
     */
    protected SVGOMAnimatedNumber pointsAtX;

    /**
     * The 'pointsAtY' attribute value.
     */
    protected SVGOMAnimatedNumber pointsAtY;

    /**
     * The 'pointsAtZ' attribute value.
     */
    protected SVGOMAnimatedNumber pointsAtZ;

    /**
     * The 'specularExponent' attribute value.
     */
    protected SVGOMAnimatedNumber specularExponent;

    /**
     * The 'limitingConeAngle' attribute value.
     */
    protected SVGOMAnimatedNumber limitingConeAngle;

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
        initializeLiveAttributes();
    }

    /**
     * Initializes all live attributes for this element.
     */
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        initializeLiveAttributes();
    }

    /**
     * Initializes the live attribute values of this element.
     */
    private void initializeLiveAttributes() {
        x = createLiveAnimatedNumber(null, SVG_X_ATTRIBUTE, 0f);
        y = createLiveAnimatedNumber(null, SVG_Y_ATTRIBUTE, 0f);
        z = createLiveAnimatedNumber(null, SVG_Z_ATTRIBUTE, 0f);
        pointsAtX =
            createLiveAnimatedNumber(null, SVG_POINTS_AT_X_ATTRIBUTE, 0f);
        pointsAtY =
            createLiveAnimatedNumber(null, SVG_POINTS_AT_Y_ATTRIBUTE, 0f);
        pointsAtZ =
            createLiveAnimatedNumber(null, SVG_POINTS_AT_Z_ATTRIBUTE, 0f);
        specularExponent =
            createLiveAnimatedNumber(null, SVG_SPECULAR_EXPONENT_ATTRIBUTE, 1f);
        limitingConeAngle =
            createLiveAnimatedNumber
                (null, SVG_LIMITING_CONE_ANGLE_ATTRIBUTE, 0f);
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
        return x;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getY()}.
     */
    public SVGAnimatedNumber getY() {
        return y;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getZ()}.
     */
    public SVGAnimatedNumber getZ() {
        return z;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtX()}.
     */
    public SVGAnimatedNumber getPointsAtX() {
        return pointsAtX;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtY()}.
     */
    public SVGAnimatedNumber getPointsAtY() {
        return pointsAtY;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpotLightElement#getPointsAtZ()}.
     */
    public SVGAnimatedNumber getPointsAtZ() {
        return pointsAtZ;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpotLightElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
        return specularExponent;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpotLightElement#getLimitingConeAngle()}.
     */
    public SVGAnimatedNumber getLimitingConeAngle() {
        return limitingConeAngle;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpotLightElement();
    }

    /**
     * Returns the table of TraitInformation objects for this element.
     */
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
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

    /**
     * Returns the underlying value of an animatable XML attribute.
     */
    public AnimatableValue getUnderlyingValue(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_X_ATTRIBUTE)) {
                return getBaseValue(getX());
            } else if (ln.equals(SVG_Y_ATTRIBUTE)) {
                return getBaseValue(getY());
            } else if (ln.equals(SVG_Z_ATTRIBUTE)) {
                return getBaseValue(getZ());
            } else if (ln.equals(SVG_POINTS_AT_X_ATTRIBUTE)) {
                return getBaseValue(getPointsAtX());
            } else if (ln.equals(SVG_POINTS_AT_Y_ATTRIBUTE)) {
                return getBaseValue(getPointsAtY());
            } else if (ln.equals(SVG_POINTS_AT_Z_ATTRIBUTE)) {
                return getBaseValue(getPointsAtZ());
            } else if (ln.equals(SVG_SPECULAR_EXPONENT_ATTRIBUTE)) {
                return getBaseValue(getSpecularExponent());
            } else if (ln.equals(SVG_LIMITING_CONE_ANGLE_ATTRIBUTE)) {
                return getBaseValue(getLimitingConeAngle());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
