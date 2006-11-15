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
import org.w3c.dom.svg.SVGAnimatedEnumeration;
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
     * The 'operator' attribute values.
     */
    protected final static String[] OPERATOR_VALUES = {
        "",
        SVG_ERODE_VALUE,
        SVG_DILATE_VALUE
    };

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
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEMorphologyElement#getOperator()}.
     */
    public SVGAnimatedEnumeration getOperator() {
        return getAnimatedEnumerationAttribute
            (null, SVG_OPERATOR_ATTRIBUTE, OPERATOR_VALUES, (short)1);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFEMorphologyElement#getRadiusX()}.
     */
    public SVGAnimatedNumber getRadiusX() {
        throw new UnsupportedOperationException
            ("SVGFEMorphologyElement.getRadiusX is not implemented"); // XXX
    } 

    /**
     * <b>DOM</b>: Implements {@link SVGFEMorphologyElement#getRadiusY()}.
     */
    public SVGAnimatedNumber getRadiusY() {
        throw new UnsupportedOperationException
            ("SVGFEMorphologyElement.getRadiusY is not implemented"); // XXX
    } 

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFEMorphologyElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_IN_ATTRIBUTE)
                    || ln.equals(SVG_OPERATOR_ATTRIBUTE)
                    || ln.equals(SVG_RADIUS_ATTRIBUTE)) {
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
            } else if (ln.equals(SVG_OPERATOR_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_RADIUS_ATTRIBUTE)) {
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
            } else if (ln.equals(SVG_OPERATOR_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getOperator(), val);
                return;
            } else if (ln.equals(SVG_RADIUS_ATTRIBUTE)) {
                // XXX Needs testing.
                if (val == null) {
                    updateNumberAttributeValue(getRadiusX(), null);
                    updateNumberAttributeValue(getRadiusY(), null);
                } else {
                    AnimatableNumberOptionalNumberValue anonv =
                        (AnimatableNumberOptionalNumberValue) val;
                    SVGOMAnimatedNumber ai =
                        (SVGOMAnimatedNumber) getRadiusX();
                    ai.setAnimatedValue(anonv.getNumber());
                    ai = (SVGOMAnimatedNumber) getRadiusY();
                    if (anonv.hasOptionalNumber()) {
                        ai.setAnimatedValue(anonv.getOptionalNumber());
                    } else {
                        ai.setAnimatedValue(anonv.getNumber());
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
            } else if (ln.equals(SVG_OPERATOR_ATTRIBUTE)) {
                return getBaseValue(getOperator());
            } else if (ln.equals(SVG_RADIUS_ATTRIBUTE)) {
                return getBaseValue(getRadiusX(), getRadiusY());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
