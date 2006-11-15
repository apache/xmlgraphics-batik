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
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFECompositeElement;

/**
 * This class implements {@link SVGFECompositeElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMFECompositeElement
    extends    SVGOMFilterPrimitiveStandardAttributes
    implements SVGFECompositeElement {

    /**
     * The 'operator' attribute values.
     */
    protected final static String[] OPERATOR_VALUES = {
        "",
        SVG_OVER_VALUE,
        SVG_IN_VALUE,
        SVG_OUT_VALUE,
        SVG_ATOP_VALUE,
        SVG_XOR_VALUE,
        SVG_ARITHMETIC_VALUE
    };

    /**
     * Creates a new SVGOMFECompositeElement object.
     */
    protected SVGOMFECompositeElement() {
    }

    /**
     * Creates a new SVGOMFECompositeElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMFECompositeElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_FE_COMPOSITE_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFECompositeElement#getIn1()}.
     */
    public SVGAnimatedString getIn1() {
        return getAnimatedStringAttribute(null, SVG_IN_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFECompositeElement#getIn2()}.
     */
    public SVGAnimatedString getIn2() {
        return getAnimatedStringAttribute(null, SVG_IN2_ATTRIBUTE);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFECompositeElement#getOperator()}.
     */
    public SVGAnimatedEnumeration getOperator() {
        return getAnimatedEnumerationAttribute
            (null, SVG_OPERATOR_ATTRIBUTE, OPERATOR_VALUES, (short)1);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFECompositeElement#getK1()}.
     */
    public SVGAnimatedNumber getK1() {
        return getAnimatedNumberAttribute(null, SVG_K1_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFECompositeElement#getK2()}.
     */
    public SVGAnimatedNumber getK2() {
        return getAnimatedNumberAttribute(null, SVG_K2_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFECompositeElement#getK3()}.
     */
    public SVGAnimatedNumber getK3() {
        return getAnimatedNumberAttribute(null, SVG_K3_ATTRIBUTE, 0f);
    }

    /**
     * <b>DOM</b>: Implements {@link SVGFECompositeElement#getK4()}.
     */
    public SVGAnimatedNumber getK4() {
        return getAnimatedNumberAttribute(null, SVG_K4_ATTRIBUTE, 0f);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMFECompositeElement();
    }

    // ExtendedTraitAccess ///////////////////////////////////////////////////

    /**
     * Returns whether the given XML attribute is animatable.
     */
    public boolean isAttributeAnimatable(String ns, String ln) {
        if (ns == null) {
            if (ln.equals(SVG_IN_ATTRIBUTE)
                    || ln.equals(SVG_IN2_ATTRIBUTE)
                    || ln.equals(SVG_OPERATOR_ATTRIBUTE)
                    || ln.equals(SVG_K1_ATTRIBUTE)
                    || ln.equals(SVG_K2_ATTRIBUTE)
                    || ln.equals(SVG_K3_ATTRIBUTE)
                    || ln.equals(SVG_K4_ATTRIBUTE)) {
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
            if (ln.equals(SVG_IN_ATTRIBUTE)
                    || ln.equals(SVG_IN2_ATTRIBUTE)) {
                return SVGTypes.TYPE_CDATA;
            } else if (ln.equals(SVG_OPERATOR_ATTRIBUTE)) {
                return SVGTypes.TYPE_IDENT;
            } else if (ln.equals(SVG_K1_ATTRIBUTE)
                    || ln.equals(SVG_K2_ATTRIBUTE)
                    || ln.equals(SVG_K3_ATTRIBUTE)
                    || ln.equals(SVG_K4_ATTRIBUTE)) {
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
            } else if (ln.equals(SVG_IN2_ATTRIBUTE)) {
                updateStringAttributeValue(getIn2(), val);
                return;
            } else if (ln.equals(SVG_OPERATOR_ATTRIBUTE)) {
                updateEnumerationAttributeValue(getOperator(), val);
                return;
            } else if (ln.equals(SVG_K1_ATTRIBUTE)) {
                updateNumberAttributeValue(getK1(), val);
                return;
            } else if (ln.equals(SVG_K2_ATTRIBUTE)) {
                updateNumberAttributeValue(getK2(), val);
                return;
            } else if (ln.equals(SVG_K3_ATTRIBUTE)) {
                updateNumberAttributeValue(getK3(), val);
                return;
            } else if (ln.equals(SVG_K4_ATTRIBUTE)) {
                updateNumberAttributeValue(getK4(), val);
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
            } else if (ln.equals(SVG_IN2_ATTRIBUTE)) {
                return getBaseValue(getIn2());
            } else if (ln.equals(SVG_OPERATOR_ATTRIBUTE)) {
                return getBaseValue(getOperator());
            } else if (ln.equals(SVG_K1_ATTRIBUTE)) {
                return getBaseValue(getK1());
            } else if (ln.equals(SVG_K2_ATTRIBUTE)) {
                return getBaseValue(getK2());
            } else if (ln.equals(SVG_K3_ATTRIBUTE)) {
                return getBaseValue(getK3());
            } else if (ln.equals(SVG_K4_ATTRIBUTE)) {
                return getBaseValue(getK4());
            }
        }
        return super.getUnderlyingValue(ns, ln);
    }
}
