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

import org.apache.batik.anim.values.AnimatableNumberOptionalNumberValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.SVGTypes;

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
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEDiffuseLightingElement#getKernelUnitLengthX()}.
     */
    public SVGAnimatedNumber getKernelUnitLengthX() {
        throw new UnsupportedOperationException
            ("SVGFEDiffuseLightingElement.getKernelUnitLengthX is not implemented"); // XXX
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFEDiffuseLightingElement#getKernelUnitLengthY()}.
     */
    public SVGAnimatedNumber getKernelUnitLengthY() {
        throw new UnsupportedOperationException
            ("SVGFEDiffuseLightingElement.getKernelUnitLengthY is not implemented"); // XXX
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEDiffuseLightingElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_IN_ATTRIBUTE)
                    || ln.equals(SVG_SURFACE_SCALE_ATTRIBUTE)
                    || ln.equals(SVG_DIFFUSE_CONSTANT_ATTRIBUTE)
                    || ln.equals(SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE)) {
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
                    || ln.equals(SVG_DIFFUSE_CONSTANT_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER;
            } else if (ln.equals(SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE)) {
                return SVGTypes.TYPE_NUMBER_OPTIONAL_NUMBER;
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
            } else if (ln.equals(SVG_DIFFUSE_CONSTANT_ATTRIBUTE)) {
                updateNumberAttributeValue(getDiffuseConstant(), val);
                return;
            } else if (ln.equals(SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE)) {
                // XXX Needs testing.
                if (val == null) {
                    updateNumberAttributeValue(getKernelUnitLengthX(), null);
                    updateNumberAttributeValue(getKernelUnitLengthY(), null);
                } else {
                    AnimatableNumberOptionalNumberValue anonv =
                        (AnimatableNumberOptionalNumberValue) val;
                    SVGOMAnimatedNumber an =
                        (SVGOMAnimatedNumber) getKernelUnitLengthX();
                    an.setAnimatedValue(anonv.getNumber());
                    an = (SVGOMAnimatedNumber) getKernelUnitLengthY();
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

    /**
     * Returns the underlying value of an animatable XML attribute.
     */
    public AnimatableValue getUnderlyingValue(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_IN_ATTRIBUTE)) {
                return getBaseValue(getIn1());
            } else if (ln.equals(SVG_SURFACE_SCALE_ATTRIBUTE)) {
                return getBaseValue(getSurfaceScale());
            } else if (ln.equals(SVG_DIFFUSE_CONSTANT_ATTRIBUTE)) {
                return getBaseValue(getDiffuseConstant());
            } else if (ln.equals(SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE)) {
                return getBaseValue(getKernelUnitLengthX(),
                                    getKernelUnitLengthY());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
