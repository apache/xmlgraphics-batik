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
import org.apache.batik.util.SVGTypes;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFESpecularLightingElement;

/**
 * This class implements {@link SVGFESpecularLightingElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFESpecularLightingElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFESpecularLightingElement {

    /**
     * Creates a new SVGOMFESpecularLightingElement object.
     */
    protected SVGOMFESpecularLightingElement() {
    }

    /**
     * Creates a new SVGOMFESpecularLightingElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFESpecularLightingElement(String prefix,
                                          AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_SPECULAR_LIGHTING_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFESpecularLightingElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpecularLightingElement#getSurfaceScale()}.
     */
    public SVGAnimatedNumber getSurfaceScale() {
        return getAnimatedNumberAttribute(null,
                                          SVG_SURFACE_SCALE_ATTRIBUTE,
                                          1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpecularLightingElement#getSpecularConstant()}.
     */
    public SVGAnimatedNumber getSpecularConstant() {
        return getAnimatedNumberAttribute(null,
                                          SVG_SPECULAR_CONSTANT_ATTRIBUTE,
                                          1f);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGFESpecularLightingElement#getSpecularExponent()}.
     */
    public SVGAnimatedNumber getSpecularExponent() {
        return getAnimatedNumberAttribute(null,
                                          SVG_SPECULAR_EXPONENT_ATTRIBUTE,
                                          1f);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFESpecularLightingElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_IN_ATTRIBUTE)
                    || ln.equals(SVG_SURFACE_SCALE_ATTRIBUTE)
                    || ln.equals(SVG_SPECULAR_CONSTANT_ATTRIBUTE)
                    || ln.equals(SVG_SPECULAR_EXPONENT_ATTRIBUTE)) {
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
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            } else if (ln.equals(SVG_SURFACE_SCALE_ATTRIBUTE)
                    || ln.equals(SVG_SPECULAR_CONSTANT_ATTRIBUTE)
                    || ln.equals(SVG_SPECULAR_EXPONENT_ATTRIBUTE)) {
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
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                updateStringAttributeValue(getIn1(), val);
                return;
            } else if (ln.equals(SVG_SURFACE_SCALE_ATTRIBUTE)) {
                updateNumberAttributeValue(getSurfaceScale(), val);
                return;
            } else if (ln.equals(SVG_SPECULAR_CONSTANT_ATTRIBUTE)) {
                updateNumberAttributeValue(getSpecularConstant(), val);
                return;
            } else if (ln.equals(SVG_SPECULAR_EXPONENT_ATTRIBUTE)) {
                updateNumberAttributeValue(getSpecularExponent(), val);
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
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                return getBaseValue(getIn1());
            } else if (ln.equals(SVG_SURFACE_SCALE_ATTRIBUTE)) {
                return getBaseValue(getSurfaceScale());
            } else if (ln.equals(SVG_SPECULAR_CONSTANT_ATTRIBUTE)) {
                return getBaseValue(getSpecularConstant());
            } else if (ln.equals(SVG_SPECULAR_EXPONENT_ATTRIBUTE)) {
                return getBaseValue(getSpecularExponent());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
